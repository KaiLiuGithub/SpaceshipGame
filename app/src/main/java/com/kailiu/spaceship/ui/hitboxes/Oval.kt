package com.kailiu.spaceship.ui.hitboxes

import android.graphics.Rect
import kotlin.math.sqrt

class Oval(var x: Int = 0, var y: Int = 0, var xAxis: Int = 0, var yAxis: Int = 0) {

    constructor(oval: Oval): this(oval.x, oval.y, oval.xAxis, oval.yAxis)

    init {

    }

    fun intersects(rect: Rect): Boolean {
        val left = yAxis / xAxis * sqrt(xAxis * xAxis - rect.left * rect.left - x * x + 2.0 * rect.left * x)
        if (!left.isNaN() && y + left >= rect.top && y - left <= rect.top) return true

        val avg = rect.left + (rect.right - rect.left)/2
        val mid = yAxis / xAxis * sqrt(xAxis * xAxis - avg * avg - x * x + 2.0 * avg * x)
        if (!mid.isNaN() && y + mid >= rect.top && y - mid <= rect.top) return true

        val right= yAxis / xAxis * sqrt(xAxis * xAxis - rect.right * rect.right - x * x + 2.0 * rect.right * x)
        if (!right.isNaN() && y + right >= rect.top && y - right <= rect.top) return true

        return false
    }

    fun intersects(triangle: Triangle): Boolean {
        val c1 = triangle.p1y - (triangle.p1y - triangle.p2y) / (triangle.p1x - triangle.p2x) * triangle.p1x
        val m1 = (triangle.p1y - triangle.p2y) / (triangle.p1x - triangle.p2x)
        val x1 = xAxis * yAxis * sqrt(yAxis * yAxis + xAxis * xAxis * m1 * m1 - 2.0 * m1 * (c1 - y) * x - (c1 - y) * (c1 - y) - m1 * m1 * x * x)
        val y1 = xAxis * yAxis * m1 * sqrt(yAxis * yAxis + xAxis * xAxis * m1 * m1 + 2.0 * (c1 + m1 * x) * y - y * y - (c1 + m1 * y) * (c1 + m1 * y))
        val dx1 = (yAxis * yAxis * x - xAxis * xAxis * m1 * (c1 - y))
        val dy1 = (yAxis * yAxis * m1 * (c1 - m1 * x) + xAxis * xAxis * m1 * m1 * y)
        val div1 = yAxis * yAxis + xAxis * xAxis * m1 * m1
        if (!x1.isNaN() || !y1.isNaN()) println("${(dx1 + x1) / div1}, ${(dy1 + y1) / div1} | ${(dx1 - x1) / div1}, ${(dy1 - y1) / div1}")
        if (!x1.isNaN() && !y1.isNaN()) return true
        
        val c2 = triangle.p1y - (triangle.p1y - triangle.p3y) / (triangle.p2x - triangle.p3x) * triangle.p2x
        val m2 = (triangle.p1y - triangle.p3y) / (triangle.p1x - triangle.p3x)
        val x2 = xAxis * yAxis * sqrt(yAxis * yAxis + xAxis * xAxis * m2 * m2 - 2.0 * m2 * (c2 - y) * x - (c2 - y) * (c2 - y) - m2 * m2 * x * x)
        val y2 = xAxis * yAxis * m2 * sqrt(yAxis * yAxis + xAxis * xAxis * m2 * m2 + 2.0 * (c2 + m2 * x) * y - y * y - (c2 + m2 * y) * (c2 + m2 * y))
        val dx2 = (yAxis * yAxis * x - xAxis * xAxis * m2 * (c2 - y))
        val dy2 = (yAxis * yAxis * m2 * (c2 - m2 * x) + xAxis * xAxis * m2 * m2 * y)
        if (!x2.isNaN() || !y2.isNaN()) println("${dx2 + x2}, ${dy2 + y2} | ${dx2 - x2}, ${dy2 - y2}")
        if (!x2.isNaN() && !y2.isNaN()) return true
        
        return false
    }
}