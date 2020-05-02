package com.kailiu.spaceship

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.LinearGradient
import android.graphics.Shader
import android.widget.TextView


fun TextView.animateColor(color1: Int = R.color.red, color2: Int = R.color.blue, duration:Long = 200) {
    val colorFrom = resources.getColor(color1)
    val colorTo = resources.getColor(color2)
    val startValue = 0
    val endValue = text.length

    /*val width = paint.measureText("Tianjin, China")

    val textShader: Shader = LinearGradient(
        0f, 0f, width, textSize, intArrayOf(
            colorFrom, colorTo
        ), null, Shader.TileMode.CLAMP
    )
    paint.shader = textShader*/


    val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo, colorFrom)
    colorAnimation.duration = 1000 // milliseconds

    colorAnimation.addUpdateListener { animator -> this.setTextColor(animator.animatedValue as Int) }
    colorAnimation.repeatCount = ValueAnimator.INFINITE
    colorAnimation.start()
}