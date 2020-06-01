package com.kailiu.spaceship.dagger

import com.kailiu.spaceship.*
import com.kailiu.spaceship.cloud.UserRepository
import com.kailiu.spaceship.dialog.GameOverDialog
import com.kailiu.spaceship.dialog.LeaderboardDialog
import com.kailiu.spaceship.fragments.*
import dagger.Component
import javax.inject.Scope


@ApplicationScope
@Component(modules = [AppModule::class, RetrofitModule::class])
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
    fun inject(loginFragment: LoginFragment)
    fun inject(signupFragment: SignupFragment)

    // Other views
    fun inject(gameView: GameView)

    // Repositories
    fun inject(userRepository: UserRepository)
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationScope