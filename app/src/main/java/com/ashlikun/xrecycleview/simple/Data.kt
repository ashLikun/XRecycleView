package com.ashlikun.xrecycleview.simple

import com.ashlikun.adapter.recyclerview.section.SectionEntity

/**
 * 作者　　: 李坤
 * 创建时间: 2018/4/9 0009　下午 5:18
 * 邮箱　　：496546144@qq.com
 *
 *
 * 功能介绍：
 */

data class TestData(var type: String, var datas: List<NeibuData>) {
}

data class MultiItemData(var type: String, var data: NeibuData) {
}

data class NeibuData(var name: String, override val isHeader: Boolean = false) : SectionEntity
