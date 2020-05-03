package com.kailiu.spaceship.ui

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Rect
import android.media.SoundPool
import com.kailiu.spaceship.R
import com.kailiu.spaceship.ui.pickups.PickupType

class Rocket(var res: Resources) {
    companion object {
        private var sound: Int? = null
        var reset = true

        fun getSound(soundPool: SoundPool, context: Context): Int {
            sound = if (reset) soundPool.load(context, R.raw.bullet_rocket, 1) else sound
            reset = false
            return sound!!
        }
    }

    var x = 0
    var y = 0
    var health = 1
    var widthF: Double
    var heightF: Double
    private var widthR: Double
    private var heightR: Double
    var width: Double
    var height: Double
    var rocket: Bitmap = BitmapFactory.decodeResource(res,
        R.drawable.rocket
    )
    var flame: Bitmap = BitmapFactory.decodeResource(res,
        R.drawable.flame0
    )
    var bullets = ArrayList<Bullet>()
    var pickups = MutableList(2) { _ -> false}

    init {
        reset = true

        widthR = rocket.width.toDouble() / 25
        heightR = rocket.height.toDouble() / 25

        widthF = flame.width.toDouble() / 17
        heightF = flame.height.toDouble() / 17

        width = widthR
        height = heightR + heightF / 2

        //width *= GameView.screenRatioX
        //height *= GameView.screenRatioY

        rocket = Bitmap.createScaledBitmap(rocket, widthR.toInt(), heightR.toInt(), false)
    }
    
    fun getFlame(frame: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(180f)

        flame =  BitmapFactory.decodeResource(res,
            when (frame % 10) {
                0 -> R.drawable.flame0
                1 -> R.drawable.flame1
                2 -> R.drawable.flame2
                3 -> R.drawable.flame3
                4 -> R.drawable.flame4
                5 -> R.drawable.flame5
                6 -> R.drawable.flame6
                7 -> R.drawable.flame7
                8 -> R.drawable.flame8
                else -> R.drawable.flame9
            }
        )

        flame = Bitmap.createScaledBitmap(flame, widthF.toInt(), heightF.toInt(), false)
        flame = Bitmap.createBitmap(flame, 0, 0, widthF.toInt(), heightF.toInt(), matrix, false)
        return flame
    }

    fun resetPosition(w: Int, h: Int) {
        x = (w - width).toInt() / 2
        y = (h - height).toInt()
    }

    fun move(dx: Int, dy: Int) {
        x += dx
        y += dy
    }

    fun addPickup(type: PickupType) {
        when (type) {
            PickupType.SHIELD -> {

            }
            PickupType.AMMO -> {

            }
            else -> {}
        }
    }

    /*fun getCollisionShape(): Triangle {
        return Triangle(x + width.toInt() / 2, y, x, y + height.toInt(), x + width.toInt(), y + height.toInt())
    }*/

    fun getCollisionShape(): Rect {
        val dx = width.toInt() / 10
        return Rect(x + dx, y, x + width.toInt() - dx, y + width.toInt())
    }
}