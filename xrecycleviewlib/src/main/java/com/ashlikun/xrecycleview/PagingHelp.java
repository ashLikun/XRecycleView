package com.ashlikun.xrecycleview;

import android.content.Context;

import java.util.Collection;

public class PagingHelp {
    // 总条数
    private int recordCount = 0;
    // 服务器数据的第几页
    private int pageindex = 1;
    // 上一次加载的第几条
    private int currentCount = 0;
    // 每页多少条
    private int pageCount = 20;

    private Context context;


    private StatusChangListener statusChangListener;

    public PagingHelp(Context context) {
        this.context = context;
    }

    public void setStatusChangListener(StatusChangListener statusChangListener) {
        this.statusChangListener = statusChangListener;
    }

    public boolean isNextPaging(int result) {
        if (currentCount == result) {
            // 没有跟多数据可以加载
            if (result != 0) {
                showNoData();
                return false;
            }
        } else {
            currentCount = result;
            if (currentCount == pageCount) {
                // 如果加载满一页
                currentCount = 0;
                pageindex++;
            }
        }
        recordCount = pageCount * (pageindex - 1) + currentCount;
        return true;
    }

    public void deleteOneItem() {
        if (currentCount == 0) {
            if (pageindex != 0) {
                pageindex--;
                currentCount = pageCount - 1;
            }
        } else {
            currentCount--;
        }
    }

    public void addOneItem() {
        currentCount++;

        if (currentCount == pageCount) {
            // 如果加载满一页
            currentCount = 0;
            pageindex++;
        }
    }

    public <T> Collection<T> getValidData(Collection<T> c) {
        if (c == null || c.size() == 0) {
            showNoData();
            return c;
        }
        Object[] newPart = c.toArray();
        int max = (currentCount <= newPart.length) ? currentCount
                : newPart.length;
        for (int i = 0; i < max; i++) {
            try {
                c.remove(newPart[i]);
            } catch (IndexOutOfBoundsException e) {
            }

        }
        isNextPaging(newPart.length);
        if (pageindex == 1) {
            showNoData();
        }
        return c;
    }

    public void showNoData() {
        if (statusChangListener != null) {
            statusChangListener.noData();
        }
    }

    /*
     * 把数据清空 恢复到开始时的状态
     */
    public void clear() {
        // 总条数
        recordCount = 0;
        // 服务器数据的第几页
        pageindex = 1;
        // 上一次加载的第几条
        currentCount = 0;
        if (statusChangListener != null) {
            statusChangListener.init();
        }
    }

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public int getPageindex() {
        return pageindex;
    }

    public void setPageindex(int pageindex) {
        this.pageindex = pageindex;
    }

    public int getCurrentCount() {
        return currentCount;
    }

    public void setCurrentCount(int currentCount) {
        this.currentCount = currentCount;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public interface OnDataListenerCallback {
        //告诉自动加载的view
        void onNoDataCallback();

        //告诉自动加载的view
        void onClearDataCallback();
    }

}
