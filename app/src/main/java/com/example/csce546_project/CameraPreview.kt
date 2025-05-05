package com.example.csce546_project

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.csce546_project.model.FaceNetModel
import com.example.csce546_project.model.Prediction
import com.google.mlkit.vision.face.Face

private fun bindPreview(
	cameraProvider: ProcessCameraProvider,
	previewView: PreviewView,
	imageCapture: ImageCapture,
	lifecycleOwner: LifecycleOwner,
	imageAnalyzer: ImageAnalysis
) {
	val preview = Preview.Builder().build().also {
		it.setSurfaceProvider(previewView.surfaceProvider)
	}

	val cameraSelector = CameraSelector.Builder()
		.requireLensFacing(CameraSelector.LENS_FACING_FRONT)
		.build()

	cameraProvider.unbindAll()

	cameraProvider.bindToLifecycle(
		lifecycleOwner,
		cameraSelector,
		preview,
		imageCapture,
		imageAnalyzer
	)
}

@Composable
fun CameraPreview(
	previewView: PreviewView,
	imageCapture: ImageCapture?,
	lifecycleOwner: LifecycleOwner,
	model: FaceNetModel,
	viewModel: PictureViewModel,
	onFacesDetected: (List<Face>, Prediction) -> Unit
) {
	if(imageCapture == null)
		return
	val context = LocalContext.current
	val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

	val imageAnalyzer = remember {
		ImageAnalysis.Builder()
			.setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
			.build().also {
				it.setAnalyzer(
					ContextCompat.getMainExecutor(context),
					FaceAnalyzer(model, viewModel) { faces, prediction ->
						onFacesDetected(faces, prediction)
					}
				)
			}
	}

	AndroidView(
		factory = { previewView },
		modifier = Modifier.fillMaxSize(),
		update = { view ->
			cameraProviderFuture.addListener({
				val cameraProvider = cameraProviderFuture.get()
				bindPreview(
					cameraProvider = cameraProvider,
					previewView = view,
					imageCapture = imageCapture,
					lifecycleOwner = lifecycleOwner,
					imageAnalyzer = imageAnalyzer // pass built analyzer
				)
			}, ContextCompat.getMainExecutor(context))
		}
	)
}
