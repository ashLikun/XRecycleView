package com.ashlikun.xrecycleview

/**
 * Created by Administrator on 2016/5/11.
 */
interface ConfigChang {
    /**
     * 设置页脚没有数据的文字
     *
     * @param autoloaddingNoData
     */
    fun setNoDataFooterText(autoloaddingNoData: String = "")

    /**
     * 设置加载更多使能
     */
    var isLoadMoreEnabled: Boolean
}