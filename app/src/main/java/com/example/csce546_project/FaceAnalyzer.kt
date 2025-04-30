package com.example.csce546_project

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.media.Image
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.nio.ByteBuffer
import java.util.concurrent.Executors

class FaceAnalyzer(private val onFacesDetected: (List<Rect>, Int, Int) -> Unit) : ImageAnalysis.Analyzer {
	private val executor = Executors.newCachedThreadPool()
	private var frameCounter = 0

	private val detector = FaceDetection.getClient(
		FaceDetectorOptions.Builder()
			.setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
			.setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
			.build()
	)

	@OptIn(ExperimentalGetImage::class)
	override fun analyze(imageProxy: ImageProxy) {
		if (frameCounter % 10 == 0) {  // Skip frames to reduce processing load
			val mediaImage = imageProxy.image
			if (mediaImage != null) {
				val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

				// Process faces using the detector
				detector.process(image)
					.addOnSuccessListener(executor) { faces ->
						val faceRects = faces.map { it.boundingBox }
						val imageWidth = image.width
						val imageHeight = image.height
						onFacesDetected(faceRects, imageWidth, imageHeight)
					}
					.addOnFailureListener(executor) { e ->
						e.printStackTrace()
					}
					.addOnCompleteListener {
						imageProxy.close()
					}
			} else {
				imageProxy.close()
			}
		} else {
			imageProxy.close()  // Skip this frame
		}

		frameCounter++
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