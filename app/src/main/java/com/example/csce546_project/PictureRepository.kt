package com.example.csce546_project

import kotlinx.coroutines.flow.Flow

class PictureRepository(private val dao: PictureDAO) {
    val notes: Flow<List<PictureEntry>> = dao.getAllPictures()

    suspend fun addPicture(picture: PictureEntry) {
        dao.insertPicture(picture)
    }

    suspend fun deletePicture(picture: PictureEntry) {
        dao.deletePicture(picture)
    }
}