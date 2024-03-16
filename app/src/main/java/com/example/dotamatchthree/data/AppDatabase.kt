package com.example.dotamatchthree.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.dotamatchthree.data.api.dao.LevelDao

@Database(entities = [Level::class], version = 2)
@TypeConverters(LevelTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun levelsDao(): LevelDao
}