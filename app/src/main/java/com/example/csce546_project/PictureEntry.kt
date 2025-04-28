package com.example.csce546_project

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pictures")
data class PictureEntry (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val filepath: String  // TODO see if you can store actual image, or research filepath use
)