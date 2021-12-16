package com.ashlikun.xrecycleview.divider

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.ashlikun.xrecycleview.getRecyclerViewIndexColum
import com.ashlikun.xrecycleview.getRecyclerViewLastDividerOffset
import com.ashlikun.xrecycleview.getRecyclerViewSpanCount

/**
 * @author　　: 李坤
 * 创建时间: 2021/12/9 23:44
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：网格布局用的
 */

class GridDivider(
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

    fun drawHorizontal(c: Canvas, parent: RecyclerView) {
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

    fun drawVertical(c: Canvas, parent: RecyclerView) {
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
        if (position >= itemCount - getRecyclerViewLastDividerOffset(parent)) {
            // 如果是最后一行，则不需要绘制底部
            outRect[0, 0, 0] = 0
        } else {
            outRect.bottom = drawable!!.intrinsicHeight
        }
        val spanCount = getRecyclerViewSpanCount(parent, position)
        if (spanCount > 1) {
            //当前第几列
            val spanIndex = getRecyclerViewIndexColum(parent, view, position, spanCount)
            val dividerSize = drawable!!.intrinsicHeight
            //每列大小
            val eachWidth = (spanCount - 1) * dividerSize / spanCount
            val left = spanIndex * (dividerSize - eachWidth)
            val right = eachWidth - left
            when (spanIndex) {
                spanCount - 1 -> {
                    // 如果是最后一列，则不需要绘制右边
                    outRect.right = 0
                    outRect.left = left
                }
                0 -> {
                    //第一列不绘制左边
                    outRect.right = right
                    outRect.left = 0
                }
                else -> { //中间的左右都绘制
                    outRect.left = left
                    outRect.right = right
                }
            }
        }
    }


}