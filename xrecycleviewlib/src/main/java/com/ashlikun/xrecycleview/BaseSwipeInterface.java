package com.ashlikun.xrecycleview;

/**
 * Created by Administrator on 2016/8/8.
 */

public interface BaseSwipeInterface {
    void setRefreshLayout(RefreshLayout refreshLayout);

    public RefreshLayout getRefreshLayout();

    void setOnLoaddingListener(OnLoaddingListener swipeRefreshLayout);

    PageHelp getPageHelp();


    PageHelpListener getPageHelpListener();
}
