package com.example.dotamatchthree.data.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.dotamatchthree.data.AppDatabase
import com.example.dotamatchthree.domain.AppPrefs
import com.example.dotamatchthree.domain.PrefsHelper
import com.example.dotamatchthree.presentation.ui.game.Game
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideContext(application: Application): Context = application.applicationContext

    @Provides
    @Singleton
    fun providePrefHelper(appPrefs: AppPrefs): PrefsHelper {
        return appPrefs
    }
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext app: Context,
    ) = Room.databaseBuilder(
        app,
        AppDatabase::class.java,
        "dotaMatchThreeDB"
    ).allowMainThreadQueries()
        .build()

    @Provides
    @Singleton
    fun provideLevelsDao(db: AppDatabase) = db.levelsDao()

    @Provides
    @Singleton
    fun provideGame() : Game {
        return Game()
    }
}