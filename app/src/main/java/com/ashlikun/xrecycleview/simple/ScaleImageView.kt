package com.ashlikun.xrecycleview.simple

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

/**
 * @author　　: 李坤
 * 创建时间: 2018/8/29 11:05
 * 邮箱　　：496546144@qq.com
 *
 *
 * 功能介绍：按照比例缩放的ImageView
 */
class ScaleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatImageView(context, attrs, defStyle) {
    /**
     * 大小比例，按照宽度
     */
    private var ratio = 16 / 9.0f

    /**
     * 按照宽度（0）或者高度（1）为基础
     */
    var orientation = 0
        private set

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (ratio <= 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }
        if (orientation == 0) {
            val widthSize = MeasureSpec.getSize(widthMeasureSpec)
            val height = (widthSize / ratio).toInt()
            super.onMeasure(
                MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
            )
        } else {
            val height = MeasureSpec.getSize(heightMeasureSpec)
            val width = (height / ratio).toInt()
            super.onMeasure(
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
            )
        }
    }

    fun getRatio(): Float {
        return ratio
    }

    fun setRatio(ratio: Float) {
        if (ratio == this.ratio) {
            return
        }
        this.ratio = ratio
        requestLayout()
    }

    /**
     * 按照宽度（0）或者高度（1）为基础
     */
    fun setOrientationHeight() {
        if (1 == orientation) {
            return
        }
        orientation = 1
        requestLayout()
    }

    /**
     * 按照宽度（0）或者高度（1）为基础
     */
    fun setOrientationWidth() {
        if (0 == orientation) {
            return
        }
        orientation = 0
        requestLayout()
    }

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.ScaleImageView)
        ratio = ta.getFloat(R.styleable.ScaleImageView_sci_ratio, ratio)
        orientation = ta.getInt(R.styleable.ScaleImageView_sci_orientation, orientation)
        ta.recycle()
    }
}