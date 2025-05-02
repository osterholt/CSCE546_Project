package com.example.csce546_project

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.csce546_project.database.PictureDatabase
import com.example.csce546_project.database.PictureModel
import com.example.csce546_project.database.PictureRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PictureViewModel(application: Application) : AndroidViewModel(application) {
    private val pictureRepository: PictureRepository
    val pictures: LiveData<List<PictureModel>>

    private val _currentPicture = MutableStateFlow<PictureModel?>(null)
    val currentPicture: StateFlow<PictureModel?> = _currentPicture

    private val _showAddPopup = MutableStateFlow(false)
    val showAddPopup: StateFlow<Boolean> = _showAddPopup

    private val _showEditPopup = MutableStateFlow(false)
    val showEditPopup: StateFlow<Boolean> = _showEditPopup


    init {
        val pictureDAO = PictureDatabase.getDatabase(application).pictureDAO()
        this.pictureRepository = PictureRepository(pictureDAO)
        this.pictures = pictureRepository.pictures.asLiveData()
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
        val timestamp = SimpleDateFormat("yyyy_MM_dd_HH:mm:ss", Locale.US).format(Date())
        val imageFileName = "temp_${timestamp}"
        val image = File.createTempFile(
            imageFileName,
            ".jpg",
            context.externalCacheDir
        )

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
                pictureRepository.deletePicture(it, context)
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
}