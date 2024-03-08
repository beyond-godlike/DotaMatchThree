package com.example.dotamatchthree.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LevelTypeConverter {
    @TypeConverter
    fun fromString(value: String): List<List<Int>> {
        val listType = object : TypeToken<List<List<Int>>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun toString(value: List<List<Int>>): String {
        return Gson().toJson(value)
    }
}