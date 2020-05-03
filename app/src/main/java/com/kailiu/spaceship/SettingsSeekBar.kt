package com.kailiu.spaceship

import android.content.Context
import android.graphics.*
import android.graphics.Bitmap.Config
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.core.graphics.drawable.toBitmap


class SettingsSeekBar(context: Context, attrs: AttributeSet): AppCompatSeekBar(context, attrs) {

    private var offsetBmp: Bitmap? = null
    private var frameBmp: Bitmap? = null
    private var barBmp: Bitmap
    private var thumbBmp: Bitmap? = null

    private var paint: Paint
    private var textStartPaint: Paint
    private var textEndPaint: Paint

    init {
        barBmp = drawableToBitmap(resources.getDrawable(R.drawable.ic_seekbar, context.theme), 300, 50)!!
        paint = Paint()
        textStartPaint = Paint()
        textStartPaint.textSize = 15f * resources.displayMetrics.density
        textEndPaint = Paint()
        textEndPaint.textSize = 15f * resources.displayMetrics.density
        textEndPaint.textAlign = Paint.Align.RIGHT
    }

    private fun drawableToBitmap(drawable: Drawable, w: Int, h: Int): Bitmap? {
        return drawable.toBitmap(w, h, Config.ARGB_8888)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = SeekBar.resolveSizeAndState(minw, widthMeasureSpec, 1)

        val minh: Int = MeasureSpec.getSize(w) + paddingBottom + paddingTop
        val h: Int = View.resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )

        setMeasuredDimension(w, h)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (thumbBmp == null) thumbBmp = drawableToBitmap(resources.getDrawable(R.drawable.ic_seekbar_thumb, context.theme), height - paddingTop - paddingBottom, height - paddingTop - paddingBottom)!!
        if (frameBmp == null) {
            val width = width - paddingStart - paddingEnd + thumbBmp!!.width
            frameBmp = drawableToBitmap(resources.getDrawable(R.drawable.ic_seekbar_frame, context.theme), width, height - paddingTop - paddingBottom)!!
        }
        if (offsetBmp == null) {
            offsetBmp = drawableToBitmap(resources.getDrawable(R.drawable.ic_seekbar, context.theme), height - paddingTop - paddingBottom, height - paddingTop - paddingBottom)!!
        }

        barBmp = drawableToBitmap(resources.getDrawable(R.drawable.ic_seekbar, context.theme), (width - paddingStart - paddingEnd) * (progress + 1) / 100 + thumbBmp!!.width * 9 / 10, height - paddingTop - paddingBottom)!!
        canvas.apply {
            drawBitmap(frameBmp!!, paddingStart.toFloat() - thumbBmp!!.width / 2, paddingTop.toFloat(), paint)
            drawBitmap(offsetBmp!!, paddingStart.toFloat() - thumbBmp!!.width / 2, paddingTop.toFloat(), paint)
            if (progress > 5) {
                drawBitmap(barBmp, paddingStart.toFloat() - thumbBmp!!.width / 2, paddingTop.toFloat(), paint)
            }
            drawBitmap(thumbBmp!!, (width - paddingStart - paddingEnd + 10) * progress / 100f + paddingStart.toFloat() - thumbBmp!!.width / 2, paddingTop.toFloat(), paint)

            val alpha = (progress / 100.0 * 255).toInt()
            var alpha1 = alpha.toString(16)
            var alpha2 = (255 - alpha).toString(16)
            if (alpha1.length < 2) alpha1 = "0$alpha1"
            if (alpha2.length < 2) alpha2 = "0$alpha2"

            textEndPaint.color = Color.parseColor("#${alpha2}F27DFD")
            drawText("$progress%", frameBmp!!.width.toFloat() - thumbBmp!!.width / 2, paddingTop.toFloat() + thumbBmp!!.width * 3 / 4, textEndPaint)

            textStartPaint.color = Color.parseColor("#${alpha1}000000")
            drawText("$progress%", paddingStart.toFloat(), paddingTop.toFloat() + thumbBmp!!.width * 3 / 4, textStartPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        invalidate()
        return super.onTouchEvent(event)
    }
}