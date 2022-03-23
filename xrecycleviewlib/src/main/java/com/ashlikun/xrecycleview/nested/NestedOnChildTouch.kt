package com.ashlikun.xrecycleview.nested

import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration

/**
 * @author　　: 李坤
 * 创建时间: 2021/12/9 22:21
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：内部滑动控件的处理工具类
 */

open class NestedOnChildTouch(var view: View) {
    //滚动的手指触摸id
    var mScrollPointerId = INVALID_POINTER
    var mInitialTouchX = 0f
    var mInitialTouchY = 0f
    var touchSlop: Int

    /**
     * @param e
     * @return true:拦截掉
     */
    fun onTouchEvent(e: MotionEvent): Boolean {
        val actionIndex = e.actionIndex
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                mScrollPointerId = e.getPointerId(0)
                mInitialTouchX = e.x
                mInitialTouchY = e.y
                // 必须加上这个，让 RecyclerView 也要处理滑动冲突才行
                view.parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                mInitialTouchX = e.getX(actionIndex)
                mInitialTouchY = e.getY(actionIndex)
                mScrollPointerId = e.getPointerId(actionIndex)
                // 必须加上这个，让 RecyclerView 也要处理滑动冲突才行
                view.parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                val index = e.findPointerIndex(mScrollPointerId)
                if (index < 0) {
                    Log.e(
                        view.javaClass.simpleName, "Error processing scroll; pointer index for id "
                                + mScrollPointerId + " not found. Did any MotionEvents get skipped?"
                    )
                    return false
                }
                val x = e.getX(index)
                val y = e.getY(index)
                val dx = x - mInitialTouchX
                val dy = y - mInitialTouchY
                //通过距离差判断方向
                val orientation = getOrientation(dx, dy)
                when (orientation) {
                    "r", "l" ->                         // 要求左右滑动很大才能触发父类的左右滑动
                        if (Math.abs(dx) > touchSlop * 1.5f) {
                            //父类处理
                            view.parent.requestDisallowInterceptTouchEvent(false)
                            return true
                        } else {
                            view.parent.requestDisallowInterceptTouchEvent(true)
                        }
                    "t", "b" ->                         //上下滚动子类处理
                        view.parent.requestDisallowInterceptTouchEvent(true)
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mScrollPointerId = INVALID_POINTER
            }
        }
        return false
    }

    private fun getOrientation(dx: Float, dy: Float): String {
        return if (Math.abs(dx * 0.7f) > Math.abs(dy)) {
            //X轴移动
            if (dx > 0) "r" else "l" //右,左
        } else {
            //Y轴移动
            if (dy > 0) "b" else "t" //下//上
        }
    }

    companion object {
        private const val INVALID_POINTER = -1
    }

    init {
        touchSlop = ViewConfiguration.get(view.context).scaledPagingTouchSlop
    }
}