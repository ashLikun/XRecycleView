package com.ashlikun.xrecycleview.divider

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * @author　　: 李坤
 * 创建时间: 2021/12/9 23:44
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：网格布局用的
 */

class DividerGridItem(
    var size: Int = 2,
    var color: Int = 0,
    var drawable: Drawable? = null
) :
    ItemDecoration() {

    init {
        if (drawable == null) {
            val drawable = GradientDrawable()
            drawable.setSize(size, size)
            drawable.setColor(color)
            this.drawable = drawable
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.layoutManager == null) {
            return
        }
        drawVertical(c, parent)
        drawHorizontal(c, parent)
    }

    fun drawHorizontal(c: Canvas?, parent: RecyclerView) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child
                .layoutParams as RecyclerView.LayoutParams
            val left = child.left - params.leftMargin
            val right = (child.right + params.rightMargin
                    + drawable!!.intrinsicWidth)
            val top = child.bottom + params.bottomMargin
            val bottom = top + drawable!!.intrinsicHeight
            drawable!!.setBounds(left, top, right, bottom)
            drawable!!.draw(c)
        }
    }

    fun drawVertical(c: Canvas?, parent: RecyclerView) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child
                .layoutParams as RecyclerView.LayoutParams
            val top = child.top - params.topMargin
            val bottom = child.bottom + params.bottomMargin
            val left = child.right + params.rightMargin
            val right = left + drawable!!.intrinsicWidth
            drawable!!.setBounds(left, top, right, bottom)
            drawable!!.draw(c)
        }
    }

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = parent.adapter!!.itemCount
        if (position >= itemCount - getLastDividerOffset(parent)) {
            // 如果是最后一行，则不需要绘制底部
            outRect[0, 0, 0] = 0
        } else {
            outRect.bottom = drawable!!.intrinsicHeight
        }
        val spanCount = getSpanCount(parent, position)
        if (spanCount > 1) {
            //当前第几列
            val spanIndex = getIndexColum(parent, view, position, spanCount)
            val dividerSize = drawable!!.intrinsicHeight
            //每列大小
            val eachWidth = (spanCount - 1) * dividerSize / spanCount
            val left = spanIndex * (dividerSize - eachWidth)
            val right = eachWidth - left
            if (spanIndex == spanCount - 1) {
                // 如果是最后一列，则不需要绘制右边
                outRect.right = 0
                outRect.left = left
            } else if (spanIndex == 0) {
                //第一列不绘制左边
                outRect.right = right
                outRect.left = 0
            } else { //中间的左右都绘制
                outRect.left = left
                outRect.right = right
            }
        }
    }

    /**
     * 当前是第几列
     *
     * @param parent
     * @param pos
     * @param spanCount
     * @return
     */
    protected fun getIndexColum(parent: RecyclerView, view: View, pos: Int, spanCount: Int): Int {
        val layoutManager = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            return layoutManager.spanSizeLookup.getSpanIndex(pos, spanCount)
        } else if (layoutManager is LinearLayoutManager) {
            //水平布局
            if (layoutManager.orientation == RecyclerView.HORIZONTAL) {
                return pos % spanCount
            }
        } else if (layoutManager is StaggeredGridLayoutManager) {
            //瀑布流专属
            val params = view.layoutParams as StaggeredGridLayoutManager.LayoutParams
            return params.spanIndex
        }
        return spanCount
    }

    /**
     * 一共多少列
     *
     * @param parent
     * @param position
     * @return
     */
    protected fun getSpanCount(parent: RecyclerView, position: Int): Int {
        // 列数
        var spanCount = 1
        val layoutManager = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            spanCount = layoutManager.spanCount + 1
            spanCount = Math.abs(spanCount - layoutManager.spanSizeLookup.getSpanSize(position))
        } else if (layoutManager is LinearLayoutManager) {
            //水平布局
            if (layoutManager.orientation == RecyclerView.HORIZONTAL) {
                return parent.adapter!!.itemCount
            }
        } else if (layoutManager is StaggeredGridLayoutManager) {
            spanCount = layoutManager
                .spanCount
        }
        return spanCount
    }

    private fun getLastDividerOffset(parent: RecyclerView): Int {
        if (parent.layoutManager is GridLayoutManager) {
            val layoutManager = parent.layoutManager as GridLayoutManager?
            val spanSizeLookup = layoutManager!!.spanSizeLookup
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
}