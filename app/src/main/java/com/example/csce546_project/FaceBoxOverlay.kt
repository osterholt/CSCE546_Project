package com.example.csce546_project

import android.graphics.Bitmap
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
				   rotatedBitmap: Bitmap?,
				   modifier: Modifier = Modifier) {
	Canvas(modifier = modifier) {
		if (rotatedBitmap == null)
			return@Canvas
		val scaleX = size.width / rotatedBitmap.width.toFloat()
		val scaleY = size.height / rotatedBitmap.height.toFloat()

		for (faceRect in faces) {
			val left = faceRect.left * scaleX
			val top = faceRect.top * scaleY
			val width = faceRect.width() * scaleX
			val height = faceRect.height() * scaleY

			drawRect(
				color = Color.Green,
				topLeft = Offset(left, top),
				size = Size(width, height),
				style = Stroke(width = 4f)
			)
		}
	}
}