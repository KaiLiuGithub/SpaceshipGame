package com.kailiu.spaceship.dagger

import android.content.Context
import com.kailiu.spaceship.cloud.OkHttpClientHelper
import com.kailiu.spaceship.cloud.RetrofitClientInstance
import com.kailiu.spaceship.cloud.UserRepository
import com.kailiu.spaceship.cloud.UserService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
open class RetrofitModule(var context: Context) {

    @Provides
    @ApplicationScope
    open fun providesUserService(): UserService {

        return RetrofitClientInstance.getRetrofitInstance().create(UserService::class.java)
    }

    @Provides
    @ApplicationScope
    open fun providesUserRepository(): UserRepository {

        return UserRepository(context).createInstance()
    }
}