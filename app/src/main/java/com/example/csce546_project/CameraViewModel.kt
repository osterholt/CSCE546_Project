package com.example.csce546_project

import android.graphics.Rect
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class CameraViewModel : ViewModel() {
	private val _faces = MutableStateFlow<List<Rect>>(emptyList())
	val faces: StateFlow<List<Rect>> = _faces

	fun updateFaces(newFaces: List<Rect>) {
		_faces.value = newFaces
	}
}