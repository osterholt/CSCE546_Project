package com.example.csce546_project.database

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

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
class PictureModel(val id: Int?, var name: String?, var filepath: Uri?) {

    var faceData: String = "" // TODO make face data

    init {
        if (filepath != null) {
            // TODO load in face data from URI here
            this.faceData = filepath.toString()
        }
    }
}