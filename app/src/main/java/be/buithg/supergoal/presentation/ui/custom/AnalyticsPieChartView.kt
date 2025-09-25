package be.buithg.supergoal.presentation.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import be.buithg.supergoal.R
import kotlin.math.min

class AnalyticsPieChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private val arcRect = RectF()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private var strokeWidth = dp(26f)
    private var gapAngle = 3f

    private var slices: List<Slice> = emptyList()

    init {
        context.withStyledAttributes(attrs, R.styleable.AnalyticsPieChartView) {
            strokeWidth = getDimension(R.styleable.AnalyticsPieChartView_apc_strokeWidth, strokeWidth)
            gapAngle = getFloat(R.styleable.AnalyticsPieChartView_apc_gapAngle, gapAngle)
        }
        paint.strokeWidth = strokeWidth
    }

    fun setData(fractions: List<Slice>) {
        slices = fractions
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredSize = (strokeWidth * 2 + dp(120f)).toInt()
        val width = resolveSize(desiredSize, widthMeasureSpec)
        val height = resolveSize(desiredSize, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        val contentWidth = w - paddingLeft - paddingRight
        val contentHeight = h - paddingTop - paddingBottom
        val size = min(contentWidth, contentHeight).toFloat()
        val left = paddingLeft + (contentWidth - size) / 2f
        val top = paddingTop + (contentHeight - size) / 2f
        val padding = strokeWidth / 2f
        arcRect.set(
            left + padding,
            top + padding,
            left + size - padding,
            top + size - padding,
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (slices.isEmpty()) return

        paint.strokeWidth = strokeWidth
        var startAngle = -90f
        slices.forEach { slice ->
            val sweep = slice.fraction * 360f
            if (sweep <= 0f) return@forEach
            val segmentGap = if (sweep > gapAngle) gapAngle else 0f
            val adjustedSweep = (sweep - segmentGap).coerceAtLeast(0f)
            paint.color = slice.color
            canvas.drawArc(arcRect, startAngle + segmentGap / 2f, adjustedSweep, false, paint)
            startAngle += sweep
        }
    }

    private fun dp(value: Float): Float = value * resources.displayMetrics.density

    data class Slice(
        val fraction: Float,
        val color: Int,
    )
}
