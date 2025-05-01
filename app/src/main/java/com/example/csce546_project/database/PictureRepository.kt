package com.example.csce546_project.database

import android.content.Context
import android.graphics.Picture
import android.net.Uri
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PictureRepository(private val dao: PictureDAO) {
    val pictures: Flow<List<PictureModel>> = dao.getAllPictures()
        .map { pictureEntries ->
            pictureEntries.map { pictureEntry ->
                toPictureModel(pictureEntry)
            }
        }


    // Saves picture info to database and creates new image file
    suspend fun addPicture(picture: PictureModel, context: Context) {

        // Storing filepath to avoid compiler errors from later use (since it's mutable)
        val storedFilepath = picture.filepath
        if (picture.name == null || storedFilepath == null)
            return

        // Create permanent image file in app's images folder (create new dir if it doesn't exist)
        val imagesDir = File(context.filesDir, "images")
        if (!imagesDir.exists())
            imagesDir.mkdir()
        val timestamp = SimpleDateFormat("yyyy_MM_dd_HH:mm:ss", Locale.US).format(Date())
        val newImage = File(imagesDir, "FaceImage_${timestamp}.jpg")

        // Copy cached image contents to newly-created file; on failure, delete new file and abort
        try {
            // Get temp file from ContentResolver as a stream and open I/O-safe context to copy
            context.contentResolver.openInputStream(storedFilepath)?.use { cachedImageStream ->
                FileOutputStream(newImage).use { newImageStream ->
                    cachedImageStream.copyTo(newImageStream)
                }
            }
        }
        catch (e: Exception) {
            Log.e("DATABASE::addPicture()", "Error saving temp file to disk, aborting")
            newImage.delete()
            return
        }

        // Update filepath in picture to new permanent URI and add to database
        val updatedPicture = PictureModel(picture.id, picture.name, newImage.toUri())
        val databaseEntry = this.toPictureEntry(updatedPicture)
        if (databaseEntry != null)
            dao.insertPicture(databaseEntry)
    }

    // Saves picture info to database and does not create new image file
    suspend fun updatePicture(picture: PictureModel) {
        val pictureEntry: PictureEntry? = toPictureEntry(picture)
        if (pictureEntry == null)
            return
        dao.insertPicture(pictureEntry)
    }

    // Deletes picture info from database and associated image file
    suspend fun deletePicture(picture: PictureModel, context: Context) {
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