package com.app.ocrscanner.view.scan

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.app.ocrscanner.R
import com.app.ocrscanner.Utils.toDp
import com.app.ocrscanner.Utils.toSp

class OcrViewMask : AppCompatTextView {

    private val surfacePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val titleTextPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
    private val resultTextPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)

    private lateinit var backgroundRect: Rect
    private lateinit var recognitionRect: RectF
    private lateinit var recognitionArea: Path

    private var cornerR = 0f
    private val op = Region.Op.DIFFERENCE

    private val titleTopMargin = 40f.toDp(context)

    private var resultText: String? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    private fun init(context: Context) {
        surfacePaint.color = ContextCompat.getColor(context, R.color.scan_mask_color)
        cornerR = resources.getDimension(R.dimen.corners_ocr_view_mask)

        titleTextPaint.apply {
            default()
            color = currentTextColor
        }

        resultTextPaint.apply {
            default()
            color = resources.getColor(R.color.text_ocr_result)
        }
    }

    fun Paint.default(){
        this.apply {
            textSize = this@OcrViewMask.textSize
            typeface = this@OcrViewMask.typeface
            textAlign = Paint.Align.CENTER
        }
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        backgroundRect = Rect(0, 0, w, h)
        recognitionRect = RectF(
            paddingStart.toFloat(),
            paddingTop.toFloat(),
            (w - paddingEnd).toFloat(),
            paddingTop + resources.getDimension(R.dimen.recognition_area_height)
        )
        recognitionArea = Path().apply {
            addRoundRect(recognitionRect, cornerR, cornerR, Path.Direction.CW)
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.apply {
            save()
            clipRect(backgroundRect)
            clipPath(recognitionArea, op)
            drawRect(backgroundRect, surfacePaint)
            restore()

            recognitionRect.apply {
                val x = (right - left) / 2f + left

                resultText?.apply {
                    resultTextPaint.let {
                        while (it.measureText(this) > right - left){
                            it.textSize = it.textSize - 2f.toSp(context)
                        }
                    }

                    drawText(
                        this,
                        x,
                        ((bottom - top) / 2f) + top - ((titleTextPaint.descent() + titleTextPaint.ascent()) / 2),
                        resultTextPaint
                    )
                }

                drawText(
                    text.toString(),
                    x,
                    bottom + titleTopMargin,
                    titleTextPaint
                )
            }
        }
    }

    fun setResult(result: String) {
        resultText = result
        invalidate()
    }

    fun getRecognitionRect(): RectF {
        return recognitionRect
    }
}