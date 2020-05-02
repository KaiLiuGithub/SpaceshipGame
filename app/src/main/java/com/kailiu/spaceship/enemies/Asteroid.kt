package com.kailiu.spaceship.enemies

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import com.kailiu.spaceship.R
import com.kailiu.spaceship.hitboxes.Oval
import kotlin.math.sqrt
import kotlin.random.Random


class Asteroid(res: Resources): Enemy(res) {

    init {
        hp = 1

        val img = when (Random.nextInt(0, 3)) {
            0 -> R.drawable.asteroid_blue
            1 -> R.drawable.asteroid_brown
            else-> R.drawable.asteroid_purple
        }

        enemy = BitmapFactory.decodeResource(res, img)

        width = enemy.width.toDouble()
        height = enemy.height.toDouble()

        width /= 15
        height /= 15

        //width *= GameView.screenRatioX
        //height *= GameView.screenRatioY

        val matrix = Matrix()

        matrix.postRotate(Random.nextInt(0, 3) * 90f)
        if (Random.nextInt(0, 1) == 0) matrix.postScale(-1f, 1f, width.toFloat() / 2, height.toFloat() / 2)
        if (Random.nextInt(0, 1) == 0) matrix.postScale(1f, -1f, width.toFloat() / 2, height.toFloat() / 2)

        enemy = Bitmap.createScaledBitmap(enemy, width.toInt(), height.toInt(), false)
        enemy = Bitmap.createBitmap(enemy, 0, 0, enemy.width, enemy.height, matrix, false)
    }

    override fun getCollisionShape(): Oval {
        return Oval(x + width.toInt() / 2, y + height.toInt() / 2, width.toInt() / 2,height.toInt() / 2)
    }

    override fun animate(frame: Int) {
        if (frame % 30 == 0) {
            val matrix = Matrix()
            matrix.postRotate(90f)
            enemy = Bitmap.createBitmap(enemy, 0, 0, width.toInt(), height.toInt(), matrix, false)
        }
    }

    override fun move(dx: Int?, dy: Int?, frame: Int, fieldW: Int) {
        x += dx ?: 0
        y += dy ?: 5
    }
}