package com.ashlikun.xrecycleview.nested

import com.ashlikun.xrecycleview.RecyclerViewExtend
import androidx.core.view.NestedScrollingParent3
import androidx.core.view.NestedScrollingParentHelper
import android.view.View
import com.ashlikun.xrecycleview.nested.NestedOuterRecyclerView.ToTopListener
import android.content.Context
import android.util.AttributeSet

/**
 * 作者　　: 李坤
 * 创建时间: 2019/12/13　10:31
 * 邮箱　　：496546144@qq.com
 *
 *
 * 功能介绍：RecyclerView里面嵌套ViewPager的时候的外部RecyclerView
 */
class NestedOuterRecyclerView : RecyclerViewExtend, NestedScrollingParent3 {
    protected var parentHelper = NestedScrollingParentHelper(this)
    protected var mNestedScrollingTarget: View? = null
    protected var mNestedScrollingChildView: View? = null

    /**
     * 子view是否展开的监听
     */
    private var topListener: ToTopListener? = null

    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!,
        attrs,
        defStyle
    ) {
    }

    /**
     * 设置是否到达顶部的监听
     *
     * @param topListener
     */
    fun setTopListener(topListener: ToTopListener?) {
        this.topListener = topListener
    }

    //recyclerView是否到达底部,false:到底部了
    val isTop: Boolean
        get() = if (topListener != null) {
            !topListener!!.isTop
        } else canScrollVertically(1)
    //recyclerView是否到达底部,false:到底部了
    /**
     * 是否接受嵌套滚动
     *
     * @return true:接受
     */
    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        return true
    }

    /**
     * 当接受嵌套滚动
     */
    override fun onNestedScrollAccepted(
        child: View,
        target: View,
        nestedScrollAxes: Int,
        type: Int
    ) {
        parentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes, type)
        mNestedScrollingTarget = target
        mNestedScrollingChildView = child
    }

    /**
     * 在内层view处理滚动事件前先被调用,可以让外层view先消耗部分滚动
     *
     * @param target   被滚动的view
     * @param dx
     * @param dy
     * @param consumed 消耗的部分
     * @param type
     */
    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        if (mNestedScrollingChildView == null) {
            return
        }
        // 如果是向上的
        if (dy >= 0) {
            // ViewPager当前所处位置没有在顶端，交由父类去滑动
            if (isTop) {
                consumed[0] = 0
                consumed[1] = dy
                scrollBy(0, dy)
            }
        } else {
            //ViewPager当前所处位置没有在顶端，交由父类去滑动
            if (isTop) {
                if (!target.canScrollVertically(-1)) {
                    consumed[0] = 0
                    consumed[1] = dy
                    scrollBy(0, dy)
                }
            } else {
                if (!target.canScrollVertically(-1)) {
                    consumed[0] = 0
                    consumed[1] = dy
                    scrollBy(0, dy)
                }
            }
        }
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        parentHelper.onStopNestedScroll(target, type)
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
    }

    interface ToTopListener {
        /**
         * 是否到顶部了，该ViewPager内部滚动了
         *
         * @return true:到顶部了
         */
        val isTop: Boolean
    }
}