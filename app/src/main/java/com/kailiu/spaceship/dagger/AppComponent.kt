package com.kailiu.spaceship.dagger

import com.kailiu.spaceship.*
import com.kailiu.spaceship.dialog.GameOverDialog
import com.kailiu.spaceship.dialog.LeaderboardDialog
import com.kailiu.spaceship.fragments.LeaderboardFragment
import com.kailiu.spaceship.fragments.MainFragment
import com.kailiu.spaceship.fragments.SettingsFragment
import dagger.Component
import javax.inject.Scope


@ApplicationScope
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(spaceshipApp: SpaceshipApp)

    // Activity
    fun inject(gameActivity: GameActivity)

    // Dialog
    fun inject(gameOverDialog: GameOverDialog)
    fun inject(leaderboardDialog: LeaderboardDialog)

    // Fragments
    fun inject(settingsFragment: SettingsFragment)
    fun inject(mainFragment: MainFragment)
    fun inject(leaderboardFragment: LeaderboardFragment)

    // Other views
    fun inject(gameView: GameView)
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationScope