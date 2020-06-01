package com.kailiu.spaceship.ui

import android.graphics.Rect
import android.media.SoundPool
import com.kailiu.spaceship.R
import com.kailiu.spaceship.ui.hitboxes.Oval
import kotlin.coroutines.coroutineContext

class Bullet(xPos: Int, yPos: Int, height: Int = 20, width: Int = 10, var longevity: Int = Int.MAX_VALUE, isSaucer: Boolean = true) {
    var top = yPos
    var bottom = yPos + height
    var left = xPos - width / 2
    var right = xPos + width / 2
    var bullet: Rect
    var fake: Rect =  Rect(0, 0, 0, 0)
    private var firstFrame: Int = 0
    var value: Int = 10

    init {
        bullet = Rect(left, top, right, bottom)
    }

    fun setMovement(value: Int) {
        this.value = value
    }

    fun move(value: Int = this.value, isRocket: Boolean = true, frame: Int = 0): Boolean {
        if (firstFrame == 0) {
            firstFrame = frame
        } else if (longevity != Int.MAX_VALUE && frame >= longevity + firstFrame) {
            return false
        }

        val direction = if (!isRocket) -1 else 1
        top -= value * direction
        bottom -= value * direction
        bullet = Rect(left, top, right, bottom)

        return true
    }

    fun getCollisionShape(): Rect {
        return if (longevity < Int.MAX_VALUE) fake else bullet
    }
}