package com.ashlikun.xrecycleview;

/**
 * @author　　: 李坤
 * 创建时间: 2020/12/30 17:10
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */

public interface PageHelpListener {

    /**
     * 没有更多数据加载
     */
    void noData();
    void noData(String message);

    /**
     * 初始化状态
     */
    void init();
    void init(String message);

    /**
     * 加载完成
     */
    void complete();
    void complete(String message);

    /**
     * 加载失败
     */
    void failure();
    void failure(String message);

    /**
     * 获取内容个数
     */
    int getItemCount();

    /**
     * 获取PageHelp
     * @return
     */
    PageHelp getPageHelp();
}
