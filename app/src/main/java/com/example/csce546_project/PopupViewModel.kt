package com.example.csce546_project

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class PopupViewModel(application: Application) : AndroidViewModel(application) {
    private val pictureRepository: String = ""

    val pictures: LiveData<List<PictureEntry>>?  // TODO complete PictureEntry
    var currentPicture: PictureEntry?

    init {
        pictures = null
        currentPicture = null
    }

    fun takePicture() {
        // TODO implement camera
    }

    fun getPicture() {
        // TODO implement get image from gallery
    }

    fun clearPicture() {
        this.currentPicture = null
    }

    fun savePicture(picture: String) {
        if (currentPicture == null)
            return

        // TODO save picture (or filepath) in room database

        this.currentPicture = null
    }

    fun deletePicture(picture: String) {
        if (currentPicture == null)
            return

        // TODO delete picture (or filepath) from room database

        this.currentPicture = null
    }
}