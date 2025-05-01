package com.example.csce546_project.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PictureEntry::class], version = 1)
abstract class PictureDatabase : RoomDatabase() {
    abstract fun pictureDAO(): PictureDAO

    companion object {
        @Volatile private var INSTANCE: PictureDatabase? = null

        fun getDatabase(context: Context): PictureDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    PictureDatabase::class.java,
                    "picture_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}