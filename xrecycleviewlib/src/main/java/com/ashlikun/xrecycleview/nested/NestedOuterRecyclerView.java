package com.ashlikun.xrecycleview.nested;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.NestedScrollingParentHelper;

import com.ashlikun.xrecycleview.RecyclerViewExtend;

/**
 * 作者　　: 李坤
 * 创建时间: 2019/12/13　10:31
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：RecyclerView里面嵌套ViewPager的时候的外部RecyclerView
 */
public class NestedOuterRecyclerView extends RecyclerViewExtend implements NestedScrollingParent3 {
    protected NestedScrollingParentHelper parentHelper = new NestedScrollingParentHelper(this);
    protected View mNestedScrollingTarget;
    protected View mNestedScrollingChildView;
    /**
     * 子view是否展开的监听
     */
    private ToTopListener topListener;


    public NestedOuterRecyclerView(Context context) {
        super(context);
    }

    public NestedOuterRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NestedOuterRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 设置是否到达顶部的监听
     *
     * @param topListener
     */
    public void setTopListener(ToTopListener topListener) {
        this.topListener = topListener;
    }


    public boolean isTop() {
        if (topListener != null) {
            return !topListener.isTop();
        }
        //recyclerView是否到达底部,false:到底部了
        return canScrollVertically(1);
    }

    /**
     * 是否接受嵌套滚动
     *
     * @return true:接受
     */
    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        return true;
    }

    /**
     * 当接受嵌套滚动
     */
    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int nestedScrollAxes, int type) {
        parentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes, type);
        mNestedScrollingTarget = target;
        mNestedScrollingChildView = child;
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
    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        if (mNestedScrollingChildView == null) {
            return;
        }
        // 如果是向上的
        if (dy >= 0) {
            // ViewPager当前所处位置没有在顶端，交由父类去滑动
            if (isTop()) {
                consumed[0] = 0;
                consumed[1] = dy;
                scrollBy(0, dy);
            }
        }
        // 如果是向下的
        else {
            //ViewPager当前所处位置没有在顶端，交由父类去滑动
            if (isTop()) {
                if (!target.canScrollVertically(-1)) {
                    consumed[0] = 0;
                    consumed[1] = dy;
                    scrollBy(0, dy);
                }
            } else {
                if (!target.canScrollVertically(-1)) {
                    consumed[0] = 0;
                    consumed[1] = dy;
                    scrollBy(0, dy);
                }
            }
        }
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type, @NonNull int[] consumed) {

    }


    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        parentHelper.onStopNestedScroll(target, type);
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {

    }

    public interface ToTopListener {

        /**
         * 是否到顶部了，该ViewPager内部滚动了
         *
         * @return true:到顶部了
         */
        boolean isTop();
    }
}
