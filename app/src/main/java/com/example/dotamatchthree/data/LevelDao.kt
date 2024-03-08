package com.example.dotamatchthree.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LevelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLevels(levels: List<Level>)

    @Query("SELECT * FROM levels")
    fun getLevels(): List<Level>

    @Query("SELECT * FROM levels WHERE level =:level")
    fun getLevel(level: Int): Level
}