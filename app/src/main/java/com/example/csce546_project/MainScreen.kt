package com.example.csce546_project

import android.graphics.Rect
import androidx.camera.core.ImageCapture
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

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
	val currentPicture by viewModel.currentPicture.collectAsState()
	val showAdd by viewModel.showAddPopup.collectAsState()
	val showEdit by viewModel.showEditPopup.collectAsState()

	// Cam's AI Boxes
	var faces by remember { mutableStateOf(emptyList<Rect>()) }
	var imageWidth by remember { mutableStateOf(1) }
	var imageHeight by remember { mutableStateOf(1) }

	val TEST_PICTURE = PictureEntry(0, "Example", "exampleFilePath")  // TODO

	// START MAIN SCREEN
	Box (
		modifier = Modifier.fillMaxHeight()
	) {
		// Popups, shown conditionally
		if (showAdd) {
			Popup (
				onDismissRequest = { viewModel.closePopup() },
				alignment = Alignment.Center
			) {
				AddPopup(viewModel, { viewModel.closePopup() })
			}
		}
		else if (showEdit) {
			Popup (
				onDismissRequest = { viewModel.closePopup() },
				alignment = Alignment.Center
			) {
				EditPopup(viewModel, { viewModel.closePopup() })
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
							lifecycleOwner = lifecycleOwner,
							onFacesDetected = { detectedFaces, width, height ->
								faces = detectedFaces
								imageWidth = width
								imageHeight = height
							}
						)
					}

					FaceBoxOverlay(
						faces = faces,
						imageWidth = imageWidth,
						imageHeight = imageHeight,
						modifier = Modifier.fillMaxSize()
					)

					// Debug to show faces
					Text(
						text = "Number of faces: ${faces.size}",
						modifier = Modifier
							.align(Alignment.BottomCenter)
							.background(Color.Black.copy(alpha = 0.5f)),
						color = Color.White
					)

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
					onClick = { viewModel.openAddPopup() },
				) {
					Text("Test add popup")
				}

				Button(
					onClick = { viewModel.openEditPopup(TEST_PICTURE) },
				) {
					Text("Test view popup")
				}
			}
		}
	}
}