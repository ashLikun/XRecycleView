package com.ashlikun.xrecycleview.simple

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.ashlikun.adapter.recyclerview.common.CommonAdapter
import com.ashlikun.xrecycleview.divider.HorizontalDivider
import com.ashlikun.xrecycleview.divider.VerticalDivider
import com.ashlikun.xrecycleview.listener.RecycleViewSwipeListener
import com.ashlikun.xrecycleview.simple.databinding.ActivityPageTestBinding
import com.ashlikun.xrecycleview.simple.databinding.ItemViewMain2Binding

/**
 * 作者　　: 李坤
 * 创建时间: 2018/6/22 0022　下午 3:06
 * 邮箱　　：496546144@qq.com
 *
 *
 * 功能介绍：分页测试
 */
class PageTestActivity : AppCompatActivity(), RecycleViewSwipeListener {
    val binding by lazy {
        ActivityPageTestBinding.inflate(layoutInflater)
    }
    var list: MutableList<String> = ArrayList()
    var adapterx =
        CommonAdapter<String?>(this, list, ItemViewMain2Binding::class.java) { holder, t ->
            holder.binding<ItemViewMain2Binding>().apply {
//                image.setRatio((Math.random() * 10).toFloat())
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        for (i in 0..29) {
            list.add(list.size.toString() + "")
        }
        binding.recyclerView.apply {
            addItemDecoration(HorizontalDivider(context, size = 30, color = 0xffff0000.toInt()))
            addItemDecoration(VerticalDivider(context, size = 30, color = 0xffff0000.toInt()))
            layoutManager = GridLayoutManager(context, 2).apply {
//                reverseLayout = true
            }
            adapter = adapterx
            onLoaddingListener = this@PageTestActivity
            pageHelp.setPageInfo(1, 100)
        }
    }

    override fun onLoadding() {
        binding.recyclerView.postDelayed({ loadData(true) }, 3000)
    }

    override fun onRefresh() {
        binding.recyclerView.postDelayed({ loadData(true) }, 3000)
    }

    private fun loadData(b: Boolean) {
        for (i in 0..9) {
            list.add(list.size.toString() + "")
        }
        adapterx.notifyDataSetChanged()
        binding.recyclerView.pageHelp.nextPage()
    }

    fun onClick(view: View?) {
        list.clear()
        binding.recyclerView.pageHelp.setPageInfo(1, 100)
        for (i in 0..9) {
            list.add(0, list.size.toString() + "")
        }
        adapterx.notifyDataSetChanged()
    }
}