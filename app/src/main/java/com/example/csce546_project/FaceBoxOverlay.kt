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
fun FaceBoxOverlay(faces: List<Rect>, modifier: Modifier = Modifier) {
	Canvas(modifier = modifier) {
		for (box in faces) {
			drawRect(
				color = Color.Green,
				topLeft = Offset(box.left.toFloat(), box.top.toFloat()),
				size = Size(
					width = (box.width()).toFloat(),
					height = (box.height()).toFloat()
				),
				style = Stroke(width = 4f)
			)
		}
	}
}