package com.example.csce546_project

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.csce546_project.database.PictureDatabase
import com.example.csce546_project.database.PictureModel
import com.example.csce546_project.database.PictureRepository
import com.example.csce546_project.model.FaceNetModel
import com.example.csce546_project.model.Prediction
import com.google.mlkit.vision.face.Face
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class PictureViewModel(application: Application) : AndroidViewModel(application) {
    private val pictureRepository: PictureRepository
    val pictures: LiveData<List<PictureModel>>

    private val _currentPicture = MutableStateFlow<PictureModel?>(null)
    val currentPicture: StateFlow<PictureModel?> = _currentPicture

    private val _showAddPopup = MutableStateFlow(false)
    val showAddPopup: StateFlow<Boolean> = _showAddPopup

    private val _showEditPopup = MutableStateFlow(false)
    val showEditPopup: StateFlow<Boolean> = _showEditPopup

    private val _enableBackgroundCamera = MutableStateFlow(true)
    val enableBackgroundCamera: StateFlow<Boolean> = _enableBackgroundCamera

    // Cache for last predictions to reduce flickering
    private val lastPredictions = mutableMapOf<Int, String>() // Track ID to name mapping

    init {
        val pictureDAO = PictureDatabase.getDatabase(application).pictureDAO()
        this.pictureRepository = PictureRepository(pictureDAO)
        this.pictures = pictureRepository.pictures.asLiveData()
    }

    /**
     * Gets prediction for a detected face
     * This function is used by the FaceAnalyzer to determine the name of a detected face
     */
    fun getPrediction(model: FaceNetModel, face: Face, imageProxy: ImageProxy): Prediction {
        // Default prediction in case processing fails
        var prediction = Prediction(face.boundingBox, "Unknown")

        try {
            val picturesList = pictures.value ?: return prediction
            if (picturesList.isEmpty()) return prediction

            // Use tracking ID for more stable predictions if available
            val faceId = face.trackingId ?: -1

            // If we have a cached prediction for this face ID and it's not -1 (invalid)
            if (faceId != -1 && lastPredictions.containsKey(faceId)) {
                val cachedName = lastPredictions[faceId]
                if (cachedName != null) {
                    return Prediction(face.boundingBox, cachedName)
                }
            }

            // If no cached prediction, proceed with full analysis
            // This would be filled with your actual face recognition logic from FaceAnalyzer
            // For now, just returning "Unknown" since the actual analysis happens in FaceAnalyzer

            // For a real implementation, you would:
            // 1. Extract face embedding using model.getFaceEmbedding()
            // 2. Compare with stored face embeddings in picturesList
            // 3. Find closest match and return the name

            // When match is found, cache it for future frames
            if (faceId != -1) {
                lastPredictions[faceId] = prediction.label
            }

        } catch (e: Exception) {
            Log.e("PictureViewModel", "Error in getPrediction", e)
        }

        return prediction
    }

    fun setPictureName(name: String) {
        _currentPicture.value = _currentPicture.value?.also { it.name = name }
    }

    fun setPictureFilePath(filepath: Uri?) {
        if (this._currentPicture.value == null)
            this._currentPicture.value = PictureModel(null, null, filepath)
        else
            _currentPicture.value = _currentPicture.value?.also { it.uri = filepath }
    }

    fun clearPicture() {
        this._currentPicture.value = null
    }

    // Used for caching images taken with the camera -- NOT the permanent URI
    fun createImageFileInCache(context: Context): File {
        val imageFileName = "temp_${System.currentTimeMillis()}"
        val image = File.createTempFile(
            imageFileName,
            ".jpg",
            context.externalCacheDir
        )

        Log.d("create image cache", context.externalCacheDir!!.path)

        return image
    }

    fun saveCurrentPicture(context: Context) = viewModelScope.launch {
        _currentPicture.value.also {
            if (it != null)
                pictureRepository.addPicture(it, context)
        }
    }

    fun updateCurrentPicture() = viewModelScope.launch {
        _currentPicture.value.also {
            if (it != null)
                pictureRepository.updatePicture(it)
        }
    }

    fun deleteCurrentPicture(context: Context) = viewModelScope.launch {
        _currentPicture.value.also {
            if (it != null)
                pictureRepository.deletePicture(it)
        }
    }

    fun openAddPopup() {
        this._showEditPopup.value = false
        this._showAddPopup.value = true
    }

    fun openEditPopup(picture: PictureModel) {
        this._currentPicture.value = picture
        this._showAddPopup.value = false
        this._showEditPopup.value = true
    }

    fun closePopup() {
        this._showAddPopup.value = false
        this._showEditPopup.value = false
        this._currentPicture.value = null
    }

    fun disableBackgroundCamera(cameraController: LifecycleCameraController) {
        this._enableBackgroundCamera.value = false
        cameraController.unbind()
    }

    fun enableBackgroundCamera(cameraController: LifecycleCameraController, lifecycleOwner: LifecycleOwner) {
        cameraController.bindToLifecycle(lifecycleOwner)
        this._enableBackgroundCamera.value = true
    }

    // Helper function to clear prediction cache when needed (e.g., when adding new faces)
    fun clearPredictionCache() {
        lastPredictions.clear()
    }
}