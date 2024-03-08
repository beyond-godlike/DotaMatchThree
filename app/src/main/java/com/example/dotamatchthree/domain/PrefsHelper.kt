package com.example.dotamatchthree.domain

import androidx.core.content.edit

interface PrefsHelper {
    fun isFirstRun(): Boolean

    fun setFirstRun(enable: Boolean = false)

    fun saveLevel(level: Int)

    fun getLevel(): Int
}