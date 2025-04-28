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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
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

	// Check Camera Permissions
	val cameraPermissionState = rememberPermissionState(
		android.Manifest.permission.CAMERA
	)

	// Information used for popups and adding/editing pictures
	val viewModel: PopupViewModel = viewModel()
	var showAddPopup by remember { mutableStateOf(false) }
	var showEditPopup by remember { mutableStateOf(false) }

	// START MAIN SCREEN
	Box (
		modifier = Modifier.fillMaxHeight()
	) {
		// Popups, shown conditionally
		if (showAddPopup) {
			Popup (
				onDismissRequest = { showAddPopup = false },
				alignment = Alignment.Center
			) {
				AddPopup(viewModel, { showAddPopup = false })
			}
		}
		else if (showEditPopup) {
			Popup (
				onDismissRequest = { showEditPopup = false },
				alignment = Alignment.Center
			) {
				EditPopup(viewModel, { showEditPopup = false })
			}
		}

		// The rest of the main screen
		Column(
			modifier = Modifier.fillMaxHeight()
		) {
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
			// TODO should not be a column
			Column(
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
				) {
					Text("Test add popup")
				}

				Button(
					onClick = {
						viewModel.currentPicture = PictureEntry(0, "Example", "examplePicture")  // TODO pass in clicked picture
						showEditPopup = true
					},
				) {
					Text("Test view popup")
				}
			}
		}
	}
}