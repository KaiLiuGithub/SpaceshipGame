package com.kailiu.spaceship

import android.content.Context
import android.graphics.*
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.SurfaceView
import com.kailiu.spaceship.ui.Background
import com.kailiu.spaceship.ui.Bullet
import com.kailiu.spaceship.ui.Rocket
import com.kailiu.spaceship.ui.enemies.Asteroid
import com.kailiu.spaceship.ui.enemies.Enemy
import com.kailiu.spaceship.ui.pickups.Ammo
import com.kailiu.spaceship.ui.pickups.Life
import com.kailiu.spaceship.ui.pickups.Pickups
import com.kailiu.spaceship.ui.pickups.Shield
import java.util.concurrent.Semaphore
import javax.inject.Inject
import kotlin.concurrent.thread
import kotlin.random.Random


class GameView(context: Context, var x: Int = 0, var y: Int = 0): SurfaceView(context), Runnable {
    companion object {
        var screenRatioX = 0f
        var screenRatioY = 0f
        private const val BULLET_FRAMES = 15
        private const val INFO_OFFSET = 20
    }

    var score = 0

    @Inject
    lateinit var settingsSharedPreferences: SettingsSharedPreferences

    lateinit var thread: Thread
    private var isPlaying = false
    var isGameOver = false
    private var paint: Paint
    private var background1 = Background(x, y, resources)
    private var background2 = Background(x, y, resources)
    private var rocket = Rocket(resources)
    private var enemies = ArrayList<Enemy>()
    private var pickups = ArrayList<Pickups>()
    var info = ArrayList<Pickups>()

    private val rocketSemaphore: Semaphore = Semaphore(1, true)
    private val enemiesSemaphore: Semaphore = Semaphore(1, true)
    private val pickupsSemaphore: Semaphore = Semaphore(1, true)
    private val bulletsSemaphore: Semaphore = Semaphore(1, true)
    private val infoSemaphore: Semaphore = Semaphore(1, true)

    private var guideLines = ArrayList<Rect>()
    private var infoBox: Rect
    
    private var isShooting = false
    private var frames = 0
    private var shootingFrames = 0

    private var soundPool: SoundPool? = null

    var tapX1 = 0f
    var tapX2 = 0f
    var tapY1 = 0f
    var tapY2 = 0f

    init {
        score = 0

        (context.applicationContext as SpaceshipApp).appComponent.inject(this)

        background2.y = -y

        paint = Paint()

        screenRatioX = 1920f / x
        screenRatioY = 1080f / y

        rocket.resetPosition(x, y)

        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributes.USAGE_GAME)
            .build()

        soundPool = SoundPool.Builder()
            .setAudioAttributes(audioAttributes)
            .build()

        for (i in 0 until 6) {
            val enemy = Asteroid(resources)
            enemy.x = Random.nextInt(0, x - enemy.width.toInt())
            enemy.y -= enemy.height.toInt()
            enemies.add(enemy)
        }

        info.add(Life(resources).apply {
            this.x = Companion.INFO_OFFSET
            this.y = Companion.INFO_OFFSET
        })
        info.add(Life(resources).apply {
            this.x = Companion.INFO_OFFSET + Companion.INFO_OFFSET / 2 + this.width.toInt()
            this.y = Companion.INFO_OFFSET
        })
        info.add(Life(resources).apply {
            this.x = Companion.INFO_OFFSET + Companion.INFO_OFFSET + 2 * this.width.toInt()
            this.y = Companion.INFO_OFFSET
        })
        info.add(Shield(resources).apply {
            this.x = this@GameView.x - 2 * this.width.toInt() - Companion.INFO_OFFSET
            this.y = Companion.INFO_OFFSET
        })
        info.add(Ammo(resources).apply {
            this.x = this@GameView.x - this.width.toInt() - Companion.INFO_OFFSET
            this.y = Companion.INFO_OFFSET
        })

        guideLines.add(Rect(0, 0, x, 5))

        infoBox = Rect(
            Companion.INFO_OFFSET,
            Companion.INFO_OFFSET, x, info[0].height.toInt() + Companion.INFO_OFFSET
        )
    }

    override fun run() {
        while (isPlaying) {

            update()
            draw()
            sleep()
        }
    }

    fun update() {
        // background
        background1.y += 3
        background2.y += 3

        if (background1.y >= background1.background.height) {
            background1.y = -y
        } else if (background2.y >= background2.background.height) {
            background2.y = -y
        }

        // bullets (rocket)
        thread {
            while (!bulletsSemaphore.tryAcquire()) {}
            var i = 0
            val bullets = rocket.bullets
            while (i in 0 until bullets.size) {
                if (Rect.intersects(bullets[i].bullet, guideLines[0])) {
                    bullets.removeAt(i)
                } else {
                    bullets[i].move()
                    var j = 0
                    while (j < enemies.size) {
                        if (enemies[j].getCollisionShape().intersects(bullets[i].bullet)) {
                            enemies[j].hp--
                            if (enemies[j].hp <= 0) enemies.removeAt(j)
                            bullets.removeAt(i)
                            score++
                            i--
                            break
                        }
                        j++
                    }
                    i++
                }
            }
            bulletsSemaphore.release()
        }
        
        // enemies
        thread {
            while (!enemiesSemaphore.tryAcquire()) {}
            if (Random.nextInt(
                    0,
                    100
                ) < 2 && !enemies[enemies.size - 1].getCollisionShape().intersects(guideLines[0])
            ) {
                val index = Random.nextInt(0, 5)
                enemies.add(enemies[index])
                val enemy = Enemy.generateEnemy(resources)
                enemy.x = Random.nextInt(0, x - enemy.width.toInt())
                enemy.y -= enemy.height.toInt()
                enemies[index] = enemy
            }

            var i = 5
            while (i < enemies.size) {
                val collide = enemies[i].getCollisionShape().intersects(rocket.getCollisionShape())
                if (enemies[i].y >= y || collide) {
                    enemies.removeAt(i)
                    if (collide) {
                        if (rocket.pickups[0]) {
                            rocket.pickups[0] = false
                        } else {
                            if (rocket.pickups[1]) {
                                rocket.pickups[1] = false
                            }
                            rocket.health--
                        }
                    }
                } else {
                    enemies[i].move(frame = frames, fieldW = x)
                    enemies[i].animate(frames)
                    if (enemies[i].bullets.size < enemies[i].maxBullets) {
                        if (frames % Companion.BULLET_FRAMES == 0) {
                            enemies[i].bullets.add(
                                Bullet(
                                    enemies[i].x + enemies[i].height.toInt() / 2,
                                    enemies[i].y + enemies[i].height.toInt(),
                                    15,
                                    15
                                )
                            )
                            if (settingsSharedPreferences.getSoundEffects()) {
                                soundPool?.play(Enemy.getSound(soundPool!!, context), 1f, 1f, 0, 0, 1f);
                            }
                        }
                    }
                    var j = 0
                    while (j in 0 until enemies[i].bullets.size) {
                        if (Rect.intersects(
                                enemies[i].bullets[j].bullet,
                                rocket.getCollisionShape()
                            )
                        ) {
                            if (rocket.pickups[0]) {
                                rocket.pickups[0] = false
                            } else {
                                if (rocket.pickups[1]) {
                                    rocket.pickups[1] = false
                                }
                                rocket.health--
                            }
                            enemies[i].bullets.removeAt(j)
                            j--
                        } else {
                            enemies[i].bullets[j].move(isRocket = false)
                            j++
                        }
                    }
                    i++
                }
            }
            enemiesSemaphore.release()
        }

        // pickups
        thread {
            while (!pickupsSemaphore.tryAcquire()) {}
            if (pickups.size < 2 && Random.nextInt(0, 5000) < 5) {
                val pickup =
                    Pickups.createPickup(resources)
                pickups.add(pickup.apply {
                    this.x = Random.nextInt(0, this@GameView.x - width.toInt())
                    this.y -= height.toInt()
                })
            }
            if (pickups.isNotEmpty()) {
                pickups[0].y += 5
                val collide = pickups[0].getCollisionShape().intersects(rocket.getCollisionShape())
                if (pickups[0].y >= y || collide) {
                    if (collide) {
                        when (pickups[0]) {
                            is Shield -> rocket.pickups[0] = true
                            is Ammo -> rocket.pickups[1] = true
                            else -> if (rocket.health < 3) rocket.health++
                        }
                    }
                    pickups.removeAt(0)
                }
            }
            pickupsSemaphore.release()
        }

        // rocket
        thread {
            while (!rocketSemaphore.tryAcquire()) {}
            if (isShooting) {
                if (shootingFrames % 15 == 0) {
                    if (rocket.pickups[1]) {
                        rocket.bullets.add(
                            Bullet(
                                rocket.x + (rocket.width / 2).toInt() - 15,
                                rocket.y
                            )
                        )
                        rocket.bullets.add(
                            Bullet(
                                rocket.x + (rocket.width / 2).toInt() + 15,
                                rocket.y
                            )
                        )
                    } else {
                        rocket.bullets.add(
                            Bullet(
                                rocket.x + (rocket.width / 2).toInt(),
                                rocket.y
                            )
                        )
                    }
                    if (settingsSharedPreferences.getSoundEffects()) {
                        soundPool?.play(Rocket.getSound(soundPool!!, context), 1f, 1f, 0, 0, 1f);
                    }
                }
                shootingFrames++

                if (tapX1 < rocket.x) {
                    rocket.move(-15, 0)
                } else if (tapX1 > rocket.x + rocket.width) {
                    rocket.move(15, 0)
                } else if (rocket.x > 0 && rocket.x + rocket.width < x) {
                    val dx = tapX1 - (rocket.x + (rocket.width / 2).toInt())
                    rocket.move(0, 0)
                }
            }
            rocketSemaphore.release()
        }

        thread {
            while (!infoSemaphore.tryAcquire()) {}
            (info[0] as Life).setStatus(rocket.health, 1)
            (info[1] as Life).setStatus(rocket.health, 2)
            (info[2] as Life).setStatus(rocket.health, 3)
            infoSemaphore.release()
        }

        // Check Game Over
        if (rocket.health <= 0) {
            isGameOver = true
            isPlaying = false
        }
    }

    fun draw() {
        if (holder.surface.isValid) {
            frames++
            val canvas = holder.lockCanvas()
            // Background
            canvas.drawBitmap(background1.background, background1.x.toFloat(), background1.y.toFloat(), paint)
            canvas.drawBitmap(background2.background, background2.x.toFloat(), background2.y.toFloat(), paint)

            // Guide Lines
            paint.color = Color.TRANSPARENT
            canvas.drawRect(guideLines[0], paint)

            paint.color = Color.parseColor("#22000000")
            canvas.drawRect(infoBox, paint)

            // Bullets
            while (!bulletsSemaphore.tryAcquire()) {}
            paint.color = Color.MAGENTA
            paint.strokeWidth = 3f
            paint.style = Paint.Style.FILL_AND_STROKE
            paint.shader = LinearGradient(0f, 0f, 0f, 0f, Color.MAGENTA,Color.MAGENTA, Shader.TileMode.MIRROR)

            for (bullet in rocket.bullets) {
                canvas.drawRect(bullet.bullet, paint)
            }
            bulletsSemaphore.release()

            // Pickups
            while (!pickupsSemaphore.tryAcquire()) {}
            if (pickups.isNotEmpty()) {
                canvas.drawBitmap(pickups[0].pickup, pickups[0].x.toFloat(), pickups[0].y.toFloat(), paint)
            }
            pickupsSemaphore.release()

            // Enemies
            while (!enemiesSemaphore.tryAcquire()) {}
            for (enemy in enemies) {
                canvas.drawBitmap(enemy.enemy, enemy.x.toFloat(), enemy.y.toFloat(), paint)
                for (bullet in enemy.bullets) {
                    paint.color = Color.YELLOW
                    paint.shader = LinearGradient(0f, 0f, 0f, 0f, Color.YELLOW,Color.YELLOW, Shader.TileMode.MIRROR)
                    val r = (bullet.right - bullet.left) / 2f
                    canvas.drawCircle(bullet.left + r, bullet.top  + r, r, paint)
                }
            }
            enemiesSemaphore.release()

            // Rocket
            while (!rocketSemaphore.tryAcquire()) {}
            canvas.drawBitmap(rocket.getFlame(frames), rocket.x.toFloat() + rocket.rocket.width / 2 - rocket.flame.width / 2, rocket.y.toFloat() + rocket.rocket.height  - rocket.flame.height / 2, paint)
            canvas.drawBitmap(rocket.rocket, rocket.x.toFloat(), rocket.y.toFloat(), paint)
            if (rocket.pickups[0]) {
                paint.strokeWidth = 10f
                paint.style = Paint.Style.STROKE
                paint.shader = LinearGradient(rocket.x.toFloat() - 10, rocket.y.toFloat() - 15, rocket.x.toFloat() - 5, (rocket.y + rocket.width + 10).toFloat(), Color.RED, Color.BLUE, Shader.TileMode.MIRROR)
                val oval = RectF()
                oval.set(rocket.x.toFloat() - 10, rocket.y.toFloat() - 10, (rocket.x + rocket.width + 10).toFloat(), (rocket.y + rocket.height).toFloat())
                val myPath = Path()
                myPath.arcTo(oval, 0f, -180f, true)
                canvas.drawPath(myPath, paint)
            }
            rocketSemaphore.release()


            // Info
            paint.shader = LinearGradient(0f, 0f, 0f, 0f, Color.YELLOW, Color.YELLOW, Shader.TileMode.CLAMP)
            paint.textSize = 25f * resources.displayMetrics.density

            var str = resources.getString(R.string.score_info, score)
            canvas.drawText(str, (x - paint.measureText(str)) / 2f, infoBox.exactCenterY(), paint)

            while (!infoSemaphore.tryAcquire()) {}
            canvas.drawBitmap(info[0].pickup, info[0].x.toFloat(), info[0].y.toFloat(), paint)
            canvas.drawBitmap(info[1].pickup, info[1].x.toFloat(), info[1].y.toFloat(), paint)
            canvas.drawBitmap(info[2].pickup, info[2].x.toFloat(), info[2].y.toFloat(), paint)
            if (rocket.pickups[0])
                canvas.drawBitmap(info[3].pickup, info[3].x.toFloat(), info[3].y.toFloat(), paint)
            if (rocket.pickups[1])
                canvas.drawBitmap(info[4].pickup, info[4].x.toFloat(), info[4].y.toFloat(), paint)
            infoSemaphore.release()

            holder.unlockCanvasAndPost(canvas)
        }
    }

    fun sleep() {
        try {
            Thread.sleep(17)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun resume() {
        isPlaying = true
        thread = Thread(this)
        thread.start()
    }

    fun pause() {
        try {
            isPlaying = false
            thread.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                tapX1 = event.x
                tapY1 = event.y
                isShooting = true
            }
            MotionEvent.ACTION_MOVE -> {
                tapX1 = event.x
                tapY1 = event.y
            }
            MotionEvent.ACTION_UP -> {
                isShooting = false
                shootingFrames = 0
            }
        }
        return true
    }
}