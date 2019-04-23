package com.ashlikun.xrecycleview;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.core.view.NestedScrollingChild;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.ashlikun.swiperefreshlayout.SwipeRefreshLayout;


/**
 * 作者　　: 李坤
 * 创建时间: 12:49 Administrator
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：自定义的刷新控件
 */

public class SuperRefreshLayout extends SwipeRefreshLayout
        implements RefreshLayout, SwipeRefreshLayout.OnRefreshListener {

    RefreshLayout.OnRefreshListener mListener;
    boolean isMOVE = false;

    public SuperRefreshLayout(Context context) {
        this(context, null);
    }

    public SuperRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray array = context.getTheme().obtainStyledAttributes(new int[]{R.attr.SwipeRefreshLayout_Color1, R.attr.SwipeRefreshLayout_Color2, R.attr.SwipeRefreshLayout_Color3, R.attr.SwipeRefreshLayout_Color4});
        setColorSchemeColors(array.getColor(0, 0xff0000), array.getColor(1, 0xff0000), array.getColor(2, 0xff0000), array.getColor(3, 0xff0000));
        array.recycle();
        setRefreshStyle(com.ashlikun.swiperefreshlayout.SwipeRefreshLayout.FLOAT);
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isMOVE) {
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 与一些其他的滑动控件滑动的时候，屏蔽下拉
     *
     * @param view
     */
    public void addNestedView(View view) {
        if (view instanceof NestedScrollingChild) {
            ((NestedScrollingChild) view).setNestedScrollingEnabled(false);
        }
        view.setOnTouchListener(onTouchListener);
    }

    /**
     * 还原
     *
     * @param view
     */
    public void removeNestedView(View view) {
        if (view instanceof NestedScrollingChild) {
            ((NestedScrollingChild) view).setNestedScrollingEnabled(true);
        }
        view.setOnTouchListener(null);
    }

    OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    isMOVE = true;
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    isMOVE = false;
                    break;
            }
            return false;
        }
    };

    @Override
    public void setOnRefreshCallback(RefreshLayout.OnRefreshListener listener) {
        this.mListener = listener;
        super.setOnRefreshListener(this);
    }

    /**
     * 作者　　: 李坤
     * 创建时间: 2017/4/12 0012 16:29
     * <p>
     * 方法功能：调用上面的之定义监听
     */
    @Deprecated
    @Override
    public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener listener) {
        new Exception("调用上面的之定义监听setOnRefreshListener(RefreshLayout.OnRefreshListener listener)");
    }

    @Override
    public void onRefresh() {
        if (mListener != null) {
            mListener.onRefresh();
        }
    }
}
