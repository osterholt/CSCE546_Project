package com.example.csce546_project

import android.graphics.Rect
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class FaceAnalyzer (private val onFacesDetected: (List<Rect>) -> Unit) : ImageAnalysis.Analyzer {
	// Detailed detector
//	private val detector: FaceDetector by lazy {
//		val options = FaceDetectorOptions.Builder()
//			.setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
//			.setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
//			.setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
//			.setMinFaceSize(0.15f) // TODO: change based off need
//			.enableTracking()
//			.build()
//		FaceDetection.getClient(options)
//	}

	// Simple detector
	private val detector = FaceDetection.getClient(
		FaceDetectorOptions.Builder()
			.setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
			.build()
	)

	@OptIn(ExperimentalGetImage::class)
	override fun analyze(imageProxy: ImageProxy) {
		val mediaImage = imageProxy.image
		if(mediaImage != null) {
			val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
			detector.process(image)
				.addOnSuccessListener { faces ->
					val faceRects = faces.map { it.boundingBox }
					onFacesDetected(faceRects)
				}
				.addOnFailureListener { e ->
					e.printStackTrace()
				}
				.addOnCompleteListener {
					imageProxy.close() // Apparently important, else it can crash
				}
		} else {
			imageProxy.close()
		}
	}




}