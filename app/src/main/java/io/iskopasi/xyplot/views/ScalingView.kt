package io.iskopasi.xyplot.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.View.OnTouchListener

open class ScalingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes), OnTouchListener {
    private var scalePivotX = 1f
    private var xScaleFactor = 1f

    init {
        setOnTouchListener(this)
    }

    val drawMatrix: Matrix = Matrix()
    private val dragGestureDetector by lazy {
        GestureDetector(context.applicationContext, object : GestureDetector.OnGestureListener {
            override fun onDown(p0: MotionEvent): Boolean {
                return false
            }

            override fun onShowPress(p0: MotionEvent) {
            }

            override fun onSingleTapUp(p0: MotionEvent): Boolean {
                return false
            }

            override fun onScroll(
                p0: MotionEvent?,
                p1: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                drawMatrix.postTranslate(-distanceX, 0f)
                invalidate()

                return true
            }

            override fun onLongPress(p0: MotionEvent) {
            }

            override fun onFling(
                p0: MotionEvent?,
                p1: MotionEvent,
                p2: Float,
                p3: Float
            ): Boolean {
                return false
            }

        })
    }

    private val scaleGestureDetector by lazy {
        ScaleGestureDetector(context.applicationContext,
            object : ScaleGestureDetector.SimpleOnScaleGestureListener() {

                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    xScaleFactor *= detector.scaleFactor
                    invalidate()

                    return true
                }

                override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                    scalePivotX = detector.focusX
                    return true
                }
            })
    }

    override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
        if (event == null) return false

        scaleGestureDetector.onTouchEvent(event)
        dragGestureDetector.onTouchEvent(event)

        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.setMatrix(drawMatrix)
        canvas.scale(xScaleFactor, 1f, scalePivotX, 1f)
    }
}