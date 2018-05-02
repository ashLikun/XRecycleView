package com.ashlikun.xrecycleview;

import android.content.Context;

public class PageHelp {
    // 服务器数据的第几页
    private int currentPage = 1;
    // 一共多少页
    private int recordPage = 0;

    private Context context;


    private StatusChangListener statusChangListener;

    public PageHelp(Context context) {
        this.context = context;
    }

    public void setStatusChangListener(StatusChangListener statusChangListener) {
        this.statusChangListener = statusChangListener;
    }

    /**
     * 显示没有数据
     */
    public void showNoData() {
        if (statusChangListener != null) {
            statusChangListener.noData();
        }
    }

    /**
     * 完成当前页
     * 可以加载下一页
     */
    public void showComplete() {
        if (statusChangListener != null) {
            statusChangListener.complete();
        }
    }

    /**
     * 把数据清空 恢复到开始时的状态
     */
    public void clear() {
        currentPage = 1;
        recordPage = 0;
        // 服务器数据的第几页
        if (statusChangListener != null) {
            statusChangListener.init();
        }
    }

    /**
     * 设置页数信息
     * 会自动下一页+1
     */
    public void setPageInfo(int currentPage, int recordPage) {
        this.currentPage = currentPage;
        this.recordPage = recordPage;
        nextPage();
    }

    /**
     * 设置页数信息
     * 不会自动下一页
     */
    public void setPageInfoNoNext(int currentPage, int recordPage) {
        this.currentPage = currentPage;
        this.recordPage = recordPage;
        if (!isNext()) {
            showNoData();
        } else {
            showComplete();
        }
    }

    /**
     * 设置分页数据为下一页
     * 为下次加载做准备
     * 会自动向+1
     */

    public void nextPage() {
        this.currentPage++;
        if (!isNext()) {
            showNoData();
        } else {
            showComplete();
        }
    }

    /**
     * 是否可以下一页
     */
    public boolean isNext() {
        //如果当前页数>总页数,就说明没有数据了
        return this.currentPage <= this.recordPage;
    }

    /**
     * 获取当前页
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * 获取总页数
     */
    public int getRecordPage() {
        return recordPage;
    }

    /**
     * 设置当前页信息
     */
    public void setCurrentPage(int currentPage) {
        setPageInfo(currentPage, recordPage);
    }

    /**
     * 设置总页数
     */
    public void setRecordPage(int recordPage) {
        setPageInfo(currentPage, recordPage);
    }


    public interface OnDataListenerCallback {
        //告诉自动加载的view
        void onNoDataCallback();

        //告诉自动加载的view
        void onClearDataCallback();
    }

}
