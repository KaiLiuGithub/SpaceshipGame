package com.kailiu.spaceship.ui

import android.graphics.Rect
import android.media.SoundPool
import com.kailiu.spaceship.R
import kotlin.coroutines.coroutineContext

class Bullet(xPos: Int, yPos: Int, height: Int = 20, width: Int = 10) {
    var top = yPos - height
    var bottom = yPos
    var left = xPos - width / 2
    var right = xPos + width / 2
    var bullet: Rect

    init {
        bullet = Rect(left, top, right, bottom)
    }

    fun move(value: Int = 10, isRocket: Boolean = true) {
        val direction = if (!isRocket) -1 else 1
        top -= value * direction
        bottom -= value * direction
        bullet = Rect(left, top, right, bottom)
    }
}