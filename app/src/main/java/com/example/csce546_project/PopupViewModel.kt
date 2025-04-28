package com.example.csce546_project

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class PopupViewModel(application: Application) : AndroidViewModel(application) {
    private val pictureRepository: String = ""  // TODO create repo

    val pictures: LiveData<List<PictureEntry>>?
    var currentPicture: PictureEntry?  // TODO forgot how to do dataflow thing so changes update on screen

    init {
        pictures = null  // TODO init properly
        currentPicture = null
    }

    fun takePicture() {
        // TODO implement camera
    }

    fun openPicture() {
        // TODO implement get image from gallery
    }

    fun clearPicture() {
        this.currentPicture = null
    }

    fun saveCurrentPicture() {
        if (currentPicture == null)
            return

        // TODO save picture (or filepath) in room database

        this.currentPicture = null
    }

    fun deleteCurrentPicture() {
        if (currentPicture == null)
            return

        // TODO delete picture (or filepath) from room database

        this.currentPicture = null
    }
}