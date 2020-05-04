package com.kailiu.spaceship

import android.content.DialogInterface
import android.graphics.Point
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.kailiu.spaceship.database.ScoreRepository
import com.kailiu.spaceship.dialog.GameOverDialog
import javax.inject.Inject
import kotlin.concurrent.thread
import kotlin.math.ln


class GameActivity: AppCompatActivity() {

    @Inject
    lateinit var scoreRepository: ScoreRepository

    @Inject
    lateinit var settingsSharedPreferences: SettingsSharedPreferences

    lateinit var gameView: GameView

    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (applicationContext as SpaceshipApp).appComponent.inject(this)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        val point = Point()
        windowManager.defaultDisplay.getSize(point)

        gameView = GameView(this, point.x, point.y)

        mediaPlayer = MediaPlayer.create(applicationContext, R.raw.music_brahams)
        val volume = ln(settingsSharedPreferences.getMusicVolume() / 100f)
        mediaPlayer.setVolume(volume, volume)
        mediaPlayer.isLooping = true
        if (settingsSharedPreferences.getMusicVolume() != 0f) {
            mediaPlayer.start()
        }

        setContentView(gameView)

        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        val prev: Fragment? = supportFragmentManager.findFragmentByTag("dialog")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)

        thread {
            while (!gameView.isGameOver) {}

            val dialogFragment: DialogFragment =
                GameOverDialog(
                    point.x,
                    point.y,
                    gameView.score,
                    this
                )

            mediaPlayer.stop()

            Handler(Looper.getMainLooper()).post {
                dialogFragment.show(ft, "dialog_gameover")
            }
        }
    }

    override fun onResume() {
        super.onResume()

        gameView.resume()
        if (settingsSharedPreferences.getMusicVolume() != 0f) {
            mediaPlayer.start()
        }
    }

    override fun onPause() {
        super.onPause()

        gameView.pause()
        mediaPlayer.pause()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            onResume()
        } else {
            onPause()
        }
    }

    override fun onStop() {
        super.onStop()

        mediaPlayer.stop()
    }

    override fun onDestroy() {
        super.onDestroy()

        mediaPlayer.stop()
    }
}

interface DialogCloseListener {
    fun handleDialogClose(dialog: DialogInterface?)
}