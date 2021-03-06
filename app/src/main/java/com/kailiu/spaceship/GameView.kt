package com.kailiu.spaceship

import android.content.Context
import android.graphics.*
import android.media.AudioManager
import android.media.SoundPool
import android.view.MotionEvent
import android.view.SurfaceView
import com.kailiu.spaceship.ui.Background
import com.kailiu.spaceship.ui.Bullet
import com.kailiu.spaceship.ui.Rocket
import com.kailiu.spaceship.ui.enemies.Asteroid
import com.kailiu.spaceship.ui.enemies.Enemy
import com.kailiu.spaceship.ui.enemies.Satellite
import com.kailiu.spaceship.ui.enemies.Saucer
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

    private var isShooting = false
    private var frames = 0
    private var shootingFrames = 0
    private var enemyTotal = 0

    private var paint: Paint
    private var paintText: Paint
    private var paintGuidlines: Paint
    private var paintInfo: Paint
    private var paintBullets: Paint
    private var paintEnemyBullets: Paint

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
        paintText = Paint().apply {
            color = Color.YELLOW
            textSize = 25f * resources.displayMetrics.density
        }
        paintGuidlines = Paint().apply {
            color = Color.TRANSPARENT
        }
        paintInfo = Paint().apply {
            color = Color.parseColor("#22000000")
        }
        paintBullets = Paint().apply {
            color = Color.MAGENTA
            strokeWidth = 3f
            style = Paint.Style.FILL_AND_STROKE
            shader = LinearGradient(0f, 0f, 0f, 0f, Color.MAGENTA, Color.MAGENTA, Shader.TileMode.MIRROR)
        }
        val colors = arrayOf(Color.YELLOW, Color.RED, Color.YELLOW).toIntArray()
        val positions = arrayOf(0f, 0.3f, 0.6f).toFloatArray()
        paintEnemyBullets = Paint().apply {
            color = Color.YELLOW
            strokeWidth = 3f
            style = Paint.Style.FILL_AND_STROKE
            shader = LinearGradient(0f, 0f, 0f, 0f, colors, positions, Shader.TileMode.CLAMP)
        }

        screenRatioX = 1920f / x
        screenRatioY = 1080f / y

        rocket.resetPosition(x, y)

        soundPool = SoundPool(12, AudioManager.STREAM_MUSIC, 0)

        Enemy.getSound(soundPool!!, context, false)
        Rocket.getSound(soundPool!!, context)

        for (i in 0 until 6) {
            //val enemy = Asteroid(resources)
            val enemy = Satellite(resources)
            enemy.x = Random.nextInt(0, x - enemy.width.toInt())
            enemy.y -= enemy.height.toInt()
            enemies.add(enemy)
            enemyTotal++
        }

        info.add(Life(resources).apply {
            this.x = INFO_OFFSET
            this.y = INFO_OFFSET
        })
        info.add(Life(resources).apply {
            this.x = INFO_OFFSET + INFO_OFFSET / 2 + this.width.toInt()
            this.y = INFO_OFFSET
        })
        info.add(Life(resources).apply {
            this.x = INFO_OFFSET + INFO_OFFSET + 2 * this.width.toInt()
            this.y = INFO_OFFSET
        })
        info.add(Shield(resources).apply {
            this.x = this@GameView.x - 2 * this.width.toInt() - INFO_OFFSET
            this.y = INFO_OFFSET
        })
        info.add(Ammo(resources).apply {
            this.x = this@GameView.x - this.width.toInt() - INFO_OFFSET
            this.y = INFO_OFFSET
        })

        guideLines.add(Rect(0, 0, x, 5))

        infoBox = Rect(
            INFO_OFFSET,
            INFO_OFFSET, x, info[0].height.toInt() + INFO_OFFSET
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
                if (Rect.intersects(bullets[i].getCollisionShape(), guideLines[0])) {
                    bullets.removeAt(i)
                } else {
                    bullets[i].move()
                    var j = 0
                    while (j < enemies.size) {
                        if (enemies[j].getCollisionShape().intersects(bullets[i].getCollisionShape())) {
                            enemies[j].hp--
                            if (enemies[j].hp <= 0) {
                                enemies.removeAt(j)
                                score++
                            }
                            bullets.removeAt(i)
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
                enemyTotal++
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
                            rocket.editHealth(frames, false)
                        }
                    }
                } else {
                    enemies[i].move(frame = frames, fieldW = x)
                    enemies[i].animate(frames)
                    if (enemies[i].bullets.size < enemies[i].maxBullets) {

                        if (enemies[i].addBullet(frames, y) && settingsSharedPreferences.getSoundEffects()) {
                            soundPool?.play(Enemy.getSound(soundPool!!, context, enemies[i] is Saucer), 1f, 1f, 0, 0, 1f)
                        }
                    }
                    var j = 0
                    while (j in 0 until enemies[i].bullets.size) {
                        if (enemies.size <= i) break
                        if (Rect.intersects(enemies[i].bullets[j].bullet, rocket.getCollisionShape())) {
                            if (rocket.pickups[0]) {
                                rocket.pickups[0] = false
                            } else {
                                if (rocket.pickups[1]) {
                                    rocket.pickups[1] = false
                                }
                                rocket.editHealth(frames, false)
                            }
                            enemies[i].bullets.removeAt(j)
                            j--
                        } else {
                            if (!enemies[i].bullets[j].move(isRocket = false, frame = frames)) {
                                enemies[i].bullets.removeAt(j)
                            } else {
                                j++
                            }
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
                            else -> if (rocket.health < 3) rocket.editHealth()
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
                if (shootingFrames % 7 == 0) {
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
                        soundPool?.play(Rocket.getSound(soundPool!!, context), 1f, 1f, 0, 0, 1f)
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
            canvas.drawRect(guideLines[0], paintGuidlines)

            canvas.drawRect(infoBox, paintInfo)

            // Bullets
            while (!bulletsSemaphore.tryAcquire()) {}

            for (bullet in rocket.bullets) {
                canvas.drawRect(bullet.bullet, paintBullets)
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
                    val r = (bullet.right - bullet.left) / 2f
                    canvas.drawRect(bullet.bullet, paintEnemyBullets)
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
            val str = resources.getString(R.string.score_info, score)
            canvas.drawText(str, (x - paint.measureText(str)) / 2f, infoBox.exactCenterY(), paintText)

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
            }
        }
        return true
    }
}