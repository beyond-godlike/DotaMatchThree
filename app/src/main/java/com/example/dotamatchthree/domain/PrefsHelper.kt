package com.example.dotamatchthree.domain
interface PrefsHelper {
    fun isFirstRun(): Boolean

    fun setFirstRun(enable: Boolean = false)

    fun saveLevel(level: Int)

    fun getLevel(): Int
}