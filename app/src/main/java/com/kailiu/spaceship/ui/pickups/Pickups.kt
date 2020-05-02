package com.kailiu.spaceship.ui.pickups

import android.content.res.Resources
import android.graphics.Bitmap
import com.kailiu.spaceship.ui.hitboxes.Oval
import kotlin.random.Random

abstract class Pickups(var res: Resources) {
    var x = 0
    var y = 0
    var width = 0.0
    var height = 0.0
    lateinit var pickup: Bitmap

    abstract fun getCollisionShape(): Oval

    companion object {
        fun createPickup(res: Resources): Pickups {
            return when (Random.nextInt(0, 10)) {
                in 0 until 4 -> Ammo(res)
                in 4 until 8 -> Shield(res)
                else -> Life(res)
            }
        }
    }
}
