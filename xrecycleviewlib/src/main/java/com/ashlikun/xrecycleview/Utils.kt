package com.ashlikun.xrecycleview

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * 作者　　: 李坤
 * 创建时间: 2021.12.9　16:41
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：本库的工具类
 */
fun getCommonAdapterClass(cls: Class<*>): Class<*>? {
    return if (cls.isAssignableFrom(RecyclerView.Adapter::class.java) || cls.superclass == null) {
        null
    } else {
        try {
            cls.getDeclaredField(RecyclerViewExtend.HEADERSIZE)
            cls.getDeclaredField(RecyclerViewExtend.FOOTERSIZE)
            cls
        } catch (e: NoSuchFieldException) {
            getCommonAdapterClass(cls.superclass!!)
        }
    }
}

fun setHeaderFooterSize(obj: Any, fieldName: String, fieldValue: Any) {
    //应为CommonAdapter为抽象类
    val cls = getCommonAdapterClass(obj::class.java) ?: return
    try {
        val field = cls.getDeclaredField(fieldName)
        field.isAccessible = true
        try {
            field[obj] = fieldValue
        } catch (e: IllegalAccessException) {
            Log.w(fieldName, "adapter设置" + fieldName + "字段失败")
        }
    } catch (e: NoSuchFieldException) {
        Log.w(fieldName, "adapter没有" + fieldName + "字段")
    }
    //刷新adapter
}

open class SpanSizeLookupGroup(
    var manager: GridLayoutManager,
    var positionDiff: Int = 0,
    var isOne: (position: Int) -> Boolean
) :
    GridLayoutManager.SpanSizeLookup() {
    var old: GridLayoutManager.SpanSizeLookup? = manager.spanSizeLookup

    override fun getSpanSize(position: Int): Int {
        return if (isOne(position)) {
            //占满全行
            manager.spanCount
        } else {
            //使用之前设置过的 并且positions是去除头部后的位置
            old?.getSpanSize(position - positionDiff) ?: 1
        }
    }
}


fun findMax(lastPositions: IntArray): Int {
    var max = lastPositions[0]
    for (value in lastPositions) {
        if (value > max) {
            max = value
        }
    }
    return max
}

fun dip2px(context: Context, dip: Int): Int {
    val scale = context.resources.displayMetrics.density
    return (dip * scale + 0.5f * if (dip >= 0) 1 else -1).toInt()
}

@SuppressLint("ResourceType")
fun setColorSchemeResources(context: Context, refreshLayout: RefreshLayout) {
    val array = context.theme.obtainStyledAttributes(
        intArrayOf(
            R.attr.SwipeRefreshLayout_Color1,
            R.attr.SwipeRefreshLayout_Color2,
            R.attr.SwipeRefreshLayout_Color3,
            R.attr.SwipeRefreshLayout_Color4
        )
    )
    refreshLayout.setColorSchemeColors(
        array.getColor(0, -0x10000),
        array.getColor(1, -0x10000),
        array.getColor(2, -0x10000),
        array.getColor(3, -0x10000)
    )
    array.recycle()
}

/**
 * 当前是第几列
 * @return
 */
fun getRecyclerViewIndexColum(parent: RecyclerView, view: View, pos: Int, spanCount: Int): Int {
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
 */
fun getRecyclerViewSpanCount(parent: RecyclerView, position: Int): Int {
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

fun getRecyclerViewLastDividerOffset(parent: RecyclerView): Int {
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