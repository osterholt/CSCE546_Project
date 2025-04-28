package com.example.csce546_project

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pictures")
data class PictureEntry (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val picture: String  // TODO make picture
)