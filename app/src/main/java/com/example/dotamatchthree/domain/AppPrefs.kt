package com.example.dotamatchthree.domain

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppPrefs @Inject constructor(
    @ApplicationContext private val context: Context
) : PrefsHelper {
    private val sharedPreferences by lazy {
        context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    }

    override fun isFirstRun(): Boolean {
        return sharedPreferences.getBoolean(FIRST_RUN_TAG, true)
    }

    override fun setFirstRun(enable: Boolean) {
        sharedPreferences.edit { putBoolean(FIRST_RUN_TAG, enable) }
    }

    override fun saveLevel(level: Int) {
        sharedPreferences.edit { putInt(LEVEL_TAG, level) }
    }

    override fun getLevel(): Int {
        return sharedPreferences.getInt(LEVEL_TAG, 1)
    }


    companion object {
        private const val FIRST_RUN_TAG = "first_run"
        private const val LEVEL_TAG = "level"
    }
}
