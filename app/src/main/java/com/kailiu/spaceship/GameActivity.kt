package com.kailiu.spaceship

import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import kotlin.concurrent.thread

class GameActivity: AppCompatActivity() {

    lateinit var gameView: GameView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        val point = Point()
        windowManager.defaultDisplay.getSize(point)

        gameView = GameView(this, point.x, point.y)

        setContentView(gameView)

        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        val prev: Fragment? = supportFragmentManager.findFragmentByTag("dialog")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)

        thread {
            while (!gameView.isGameOver) {}

            val dialogFragment: DialogFragment = GameOverDialog(point.x, point.x, gameView.score, this)

            Handler(Looper.getMainLooper()).post {
                dialogFragment.show(ft, "dialog")
            }
        }
    }

    override fun onResume() {
        super.onResume()

        gameView.resume()
    }

    override fun onPause() {
        super.onPause()

        gameView.pause()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            onResume()
        } else {
            onPause()
        }
    }
}