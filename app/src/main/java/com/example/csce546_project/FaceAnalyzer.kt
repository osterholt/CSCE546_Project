package com.example.csce546_project

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.csce546_project.database.PictureModel
import com.example.csce546_project.model.FaceNetModel
import com.example.csce546_project.model.Prediction
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executors
import kotlin.math.pow
import kotlin.math.sqrt

class FaceAnalyzer(
	private var model: FaceNetModel,
	private var viewModel: PictureViewModel,
	private val onFacesDetected: (List<Face>, Prediction) -> Unit
) : ImageAnalysis.Analyzer {

	private val pictures = viewModel.pictures
	private val executor = Executors.newSingleThreadExecutor() // Single thread for consistency
	private var isProcessing = false

	// Add debounce mechanism
	private var lastProcessingTimeMs: Long = 0
	private val MINIMUM_PROCESS_INTERVAL_MS = 500 // Process at most every 500ms

	private var subject = FloatArray(model.embeddingDim)
	private val nameScoreHashmap = HashMap<PictureModel, ArrayList<Float>>()

	// Cache last prediction to reduce flickering
	private var lastPrediction: Prediction? = null
	private var consecutiveNoMatches = 0
	private val MAX_NO_MATCHES = 5 // Number of frames before clearing last prediction

	// Use any one of the two metrics, "cosine" or "l2"
//	private val metricToBeUsed = "l2"
	private val metricToBeUsed = "cosine"

	private val detector = FaceDetection.getClient(
		FaceDetectorOptions.Builder()
			.setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
			.setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
			.setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
			.setMinFaceSize(0.15f) // Increase minimum face size to reduce distant false detections
			.build()
	)

	@OptIn(ExperimentalGetImage::class)
	override fun analyze(imageProxy: ImageProxy) {
		val currentTimeMs = System.currentTimeMillis()

		// Skip processing if not enough time has passed or already processing
		if (isProcessing || (currentTimeMs - lastProcessingTimeMs < MINIMUM_PROCESS_INTERVAL_MS)) {
			imageProxy.close()
			return
		}

		lastProcessingTimeMs = currentTimeMs
		isProcessing = true

		try {
			val mediaImage = imageProxy.image
			if (mediaImage != null) {
				// Convert to bitmap IMMEDIATELY, before any async operations
				val bitmap = toBitmap(mediaImage)
				val rotationDegrees = imageProxy.imageInfo.rotationDegrees
				val rotatedBitmap = rotateBitmap(bitmap, rotationDegrees.toFloat())

				// Process the image with ML Kit's face detection
				val inputImage = InputImage.fromMediaImage(mediaImage, rotationDegrees)

				detector.process(inputImage)
					.addOnSuccessListener(executor) { faces ->
						if (faces.isNotEmpty()) {
							// Now use the already-created bitmap that was made before any callbacks
							CoroutineScope(Dispatchers.Default).launch {
								runModel(faces, rotatedBitmap)
							}
						} else {
							// No faces detected
							handleNoFacesDetected()
						}
					}
					.addOnFailureListener(executor) { e ->
						Log.e("FaceAnalyzer", "Face detection failed", e)
						isProcessing = false
					}
					.addOnCompleteListener {
						imageProxy.close()
					}
			} else {
				imageProxy.close()
				isProcessing = false
			}
		} catch (e: Exception) {
			Log.e("FaceAnalyzer", "Error in analyze", e)
			imageProxy.close()
			isProcessing = false
		}
	}

	private fun handleNoFacesDetected() {
		consecutiveNoMatches++
		if (consecutiveNoMatches >= MAX_NO_MATCHES) {
			// Reset last prediction after several frames with no faces
			lastPrediction = null
			CoroutineScope(Dispatchers.Main).launch {
				onFacesDetected(emptyList(), Prediction(Rect(), "Unknown"))
			}
		} else if (lastPrediction != null) {
			// Keep using last prediction for a few frames to reduce flickering
			CoroutineScope(Dispatchers.Main).launch {
				onFacesDetected(emptyList(), lastPrediction!!)
			}
		}
		isProcessing = false
	}

	private suspend fun runModel(faces: List<Face>, cameraFrameBitmap: Bitmap) {
		if (pictures.value.isNullOrEmpty()) {
			withContext(Dispatchers.Main) {
				isProcessing = false
			}
			return
		}

		withContext(Dispatchers.Default) {
			try {
				val predictions = ArrayList<Prediction>()
				for (face in faces) {
					try {
						val croppedBitmap = cropRectFromBitmap(cameraFrameBitmap, face.boundingBox)
						subject = model.getFaceEmbedding(croppedBitmap)

						pictures.value?.let { picturesList ->
							for (picture in picturesList) {
								if (picture.faceData == null) continue

								// Initialize face embedding if needed
								if (picture.mlFace.isEmpty()) {
									picture.mlFace = model.getFaceEmbedding(picture.faceData!!)
								}

								// Calculate similarity score
								val scores = nameScoreHashmap.getOrPut(picture) { ArrayList() }
								val score = if (metricToBeUsed == "cosine") {
									cosineSimilarity(subject, picture.mlFace)
								} else {
									L2Norm(subject, picture.mlFace)
								}
								scores.add(score)
							}
						}

						// Find best match
						var bestScoreName = "Unknown"
						if (nameScoreHashmap.isNotEmpty()) {
							val avgScores = nameScoreHashmap.map { entry ->
								Log.d("Face Analyzer - Run Model", "Name = ${entry.key.name} Average = ${entry.value.average()}")
								Pair(entry.key.name, entry.value.average())
							}

							bestScoreName = if (metricToBeUsed == "cosine") {
								val maxEntry = avgScores.maxByOrNull { it.second }
								model.model.cosineThreshold = 0.1f
								if (maxEntry != null && maxEntry.second > model.model.cosineThreshold) {
									maxEntry.first ?: "Unknown"
								} else "Unknown"
							} else {
								val minEntry = avgScores.minByOrNull { it.second }
								if (minEntry != null && minEntry.second < model.model.l2Threshold) {
									minEntry.first ?: "Unknown"
								} else "Unknown"
							}
						}

						Log.d("Face Analyzer - Run Model", "best score name = ${bestScoreName}")
						val newPrediction = Prediction(face.boundingBox, bestScoreName)
						predictions.add(newPrediction)

						// Reset no matches counter since we found a face
						consecutiveNoMatches = 0

						// Update last prediction
						lastPrediction = newPrediction

						// Clear hashmap for next analysis
						nameScoreHashmap.clear()
					} catch (e: Exception) {
						Log.e("FaceAnalyzer", "Error processing face: ${e.message}")
					}
				}

				withContext(Dispatchers.Main) {
					val firstPrediction = predictions.firstOrNull() ?: lastPrediction
					if (firstPrediction != null) {
						onFacesDetected(faces, firstPrediction)
					}
					isProcessing = false
				}
			} catch (e: Exception) {
				Log.e("FaceAnalyzer", "Error in runModel", e)
				withContext(Dispatchers.Main) {
					isProcessing = false
				}
			}
		}
	}

	private fun cropRectFromBitmap(source: Bitmap, rect: Rect): Bitmap {
		// Ensure bounds are within the bitmap dimensions
		val x = rect.left.coerceAtLeast(0)
		val y = rect.top.coerceAtLeast(0)
		val width = rect.width().coerceAtMost(source.width - x)
		val height = rect.height().coerceAtMost(source.height - y)

		return if (width > 0 && height > 0) {
			Bitmap.createBitmap(source, x, y, width, height)
		} else {
			// Return the original if we can't crop
			source
		}
	}

	private fun rotateBitmap(source: Bitmap, degrees: Float): Bitmap {
		if (degrees == 0f) return source

		val matrix = Matrix()
		matrix.postRotate(degrees)
		return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, false)
	}

	// Compute the L2 norm of (x2 - x1)
	private fun L2Norm(x1: FloatArray, x2: FloatArray): Float {
		return sqrt(x1.mapIndexed { i, xi -> (xi - x2[i]).pow(2) }.sum())
	}

	// Compute the cosine of the angle between x1 and x2
	private fun cosineSimilarity(x1: FloatArray, x2: FloatArray): Float {
		val mag1 = sqrt(x1.map { it * it }.sum())
		val mag2 = sqrt(x2.map { it * it }.sum())
		val dot = x1.mapIndexed { i, xi -> xi * x2[i] }.sum()
		return dot / (mag1 * mag2)
	}

	fun toBitmap(image: Image): Bitmap {
		val yBuffer = image.planes[0].buffer
		val uBuffer = image.planes[1].buffer
		val vBuffer = image.planes[2].buffer

		val ySize = yBuffer.remaining()
		val uSize = uBuffer.remaining()
		val vSize = vBuffer.remaining()

		val nv21 = ByteArray(ySize + uSize + vSize)

		yBuffer.get(nv21, 0, ySize)
		vBuffer.get(nv21, ySize, vSize)
		uBuffer.get(nv21, ySize + vSize, uSize)

		val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
		val out = ByteArrayOutputStream()
		yuvImage.compressToJpeg(Rect(0, 0, image.width, image.height), 75, out) // Reduced quality for better performance
		val jpegBytes = out.toByteArray()

		return BitmapFactory.decodeByteArray(jpegBytes, 0, jpegBytes.size)
	}
}