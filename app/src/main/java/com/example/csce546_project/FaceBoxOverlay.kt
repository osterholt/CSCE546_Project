package com.example.csce546_project

import android.graphics.Rect
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun FaceBoxOverlay(
	faces: List<Rect>,
	imageWidth: Int,
	imageHeight: Int,
	modifier: Modifier = Modifier
) {
	val density = LocalDensity.current
	val strokeWidth = with(density) { 2.dp.toPx() }

	Canvas(modifier = modifier) {
		val canvasWidth = size.width
		val canvasHeight = size.height

		// Camera images usually come in landscape orientation (width > height)
		// But the preview display is typically in portrait mode on a phone
		// So we need to swap width and height and handle rotation

		// Calculate scale factors accounting for preview rotation
		val scaleX = canvasWidth / imageHeight.toFloat()  // Swap width/height due to rotation
		val scaleY = canvasHeight / imageWidth.toFloat()  // Swap width/height due to rotation

		// Use the same scale for both dimensions to maintain aspect ratio
		val scale = minOf(scaleX, scaleY)

		// Calculate centering offsets if needed
		val offsetX = (canvasWidth - (imageHeight * scale)) / 2f
		val offsetY = (canvasHeight - (imageWidth * scale)) / 2f

		for (face in faces) {
			// Account for 90-degree rotation from camera to display
			// In a 90-degree rotation:
			// newX = oldY
			// newY = imageWidth - oldX - width

			val rotatedLeft = face.top * scale + offsetX
			val rotatedTop = (imageWidth - face.right) * scale + offsetY
			val rotatedWidth = face.height() * scale
			val rotatedHeight = face.width() * scale

			// Draw the face rectangle
			drawRect(
				color = Color.Green,
				topLeft = Offset(rotatedLeft, rotatedTop),
				size = Size(rotatedWidth, rotatedHeight),
				style = Stroke(width = strokeWidth)
			)
		}
	}
}