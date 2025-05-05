package com.example.csce546_project

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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

    Column(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .fillMaxHeight(0.7f)
            .background(color = Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (currentPicture.value?.uri == null) {
            TakePhotoButton(viewModel, cameraController, lifecycleOwner)

            Text(text = "or")

            PickPhotoButton(viewModel)
        }
        else {
            var nameInput by remember { mutableStateOf("") }
            OutlinedTextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                label = { Text(text = "Who is this?") },
                modifier = Modifier
            )

            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.4f),
                model = currentPicture.value!!.uri,
                contentDescription = ""
            )

            Row {
                Button(
                    onClick = {
                        viewModel.clearPicture()
                    }
                ) {
                    Text(text = "Retake")
                }

                Button(
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

@Composable
fun EditPopup(viewModel: PictureViewModel, onClose: () -> Unit) {
    val context = LocalContext.current
    val currentPicture = viewModel.currentPicture.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .fillMaxHeight(0.7f)
            .background(color = Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        var nameInput by remember { mutableStateOf(currentPicture.value?.name ?: "") }
        OutlinedTextField(
            value = nameInput,
            onValueChange = { nameInput = it },
            label = { Text(text = "Edit Name") },
            modifier = Modifier
        )

        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f),
            model = currentPicture.value!!.uri,
            contentDescription = currentPicture.value!!.name
        )

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    viewModel.deleteCurrentPicture(context)
                    onClose()
                }
            ) {
                Text(text = "Delete Picture")
            }
            Button(
                onClick = {
                    if (nameInput.isNotEmpty()) {
                        viewModel.setPictureName(nameInput)
                        viewModel.updateCurrentPicture()
                        onClose()
                    }
                }
            ) {
                Text(text = "Save and Close")
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

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = {
            viewModel.setPictureFilePath(uri)
            // viewModel.enableBackgroundCamera(cameraController, lifecycleOwner)
        }
    )

    Button(
        onClick = {
            viewModel.disableBackgroundCamera(cameraController)
            cameraLauncher.launch(uri)
        }
    ) {
        Row{
            Icon(
                painter = painterResource(id = android.R.drawable.ic_input_add),
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

    Button(
        onClick = { photoPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    ) {
        Row{
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