package com.ashlikun.xrecycleview.divider

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * @author　　: 李坤
 * 创建时间: 2018/8/30 16:52
 * 邮箱　　：496546144@qq.com
 *
 *
 * 功能介绍：水平分割线
 */
open class HorizontalDivider(
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
    var marginLeftConvert: Convert<Int>? = null,
    var marginRightConvert: Convert<Int>? = null,
    //顶部线的大小,>0 显示第一个顶部分割线
    open var firstTopDividerSize: Int = 0,
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
        val groupIndex = getGroupIndex(position, parent)
        val showFirstTopDivider =
            groupIndex == 0 && firstTopDividerSize > 0
        //绘制第一个的顶部
        if (showFirstTopDivider) {
            val bounds = getDividerBound(position, parent, child, true)
            onDraw(c, bounds, -1, parent, firstTopDividerSize)
        }
        val bounds = getDividerBound(position, parent, child, false)
        onDraw(c, bounds, position, parent, getDividerSize(position, parent))
    }

    protected fun getDividerBound(
        position: Int,
        parent: RecyclerView,
        child: View,
        isTop: Boolean
    ): Rect {
        val bounds = Rect(0, 0, 0, 0)
        val transitionY = child.translationY.toInt()
        val dividerSize: Int = if (isTop) firstTopDividerSize else getDividerSize(position, parent)
        val params = child.layoutParams as RecyclerView.LayoutParams
        bounds.left =
            child.left - params.leftMargin + if (isTop) 0 else marginLeftConvert?.invoke(
                position,
                parent
            ) ?: 0
        bounds.right =
            child.right + params.rightMargin + dividerSize - if (isTop) 0 else marginRightConvert?.invoke(
                position,
                parent
            ) ?: 0
        val isReverseLayout = isReverseLayout(parent)
        if (dividerType === DividerType.DRAWABLE) {
            if (isReverseLayout || isTop) {
                bounds.bottom = child.top - params.topMargin + transitionY
                bounds.top = bounds.bottom - dividerSize
            } else {
                bounds.top = child.bottom + params.bottomMargin + transitionY
                bounds.bottom = bounds.top + dividerSize
            }
        } else {
            val halfSize = dividerSize / 2
            if (isReverseLayout || isTop) {
                bounds.top = child.top - params.topMargin - halfSize + transitionY
            } else {
                bounds.top = child.bottom + params.bottomMargin + halfSize + transitionY
            }
            bounds.bottom = bounds.top
        }
        if (positionInsideItem) {
            if (isReverseLayout) {
                bounds.top += dividerSize
                bounds.bottom += dividerSize
            } else {
                bounds.top -= dividerSize
                bounds.bottom -= dividerSize
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
        if (isReverseLayout(parent)) {
            outRect[0, 0, getDividerSize(position, parent)] = 0
        } else if (showFirstDivider && firstTopDividerSize > 0 && getGroupIndex(
                position,
                parent
            ) == 0
        ) {
            outRect[0, if (firstTopDividerSize > 0) firstTopDividerSize else getDividerSize(
                position,
                parent
            ), 0] = getDividerSize(position, parent)
        } else {
            outRect[0, 0, 0] = getDividerSize(position, parent)
        }
    }

}