package com.kailiu.spaceship.cloud

import com.google.gson.annotations.SerializedName
import java.util.*

class ScoreItem(
    @SerializedName("name")
    val name: String = "",
    @SerializedName("score")
    val score: Int = 0,
    @SerializedName("date")
    val time: Date = Date()
)