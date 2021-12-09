package com.ashlikun.xrecycleview

/**
 * Created by Administrator on 2016/8/8.
 */
interface BaseSwipeInterface {
    val refreshLayout: RefreshLayout?
    val onLoaddingListener: OnLoaddingListener?
    val pageHelp: PageHelp
    val pageHelpListener: PageHelpListener?
}