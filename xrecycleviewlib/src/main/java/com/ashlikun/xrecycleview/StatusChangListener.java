package com.ashlikun.xrecycleview;

/**
 * Created by Administrator on 2016/3/17.
 */
public interface StatusChangListener {

    /**
     * 没有更多数据加载
     */
    void noData();

    /**
     * 初始化状态
     */
    void init();

    /**
     * 加载完成
     */
    void complete();

    /**
     * 加载失败
     */
    void failure();
}
