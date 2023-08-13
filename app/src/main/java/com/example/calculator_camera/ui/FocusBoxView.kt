package com.example.calculator_camera.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View


class FocusBoxView(
    context: Context,
    left: Int,
    right: Int,
    top: Int,
    bottom: Int
) : View(context) {
    private var paint: Paint? = null
    private var rect: Rect? = null

    init {
        paint = Paint()
        rect = Rect(left, top, right, bottom)
    }

    /**
     * getRect - get right, left, top and bottom points of the rectangle
     * @return the right, left, top and bottom points of the rectangle
     */
    fun getRect(): Rect? {
        return rect
    }

    /**
     * Draw the light gray rectangle.
     * @param canvas
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint?.style = Paint.Style.STROKE
        paint?.color = Color.LTGRAY
        paint?.strokeWidth = 2f
        if (rect != null && paint != null) {
            canvas.drawRect(rect!!, paint!!)
        }
    }
}