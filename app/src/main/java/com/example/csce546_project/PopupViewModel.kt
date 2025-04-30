package com.example.csce546_project

import android.app.Application
import android.content.Context
import android.graphics.Picture
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

class PopupViewModel(application: Application) : AndroidViewModel(application) {
    private val pictureRepository: String = ""  // TODO create repo

    val pictures: LiveData<List<PictureModel>>?

    private val _currentPicture = MutableStateFlow<PictureModel?>(null)
    val currentPicture: StateFlow<PictureModel?> = _currentPicture

    // TODO clean this up and make it use the PictureModel instead
    private val _currentImageURI = MutableStateFlow<Uri?>(null)
    val currentImageURI: StateFlow<Uri?> = _currentImageURI
    fun setCurrentImageURI(uri: Uri?) {
        this._currentImageURI.value = uri
        Log.d("TAG","CURRENT IMAGE URI = " + this.currentImageURI.value)
    }

    private val _showAddPopup = MutableStateFlow(false)
    val showAddPopup: StateFlow<Boolean> = _showAddPopup

    private val _showEditPopup = MutableStateFlow(false)
    val showEditPopup: StateFlow<Boolean> = _showEditPopup

    init {
        this.pictures = null  // TODO init properly
        this._currentPicture.value = null
    }

    fun setPicture(picture: PictureModel) {
        this._currentPicture.value = picture
    }

    fun setPictureFilePath(filepath: Uri) {

    }

    fun setPictureName(name: String) {

    }

    fun clearPicture() {
        this._currentPicture.value = null
        this._currentImageURI.value = null  // TODO remove later
    }

    // Used for taking images -- creates a temporary file for the taken picture to reside
    fun createImageFileInCache(context: Context): File {
        val timestamp = SimpleDateFormat("yyyy_MM_dd_HH:mm:ss").format(Date())
        val imageFileName = "JPEG_" + timestamp + "_"
        val image = File.createTempFile(
            imageFileName,
            ".jpg",
            context.externalCacheDir
        )

        return image
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

    fun saveCurrentPicture() {
        if (this._currentPicture.value == null)
            return

        // TODO save picture (or filepath) in room database

        this._currentPicture.value = null
    }

    fun deleteCurrentPicture() {
        if (this._currentPicture.value == null)
            return

        // TODO delete picture (or filepath) from room database

        this._currentPicture.value = null
    }
}