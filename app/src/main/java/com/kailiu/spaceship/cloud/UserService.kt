package com.kailiu.spaceship.cloud

import androidx.lifecycle.LiveData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserService {
    @POST("/signup")
    fun signup(@Body username: String, @Body password: String): LiveData<Resource<String>>

    @POST("/login")
    fun login(@Body username: String, @Body password: String): LiveData<Resource<Token>>
}