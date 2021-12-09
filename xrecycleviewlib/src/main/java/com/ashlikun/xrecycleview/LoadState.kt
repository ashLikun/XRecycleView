package com.ashlikun.xrecycleview

/**
 * Created by Administrator on 2016/3/14.
 */
enum class LoadState {
    Init,  //初始化
    Loadding,  //加载中
    Complete,  //加载完成
    NoData,  // 没有更多数据加载
    Hint,  //隐藏
    Failure //加载失败
}