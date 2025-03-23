package com.example.csce546_project

import androidx.camera.core.ImageCapture
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
@Preview
fun ScreenView() {
	val defaultName = "NAME UNKNOWN"
	val context = LocalContext.current
	val lifecycleOwner = LocalLifecycleOwner.current
	val previewView = remember { PreviewView(context) }
	val imageCapture = remember { ImageCapture.Builder().build() }
//	val recognizer // TODO: make this recognize facts

	// Check Camera Permissions
	val cameraPermissionState = rememberPermissionState(
		android.Manifest.permission.CAMERA
	)

	if (cameraPermissionState.status.isGranted) {
		// App Column, only one wide
		Column(modifier = Modifier.fillMaxSize()) {
			// Box Representing Image Preview
			Box(modifier = Modifier.weight(0.9f)) {
				CameraPreview(
					previewView = previewView,
					imageCapture = imageCapture,
					lifecycleOwner = lifecycleOwner
				)
			}
			Box(modifier = Modifier.weight(0.1f)) {
				Text(
					text = defaultName,
					fontSize = 20.sp,
					textAlign = TextAlign.Center
				)
			}


		}



	} else { //TODO: Needs testing
		LaunchedEffect(Unit) {
			cameraPermissionState.launchPermissionRequest()
		}
	}
}