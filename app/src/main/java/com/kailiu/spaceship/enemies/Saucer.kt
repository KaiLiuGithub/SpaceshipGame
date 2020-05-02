package com.kailiu.spaceship.enemies

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.kailiu.spaceship.R
import com.kailiu.spaceship.hitboxes.Oval
import kotlin.random.Random

class Saucer(res: Resources): Enemy(res) {
    init {
        hp = 10
        maxBullets = 3

        val img = when (Random.nextInt(0, 3)) {
            0 -> R.drawable.saucer
            1 -> R.drawable.saucer
            else-> R.drawable.saucer
        }

        enemy = BitmapFactory.decodeResource(res, img)

        width = enemy.width.toDouble()
        height = enemy.height.toDouble()

        width /= 15
        height /= 15

        //width *= GameView.screenRatioX
        //height *= GameView.screenRatioY

        enemy = Bitmap.createScaledBitmap(enemy, width.toInt(), height.toInt(), false)
    }
    override fun getCollisionShape(): Oval {
        return Oval(x + width.toInt() / 2, y + height.toInt() / 2, width.toInt() / 2,height.toInt() / 2)
    }

    override fun animate(frame: Int) {
    }

    override fun move(dx: Int?, dy: Int?, frame: Int, fieldW: Int) {
        var switchDir = if (frame % 10 == 0)  Random.nextBoolean()  else false
        if (switchDir) moveLeft = !moveLeft
        if (x + width + (dx ?: 1) > fieldW) {
            moveLeft = true
            switchDir = false
        }
        if (x + (-1) * (width + (dx ?: 1)) < 0) {
            moveLeft = false
            switchDir = false
        }

        val dir = if (switchDir) -1 else 1

        x += if (moveLeft) -1 * dir * (dx ?: 5) else dir * (dx ?: 5)
        y += dy ?: 5
    }

}