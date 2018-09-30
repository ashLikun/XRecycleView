package com.ashlikun.xrecycleview;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.widget.OverScroller;

import java.lang.reflect.Field;

/**
 * 作者　　: 李坤
 * 创建时间: 2018/9/30　16:56
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */
public class XRecycleUtils {
    public static AppBarLayout.Behavior findAppBarLayouBehavior(View view) {
        if (view == null) {
            return null;
        }
        View findView = findView(view, CoordinatorLayout.class);
        if (findView != null) {
            CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findView;
            for (int i = 0; i < coordinatorLayout.getChildCount(); i++) {
                View cv = coordinatorLayout.getChildAt(i);
                CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) cv.getLayoutParams()).getBehavior();
                if (behavior != null) {
                    if (behavior instanceof AppBarLayout.Behavior) {
                        return (AppBarLayout.Behavior) behavior;
                    }
                }
            }
        }
        return null;
    }

    public static View findView(View view, Class c) {
        if (view == null) {
            return null;
        }
        if (c.isInstance(view)) {
            return view;
        } else if (view.getParent() != null) {
            return findView((View) view.getParent(), c);
        } else {
            return null;
        }
    }

    /**
     * 清空OverScroller
     */
    public static void cleanVelocityTracker(AppBarLayout.Behavior headerBehavior) {
        if (headerBehavior == null) {
            return;
        }
        //根据 对象和属性名通过反射 调用上面的方法获取 Field对象
        //Field field = getDeclaredField("mVelocityTracker");
        Field field = getDeclaredField(headerBehavior.getClass(), "mScroller");
        //抑制Java对其的检查
        if (field != null) {
            field.setAccessible(true);
            try {
                //将 object 中 field 所代表的值 设置为 value
                OverScroller v = (OverScroller) field.get(headerBehavior);
                if (v != null) {
                    v.forceFinished(true);
                }
                field.set(headerBehavior, null);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 循环向上转型, 获取对象的 DeclaredField
     *
     * @param fieldName : 父类中的属性名
     * @return 父类中的属性对象
     */
    public static Field getDeclaredField(Class clazz, String fieldName) {
        Field field = null;
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredField(fieldName);
                return field;
            } catch (Exception e) {
            }
        }
        return null;
    }
}
