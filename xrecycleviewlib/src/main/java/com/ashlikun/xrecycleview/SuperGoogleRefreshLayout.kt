package com.ashlikun.xrecycleview

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.view.NestedScrollingChild
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

/**
 * 作者　　: 李坤
 * 创建时间: 12:49 Administrator
 * 邮箱　　：496546144@qq.com
 *
 *
 * 功能介绍：Google官方的下拉
 */
@SuppressLint("ResourceType")
class SuperGoogleRefreshLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : SwipeRefreshLayout(context, attrs), RefreshLayout, SwipeRefreshLayout.OnRefreshListener {
    var mListener: RefreshLayout.OnRefreshListener? = null
    var isMOVE = false
    var isLayoutOk = false

    init {
        setColorSchemeResources(context, this)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        isLayoutOk = true
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return if (isMOVE) false else super.onInterceptTouchEvent(ev)
    }

    /**
     * 与一些其他的滑动控件滑动的时候，屏蔽下拉
     *
     * @param view
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


    override fun setRefreshing(refreshing: Boolean) {
        setRefreshing(refreshing, true)
    }

    override fun setRefreshing(refreshing: Boolean, notify: Boolean) {
        if (isLayoutOk) {
            super.setRefreshing(refreshing)
            if (notify && refreshing) {
                mListener!!.onRefresh()
            }
        } else {
            postDelayed({
                super@SuperGoogleRefreshLayout.setRefreshing(refreshing)
                if (notify && refreshing) {
                    mListener!!.onRefresh()
                }
            }, 400)
        }
    }

    override fun getRefreshView(): View? {
        for (i in 0 until childCount) {
            val view = getChildAt(i)
            if (view.javaClass.simpleName.contains("CircleImageView")) {
                return view
            }
        }
        return null
    }

    override fun setOnRefreshCallback(listener: RefreshLayout.OnRefreshListener) {
        this.mListener = listener
        super.setOnRefreshListener(this)
    }

    /**
     * 作者　　: 李坤
     * 创建时间: 2017/4/12 0012 16:29
     *
     *
     * 方法功能：调用上面的之定义监听
     */
    @Deprecated("")
    override fun setOnRefreshListener(listener: OnRefreshListener?) {
        Exception("调用上面的之定义监听setOnRefreshListener(RefreshLayout.OnRefreshListener listener)")
    }

    override fun onRefresh() {
        if (mListener != null) {
            mListener!!.onRefresh()
        }
    }


}