package com.example.csce546_project

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
    val filepath: String  // TODO convert Uri to String before storing
)

/**
 * Class used to store active image data. Loaded from and saved to PictureEntry on start/save.
 * TODO learn how to map between the two for database loading/saving -- possibly make normal class
 */
data class PictureModel(
    val id: Int = 0,
    val name: String?,
    val filepath: Uri?,
)