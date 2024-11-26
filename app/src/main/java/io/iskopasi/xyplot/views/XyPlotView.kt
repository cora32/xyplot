package io.iskopasi.xyplot.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import io.iskopasi.xyplot.R
import io.iskopasi.xyplot.pojo.MinMaxYValue
import io.iskopasi.xyplot.room.PointsEntity
import io.iskopasi.xyplot.spToPx
import kotlin.math.abs

data class XyPlotValue(
    val data: List<PointsEntity> = emptyList<PointsEntity>(),
    val minMaxY: MinMaxYValue = MinMaxYValue(0f, 0f)
)

class XyPlotView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ScalingView(context, attrs, defStyleAttr, defStyleRes) {
    var data: XyPlotValue = XyPlotValue()
        set(value) {
            field = value
//            field = XyPlotValue(
//                listOf(
//                    PointsEntity(x = 0f, y= 0f, ),
//                    PointsEntity(x = 1f, y= 5f, ),
//                    PointsEntity(x = 2f, y= 0f, ),
//                ),
//                MinMaxYValue(0f, 5f)
//            )

            recalculateFactors()
            invalidate()
        }
    private val paint by lazy {
        Paint().apply {
            style = Paint.Style.STROKE
            color = ResourcesCompat.getColor(resources, R.color.color_1, null)
            strokeCap = Paint.Cap.ROUND
            strokeWidth = 2f
            isAntiAlias = true
            textSize = context.spToPx(15)
            isDither = true
            isAntiAlias = true
        }
    }
    private val paintThin by lazy {
        Paint().apply {
            style = Paint.Style.STROKE
            color = ResourcesCompat.getColor(resources, R.color.color_1, null)
            strokeCap = Paint.Cap.ROUND
            strokeWidth = 0.5f
            isAntiAlias = true
            isDither = true
            isAntiAlias = true
        }
    }
    private val path = Path()
    private var xFactor = 0f
    private var yFactor = 0f
    private var yCenter = 0f
    private var initialXOffset = 0f

    private fun recalculateFactors() {
        val lastX = abs(data.data.lastOrNull()?.x ?: 0f)
        val firstX = abs(data.data.firstOrNull()?.x ?: 0f)

        initialXOffset = firstX
        xFactor = width / abs(lastX)
        yFactor = (height / 2f) / (abs(data.minMaxY.max) + abs(data.minMaxY.min))
        yCenter = height / 2f
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        setMeasuredDimension(width, height)

        // If view size changed after data was set, recalculate factors again
        recalculateFactors()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        recalculateFactors()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (data.data.isEmpty()) return

        // Draw 0-line
        val lastX = abs((data.data.lastOrNull()?.x ?: 0f) + initialXOffset) * xFactor
        canvas.drawLine(0f, yCenter, lastX, yCenter, paintThin)

        drawLines(canvas)
    }

    private fun drawLines(canvas: Canvas) {
        // Clear previous data
        path.rewind()

        // Data is sorted so just display points
        // Move path to first item
        val pointList = data.data
        val firstItem = pointList[0]
        var newPointX = calculatePointX(firstItem.x)
        var newPointY = calculatePointY(firstItem.y)
        path.moveTo(newPointX, newPointY)

        // Draw connection point
        canvas.drawCircle(newPointX, newPointY, 2f, paint)

        for (i in 1 until pointList.size) {
            val previousItem = pointList[i - 1]
            val item = pointList[i]

            // Calculate coordinates
            newPointX = calculatePointX(item.x)
            newPointY = calculatePointY(item.y)

            // Calculate control points
            val x1 = calculatePointX(previousItem.x)
            val y1 = calculatePointY(previousItem.y)
            val x3 = newPointX
            val y3 = newPointY

            // Write spline data into the path
            path.cubicTo(
                x1 + (x3 - x1) / 2f, y1,
                x1 + (x3 - x1) / 2f, y3,
                x3, y3,
            )

            // Draw connection point
            canvas.drawCircle(newPointX, newPointY, 2f, paint)

            Log.e("->>", "x3: $x3, y3: $y3")
        }

        canvas.drawPath(path, paint)
    }

    private fun calculatePointX(newX: Float): Float {
        return abs(newX + initialXOffset) * xFactor
    }

    private fun calculatePointY(newY: Float): Float {
        return -newY * yFactor + height / 2f
    }
}