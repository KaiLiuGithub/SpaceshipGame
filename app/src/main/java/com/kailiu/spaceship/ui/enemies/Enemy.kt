package com.kailiu.spaceship.ui.enemies

import android.content.res.Resources
import android.graphics.Bitmap
import com.kailiu.spaceship.ui.Bullet
import com.kailiu.spaceship.ui.hitboxes.Oval
import kotlin.random.Random

abstract class Enemy(res: Resources) {
    val IMAGE_SCALE = 25

    var x = 0
    var y = 0
    var width = 0.0
    var height = 0.0
    var hp = 0
    var bullets = ArrayList<Bullet>()
    var maxBullets = 0
    lateinit var enemy: Bitmap
    var moveLeft: Boolean = Random.nextBoolean()

    abstract fun getCollisionShape(): Oval

    abstract fun animate(frame: Int)

    abstract fun move(dx: Int? = null, dy: Int? = null, frame: Int = 0, fieldW: Int = 0)

    companion object {
        fun generateEnemy(res: Resources): Enemy {
            return when (Random.nextInt(0, 200)) {
                in 0 until 15 -> Saucer(res)
                in 15 until 45 -> Alien(res)
                else -> Asteroid(res)
            }

        }
    }
}