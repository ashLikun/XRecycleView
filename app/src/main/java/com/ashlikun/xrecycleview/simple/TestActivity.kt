package com.ashlikun.xrecycleview.simple

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.vlayout.VirtualLayoutManager
import com.ashlikun.adapter.recyclerview.common.CommonAdapter
import com.ashlikun.adapter.recyclerview.section.SectionAdapter
import com.ashlikun.adapter.recyclerview.vlayout.MultipleAdapterHelp
import com.ashlikun.adapter.recyclerview.vlayout.mode.LayoutStyle
import com.ashlikun.xrecycleview.listener.RecycleViewSwipeListener
import com.ashlikun.xrecycleview.simple.databinding.*
import java.util.*

class TestActivity : AppCompatActivity(), RecycleViewSwipeListener {

    var data = ArrayList<TestData>()
    val binding by lazy {
        ActivityTestBinding.inflate(layoutInflater)
    }
    val help by lazy {
        MultipleAdapterHelp(binding.recyclerView.recyclerView)
    }
    val adapter by lazy {
        help.adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.apply {
            recyclerView.setLayoutManager(VirtualLayoutManager(this@TestActivity))
            recyclerView.setOnLoaddingListener(this@TestActivity)
            recyclerView.setOnRefreshListener(this@TestActivity)
            recyclerView.setRefreshing(true)
        }

    }

    override fun onLoadding() {
        binding.recyclerView.postDelayed({ loadData(false) }, 3000)
    }

    override fun onRefresh() {
        binding.recyclerView.postDelayed({ loadData(true) }, 3000)
    }

    fun loadData(isStart: Boolean) {
        if (isStart) {
            data.clear()
            adapter.clear()
            binding.recyclerView.pageHelp.setPageInfo(1, 5)
            binding.recyclerView.setRefreshing(false)
            val neibu1 = mutableListOf<NeibuData>()
            for (j in 0..10) {
                neibu1.add(NeibuData("我是type1,数据是$j"))
            }
            data.add(TestData("type1", neibu1))
            val neibu2 = mutableListOf<NeibuData>()
            for (j in 0..10) {
                neibu2.add(NeibuData("我是type2,数据是$j"))
            }
            data.add(TestData("type2", neibu2))
            val neibu3 = mutableListOf<NeibuData>()
            for (j in 0..10) {
                neibu3.add(NeibuData("我是type3,数据是$j"))
            }
            data.add(TestData("type3", neibu3))
            bindInitUi()
        } else {
            for (i in 0..10) {
                val neibu = mutableListOf<NeibuData>()
                for (j in 0..10) {
                    neibu.add(NeibuData("我是goods,数据是$j"))
                }
                data.add(TestData("goods", neibu))
            }
            binding.recyclerView.pageHelp.nextPage()
            bindGoodsUi()
        }
    }

    private fun bindInitUi() {
        adapter.addAdapters(data.map {
            when (it.type) {
                "type1" ->
                    CommonAdapter(this, it.datas, ItemView1Binding::class.java,
                        layoutStyle = LayoutStyle(single = true),
                        onItemClick = {
                            Toast.makeText(this, it.name, Toast.LENGTH_LONG).show()
                        }) { holder, t ->
                        holder.binding<ItemView1Binding>().run {
                            textView.setTextColor(0xffff3300.toInt())
                            textView.text = t?.name
                        }
                    }
                "type2" ->
                    CommonAdapter(this, it.datas, ItemView1Binding::class.java,
                        layoutStyle = LayoutStyle(spanCount = 3),
                        onItemClick = {
                            Toast.makeText(this, it.name, Toast.LENGTH_LONG).show()
                        }) { holder, t ->
                        holder.binding<ItemView1Binding>().run {
                            textView.setTextColor(0xff0fff00.toInt())
                            textView.text = t?.name
                        }
                    }
                "type3" ->
                    SectionAdapter(this, it.datas, ItemViewBinding::class.java,
                        layoutStyle = LayoutStyle(spanCount = 3),
                        onItemClick = {
                            Toast.makeText(this, it.name, Toast.LENGTH_LONG).show()
                        },
                        bndingHead = ItemHeaderBinding::class.java,
                        convertHeader = { holder, t ->
                            holder.binding<ItemHeaderBinding>().run {
                                tvHeader.text = t?.name
                            }
                        }
                    ) { holder, t ->
                        holder.binding<ItemViewBinding>().run {
                            textView.text = t?.name
                        }
                    }
                else ->
                    CommonAdapter(this, it.datas, ItemViewBinding::class.java,
                        onItemClick = {
                            Toast.makeText(this, it.name, Toast.LENGTH_LONG).show()
                        }) { holder, t ->
                        holder.binding<ItemViewBinding>().run {
                            textView.text = t?.name
                        }
                    }
            }
        })
    }

    private fun bindGoodsUi() {

        if (!adapter.haveByViewType("goods")) {
            adapter.addAdapter(CommonAdapter(this,
                data.find { it.type == "goods" }?.datas,
                ItemView1Binding::class.java,
                onItemClick = {
                    Toast.makeText(this, it.name, Toast.LENGTH_LONG).show()
                }) { holder, t ->
                holder.binding<ItemView1Binding>().run {
                    textView.setTextColor(0xfff13300.toInt())
                    textView.text = t?.name
                }
            })
        }

        adapter.findByViewTypeX<CommonAdapter<NeibuData>>("goods")?.apply {
            dataHandle.setDatas(data.find { it.type == "goods" }?.datas)
        }
    }
}