package com.ashlikun.xrecycleview;

/**
 * Created by Administrator on 2016/5/11.
 */
public interface ConfigChang {


    /**
     * 设置页脚没有数据的文字
     *
     * @param autoloaddingNoData
     */
    void setNoDataFooterText(String autoloaddingNoData);

    /**
     * 设置加载更多使能
     */
    boolean isLoadMoreEnabled();

    void setLoadMoreEnabled(boolean loadMoreEnabled);
}
