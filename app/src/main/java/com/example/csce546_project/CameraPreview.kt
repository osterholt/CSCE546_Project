package com.example.csce546_project

import androidx.camera.core.CameraSelector
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

private fun bindPreview(
	cameraProvider: ProcessCameraProvider,
	previewView: PreviewView,
	imageCapture: ImageCapture,
	lifecycleOwner: LifecycleOwner
) {
	val preview = Preview.Builder().build().also {
		it.surfaceProvider = previewView.surfaceProvider
	}
	val cameraSelector = CameraSelector.Builder()
		.requireLensFacing(CameraSelector.LENS_FACING_BACK)
		.build()
	cameraProvider.unbindAll()
	cameraProvider.bindToLifecycle(
		lifecycleOwner,
		cameraSelector,
		preview,
		imageCapture
	)
}

@Composable
fun CameraPreview(
	previewView: PreviewView,
	imageCapture: ImageCapture,
	lifecycleOwner: LifecycleOwner
) {
	val context = LocalContext.current
	val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

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
					lifecycleOwner = lifecycleOwner
				)
			}, ContextCompat.getMainExecutor(context) )
		}
	)
}
