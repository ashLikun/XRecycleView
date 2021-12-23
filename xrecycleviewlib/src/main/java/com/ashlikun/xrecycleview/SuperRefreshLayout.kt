package com.ashlikun.xrecycleview

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.view.NestedScrollingChild
import com.ashlikun.swiperefreshlayout.SwipeRefreshLayout

/**
 * 作者　　: 李坤
 * 创建时间: 12:49 Administrator
 * 邮箱　　：496546144@qq.com
 *
 *
 * 功能介绍：自定义的刷新控件
 */
@SuppressLint("ResourceType")
open class SuperRefreshLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) :
    SwipeRefreshLayout(context, attrs), RefreshLayout, SwipeRefreshLayout.OnRefreshListener {

    var onRefresh: OnRefresh? = null
    var isMOVE = false
    override fun onInterceptTouchEvent(ev: MotionEvent) =
        if (isMOVE) false else super.onInterceptTouchEvent(ev)

    init {
        setColorSchemeResources(context, this)
        setRefreshStyle(FLOAT)
    }

    /**
     * 与一些其他的滑动控件滑动的时候，屏蔽下拉
     */
    fun addNestedView(view: View) {
        if (view is NestedScrollingChild) {
            (view as NestedScrollingChild).isNestedScrollingEnabled = false
        }
        view.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_MOVE -> isMOVE = true
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> isMOVE = false
            }
            false
        }
    }

    /**
     * 还原
     */
    fun removeNestedView(view: View) {
        if (view is NestedScrollingChild) {
            (view as NestedScrollingChild).isNestedScrollingEnabled = true
        }
        view.setOnTouchListener(null)
    }


    override fun setOnRefreshCallback(
        listener: RefreshLayout.OnRefreshListener?,
        onRefresh: OnRefresh?
    ) {
        this.onRefresh = onRefresh ?: {
            listener?.onRefresh()
        }
        super.setOnRefreshListener(this)
    }


    /**
     * ：调用上面的之定义监听
     */
    @Deprecated("")
    override fun setOnRefreshListener(listener: OnRefreshListener) {
        Exception("调用上面的之定义监听setOnRefreshListener(RefreshLayout.OnRefreshListener listener)")
    }

    override fun onRefresh() {
        onRefresh?.invoke()
    }


}