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

class FaceAnalyzer(private var model: FaceNetModel,
				   private var viewModel: PictureViewModel,
				   private val onFacesDetected: (List<Face>, Prediction, Bitmap?) -> Unit) : ImageAnalysis.Analyzer {

	private val pictures = viewModel.pictures
   	private val executor = Executors.newCachedThreadPool()
	private var isProcessing = false

	private var subject = FloatArray( model.embeddingDim )
	private val nameScoreHashmap = HashMap<PictureModel,ArrayList<Float>>()

	// Use any one of the two metrics, "cosine" or "l2"
	private val metricToBeUsed = "l2"

	private val detector = FaceDetection.getClient(
		FaceDetectorOptions.Builder()
			.setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
			.setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
			.build()
	)

	@OptIn(ExperimentalGetImage::class)
	override fun analyze(imageProxy: ImageProxy) {
		if(isProcessing) {
			imageProxy.close()
			return
		} else {
			isProcessing = true
			Log.d("FaceAnalyzer - Analyze", "Is processing: True")
			Log.d("FaceAnalyzer - Analyze", "Image proxy == ${imageProxy.image != null}")
			Log.d("FaceAnalyzer - Analyze", "Image Proxy Format: ${imageProxy.format}, Width: ${imageProxy.width}, Height: ${imageProxy.height}")
			val mediaImage = imageProxy.image
			if (mediaImage != null) {
				val rotationDegrees = imageProxy.imageInfo.rotationDegrees
				val bitmap = toBitmap(mediaImage)
				val rotatedBitmap = rotateBitmap(bitmap, rotationDegrees.toFloat())

				val inputImage = InputImage.fromBitmap(rotatedBitmap, 0)
				detector.process(inputImage)
					.addOnSuccessListener(executor) { faces ->
						Log.d("FaceAnalyzer - Analyze", "Detector Process Image successful")
						CoroutineScope(Dispatchers.Default).launch {
							runModel(faces, rotatedBitmap)
						}
					}
					.addOnFailureListener(executor) { e ->
						isProcessing = false
						Log.e("FaceAnalyzer - Analyze", "Is processing: False from Error")
						e.printStackTrace()
					}
					.addOnCompleteListener {
						imageProxy.close()
					}
			}
		}
	}

	private suspend fun runModel(faces: List<Face>, cameraFrameBitmap: Bitmap) {
		if(pictures.value!!.isEmpty()){
			Log.d("FaceAnalyzer - Run Model", "No pictures, is processing is now false")
			isProcessing = false
			return
		}
		withContext(Dispatchers.Default) {
			val predictions = ArrayList<Prediction>()
			Log.d("FaceAnalyzer - Run Model", "Number of faces = " + faces.size)
			for (face in faces) {
				try {
					val croppedBitmap = cropRectFromBitmap(cameraFrameBitmap, face.boundingBox)
					subject = model.getFaceEmbedding(croppedBitmap)
					Log.d("FaceAnalyzer - Run Model", "pictures.size = ${pictures.value!!.size}")

					for (i in 0 until pictures.value!!.size) {
						val picture = pictures.value!![i]
						if(picture.faceData == null)
							continue // skip this picture
						if(picture.mlFace.isEmpty()) {
							pictures.value!![i].mlFace = model.getFaceEmbedding(picture.faceData!!)
						}
						if (nameScoreHashmap[picture] == null) {
							val p = ArrayList<Float>()
							if (metricToBeUsed == "cosine")
								p.add(cosineSimilarity(subject, picture.mlFace))
							else
								p.add(L2Norm(subject, picture.mlFace))
							nameScoreHashmap[picture] = p
						} else {
							if (metricToBeUsed == "cosine")
								nameScoreHashmap[picture]?.add(cosineSimilarity(subject, picture.mlFace))
							else
								nameScoreHashmap[picture]?.add(L2Norm(subject, picture.mlFace))
						}
					}

					val avgScores = nameScoreHashmap.values.map { scores -> scores.toFloatArray().average() }
					val names = nameScoreHashmap.keys.map { picture -> picture.name }

					// DEBUG: Log all names and their average scores
					for ((index, name) in names.withIndex()) {
						val score = avgScores[index]
						Log.d("FaceAnalyzer - Run Model", "Name: $name, Score: $score")
					}


					val bestScoreName: String = if (metricToBeUsed == "cosine") {
						val max = avgScores.maxOrNull()
						if (max != null && max > model.model.cosineThreshold) {
							names[avgScores.indexOf(max)] ?: "Unknown"
						} else {
							"Unknown"
						}
					} else {
						val min = avgScores.minOrNull()
						if (min != null && min < model.model.l2Threshold) {
							"Unknown"
						} else {
							names[avgScores.indexOf(min!!)] ?: "Unknown"
						}
					}

					Log.d("FaceAnalyzer - Run Model", "bestScoreName is \"$bestScoreName\"")
					predictions.add(Prediction(face.boundingBox, bestScoreName))
					nameScoreHashmap.clear()

				} catch (e: Exception) {
					Log.e("FaceAnalyzer - Run Model", "Error during model execution: ${e.message}")
					e.printStackTrace()
				}
			}
			withContext(Dispatchers.Main) {
				val firstPrediction = predictions.firstOrNull()
				if(firstPrediction != null) {
					onFacesDetected(faces, firstPrediction, cameraFrameBitmap)
				}
				Log.d("FaceAnalyzer - Run Model", "Is Processing: False from Clean Exit")
				isProcessing = false
			}
		}
	}

	private fun cropRectFromBitmap(source: Bitmap, rect: Rect ): Bitmap {
		var width = rect.width()
		var height = rect.height()
		if ( (rect.left + width) > source.width ){
			width = source.width - rect.left
		}
		if ( (rect.top + height ) > source.height ){
			height = source.height - rect.top
		}
		val croppedBitmap = Bitmap.createBitmap( source , rect.left , rect.top , width , height )
		return croppedBitmap
	}

	fun rotateBitmap( source: Bitmap , degrees : Float ): Bitmap {
		val matrix = Matrix()
		matrix.postRotate( degrees )
		return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix , false )
	}

	// Compute the L2 norm of ( x2 - x1 )
	private fun L2Norm( x1 : FloatArray, x2 : FloatArray ) : Float {
		return sqrt( x1.mapIndexed{ i , xi -> (xi - x2[ i ]).pow( 2 ) }.sum() )
	}


	// Compute the cosine of the angle between x1 and x2.
	private fun cosineSimilarity( x1 : FloatArray , x2 : FloatArray ) : Float {
		val mag1 = sqrt( x1.map { it * it }.sum() )
		val mag2 = sqrt( x2.map { it * it }.sum() )
		val dot = x1.mapIndexed{ i , xi -> xi * x2[ i ] }.sum()
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
		yuvImage.compressToJpeg(Rect(0, 0, image.width, image.height), 100, out)
		val jpegBytes = out.toByteArray()

		return BitmapFactory.decodeByteArray(jpegBytes, 0, jpegBytes.size)
	}

//	private fun downsampleMediaImage(mediaImage: Image?, sampleSize: Int): Bitmap? {
//		mediaImage?.let {
//			// Extract the image bytes from the ImageProxy
//			val plane = mediaImage.planes[0]
//			val buffer: ByteBuffer = plane.buffer
//			val byteArray = ByteArray(buffer.remaining())
//			buffer.get(byteArray)
//
//			// Use BitmapFactory to decode the byte array and downsample the image
//			val options = BitmapFactory.Options().apply {
//				inSampleSize = sampleSize  // Set sample size for downsampling
//			}
//
//			val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, options)
//
//			// Close the ImageProxy to avoid memory leaks
//			mediaImage.close()
//			return bitmap
//		}
//		return null  // Return null if the image is not available
//	}
}