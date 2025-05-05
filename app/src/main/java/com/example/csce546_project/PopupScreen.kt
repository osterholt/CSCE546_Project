package com.example.csce546_project

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.LifecycleOwner
import coil.compose.AsyncImage
import java.util.Objects

@Composable
fun AddPopup(
    viewModel: PictureViewModel,
    cameraController: LifecycleCameraController,
    lifecycleOwner: LifecycleOwner,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val currentPicture = viewModel.currentPicture.collectAsState()

    val appBlue = Color.hsl(219f, 0.65f, 0.36f)

    Box(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .fillMaxHeight(0.7f)
            .background(color = Color.White, shape = RoundedCornerShape(32.dp))
            .border(width = 3.dp, shape = RoundedCornerShape(32.dp),
                color = appBlue)
    ) {
        // X icon
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Add Image",
                modifier = Modifier
                    .padding(24.dp, 24.dp)
                    .size(24.dp)
                    .clickable {
                        onClose()
                    }
            )
        }

        // Popup content
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (currentPicture.value?.uri == null) {
                TakePhotoButton(viewModel, cameraController, lifecycleOwner)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "or")
                Spacer(modifier = Modifier.height(8.dp))
                PickPhotoButton(viewModel)
            }
            else {
                var nameInput by remember { mutableStateOf("") }
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text(text = "Who is this?") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = appBlue,
                        focusedLabelColor = appBlue,
                        cursorColor = appBlue
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.4f),
                    model = currentPicture.value!!.uri,
                    contentDescription = ""
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row (
                    horizontalArrangement = Arrangement.Center
                ){
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = appBlue,
                        ),
                        border = BorderStroke(2.dp, appBlue),
                        onClick = {
                            viewModel.clearPicture()
                        }
                    ) {
                        Text(text = "Retake")
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = appBlue,
                            contentColor = Color.White
                        ),
                        border = BorderStroke(2.dp, appBlue),
                        onClick = {
                            if (nameInput.isNotEmpty()) {
                                viewModel.setPictureName(nameInput)
                                viewModel.saveCurrentPicture(context)
                                onClose()
                            }
                        }
                    ) {
                        Text(text = "Save")
                    }
                }
            }
        }
    }
}

@Composable
fun EditPopup(viewModel: PictureViewModel, onClose: () -> Unit) {
    val context = LocalContext.current
    val currentPicture = viewModel.currentPicture.collectAsState()

    val appBlue = Color.hsl(219f, 0.65f, 0.36f)
    val deleteRed = Color.hsl(5f, 1f, 0.26f)

    Box(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .fillMaxHeight(0.7f)
            .background(color = Color.White, shape = RoundedCornerShape(32.dp))
            .border(width = 3.dp, shape = RoundedCornerShape(32.dp),
                color = appBlue)
    ) {
        // X icon
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Add Image",
                modifier = Modifier
                    .padding(24.dp, 24.dp)
                    .size(24.dp)
                    .clickable {
                        onClose()
                    }
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            var nameInput by remember { mutableStateOf(currentPicture.value?.name ?: "") }
            OutlinedTextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                label = { Text(text = "Edit Name") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = appBlue,
                    focusedLabelColor = appBlue,
                    cursorColor = appBlue
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.4f),
                model = currentPicture.value!!.uri,
                contentDescription = currentPicture.value!!.name
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = deleteRed
                    ),
                    border = BorderStroke(2.dp, deleteRed),
                    onClick = {
                        viewModel.deleteCurrentPicture(context)
                        onClose()
                    }
                ) {
                    Text(text = "Delete")
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = appBlue,
                        contentColor = Color.White
                    ),
                    border = BorderStroke(2.dp, appBlue),
                    onClick = {
                        if (nameInput.isNotEmpty()) {
                            viewModel.setPictureName(nameInput)
                            viewModel.updateCurrentPicture()
                            onClose()
                        }
                    }
                ) {
                    Text(text = "Save")
                }
            }
        }
    }
}

@Composable
fun TakePhotoButton(
    viewModel: PictureViewModel,
    cameraController: LifecycleCameraController,
    lifecycleOwner: LifecycleOwner
) {
    val context = LocalContext.current
    val file = viewModel.createImageFileInCache(context)
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        context.packageName + ".provider",
        file
    )

    val appBlue = Color.hsl(219f, 0.65f, 0.36f)

    Log.d("TakePhoto", "File exists before launch: ${file.exists()}, path: ${file.path}")

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = {
            viewModel.setPictureFilePath(uri)
            Log.w("CAMERA", "PICTURE SUCCESSFULLY SAVED")
            viewModel.enableBackgroundCamera(cameraController, lifecycleOwner)
            Log.d("TakePhoto", "File exists: ${file.absolutePath}, size: ${file.length()} bytes")
        }
    )

    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = appBlue,
            contentColor = Color.White
        ),
        border = BorderStroke(2.dp, appBlue),
        onClick = {
            viewModel.disableBackgroundCamera(cameraController)
            cameraLauncher.launch(uri)
        }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Call,
                contentDescription = "Add Image"
            )
            Text(
                text = "Take Photo"
            )
        }
    }
}

@Composable
fun PickPhotoButton(viewModel: PictureViewModel) {

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            viewModel.setPictureFilePath(it)
        }
    )

    val appBlue = Color.hsl(219f, 0.65f, 0.36f)

    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = appBlue,
        ),
        border = BorderStroke(2.dp, Color.hsl(219f, 0.65f, 0.36f)),
        onClick = { photoPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_input_add),
                contentDescription = "Add Image"
            )
            Text(
                text = "Pick a Photo"
            )
        }
    }
}