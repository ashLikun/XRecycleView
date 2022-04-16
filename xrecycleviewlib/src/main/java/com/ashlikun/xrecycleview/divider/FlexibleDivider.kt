package com.ashlikun.xrecycleview.divider

import android.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

enum class DividerType {
    DRAWABLE, PAINT, COLOR
}
/**
 * 数据转换
 */
typealias Convert<T> = (position: Int) -> T

/**
 * @author　　: 李坤
 * 创建时间: 2021/12/9 22:44
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：ItemDecoration 的抽象
 */

abstract class FlexibleDivider(
    context: Context,
    size: Int = 2,
    color: Int? = null,
    paint: Paint? = null,
    drawable: Drawable? = null,
    sizeConvert: Convert<Int>? = null,
    colorConvert: Convert<Int>? = null,
    drawableConvert: Convert<Drawable>? = null,
    paintConvert: Convert<Paint>? = null,
    visibilityConvert: Convert<Boolean>? = null,

    //是否显示最后的分割线
    open var showLastDivider: Boolean = false,
    //是否显示第一个分割线
    open var showFirstDivider: Boolean = true,
    //分割线是否插入View里面
    open var positionInsideItem: Boolean = false
) : ItemDecoration() {
    companion object {
        private val ATTRS = intArrayOf(R.attr.listDivider)
    }

    var visibilityConvert: Convert<Boolean>? = null
    var paintConvert: Convert<Paint>? = null
    var colorConvert: Convert<Int>? = null
    var drawableConvert: Convert<Drawable>? = null
    var sizeConvert: Convert<Int>? = null

    private var mPaint: Paint? = null
    var dividerType: DividerType = DividerType.DRAWABLE

    init {
        this.visibilityConvert = visibilityConvert
        this.paintConvert = paintConvert
        this.colorConvert = colorConvert
        this.drawableConvert = drawableConvert
        this.sizeConvert = sizeConvert

        if (this.sizeConvert == null) {
            this.sizeConvert = { position -> size }
        }
        if (this.colorConvert == null && color != null) {
            this.colorConvert = { position -> color }
        }
        if (this.paintConvert == null && paint != null) {
            this.paintConvert = { position -> paint }
        }
        if (this.drawableConvert == null && drawable != null) {
            this.drawableConvert = { position -> drawable }
        }

        if (this.paintConvert != null) {
            dividerType = DividerType.PAINT
        } else if (this.colorConvert != null) {
            dividerType = DividerType.COLOR
            mPaint = Paint()
        } else {
            dividerType = DividerType.DRAWABLE
            val a = context.obtainStyledAttributes(ATTRS)
            val divider = a.getDrawable(0)
            a.recycle()
            if (this.drawableConvert == null && divider != null) {
                this.drawableConvert = { position -> divider!! }
            }
        }
    }


    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val adapter = parent.adapter ?: return
        val itemCount = adapter.itemCount
        val lastDividerOffset = getLastDividerOffset(parent)
        val validChildCount = parent.childCount
        var lastChildPosition = -1
        for (i in 0 until validChildCount) {
            val child = parent.getChildAt(i)
            val childPosition = parent.getChildAdapterPosition(child)
            if (childPosition < lastChildPosition) {
                continue
            }
            lastChildPosition = childPosition
            if (!showLastDivider && childPosition > 0 && childPosition >= itemCount - lastDividerOffset) {
                continue
            }
            if (!showFirstDivider && childPosition == 0) {
                continue
            }
            onDrawDivider(c, parent, child, childPosition, itemCount, state)
        }
    }

    protected abstract fun onDrawDivider(
        c: Canvas, parent: RecyclerView, child: View, childPosition: Int, count: Int, state: RecyclerView.State
    )

    /**
     * 提供绘制分割线方法
     */
    fun onDraw(
        c: Canvas,
        bounds: Rect,
        childPosition: Int,
        parent: RecyclerView,
        dividerSize: Int
    ) {
        when (dividerType) {
            DividerType.DRAWABLE -> {
                val drawable = drawableConvert?.invoke(childPosition)
                drawable?.bounds = bounds
                drawable?.draw(c)
            }
            DividerType.PAINT -> {
                mPaint = paintConvert?.invoke(childPosition)
                if (mPaint != null)
                    c.drawLine(
                        bounds.left.toFloat(),
                        bounds.top.toFloat(),
                        bounds.right.toFloat(),
                        bounds.bottom.toFloat(),
                        mPaint!!
                    )
            }
            DividerType.COLOR -> {
                mPaint?.color = colorConvert?.invoke(childPosition)
                    ?: throw RuntimeException("请提供colorProvider")
                mPaint?.strokeWidth = dividerSize.toFloat()
                if (mPaint != null)
                    c.drawLine(
                        bounds.left.toFloat(),
                        bounds.top.toFloat(),
                        bounds.right.toFloat(),
                        bounds.bottom.toFloat(),
                        mPaint!!
                    )
            }
        }
    }

    override fun getItemOffsets(
        rect: Rect, v: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(v)
        val itemCount = parent.adapter!!.itemCount
        val lastDividerOffset = getLastDividerOffset(parent)
        if (visibilityConvert?.invoke(position) == false) {
            return
        }
        if (!showFirstDivider && position == 0) {
            return
        }
        if (!showLastDivider && position > 0 && position >= itemCount - lastDividerOffset) {
            return
        }

        setItemOffsets(rect, v, position, itemCount, parent)
    }

    /**
     * 检查recyclerview是否为反向布局
     */
    protected fun isReverseLayout(parent: RecyclerView): Boolean {
        val layoutManager = parent.layoutManager
        return layoutManager is LinearLayoutManager && layoutManager.reverseLayout
    }

    /**
     * 在showLastDivider = false的情况下，
     * 返回我们不需要划分的视图的偏移量，
     * 对于LinearLayoutManager，它就像不画最后一个子分割器一样简单，
     * 但是对于GridLayoutManager，它需要考虑最后一项的跨度计数
     * 直到我们使用为网格配置的跨度计数。
     */
    private fun getLastDividerOffset(parent: RecyclerView): Int {
        val layoutManager = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            val spanSizeLookup = layoutManager.spanSizeLookup
            val spanCount = layoutManager.spanCount
            val itemCount = parent.adapter!!.itemCount
            for (i in itemCount - 1 downTo 0) {
                if (spanSizeLookup.getSpanIndex(i, spanCount) == 0) {
                    return itemCount - i
                }
            }
        }
        return 1
    }

    /**
     * 返回GridLayoutManager的组索引。
     * 对于线性layoutmanager，总是返回位置。
     */
    fun getGroupIndex(position: Int, parent: RecyclerView): Int {
        val layoutManager = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            val spanSizeLookup = layoutManager.spanSizeLookup
            val spanCount = layoutManager.spanCount
            return spanSizeLookup.getSpanGroupIndex(position, spanCount)
        }
        return position
    }


    protected abstract fun setItemOffsets(
        outRect: Rect,
        v: View,
        position: Int,
        childCount: Int,
        parent: RecyclerView
    )


    protected fun getDividerSize(position: Int, parent: RecyclerView) =
        paintConvert?.invoke(position)?.strokeWidth?.toInt()
            ?: sizeConvert?.invoke(position)
            ?: drawableConvert?.invoke(position)?.intrinsicWidth
            ?: throw RuntimeException("failed to get size")

}