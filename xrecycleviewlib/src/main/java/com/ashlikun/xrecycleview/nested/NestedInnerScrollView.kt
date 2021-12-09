package com.ashlikun.xrecycleview.nested

import androidx.core.widget.NestedScrollView
import com.ashlikun.xrecycleview.nested.NestedOnChildTouch
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * 作者　　: 李坤
 * 创建时间: 2019/12/13　11:17
 * 邮箱　　：496546144@qq.com
 *
 *
 * 功能介绍：RecyclerView里面嵌套ViewPager的时候的内部ScrollView
 */
class NestedInnerScrollView : NestedScrollView {
    protected var childTouch = NestedOnChildTouch(this)

    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        return if (childTouch.onTouchEvent(e)) {
            false
        } else super.onTouchEvent(e)
    }
}