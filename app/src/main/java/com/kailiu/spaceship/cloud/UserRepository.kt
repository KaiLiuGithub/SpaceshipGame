package com.kailiu.spaceship.cloud

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.kailiu.spaceship.AuthSharedPreferences
import com.kailiu.spaceship.SpaceshipApp
import retrofit2.Response
import javax.inject.Inject

class UserRepository(var context: Context) {

    @Inject
    lateinit var authSharedPreferences: AuthSharedPreferences

    lateinit var userService: UserService

    fun createInstance(): UserRepository {
        (context.applicationContext as SpaceshipApp).appComponent.inject(this)
        userService = RetrofitClientInstance.getRetrofitInstance().create(UserService::class.java)
        return this
    }

    fun signup(username: String, password: String): LiveData<Resource<String>> {
        return userService.signup(username, password)
    }

    fun login(username: String, password: String) {
        val loginCall = userService.login(username, password)
        loginCall.observeForever(object: Observer<Resource<Token>> {
            override fun onChanged(t: Resource<Token>?) {
                t?.let {
                    authSharedPreferences.setAuthToken("${it.data?.accessToken}")
                    loginCall.removeObserver(this)
                }
            }
        })
    }
}