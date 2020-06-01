package com.kailiu.spaceship

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat


fun TextView.animateTextColor(color1: Int = R.color.red, color2: Int = R.color.blue, duration:Long = 200) {
    val colorFrom = resources.getColor(color1)
    val colorTo = resources.getColor(color2)
    val startValue = 0
    val endValue = text.length

    val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo, colorFrom)
    colorAnimation.duration = 1000

    colorAnimation.addUpdateListener { animator -> this.setTextColor(animator.animatedValue as Int) }
    colorAnimation.repeatCount = ValueAnimator.INFINITE
    colorAnimation.start()
}
lateinit var colorAnimation: ValueAnimator
fun ImageView.startAnimateBorderColor(color1: Int = R.color.score, color2: Int = R.color.dialog, duration:Long = 200) {
    val colorFrom = resources.getColor(color1)
    val colorTo = resources.getColor(color2)
    colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo, colorFrom)
    colorAnimation.duration = 5000

    val colorA = resources.getColor(R.color.score)
    val colorB = resources.getColor(R.color.dialog)

    val gradientDrawable = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(colorA, colorB))
    gradientDrawable.cornerRadius = 12f
    gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT
    setImageDrawable(gradientDrawable)

    colorAnimation.addUpdateListener { animator ->
        gradientDrawable.colors = intArrayOf(animator.animatedValue as Int, animator.animatedValue as Int + 100)
        setImageDrawable(gradientDrawable)
    }

    colorAnimation.repeatCount = ValueAnimator.INFINITE
    colorAnimation.start()
}

fun ImageView.stopAnimateBorderColor() {
    val gradientDrawable = resources.getDrawable(R.drawable.ic_menu_button)
    setImageDrawable(gradientDrawable)
    colorAnimation.cancel()
}

fun TextView.toggleText(state: Boolean? = null, textPos: String = "on", textNeg: String = "off") {
    var bool= state ?: (text == textPos)
    text = if (bool) {
        isPressed = true
        setTextColor(resources.getColor(R.color.settings_toggle))
        textNeg
    } else {
        isPressed = false
        setTextColor(resources.getColor(R.color.settings))
        textPos
    }
}

abstract class OnTouchAndClickListener: View.OnTouchListener {
    lateinit var rect: Rect

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event != null && v != null) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    setPressed(v, true)
                    rect = Rect(v.left, v.top, v.right, v.bottom)
                }
                MotionEvent.ACTION_UP -> {
                    if (rect.contains((v.left + event.x).toInt(), (v.top + event.y).toInt())) {
                        v.performClick()
                        onClick(v)
                    }
                    setPressed(v, false)
                }
                MotionEvent.ACTION_CANCEL -> {
                    setPressed(v, false)
                }
            }
        }
        return true
    }

    abstract fun setPressed(v: View, status: Boolean)

    abstract fun onClick(v: View)
}