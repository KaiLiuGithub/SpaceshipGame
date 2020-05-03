package com.kailiu.spaceship.dagger

import com.kailiu.spaceship.*
import com.kailiu.spaceship.dialog.GameOverDialog
import com.kailiu.spaceship.dialog.LeaderboardDialog
import dagger.Component
import javax.inject.Scope


@ApplicationScope
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(spaceshipApp: SpaceshipApp)

    fun inject(gameActivity: GameActivity)

    fun inject(gameOverDialog: GameOverDialog)
    fun inject(leaderboardDialog: LeaderboardDialog)

    fun inject(gameView: GameView)
    fun inject(settingsFragment: SettingsFragment)

    fun inject(mainFragment: MainFragment)
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationScope