package com.example.csce546_project.database

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.File

/**
 * Class used to map instance variables to table columns
 */
@Entity(tableName = "pictures")
data class PictureEntry (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val filepath: String
)

/**
 * Class used to store active image data. Loaded from and saved to PictureEntry on start/save.
 */
class PictureModel(val id: Int?, var name: String?, var uri: Uri?) {

    var faceData: Bitmap? = null  // TODO make face data
    var mlFace: FloatArray = floatArrayOf()

    init {
        // TODO below is example code to open file and turn it into a bitmap -- rework it
        val imageUri: Uri? = this.uri
        if (imageUri?.path != null) {
            val imageFile = File(imageUri.path!!)
            this.faceData = BitmapFactory.decodeFile(imageFile.absolutePath)
            if (this.faceData == null) {
                Log.e("PictureModel::init","IMAGE NOT FOUND AT PATH: " + this.uri)
            } else {
                Log.d("PictureModel::init", "IMAGE OPENED SUCCESSFULLY")
            }
        }
    }
}