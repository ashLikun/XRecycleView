package com.ashlikun.xrecycleview.nested

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.widget.NestedScrollView

/**
 * @author　　: 李坤
 * 创建时间: 2021/12/9 22:26
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：RecyclerView里面嵌套ViewPager的时候的内部ScrollView
 */

open class NestedInnerScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : NestedScrollView(context, attrs, defStyle) {
    protected var childTouch = NestedOnChildTouch(this)

    override fun onTouchEvent(e: MotionEvent): Boolean {
        return if (childTouch.onTouchEvent(e)) {
            false
        } else super.onTouchEvent(e)
    }
}