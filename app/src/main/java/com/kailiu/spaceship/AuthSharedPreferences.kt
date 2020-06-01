package com.kailiu.spaceship

import android.content.Context
import android.content.SharedPreferences


class AuthSharedPreferences(context: Context) {
    private var sharedPreferences: SharedPreferences = context.getSharedPreferences("auth_shared_preference", Context.MODE_PRIVATE)

    companion object {
        private const val AUTH_TOKEN = "auth_token"
    }

    fun setAuthToken(token: String) {
        sharedPreferences.edit().putString(AUTH_TOKEN, token).apply()
    }

    fun getAuthToken(): String {
        return sharedPreferences.getString(AUTH_TOKEN, "")!!
    }

}