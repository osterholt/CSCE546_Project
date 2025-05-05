package com.example.csce546_project

import android.Manifest
import android.graphics.Bitmap
import android.graphics.Matrix
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.csce546_project.database.PictureEntry
import com.example.csce546_project.model.FaceNetModel
import com.example.csce546_project.model.Models
import com.example.csce546_project.model.Prediction
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.face.Face

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
		Manifest.permission.CAMERA
	)

	// PictureViewModel info -- keeps track of pictures and popup state
	val viewModel: PictureViewModel = viewModel() // View model is singleton
	val pictures by viewModel.pictures.observeAsState(emptyList())
	val currentPicture by viewModel.currentPicture.collectAsState()  // TODO re-implement
	val showAdd by viewModel.showAddPopup.collectAsState()
	val showEdit by viewModel.showEditPopup.collectAsState()

	// Cam's AI Boxes
	var faces by remember { mutableStateOf(emptyList<Face>()) }
	var imageCaptured by remember { mutableStateOf(false) }
	var detectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
	var namePrediction by remember { mutableStateOf<Prediction?>(null) }
	val modelInfo = Models.FACENET
	val useGpu = true
	val faceNetModel = FaceNetModel(context, modelInfo , useGpu )


	val TEST_PICTURE = PictureEntry(0, "Example", "exampleFilePath")  // TODO

	// START MAIN SCREEN
	Box (
		modifier = Modifier.fillMaxHeight()
	) {
		// Popups, shown conditionally
		if (showAdd) {
			Popup (
				onDismissRequest = { viewModel.closePopup() },
				properties = PopupProperties(
					focusable = true,
					dismissOnClickOutside = true
				),
				alignment = Alignment.Center
			) {
				AddPopup(viewModel, { viewModel.closePopup() })
			}
		}
		else if (showEdit) {
			Popup (
				onDismissRequest = { viewModel.closePopup() },
				properties = PopupProperties(
					focusable = true,
					dismissOnClickOutside = true
				),
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
						// TODO: Switch to selfie camera or have toggle.
						CameraPreview(
							previewView = previewView,
							imageCapture = imageCapture,
							lifecycleOwner = lifecycleOwner,
							model = faceNetModel,
							viewModel = viewModel,
							onFacesDetected = { detectedFaces, prediction ->
								if (detectedFaces.isNotEmpty() && !imageCaptured) {
									faces = detectedFaces
									namePrediction = prediction
								}
							}
						)
					}

					// Display face details and such
					if(!faces.isEmpty()) {
						FaceBoxOverlay(
							faces = faces.map { face -> face.boundingBox },
							imageWidth = detectedBitmap?.width ?: 0,
							imageHeight = detectedBitmap?.height ?: 0,
							modifier = Modifier.fillMaxSize()
						)
					}

					// Debug to show faces
					Text(
						text = "Number of faces: ${faces.size}",
						modifier = Modifier
							.align(Alignment.BottomCenter)
							.background(Color.Black.copy(alpha = 0.5f)),
						color = Color.White
					)

					// This is the name of the face detected
					Box(modifier = Modifier.align(Alignment.TopCenter)) {
						Text(
							text = namePrediction?.label ?: defaultName,
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
				Button(
					onClick = { viewModel.openAddPopup() },
				) {
					Text("Test add popup")
				}

				pictures.forEach { picture ->
					Button(
						onClick = { viewModel.openEditPopup(picture) }
					) {
						Text(
							text = ("ID=" + picture.id + " | NAME=" + (picture.name ?: "NULL"))
						)
					}
				}
			}
		}
	}
}

fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
	val matrix = Matrix()
	matrix.postRotate(angle)
	return Bitmap.createBitmap(
		source, 0, 0, source.width, source.height, matrix, true
	)
}
