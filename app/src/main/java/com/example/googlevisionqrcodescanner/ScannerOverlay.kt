package com.example.googlevisionqrcodescanner

import android.content.Context
import android.graphics.*
import android.graphics.Color.blue
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import kotlin.math.roundToInt

class ScannerOverlay : ViewGroup {
    private var left = 0f
    private var top = 0f
    private var endY = 0f
    private var boxWidth = 0
    private var boxHeight = 0
    private var frames = 0
    private var revAnimation = false
    private var barColor = 0
    private var barWidth = 0
    private var paused = false

    private val line = Paint().apply {
        color = barColor
        strokeWidth = barWidth.toFloat()
    }

    private val cornerLine = Paint().apply {
        color = ContextCompat.getColor(context, R.color.blue)
        strokeWidth = barWidth.toFloat()
    }

    constructor(context: Context) : super(context) {
        setWillNotDraw(false)
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int = 0) : super(context, attrs, defStyle) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.ScannerOverlay, 0, 0)
        boxWidth = a.getInteger(R.styleable.ScannerOverlay_square_width, 270)
        boxHeight = a.getInteger(R.styleable.ScannerOverlay_square_height, 270)
        barColor = a.getColor(R.styleable.ScannerOverlay_line_color, Color.parseColor("#ffffff"))
        barWidth = a.getInteger(R.styleable.ScannerOverlay_line_width, 4)
        frames = a.getInteger(R.styleable.ScannerOverlay_line_speed, 5)
        a.recycle()

        line.color = barColor
        line.strokeWidth = barWidth.toFloat()
        cornerLine.strokeWidth = barWidth.toFloat()

        setWillNotDraw(false)
    }

    fun setBoxWidth(width: Int) {
        boxWidth = width
    }

    fun setBoxHeight(height: Int) {
        boxHeight = height
    }

    fun setBarColor(@ColorInt color: Int) {
        barColor = color
        line.color = barColor
    }

    fun setBarThickness(thickness: Int) {
        barWidth = thickness
        line.strokeWidth = barWidth.toFloat()
        cornerLine.strokeWidth = barWidth.toFloat()
    }

    fun setBarSpeed(frames: Int) {
        this.frames = frames
    }

    fun setPaused(paused: Boolean) {
        this.paused = paused
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
    }

    public override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        left = (w - dpToPx(boxWidth)) / 2.toFloat()
        top = (h - dpToPx(boxHeight)) / 2.toFloat()
        endY = top
        super.onSizeChanged(w, h, oldw, oldh)
    }

    private fun dpToPx(dp: Int): Int {
        val displayMetrics = resources.displayMetrics
        return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }

    override fun shouldDelayChildPressedState(): Boolean = false

    private val eraser = Paint().apply {
        isAntiAlias = true
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    private val rect = RectF()

    private fun drawCornerLines(canvas: Canvas, width: Int) {
//        ----  <- (1)        ----  <- (2)
// (5) -> |                      |  <- (7)
//
//                   QR
//
// (6) -> |                      |  <- (8)
//        ---- <- (3)         ----  <- (4)

        canvas.drawLine(left, top, left + width, top, cornerLine)    // (1)
        canvas.drawLine((dpToPx(boxWidth) + left - width), top, dpToPx(boxWidth) + left, top, cornerLine)    // (2)
        canvas.drawLine(left, dpToPx(boxHeight) + top, left + width, dpToPx(boxHeight) + top, cornerLine)    // (3)
        canvas.drawLine((dpToPx(boxWidth) + left - width), dpToPx(boxHeight) + top , dpToPx(boxWidth) + left, dpToPx(boxHeight) + top , cornerLine)    // (4)

        canvas.drawLine(left, top, left, top + width, cornerLine) // (5)
        canvas.drawLine(left, (dpToPx(boxHeight) + top) - width, left, (dpToPx(boxHeight) + top), cornerLine) // (6)
        canvas.drawLine(dpToPx(boxWidth) + left , top, dpToPx(boxWidth) + left , top + width, cornerLine) // (7)
        canvas.drawLine(dpToPx(boxWidth) + left, (dpToPx(boxHeight) + top) - width, dpToPx(boxWidth) + left, (dpToPx(boxHeight) + top), cornerLine) // (8)

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val cornerRadius = 0

        val rect = rect.apply {
            left = this@ScannerOverlay.left
            top = this@ScannerOverlay.top
            right = dpToPx(boxWidth) + left
            bottom = dpToPx(boxHeight) + top
        }

        canvas.drawRoundRect(
            rect,
            cornerRadius.toFloat(),
            cornerRadius.toFloat(),
            eraser
        )

        // draw the line to product animation
        if (endY >= top + dpToPx(boxHeight) + frames) {
            revAnimation = true
        } else if (endY == top + frames) {
            revAnimation = false
        }

        if (!paused) {
            // check if the line has reached to bottom
            if (revAnimation) {
                endY -= frames.toFloat()
            } else {
                endY += frames.toFloat()
            }
        }

        drawCornerLines(canvas, dpToPx(20))
        canvas.drawLine(left, endY, left + dpToPx(boxWidth), endY, line)
        invalidate()
    }
}