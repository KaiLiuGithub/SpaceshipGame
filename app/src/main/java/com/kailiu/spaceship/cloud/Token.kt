package com.kailiu.spaceship.cloud

import com.google.gson.annotations.SerializedName

class Token {
    @SerializedName("token")
    var accessToken: String? = null
    @SerializedName("expires_in")
    var expiresIn: Long? = null
}