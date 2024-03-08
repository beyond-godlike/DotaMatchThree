package com.example.dotamatchthree.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Level::class], version = 1)
@TypeConverters(LevelTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun levelsDao(): LevelDao
}