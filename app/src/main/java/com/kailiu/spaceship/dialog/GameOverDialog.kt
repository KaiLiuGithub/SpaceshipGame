package com.kailiu.spaceship.dialog

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import com.kailiu.spaceship.GameActivity
import com.kailiu.spaceship.DialogCloseListener
import com.kailiu.spaceship.R
import com.kailiu.spaceship.SpaceshipApp
import com.kailiu.spaceship.database.Score
import com.kailiu.spaceship.database.ScoreRepository
import kotlinx.android.synthetic.main.dialog_gameover.*
import javax.inject.Inject


class GameOverDialog(var width: Int, var height: Int, var pts: Int, var activity: Activity): DialogFragment() {
    var closeListener: DialogCloseListener = object : DialogCloseListener {
        override fun handleDialogClose(dialog: DialogInterface?) {
            gameoverFrame.visibility = View.VISIBLE
        }
    }

    @Inject
    lateinit var scoreRepository: ScoreRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (activity.applicationContext as SpaceshipApp).appComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_gameover, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#11FFFFFF")))
        dialog?.setCanceledOnTouchOutside(false)

        gameoverFrame.minWidth = (width * 0.9).toInt()
        gameoverFrame.minHeight = (width * 0.9).toInt()

        score.text = "$pts"

        scoreRepository.getScores().observe(this, Observer { scores ->
            scores?.let {
                val list = scores.sortedWith(compareBy({ it.score }, { it.time }, { it.name })).toMutableList()

                if (list.size > 0) {
                    if (pts > list[list.size - 1].score) highscore.visibility = View.VISIBLE
                } else {
                    highscore.visibility = View.VISIBLE
                }
            }
        })

        leaderboardBtn.setOnClickListener {
            val ft: FragmentTransaction = requireFragmentManager().beginTransaction()
            val prev: Fragment? = requireFragmentManager().findFragmentByTag("dialog")
            if (prev != null) {
                ft.remove(prev)
            }
            ft.addToBackStack(null)

            val dialog = LeaderboardDialog(width, height, activity)
            dialog.show(ft, "dialog_leaderboard")
            gameoverFrame.visibility = View.GONE

            dialog.setDismissListener(closeListener)
        }

        saveBtn.setOnClickListener {
            scoreRepository.addScore(Score(name = nameField.text.toString(), score = pts))
            saveBtn.isClickable = false
        }

        posBtn.setOnClickListener {
            val myIntent = Intent(context, GameActivity::class.java)
            this.startActivity(myIntent)
            (context as Activity).finish()
        }

        negBtn.setOnClickListener {
            activity.finish()
        }
    }
}