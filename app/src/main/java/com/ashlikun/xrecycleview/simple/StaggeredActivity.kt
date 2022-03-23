package com.ashlikun.xrecycleview.simple

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ashlikun.adapter.recyclerview.common.CommonAdapter
import com.ashlikun.xrecycleview.divider.HorizontalDivider
import com.ashlikun.xrecycleview.divider.VerticalDivider
import com.ashlikun.xrecycleview.listener.RecycleViewSwipeListener
import com.ashlikun.xrecycleview.simple.databinding.ActivityStaggeredBinding
import com.ashlikun.xrecycleview.simple.databinding.ItemViewMain2Binding

/**
 * 作者　　: 李坤
 * 创建时间: 2018/6/22 0022　下午 3:06
 * 邮箱　　：496546144@qq.com
 *
 *
 * 功能介绍：StaggeredGridLayoutManager 分页测试
 */
class StaggeredActivity : AppCompatActivity(), RecycleViewSwipeListener {
    val binding by lazy {
        ActivityStaggeredBinding.inflate(layoutInflater)
    }
    var list: MutableList<String> = ArrayList()
    var adapterx =
        CommonAdapter<String?>(this, list, ItemViewMain2Binding::class.java) { holder, t ->
            holder.binding<ItemViewMain2Binding>().apply {
                image.setRatio((Math.random() * 10).toFloat())
            }
        }

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        for (i in 0..29) {
            list.add(i.toString() + "")
        }
        setContentView(binding.root)
        binding.recyclerView.apply {
            addItemDecoration(HorizontalDivider(context, size = 30, color = 0xffff0000.toInt()))
            addItemDecoration(VerticalDivider(context, size = 30, color = 0xffff0000.toInt()))
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = adapterx
        }
    }

    override fun onLoadding() {
        binding.recyclerView.postDelayed(Runnable { loadData(true) }, 3000)
    }

    override fun onRefresh() {
        binding.recyclerView.postDelayed(Runnable { loadData(true) }, 3000)
    }

    private fun loadData(b: Boolean) {
        for (i in 0..9) {
            list.add("")
        }
        adapterx.notifyDataSetChanged()
    }

    fun onClick(view: View) {
        list.clear()
        for (i in 0..9) {
            list.add("")
        }
        adapterx.notifyDataSetChanged()
    }
}