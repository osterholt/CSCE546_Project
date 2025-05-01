package com.example.csce546_project.database

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PictureRepository(private val dao: PictureDAO) {
    val pictures: Flow<List<PictureModel>> = dao.getAllPictures()
        .map { pictureEntries ->
            pictureEntries.map { pictureEntry ->
                toPictureModel(pictureEntry)
            }
        }


    // Saves picture info to database and creates new image file
    suspend fun addPicture(picture: PictureModel) {
        val pictureEntry: PictureEntry? = toPictureEntry(picture)

        Log.e("DATABASE", "ADDING " + picture + " TO DATABASE")

        if (pictureEntry == null)
            return

        // TODO create permanent URI for file and copy image at temp URI over

        dao.insertPicture(pictureEntry)
    }

    // Saves picture info to database and does not create new image file
    suspend fun updatePicture(picture: PictureModel) {
        val pictureEntry: PictureEntry? = toPictureEntry(picture)
        if (pictureEntry == null)
            return
        dao.insertPicture(pictureEntry)
    }

    // Deletes picture info from database and associated image file
    suspend fun deletePicture(picture: PictureModel) {
        val pictureEntry: PictureEntry? = toPictureEntry(picture)
        if (pictureEntry == null)
            return

        // TODO delete image file at permanent URI

        dao.deletePicture(pictureEntry)
    }

    // Converts database entry into active picture object with derived data
    private fun toPictureModel(pictureEntry: PictureEntry): PictureModel {
        val id: Int = pictureEntry.id
        val name: String = pictureEntry.name
        val filepath: Uri = pictureEntry.filepath.toUri()

        // TODO load image/face data into PictureModel

        return PictureModel(id, name, filepath)
    }

    // Converts active picture object to database entry
    private fun toPictureEntry(pictureModel: PictureModel): PictureEntry? {
        val id: Int = pictureModel.id ?: 0
        val name: String? = pictureModel.name

        if (name == null || pictureModel.filepath == null)
            return null

        return PictureEntry(id, name, pictureModel.filepath.toString())
    }
}