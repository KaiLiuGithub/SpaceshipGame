package com.kailiu.spaceship.ui

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.kailiu.spaceship.R

class Background() {
    lateinit var background: Bitmap
    var x = 0
    var y = 0

    constructor(x: Int = 0, y: Int = 0, res: Resources): this() {
        background = BitmapFactory.decodeResource(res,
            R.drawable.background
        )
        background = Bitmap.createScaledBitmap(background, x, y, false)
    }
}