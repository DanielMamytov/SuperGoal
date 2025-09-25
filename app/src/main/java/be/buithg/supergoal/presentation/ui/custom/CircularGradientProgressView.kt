package be.buithg.supergoal.presentation.ui.custom

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import be.buithg.supergoal.R
import kotlin.math.min

class CircularGradientProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    var progress: Int = 0
        set(value) { field = value.coerceIn(0, 100); invalidate() }

    private var trackColor: Int = Color.parseColor("#242631") // тёмный как на фоне
    private var strokeWidthPx: Float = dp(12f)                 // толщина кольца
    private var startAngle: Float = 270f                       // старт внизу (12ч = 270)
    private var gapAngle: Float = 8f                           // маленький разрыв снизу
    private var gradStart: Int = Color.parseColor("#F23230")
    private var gradEnd: Int = Color.parseColor("#8C1D1C")

    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.BUTT
    }
    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.BUTT // на рефе концы без скругления
    }
    private val rect = RectF()
    private var sweepGradient: SweepGradient? = null
    private val gradientMatrix = Matrix()

    init {
        context.withStyledAttributes(attrs, R.styleable.CircularGradientProgressView) {
            progress = getInt(R.styleable.CircularGradientProgressView_cgp_progress, 0)
            trackColor = getColor(R.styleable.CircularGradientProgressView_cgp_trackColor, trackColor)
            strokeWidthPx = getDimension(R.styleable.CircularGradientProgressView_cgp_strokeWidth, strokeWidthPx)
            startAngle = getFloat(R.styleable.CircularGradientProgressView_cgp_startAngle, startAngle)
            gapAngle = getFloat(R.styleable.CircularGradientProgressView_cgp_gapAngle, gapAngle)
            gradStart = getColor(R.styleable.CircularGradientProgressView_cgp_gradientStartColor, gradStart)
            gradEnd = getColor(R.styleable.CircularGradientProgressView_cgp_gradientEndColor, gradEnd)
        }
        trackPaint.strokeWidth = strokeWidthPx
        progressPaint.strokeWidth = strokeWidthPx
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // делаем квадрат
        val w = MeasureSpec.getSize(widthMeasureSpec)
        val h = MeasureSpec.getSize(heightMeasureSpec)
        val size = resolveSize(min(w, h), widthMeasureSpec)
        setMeasuredDimension(size, size)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        val pad = strokeWidthPx / 2f + maxOf(paddingLeft, paddingTop, paddingRight, paddingBottom)
        rect.set(pad, pad, w - pad, h - pad)

        // SweepGradient по окружности: делаем почти полный круг и немного затемняем хвост,
        // чтобы повторить эффект из макета (почти равномерное тёмное кольцо и яркий сектор).
        sweepGradient = SweepGradient(
            w / 2f, h / 2f,
            intArrayOf(gradStart, gradEnd, gradEnd),              // два последних одинаковых = плавный «хвост»
            floatArrayOf(0f, 0.17f, 1f)                           // 17% — как в фигме на скрине
        )
        // Повернём градиент так, чтобы самый яркий участок оказался у разрыва снизу
        gradientMatrix.reset()
        gradientMatrix.postRotate(startAngle, w / 2f, h / 2f)
        sweepGradient?.setLocalMatrix(gradientMatrix)
        progressPaint.shader = sweepGradient
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // ТРЕК — неполный круг с разрывом
        trackPaint.color = trackColor
        val trackSweep = 360f - gapAngle
        canvas.drawArc(rect, startAngle + gapAngle / 2f, trackSweep, false, trackPaint)

        // ПРОГРЕСС — та же дуга, но на долю от 0..100
        val progressSweep = trackSweep * (progress / 100f)
        if (progressSweep > 0f) {
            canvas.drawArc(rect, startAngle + gapAngle / 2f, progressSweep, false, progressPaint)
        }
    }

    private fun dp(v: Float) = v * resources.displayMetrics.density
}
