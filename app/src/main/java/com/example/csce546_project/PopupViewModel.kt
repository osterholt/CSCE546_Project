package com.example.csce546_project

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PopupViewModel(application: Application) : AndroidViewModel(application) {
    private val pictureRepository: String = ""  // TODO create repo

    val pictures: LiveData<List<PictureEntry>>?

    private val _currentPicture = MutableStateFlow<PictureEntry?>(null)
    val currentPicture: StateFlow<PictureEntry?> = _currentPicture

    private val _showAddPopup = MutableStateFlow(false)
    val showAddPopup: StateFlow<Boolean> = _showAddPopup

    private val _showEditPopup = MutableStateFlow(false)
    val showEditPopup: StateFlow<Boolean> = _showEditPopup

    val TEST_PICTURE = PictureEntry(0, "Example", "exampleFilePath")  // TODO

    init {
        this.pictures = null  // TODO init properly
        this._currentPicture.value = null
    }

    fun openAddPopup() {
        this._showEditPopup.value = false
        this._showAddPopup.value = true
    }

    fun openEditPopup(picture: PictureEntry) {
        this._currentPicture.value = picture
        this._showAddPopup.value = false
        this._showEditPopup.value = true
    }

    fun closePopup() {
        this._showAddPopup.value = false
        this._showEditPopup.value = false
        this._currentPicture.value = null
    }

    fun takePicture() {
        // TODO implement camera

        this._currentPicture.value = TEST_PICTURE
    }

    fun openPicture() {
        // TODO implement get image from gallery

        this._currentPicture.value = TEST_PICTURE
    }

    fun clearPicture() {
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