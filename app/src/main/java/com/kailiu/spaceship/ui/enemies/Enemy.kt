package com.kailiu.spaceship.ui.enemies

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.media.SoundPool
import com.kailiu.spaceship.R
import com.kailiu.spaceship.ui.Bullet
import com.kailiu.spaceship.ui.hitboxes.Oval
import kotlin.random.Random

abstract class Enemy(res: Resources) {
    val SMALL_IMAGE_SCALE = 25
    val MID_IMAGE_SCALE = 5

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

    open fun addBullet(frame: Int = 0, fieldH: Int = 0): Boolean = false

    companion object {
        private var sound1: Int? = null
        private var sound2: Int? = null
        private var reset = true

        fun getSound(soundPool: SoundPool, context: Context, isSaucer: Boolean): Int {
            sound1 = if (reset) soundPool.load(context, R.raw.bullet_enemy, 1) else sound1
            sound2 = if (reset) soundPool.load(context, R.raw.cannon, 1) else sound2

            reset = false
            return if (isSaucer) sound1!! else sound2!!
        }

        fun generateEnemy(res: Resources, frame: Int = 0): Enemy {
            return when (Random.nextInt(0, 400)) {
                in 0 until 30 -> Saucer(res)     // 30 / 400 = 7.5%
                in 30 until 90 -> Alien(res)     // 60 / 400 = 15%
                in 90 until 95 -> Satellite(res) // 5 / 400 = 1.125%
                else -> Asteroid(res)            // 150 / 200 = 76.25%
            }
        }
    }
}