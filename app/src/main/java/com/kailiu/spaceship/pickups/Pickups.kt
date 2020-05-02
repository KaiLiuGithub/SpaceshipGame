package com.kailiu.spaceship.pickups

import android.content.res.Resources
import android.graphics.Bitmap
import com.kailiu.spaceship.hitboxes.Oval

abstract class Pickups(res: Resources) {
    var x = 0
    var y = 0
    var width = 0.0
    var height = 0.0
    lateinit var pickup: Bitmap

    abstract fun getCollisionShape(): Oval

    companion object {
        fun createPickup(type: PickupType, res: Resources): Pickups {
            return when (type) {
                PickupType.SHIELD -> Shield(res)
                PickupType.AMMO -> Ammo(res)
                PickupType.LIFE -> Shield(res)
            }
        }
    }
}
