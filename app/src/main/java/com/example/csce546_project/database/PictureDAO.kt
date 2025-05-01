package com.example.csce546_project.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PictureDAO {
    @Query("SELECT * FROM pictures")
    fun getAllPictures(): Flow<List<PictureEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPicture(picture: PictureEntry)

    @Delete
    suspend fun deletePicture(picture: PictureEntry)
}