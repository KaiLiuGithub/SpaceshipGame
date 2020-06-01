package com.kailiu.spaceship.ui.enemies

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.kailiu.spaceship.GameView
import com.kailiu.spaceship.R
import com.kailiu.spaceship.ui.hitboxes.Oval
import kotlin.random.Random

class Alien(res: Resources): Enemy(res) {
    init {
        hp = 3

        val img = when (Random.nextInt(0, 3)) {
            0 -> R.drawable.alien
            1 -> R.drawable.alien
            else-> R.drawable.alien
        }

        enemy = BitmapFactory.decodeResource(res, img)

        width = enemy.width.toDouble()
        height = enemy.height.toDouble()

        width /= SMALL_IMAGE_SCALE
        height /= SMALL_IMAGE_SCALE

        width *= GameView.screenRatioX
        height *= GameView.screenRatioX

        enemy = Bitmap.createScaledBitmap(enemy, width.toInt(), height.toInt(), false)
    }
    override fun getCollisionShape(): Oval {
        return Oval(x + width.toInt() / 2, y + height.toInt() / 2, width.toInt() / 2,height.toInt() / 2)
    }

    override fun animate(frame: Int) {
    }

    override fun move(dx: Int?, dy: Int?, frame: Int, fieldW: Int) {
        if (x + width + (dx ?: 1) > fieldW) {
            moveLeft = true
        }
        if (x + (-1) * (width + (dx ?: 1)) < 0) {
            moveLeft = false
        }

        x += if (moveLeft) -1 * (dx ?: 3) else (dx ?: 3)
        y += dy ?: 5
    }

}