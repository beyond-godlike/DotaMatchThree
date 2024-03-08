package com.example.dotamatchthree.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "levels")
data class Level(
    @PrimaryKey
    val level: Int = 0,
    val goal: Int = 0,
    val goalType: Int = 0,
    val moves: Int = 0,
    val lvl: List<List<Int>>
)