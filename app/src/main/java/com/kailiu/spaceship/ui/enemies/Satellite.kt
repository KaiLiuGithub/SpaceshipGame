package com.kailiu.spaceship.ui.enemies

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.kailiu.spaceship.R
import com.kailiu.spaceship.ui.Bullet
import com.kailiu.spaceship.ui.hitboxes.Oval
import kotlin.random.Random

class Satellite(res: Resources): Enemy(res) {
    var moveFrame = 0
    var shootFrame = 0

    init {
        hp = 10
        maxBullets = 1

        val img = when (Random.nextInt(0, 3)) {
            0 -> R.drawable.satellite
            1 -> R.drawable.satellite
            else-> R.drawable.satellite
        }

        enemy = BitmapFactory.decodeResource(res, img)

        width = enemy.width.toDouble()
        height = enemy.height.toDouble()

        width /= MID_IMAGE_SCALE
        height /= MID_IMAGE_SCALE

        //width *= GameView.screenRatioX
        //height *= GameView.screenRatioX

        enemy = Bitmap.createScaledBitmap(enemy, width.toInt(), height.toInt(), false)
    }

    override fun getCollisionShape(): Oval {
        return Oval(x + width.toInt() / 2, y + height.toInt() / 2, width.toInt() / 2,height.toInt() / 2)
    }

    override fun animate(frame: Int) {
    }

    override fun addBullet(frame: Int, fieldH: Int): Boolean {
        return if (bullets.isEmpty() && (frame - moveFrame) % 200 == 199) {
            bullets.add(Bullet(x + height.toInt() / 2, y + height.toInt(), fieldH - height.toInt(), 15, 20).apply {
                setMovement(0)
            })
            true
        } else false
    }

    override fun move(dx: Int?, dy: Int?, frame: Int, fieldW: Int) {
        if (moveFrame == 0) {
            moveFrame = frame
            x += dx ?: 5
            y += 5
        } else if (frame - moveFrame > 600) {
            y += dy ?: 15
        } else {
            if (y < height / 4) {
                y += 5
            } else {
                if (x + width + (dx ?: 1) > fieldW) {
                    moveLeft = true
                }
                if (x + (-1) * (width + (dx ?: 1)) < 0) {
                    moveLeft = false
                }
                if ((frame - moveFrame) % 200 > 80) {
                    x += if (moveLeft) -1 * (dx ?: 5) else (dx ?: 5)
                    if (bullets.isNotEmpty()) bullets.removeAt(0)
                }
            }
        }
    }

}