package com.kailiu.spaceship.enemies

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Matrix
import com.kailiu.spaceship.Bullet
import com.kailiu.spaceship.hitboxes.Oval
import kotlin.random.Random

abstract class Enemy(res: Resources) {
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
            val rand = Random.nextInt(0, 200)
            when (rand) {
                in 0 until 15 -> return Saucer(res)
                in 15 until 45 -> return Alien(res)
            }

            return Asteroid(res)
        }
    }
}