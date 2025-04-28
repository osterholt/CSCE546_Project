package com.example.csce546_project

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun AddPopup(viewModel: PopupViewModel, onClose: () -> Unit) {
    Column(

    ) {
        if (viewModel.currentPicture == null) {
            Button(
                onClick = {/* TODO implement camera */}
            ) {
                Text(text = "Take a Picture")
            }

            Text(text = "or")

            Button(
                onClick = {/* TODO implement add image from gallery */}
            ) {
                Text(text = "Add a Picture from Gallery")
            }
        }
        else {
            Text(text = "Who is this?")
            OutlinedTextField(
                value = "",
                onValueChange = {/* TODO */},
                label = { Text(text = "Who is this?") },
                modifier = Modifier
            )

            // TODO show picture here
            Box(
                modifier = Modifier.fillMaxWidth().background(color = Color.Red)
            ) {

            }

            Row {
                Button(
                    onClick = { viewModel.clearPicture() }
                ) {
                    Text(text = "Retake")
                }

                Button(
                    onClick = { viewModel.savePicture("") }
                ) {
                    Text(text = "Save")
                }
            }
        }

        // TODO make it an x instead
        Button(onClick = { onClose() }) {
            Text(text = "Close")
        }
    }
}

@Composable
fun EditPopup(viewModel: PopupViewModel, onClose: () -> Unit) {

}