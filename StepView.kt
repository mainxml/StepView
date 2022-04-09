package com.szlanyou.smartnissan.order.aftersale

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.toColorInt
import com.szlanyou.smartnissan.framework.ext.dp

/**
 * 步骤视图
 * ```
 * author: zcp
 * created on: 2022/3/15 17:24
 * ```
 */
class StepView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val stepTitleList = mutableListOf<String>()

    /**
     * 当前步数
     */
    var stepIndex = 0
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 添加步骤列表
     * @param stepTitleList List<String>
     */
    fun setList(stepTitleList: List<String>) {
        this.stepTitleList.clear()
        this.stepTitleList.addAll(stepTitleList)
        invalidate()
    }

    /*init {
        setBackgroundColor("#FF000000".toColorInt())
        setList(listOf("提交申请", "审核中", "提交物流信息", "收货审核中", "换货成功"))
        stepIndex = 2
    }*/

    //region 测量绘制
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)

        if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, 44.dp)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    private val drawTop = 10.dp.toFloat()
    private val circleWidth = 8.dp.toFloat()
    private val semicircleWidth = circleWidth / 2
    private val lineWidth = 1.dp.toFloat()
    private val circleLinePadding = 2.dp.toFloat()
    private val textPadding = 14.dp.toFloat()

    private val normalColor = "#FF9C9FA8".toColorInt()
    private val highlightColor = "#FFDAC2AC".toColorInt()

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        if (stepTitleList.size == 0) {
            return
        }
        val interval = width / (stepTitleList.size - 1)
        stepTitleList.forEachIndexed { index, stepTitle ->
            // 画圆
            if (stepIndex > index) {
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = lineWidth
            } else {
                paint.style = Paint.Style.FILL
            }
            paint.color = if (stepIndex >= index) highlightColor else normalColor

            val cx = when (index) {
                0 -> semicircleWidth + lineWidth
                stepTitleList.lastIndex -> interval * index - (semicircleWidth + lineWidth)
                else -> interval.toFloat() * index
            }
            canvas.drawCircle(cx, drawTop, semicircleWidth, paint)

            // 画线
            paint.color = if (stepIndex > index) highlightColor else normalColor
            paint.strokeCap = Paint.Cap.BUTT
            paint.strokeWidth = lineWidth

            val starX: Float
            val stopX: Float
            when (index) {
                0 -> {
                    starX = circleWidth + (lineWidth * 2) + circleLinePadding
                    stopX = interval - (semicircleWidth + lineWidth + circleLinePadding)
                }
                stepTitleList.lastIndex - 1 -> {
                    starX = interval * index + semicircleWidth + lineWidth + circleLinePadding
                    stopX = interval * (index + 1) - (circleWidth + (lineWidth * 2) + circleLinePadding)
                }
                stepTitleList.lastIndex -> {
                    starX = -1f
                    stopX = -1f
                }
                else -> {
                    starX = interval * index + semicircleWidth + lineWidth + circleLinePadding
                    stopX = interval * (index + 1) - (semicircleWidth + lineWidth + circleLinePadding)
                }
            }
            if (starX > 0 || stopX > 0) {
                canvas.drawLine(starX, drawTop, stopX, drawTop, paint)
            }

            // 绘制文字
            textPaint.color = "#CDD2D8".toColorInt()
            textPaint.textSize = 10.dp.toFloat()

            val alignment = when (index) {
                0 -> Layout.Alignment.ALIGN_NORMAL
                stepTitleList.lastIndex -> Layout.Alignment.ALIGN_OPPOSITE
                else -> Layout.Alignment.ALIGN_CENTER
            }
            val staticLayout = StaticLayout(
                stepTitle, textPaint, interval,
                alignment, 1f, 0f, false
            )

            val dy = drawTop + semicircleWidth + lineWidth + textPadding
            val dx = when (index) {
                0 -> 0f
                stepTitleList.lastIndex -> interval.toFloat() * index - staticLayout.width
                else -> interval.toFloat() * index - staticLayout.width / 2
            }

            canvas.save()
            canvas.translate(dx, dy)
            staticLayout.draw(canvas)
            canvas.restore()
        }
    }
    //endregion
}