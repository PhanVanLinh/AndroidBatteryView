package vn.linh.androidbatteryview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.PaintDrawable
import android.support.v7.content.res.AppCompatResources
import android.util.AttributeSet
import android.view.View

class BatteryView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {
    private var radius: Float = 0f
    private var isCharging: Boolean = false

    // Top
    private var topPaint =
        PaintDrawable(Color.WHITE) // I only want to corner top-left and top-right so I use PaintDrawable instead of Paint
    private var topRect = Rect()
    private var topPaintWidthPercent = 50
    private var topPaintHeightPercent = 8

    // Border
    private var borderPaint = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.STROKE
    }
    private var borderRect = RectF()
    private var borderStrokeWidthPercent = 8
    private var borderStroke: Float = 0f

    // Percent
    private var percentPaint = Paint()
    private var percentRect = RectF()
    private var percentRectTopMin = 0f
    private var percent: Int = 0

    // Charging
    private var chargingRect = RectF()
    private var chargingBitmap: Bitmap? = null

    init {
        init(attrs)
        chargingBitmap = getBitmap(R.drawable.ic_charging)
    }

    private fun init(attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.BatteryView)
        try {
            percent = ta.getInt(R.styleable.BatteryView_bv_percent, 0)
            isCharging = ta.getBoolean(R.styleable.BatteryView_bv_charging, false)
        } finally {
            ta.recycle()
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val measureWidth = View.getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        val measureHeight = (measureWidth * 1.8f).toInt()
        setMeasuredDimension(measureWidth, measureHeight)

        radius = borderStroke / 2
        borderStroke = (borderStrokeWidthPercent * measureWidth).toFloat() / 100

        // Top
        val topLeft = measureWidth * ((100 - topPaintWidthPercent) / 2) / 100
        val topRight = measureWidth - topLeft
        val topBottom = topPaintHeightPercent * measureHeight / 100
        topRect = Rect(topLeft, 0, topRight, topBottom)

        // Border
        val borderLeft = borderStroke / 2
        val borderTop = topBottom.toFloat() + borderStroke / 2
        val borderRight = measureWidth - borderStroke / 2
        val borderBottom = measureHeight - borderStroke / 2
        borderRect = RectF(borderLeft, borderTop, borderRight, borderBottom)

        // Progress
        val progressLeft = borderStroke
        percentRectTopMin = topBottom + borderStroke
        val progressRight = measureWidth - borderStroke
        val progressBottom = measureHeight - borderStroke
        percentRect = RectF(progressLeft, percentRectTopMin, progressRight, progressBottom)

        // Charging Image
        val chargingLeft = borderStroke
        var chargingTop = topBottom + borderStroke
        val chargingRight = measureWidth - borderStroke
        var chargingBottom = measureHeight - borderStroke
        val diff = ((chargingBottom - chargingTop) - (chargingRight - chargingLeft))
        chargingTop += diff / 2
        chargingBottom -= diff / 2
        chargingRect = RectF(chargingLeft, chargingTop, chargingRight, chargingBottom)
    }

    override fun onDraw(canvas: Canvas) {
        drawTop(canvas)
        drawBody(canvas)
        if (!isCharging) {
            drawProgress(canvas, percent)
        } else {
            drawCharging(canvas)
        }
    }

    private fun drawTop(canvas: Canvas) {
        topPaint.bounds = topRect
        topPaint.setCornerRadii(floatArrayOf(radius, radius, radius, radius, 0f, 0f, 0f, 0f))
        topPaint.draw(canvas)
    }

    private fun drawBody(canvas: Canvas) {
        borderPaint.strokeWidth = borderStroke
        canvas.drawRoundRect(borderRect, radius, radius, borderPaint)
    }

    private fun drawProgress(canvas: Canvas, percent: Int) {
        percentPaint.color = getPercentColor(percent)
        percentRect.top = percentRectTopMin + (percentRect.bottom - percentRectTopMin) * (100 - percent) / 100
        canvas.drawRect(percentRect, percentPaint)
    }

    // todo change color
    private fun getPercentColor(percent: Int): Int {
        if (percent > 50) {
            return Color.WHITE
        }
        if (percent > 30) {
            return Color.YELLOW
        }
        return Color.RED
    }

    private fun drawCharging(canvas: Canvas) {
        chargingBitmap?.let {
            canvas.drawBitmap(it, null, chargingRect, null)
        }
    }

    private fun getBitmap(drawableId: Int, desireWidth: Int? = null, desireHeight: Int? = null): Bitmap? {
        val drawable = AppCompatResources.getDrawable(context, drawableId) ?: return null
        val bitmap = Bitmap.createBitmap(
            desireWidth ?: drawable.intrinsicWidth,
            desireHeight ?: drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    fun charge() {
        isCharging = true
        invalidate() // can improve by invalidate(Rect)
    }

    fun unCharge() {
        isCharging = false
        invalidate()
    }

    fun setPercent(percent: Int) {
        if (percent > 100 || percent < 0) {
            return
        }
        this.percent = percent
        invalidate()
    }

    fun getPercent(): Int {
        return percent
    }
}
