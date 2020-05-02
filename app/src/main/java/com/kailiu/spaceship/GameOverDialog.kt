package com.kailiu.spaceship

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_gameover.*

class GameOverDialog(var width: Int, var height: Int, var pts: Int, var activity: Activity): DialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_gameover, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#11FFFFFF")));

        dialogFrame.minWidth = (width * 0.9).toInt()
        dialogFrame.minHeight = (height * 0.9).toInt()

        score.text = "$pts"

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