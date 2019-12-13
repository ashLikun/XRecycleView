package com.ashlikun.xrecycleview.nested;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.ashlikun.xrecycleview.RecyclerViewExtend;

/**
 * 作者　　: 李坤
 * 创建时间: 2019/12/13　11:11
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：RecyclerView里面嵌套ViewPager的时候的内部RecyclerView
 */
public class NestedInnerRecyclerView extends RecyclerViewExtend {
    protected NestedOnChildTouch childTouch = new NestedOnChildTouch(this);

    public NestedInnerRecyclerView(Context context) {
        super(context);
    }

    public NestedInnerRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NestedInnerRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (childTouch.onTouchEvent(e)) {
            return false;
        }
        return super.onTouchEvent(e);
    }
}
