package com.example.csce546_project

import android.graphics.Picture
import androidx.camera.core.ImageCapture
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
@Preview
fun MainScreen() {
	val defaultName = "NAME UNKNOWN"
	val context = LocalContext.current
	val lifecycleOwner = LocalLifecycleOwner.current
	val previewView = remember { PreviewView(context) }
	val imageCapture = remember { ImageCapture.Builder().build() }
	// val recognizer // TODO: make this recognize facts

	val viewModel: PopupViewModel = viewModel()  // View model used for screen popups
	var showAddPopup: Boolean = false
	var showEditPopup: Boolean = false

	// Check Camera Permissions
	val cameraPermissionState = rememberPermissionState(
		android.Manifest.permission.CAMERA
	)

	// START MAIN SCREEN
	Column(
		modifier = Modifier.fillMaxHeight()
	) {
		if (showAddPopup)
			AddPopup(viewModel, { showAddPopup = false })
		else if (showEditPopup)
			EditPopup(viewModel, { showEditPopup = false })

		// Camera preview
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.fillMaxHeight(0.5f)  // TODO figure out how to clip camera preview
		) {
			if (cameraPermissionState.status.isGranted) {
				// Box Representing Image Preview
				Box(modifier = Modifier) {
					CameraPreview(
						previewView = previewView,
						imageCapture = imageCapture,
						lifecycleOwner = lifecycleOwner
					)
				}
				Box(modifier = Modifier) {
					Text(
						text = defaultName,
						fontSize = 20.sp,
						textAlign = TextAlign.Center
					)
				}
			} else { //TODO: Needs testing
				LaunchedEffect(Unit) {
					cameraPermissionState.launchPermissionRequest()
				}
			}
		}

		// Image list
		Box(
			modifier = Modifier.fillMaxWidth().fillMaxHeight().background(color = Color.Red)
		) {
			Text(
				text = "TODO picture list",
				fontWeight = FontWeight.Bold,
				fontSize = 36.sp
			)

			Button(
				onClick = {
					showAddPopup = true
				},
				modifier = Modifier.padding(top = 40.dp)
			) {
				Text("Test add popup")
			}
			Button(
				onClick = {
					viewModel.currentPicture = PictureEntry(0, "Example", "examplePicture")
					showEditPopup = true
  				},
				modifier = Modifier.padding(top = 100.dp)
			) {
				Text("Test view popup")
			}
		}
	}
}