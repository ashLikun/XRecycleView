package com.ashlikun.xrecycleview.nested;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

/**
 * 作者　　: 李坤
 * 创建时间: 2019/12/13　11:17
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：RecyclerView里面嵌套ViewPager的时候的内部ScrollView
 */
public class NestedInnerScrollView extends NestedScrollView {
    protected NestedOnChildTouch childTouch = new NestedOnChildTouch(this);

    public NestedInnerScrollView(@NonNull Context context) {
        super(context);
    }

    public NestedInnerScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NestedInnerScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (childTouch.onTouchEvent(e)) {
            return false;
        }
        return super.onTouchEvent(e);
    }
}
