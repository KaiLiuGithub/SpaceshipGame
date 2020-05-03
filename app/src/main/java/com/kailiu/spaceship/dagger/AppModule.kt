package com.kailiu.spaceship.dagger

import android.content.Context
import com.kailiu.spaceship.SettingsSharedPreferences
import com.kailiu.spaceship.database.ScoreRepository
import dagger.Module
import dagger.Provides

@Module
open class AppModule(var context: Context) {

    private var scoreRepository = ScoreRepository(context)

    private var settingsSharedPreferences = SettingsSharedPreferences(context)

    @Provides
    @ApplicationScope
    open fun provideScoreRepository() = scoreRepository

    @Provides
    @ApplicationScope
    open fun provideSettingsSharedPreferences() = settingsSharedPreferences
}