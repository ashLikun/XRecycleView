package com.ashlikun.xrecycleview.nested;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * 作者　　: 李坤
 * 创建时间: 2019/12/13　11:30
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：内部滑动控件的处理工具类
 */
public class NestedOnChildTouch {
    private static final int INVALID_POINTER = -1;
    //滚动的手指触摸id
    int mScrollPointerId = INVALID_POINTER;
    float mInitialTouchX;
    float mInitialTouchY;
    View view;
    int touchSlop;

    public NestedOnChildTouch(View view) {
        this.view = view;
        touchSlop = ViewConfiguration.get(view.getContext()).getScaledPagingTouchSlop();
    }

    /**
     * @param e
     * @return true:拦截掉
     */
    public boolean onTouchEvent(MotionEvent e) {
        final int actionIndex = e.getActionIndex();
        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mScrollPointerId = e.getPointerId(0);
                mInitialTouchX = e.getX();
                mInitialTouchY = e.getY();
                // 必须加上这个，让 RecyclerView 也要处理滑动冲突才行
                view.getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mInitialTouchX = e.getX(actionIndex);
                mInitialTouchY = e.getY(actionIndex);
                mScrollPointerId = e.getPointerId(actionIndex);
                // 必须加上这个，让 RecyclerView 也要处理滑动冲突才行
                view.getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                final int index = e.findPointerIndex(mScrollPointerId);
                if (index < 0) {
                    Log.e(view.getClass().getSimpleName(), "Error processing scroll; pointer index for id "
                            + mScrollPointerId + " not found. Did any MotionEvents get skipped?");
                    return false;
                }
                final float x = e.getX(index);
                final float y = e.getY(index);
                float dx = x - mInitialTouchX;
                float dy = y - mInitialTouchY;
                //通过距离差判断方向
                String orientation = getOrientation(dx, dy);
                switch (orientation) {
                    case "r":
                    case "l":
                        // 要求左右滑动很大才能触发父类的左右滑动
                        if (Math.abs(dx) > touchSlop * 1.5f) {
                            //父类处理
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            return true;
                        } else {
                            view.getParent().requestDisallowInterceptTouchEvent(true);
                        }
                        break;
                    case "t":
                    case "b":
                        //上下滚动子类处理
                        view.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                mScrollPointerId = INVALID_POINTER;
                break;
            }
        }
        return false;
    }

    private String getOrientation(float dx, float dy) {
        if (Math.abs(dx * 0.7f) > Math.abs(dy)) {
            //X轴移动
            return dx > 0 ? "r" : "l";//右,左
        } else {
            //Y轴移动
            return dy > 0 ? "b" : "t";//下//上
        }
    }
}
