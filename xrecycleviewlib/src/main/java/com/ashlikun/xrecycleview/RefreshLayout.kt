package com.ashlikun.xrecycleview

import android.view.View
import androidx.annotation.ColorInt

/**
 * 作者　　: 李坤
 * 创建时间:2017/4/12 0012　16:10
 * 邮箱　　：496546144@qq.com
 *
 *
 * 功能介绍：为了适应不同的下拉刷新布局 ，这里抽离出公共的属性与方法，其他的刷新View必须实现这个接口
 */
typealias OnRefresh = () -> Unit

interface RefreshLayout {
    /**
     *
     * 设置刷新动作
     */
    open fun isRefreshing(): Boolean
    fun setEnabled(refreshing: Boolean)
    fun isEnabled(): Boolean

    /**
     * 获取下拉刷新控件
     */
    open fun getRefreshView(): View?

    /**
     *设置颜色值基本上是给官方下拉用的
     */
    fun setColorSchemeColors(@ColorInt vararg colors: Int)

    /**
     * 设置下拉刷新的监听
     */
    fun setOnRefreshCallback(
        listener: RefreshLayout.OnRefreshListener? = null,
        onRefresh: OnRefresh? = null
    )

    fun setRefreshing(refreshing: Boolean)
    fun setRefreshing(refreshing: Boolean, notify: Boolean)


    interface OnRefreshListener {
        fun onRefresh()
    }
}