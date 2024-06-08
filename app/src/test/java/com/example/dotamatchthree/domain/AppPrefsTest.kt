package com.example.dotamatchthree.domain

import android.content.Context
import android.content.SharedPreferences
import com.example.dotamatchthree.presentation.ui.MainDispatcherRule
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.stub
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AppPrefsTest {
    private lateinit var context: Context
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var appPrefs: AppPrefs

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        context = mock()
        sharedPreferences = mock()
        appPrefs = AppPrefs(context)

        // Mock sharedPreferences behavior
        context.stub {
            on { getSharedPreferences(context.packageName, Context.MODE_PRIVATE) }.thenReturn(sharedPreferences)
        }
    }

    @Test
    fun testIsFirstRun() {
        appPrefs.isFirstRun()

        verify(sharedPreferences).getBoolean(AppPrefs.FIRST_RUN_TAG, true)
    }

    @Test
    fun testSetFirstRun() {
        mainDispatcherRule.runCatching {
            val enable = true
            appPrefs.setFirstRun(enable)

            verify(sharedPreferences.edit()).putBoolean(AppPrefs.FIRST_RUN_TAG, enable)
        }
    }

    @Test
    fun testSaveLevel() {
        mainDispatcherRule.runCatching {
            val level = 5
            appPrefs.saveLevel(level)

            verify(sharedPreferences.edit()).putInt(AppPrefs.LEVEL_TAG, level)
        }
    }

    @Test
    fun testGetLevel() {
        appPrefs.getLevel()

        verify(sharedPreferences).getInt(AppPrefs.LEVEL_TAG, 1)
    }
}