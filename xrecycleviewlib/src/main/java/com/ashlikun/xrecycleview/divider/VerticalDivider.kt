package com.ashlikun.xrecycleview.divider

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * @author　　: 李坤
 * 创建时间: 2021/12/9 23:15
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：垂直的分割线
 */

class VerticalDivider(
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
    var marginTopConvert: Convert<Int>? = null,
    var marginBottomConvert: Convert<Int>? = null,

    //是否显示最后的分割线
    override var showLastDivider: Boolean = false,
    //是否显示第一个分割线
    override var showFirstDivider: Boolean = true,
    //分割线是否插入View里面
    override var positionInsideItem: Boolean = false
) : FlexibleDivider(
    context = context,
    visibilityConvert = visibilityConvert,
    paintConvert = paintConvert,
    colorConvert = colorConvert,
    drawableConvert = drawableConvert,
    sizeConvert = sizeConvert,
    size = size,
    color = color,
    paint = paint,
    drawable = drawable
) {
    override fun onDrawDivider(
        c: Canvas,
        parent: RecyclerView,
        child: View,
        position: Int,
        state: RecyclerView.State
    ) {
        val divSize = getDividerSize(position, parent)
        val bounds = getDividerBound(position, parent, child, false)
        onDraw(c, bounds, position, parent, divSize)
    }

    protected fun getDividerBound(
        position: Int,
        parent: RecyclerView,
        child: View,
        isLeft: Boolean
    ): Rect {
        val bounds = Rect(0, 0, 0, 0)
        val transitionX = child.translationX.toInt()
        val params = child.layoutParams as RecyclerView.LayoutParams
        bounds.top =
            child.top - params.topMargin + if (isLeft) 0 else marginTopConvert?.invoke(
                position,
                parent
            ) ?: 0
        bounds.bottom =
            child.bottom + params.bottomMargin - if (isLeft) 0 else marginBottomConvert?.invoke(
                position,
                parent
            ) ?: 0
        var dividerSize = getDividerSize(position, parent)
        if (isLeft) {
            dividerSize = dividerSize / 2
        }
        val isReverseLayout = isReverseLayout(parent)
        if (dividerType === DividerType.DRAWABLE) {
            if (isReverseLayout or isLeft) {
                bounds.right = child.left - params.leftMargin + transitionX
                bounds.left = bounds.right - dividerSize
            } else {
                bounds.left = child.right + params.rightMargin + transitionX
                bounds.right = bounds.left + dividerSize
            }
        } else {
            val halfSize = dividerSize / 2
            if (isReverseLayout or isLeft) {
                bounds.left = child.left - params.leftMargin - halfSize + transitionX
            } else {
                bounds.left = child.right + params.rightMargin + halfSize + transitionX
            }
            bounds.right = bounds.left
        }
        if (positionInsideItem) {
            if (isReverseLayout) {
                bounds.left += dividerSize
                bounds.right += dividerSize
            } else {
                bounds.left -= dividerSize
                bounds.right -= dividerSize
            }
        }
        return bounds
    }

    override fun setItemOffsets(
        outRect: Rect,
        v: View,
        position: Int,
        childCount: Int,
        parent: RecyclerView
    ) {
        if (positionInsideItem) {
            outRect[0, 0, 0] = 0
            return
        }
        //多少列
        val spanCount = getSpanCount(parent, position)
        if (spanCount <= 1) {
            outRect[0, 0, 0] = 0
            return
        }
        //当前第几列 0开始
        val spanIndex = getIndexColum(parent, v, position, spanCount)
        val dividerSize = getDividerSize(position, parent)
        var dividerSizeAll = 0
        //总大小
        for (i in 0 until spanCount - 1) {
            val cp = position + i - spanIndex
            if (cp >= 0) {
                dividerSizeAll += getDividerSize(cp, parent)
            }
        }
        //每列大小
        val eachWidth = dividerSizeAll / spanCount
        val left = spanIndex * (dividerSize - eachWidth)
        val right = eachWidth - left
        if (spanIndex == spanCount - 1) {
            // 如果是最后一列，则不需要绘制右边
            outRect[left, 0, 0] = 0
        } else if (spanIndex == 0) {
            //第一列不绘制左边
            outRect[0, 0, right] = 0
        } else {
            //中间的左右都绘制
            outRect[left, 0, right] = 0
        }
    }

    /**
     * 当前是第几列
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

}