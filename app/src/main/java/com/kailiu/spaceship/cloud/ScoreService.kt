package com.kailiu.spaceship.cloud

import androidx.lifecycle.LiveData
import retrofit2.http.*

interface ScoreService {
    @GET("/leaderboard")
    fun getGlobalLeaderboard(): LiveData<Resource<List<ScoreItem>>>

    @GET("/spaceship/{username}")
    fun getScore(@Path(value = "username") username: String): LiveData<Resource<ScoreItem>>

    @PUT("/spaceship/{username}")
    fun putScore(@Path(value = "username") username: String, @Body scoreItem: ScoreItem): LiveData<Resource<ScoreItem>>

    @POST("/spaceship/{username}")
    fun postScore(@Path(value = "username") username: String, @Body scoreItem: ScoreItem): LiveData<Resource<ScoreItem>>

    @DELETE("/spaceship/{username}")
    fun deleteScore(@Path(value = "username") username: String): LiveData<Resource<String>>
}