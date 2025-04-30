package com.example.csce546_project

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import coil.compose.AsyncImage

@Composable
fun AddPopup(viewModel: PopupViewModel, onClose: () -> Unit) {
    val currentPicture = viewModel.currentPicture.collectAsState()
    val currentImageURI = viewModel.currentImageURI.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .fillMaxHeight(0.7f)
            .background(color = Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (currentImageURI.value == null) {
            Button(
                onClick = { /* TODO */ }
            ) {
                Text(text = "Take a Picture")
            }

            Text(text = "or")

            PhotoPicker(viewModel)
        } else {
            OutlinedTextField(
                value = "",
                onValueChange = {/* TODO */ },
                label = { Text(text = "Who is this?") },
                modifier = Modifier
            )

            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.4f),
                model = viewModel.currentImageURI,
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
                        viewModel.saveCurrentPicture()
                        onClose()
                    }
                ) {
                    Text(text = "Save")
                }
            }
        }
    }
}

@Composable
fun EditPopup(viewModel: PopupViewModel, onClose: () -> Unit) {
    val currentPicture = viewModel.currentPicture.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .fillMaxHeight(0.7f)
            .background(color = Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField (
            value = currentPicture.value?.name ?: "NULL",
            onValueChange = {/* TODO */},
            label = { Text(text = "Name") },
            modifier = Modifier
        )

        // TODO show picture here
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f)
                .background(color = Color.Blue)
        ) {

        }

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    viewModel.deleteCurrentPicture()
                    onClose()
                }
            ) {
                Text(text = "Delete Picture")
            }
            Button(
                onClick = {
                    viewModel.saveCurrentPicture()
                    onClose()
                }
            ) {
                Text(text = "Save and Close")
            }
        }
    }
}

@Composable
fun CameraSelect(viewModel: PopupViewModel) {
    // TODO same structure as below but with detached camera
}

@Composable
fun PhotoPicker(viewModel: PopupViewModel) {

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            viewModel.setCurrentImageURI(it)
        }
    )

    Button(
        onClick = { photoPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        ) }
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