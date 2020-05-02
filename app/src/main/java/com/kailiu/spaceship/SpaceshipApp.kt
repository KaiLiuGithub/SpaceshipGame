package com.kailiu.spaceship

import android.app.Application
import android.content.Context
import com.kailiu.spaceship.dagger.AppComponent
import com.kailiu.spaceship.dagger.AppModule
import com.kailiu.spaceship.dagger.DaggerAppComponent
import com.kailiu.spaceship.database.ScoreRepository
import javax.inject.Inject

class SpaceshipApp : Application() {
    @Inject
    lateinit var scoreRepository: ScoreRepository

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this)).build()
        appComponent.inject(this)
    }
}