package com.ashlikun.xrecycleview

import android.content.Context
import kotlin.math.max

class PageHelp(val context: Context) {
    // 服务器数据的第几页
    var currentPage = 1
        set(value) {
            field = max(0, value)
        }

    // 一共多少页
    var recordPage = 0
        set(value) {
            field = max(0, value)
        }

    /**
     * 是否可以下一页
     * 如果当前页数>总页数,就说明没有数据了
     */
    val isNext: Boolean
        get() = currentPage <= recordPage

    var pageHelpListeners: MutableList<PageHelpListener> = mutableListOf()

    fun addStatusChangListener(statusChangListener: PageHelpListener) {
        if (!pageHelpListeners.contains(statusChangListener)) {
            pageHelpListeners.add(statusChangListener)
        }
    }

    fun removeStatusChangListener(statusChangListener: PageHelpListener) {
        pageHelpListeners.remove(statusChangListener)
    }

    /**
     * 显示没有数据
     */
    fun showNoData() {
        for (s in pageHelpListeners) {
            s.noData()
        }
    }

    /**
     * 完成当前页
     * 可以加载下一页
     */
    fun showComplete() {
        for (s in pageHelpListeners) {
            s.complete()
        }
    }

    /**
     * 把数据清空 恢复到开始时的状态
     */
    fun clear() {
        currentPage = 1
        recordPage = 0
        // 服务器数据的第几页
        for (s in pageHelpListeners) {
            s.init()
        }
    }

    /**
     * 设置页数信息
     * 会自动下一页+1
     *  @param isAddNext 是否+1
     */
    fun setPageInfo(currentPage: Int, recordPage: Int, isAddNext: Boolean = true) {
        this.currentPage = currentPage
        this.recordPage = recordPage
        nextPage(isAddNext)
    }

    /**
     * 设置页数信息
     * 不会自动下一页
     */
    fun setPageInfoNoNext(currentPage: Int, recordPage: Int) {
        this.currentPage = currentPage
        this.recordPage = recordPage
    }

    /**
     * 设置分页数据为下一页
     * 为下次加载做准备
     * 会自动向+1
     * 加载状态会改变
     * @param isAddNext 是否+1
     */
    fun nextPage(isAddNext: Boolean = true) {
        if (isAddNext)
            currentPage++
        if (!isNext) {
            showNoData()
        } else {
            showComplete()
        }
    }
}