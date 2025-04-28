package com.example.csce546_project

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Popup

@Composable
fun AddPopup(viewModel: PopupViewModel, onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .fillMaxHeight(0.7f)
            .background(color = Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (viewModel.currentPicture == null) {
            Button(
                onClick = { viewModel.takePicture() }
            ) {
                Text(text = "Take a Picture")
            }

            Text(text = "or")

            Button(
                onClick = { viewModel.openPicture() }
            ) {
                Text(text = "Add a Picture from Gallery")
            }
        } else {
            OutlinedTextField(
                value = "",
                onValueChange = {/* TODO */ },
                label = { Text(text = "Who is this?") },
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
    val currentPicture = viewModel.currentPicture

    Column(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .fillMaxHeight(0.7f)
            .background(color = Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField (
            value = currentPicture?.name ?: "NULL",
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