package com.kailiu.spaceship.ui.hitboxes

import android.graphics.Rect

class Triangle(var p1x: Int, var p1y: Int, var p2x: Int, var p2y: Int, var p3x: Int, var p3y: Int) {

    constructor(triangle: Triangle): this(triangle.p1x, triangle.p1y, triangle.p2x, triangle.p2y, triangle.p3x, triangle.p3y)

    init {

    }

    fun intersects(rect: Rect): Boolean {
        /*val left = yAxis / xAxis * sqrt(xAxis * xAxis - rect.left * rect.left - x * x + 2.0 * rect.left * x)
        if (!left.isNaN() && y - left >= rect.top) return true

        val right= yAxis / xAxis * sqrt(xAxis * xAxis - rect.right * rect.right - x * x + 2.0 * rect.right * x)
        if (!right.isNaN() &&  y - right >= rect.top) return true*/

        return false
    }
}