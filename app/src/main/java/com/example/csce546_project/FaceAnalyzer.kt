package com.example.csce546_project

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Rect
import android.media.Image
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.csce546_project.model.FaceNetModel
import com.example.csce546_project.model.Prediction
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer
import java.util.concurrent.Executors
import androidx.core.graphics.createBitmap
import com.example.csce546_project.database.PictureModel
import kotlin.math.pow
import kotlin.math.sqrt

class FaceAnalyzer(context: Context,
				   private var model: FaceNetModel,
				   private var pictures: List<PictureModel>,
				   onFacesDetected: (List<Rect>, Int, Int) -> Unit) : ImageAnalysis.Analyzer {
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
			val cameraXImage = imageProxy.image!!
			var frameBitmap = createBitmap(cameraXImage.width, cameraXImage.height) // FIXED
			frameBitmap.copyPixelsFromBuffer( imageProxy.planes[0].buffer )
			frameBitmap = rotateBitmap( frameBitmap , imageProxy.imageInfo.rotationDegrees.toFloat() )

			val inputImage = InputImage.fromBitmap(frameBitmap, 0)
			detector.process(inputImage)
				.addOnSuccessListener(executor) { faces ->
					// TODO: RUN MODEL with FACES
					val faceRects = faces.map { it.boundingBox }
					val imageWidth = inputImage.width
					val imageHeight = inputImage.height
					onFacesDetected(faceRects, imageWidth, imageHeight)
				}
				.addOnFailureListener(executor) { e ->
					e.printStackTrace()
				}
				.addOnCompleteListener {
					imageProxy.close()
				}
		}
	}

	private suspend fun runModel(faces: List<Face>, cameraFrameBitmap: Bitmap) {
		withContext(Dispatchers.Default) {
			val predictions = ArrayList<Prediction>()
			for (face in faces) {
				try {
					// Crop the frame using face.boundingbox
					val croppedBitmap = cropRectFromBitmap(cameraFrameBitmap, face.boundingBox)

					// Some code examples have a detector for if the user is wearing a mask, we are ignoring this for now

					for (i in 0 until pictures.size) {
						if( nameScoreHashmap[pictures[i]] == null ) {
							val p = ArrayList<Float>()
							if(metricToBeUsed == "cosine")
								p.add(cosineSimilarity(subject, pictures[i].mlFace))
							else
								p.add(L2Norm(subject, pictures[i].mlFace))
							nameScoreHashmap[pictures[i]] = p
						}
						else {
							if ( metricToBeUsed == "cosine" ) {
								nameScoreHashmap[ pictures[i] ]?.add( cosineSimilarity( subject , pictures[ i ].mlFace ) )
							}
							else {
								nameScoreHashmap[ pictures[ i ] ]?.add( L2Norm( subject , pictures[i].mlFace ) )
							}
						}
						//TODO: Analize the best scores
					}
				} catch (e: Exception) {
					e.printStackTrace()
				}
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

	private fun downsampleMediaImage(mediaImage: Image?, sampleSize: Int): Bitmap? {
		mediaImage?.let {
			// Extract the image bytes from the ImageProxy
			val plane = mediaImage.planes[0]
			val buffer: ByteBuffer = plane.buffer
			val byteArray = ByteArray(buffer.remaining())
			buffer.get(byteArray)

			// Use BitmapFactory to decode the byte array and downsample the image
			val options = BitmapFactory.Options().apply {
				inSampleSize = sampleSize  // Set sample size for downsampling
			}

			val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, options)

			// Close the ImageProxy to avoid memory leaks
			mediaImage.close()
			return bitmap
		}
		return null  // Return null if the image is not available
	}
}