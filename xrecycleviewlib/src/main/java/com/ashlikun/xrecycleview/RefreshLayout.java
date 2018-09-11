package com.ashlikun.xrecycleview;

import android.support.annotation.ColorInt;

/**
 * 作者　　: 李坤
 * 创建时间:2017/4/12 0012　16:10
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：为了适应不同的下拉刷新布局 ，这里抽离出公共的属性与方法，其他的刷新View必须实现这个接口
 */

public interface RefreshLayout {
    /**
     * 作者　　: 李坤
     * 创建时间: 2017/4/12 0012 16:13
     * <p>
     * 方法功能：是否刷新完成
     */

    public boolean isRefreshing();

    /**
     * 作者　　: 李坤
     * 创建时间: 2017/4/12 0012 16:20
     * <p>
     * 方法功能：设置颜色值基本上是给官方下拉用的
     */

    public void setColorSchemeColors(@ColorInt int... colors);

    /**
     * 作者　　: 李坤
     * 创建时间: 2017/4/12 0012 16:22
     * <p>
     * 方法功能：设置下拉刷新的监听
     */

    public void setOnRefreshListener(OnRefreshListener listener);

    /**
     * 作者　　: 李坤
     * 创建时间: 2017/4/12 0012 16:24
     * <p>
     * 方法功能：设置刷新动作
     */

    public void setRefreshing(boolean refreshing);


    /**
     * 作者　　: 李坤
     * 创建时间: 2017/8/3 17:43
     * 邮箱　　：496546144@qq.com
     * <p>
     * 方法功能：设置是否可以刷新
     */

    public void setEnabled(boolean refreshing);

    /**
     * 是否禁用下拉刷新
     */
    public boolean isEnabled();

    public interface OnRefreshListener {
        void onRefresh();
    }
}
