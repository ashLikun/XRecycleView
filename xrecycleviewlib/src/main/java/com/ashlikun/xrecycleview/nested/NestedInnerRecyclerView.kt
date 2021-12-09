package com.ashlikun.xrecycleview.nested

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.ashlikun.xrecycleview.RecyclerViewExtend

/**
 * 作者　　: 李坤
 * 创建时间: 2019/12/13　11:11
 * 邮箱　　：496546144@qq.com
 *
 *
 * 功能介绍：RecyclerView里面嵌套ViewPager的时候的内部RecyclerView
 */
open class NestedInnerRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RecyclerViewExtend(context, attrs, defStyle) {
    override var childTouch = NestedOnChildTouch(this)

    override fun onTouchEvent(e: MotionEvent): Boolean {
        return if (childTouch.onTouchEvent(e)) false else super.onTouchEvent(e)
    }
}