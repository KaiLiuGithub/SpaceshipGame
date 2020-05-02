package com.kailiu.spaceship.dagger

import com.kailiu.spaceship.GameActivity
import com.kailiu.spaceship.dialog.GameOverDialog
import com.kailiu.spaceship.SpaceshipApp
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
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationScope