package com.kailiu.spaceship.ui.pickups

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.kailiu.spaceship.R
import com.kailiu.spaceship.ui.hitboxes.Oval

class Life(res: Resources): Pickups(res) {
    var isEmpty = false

    init {

        pickup = BitmapFactory.decodeResource(res, R.drawable.life)

        width = pickup.width.toDouble()
        height = pickup.height.toDouble()

        width /= 15
        height /= 15

        //width *= GameView.screenRatioX
        //height *= GameView.screenRatioY

        pickup = Bitmap.createScaledBitmap(pickup, width.toInt(), height.toInt(), false)
    }

    fun setStatus(health: Int, index: Int) {
        val checkEmpty = health < index
        if (isEmpty == checkEmpty) return else isEmpty = checkEmpty
        val img = if (!checkEmpty)  R.drawable.life else R.drawable.life_empty


        pickup = BitmapFactory.decodeResource(res, img)
        pickup = Bitmap.createScaledBitmap(pickup, width.toInt(), height.toInt(), false)
    }

    override fun getCollisionShape(): Oval {
        return Oval(x + width.toInt() / 2, y + height.toInt() / 2, width.toInt() / 2,height.toInt() / 2)
    }
}