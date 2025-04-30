package com.example.csce546_project

import android.graphics.Rect
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun FaceBoxOverlay(faces: List<Rect>,
				   imageWidth: Int,
				   imageHeight: Int,
				   modifier: Modifier = Modifier) {
	Canvas(modifier = modifier) {
		val scaleX = size.width / imageWidth
		val scaleY = size.height / imageHeight

		for (box in faces) {
			drawRect(
				color = Color.Green,
				topLeft = Offset(box.left * scaleX, box.top * scaleY),
				size = Size(box.width() * scaleX, box.height() * scaleY),
				style = Stroke(width = 4f)
			)
		}
	}
}