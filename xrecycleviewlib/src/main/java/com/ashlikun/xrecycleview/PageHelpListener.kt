package com.ashlikun.xrecycleview

/**
 * @author　　: 李坤
 * 创建时间: 2020/12/30 17:10
 * 邮箱　　：496546144@qq.com
 *
 *
 * 功能介绍：
 */
interface PageHelpListener {
    /**
     * 获取内容个数
     */
    val itemCount: Int

    /**
     * PageHelp
     */
    val pageHelp: PageHelp

    /**
     * 没有更多数据加载
     */
    fun noData(message: String = "")

    /**
     * 初始化状态
     */
    fun init(message: String = "")

    /**
     * 加载完成
     */
    fun complete(message: String = "")

    /**
     * 加载失败
     */
    fun failure(message: String = "")


}