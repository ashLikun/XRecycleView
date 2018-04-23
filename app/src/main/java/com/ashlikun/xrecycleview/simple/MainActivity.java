package com.ashlikun.xrecycleview.simple;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.ashlikun.adapter.recyclerview.multiltem.MultipleAdapter;
import com.ashlikun.adapter.recyclerview.multiltem.SingAdapter;
import com.ashlikun.xrecycleview.SuperRecyclerView;
import com.ashlikun.xrecycleview.listener.RecycleViewSwipeListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecycleViewSwipeListener {
    MultipleAdapter adapter;
    ArrayList<NeibuData> neibuData = new ArrayList<>();
    SuperRecyclerView recycleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recycleView = (SuperRecyclerView) findViewById(R.id.recycleView);
        recycleView.setLayoutManager(new VirtualLayoutManager(this));
        recycleView.setOnLoaddingListener(this);
        recycleView.setOnRefreshListener(this);
        adapter = new MultipleAdapter((VirtualLayoutManager) recycleView.getRecyclerView().getLayoutManager());
        recycleView.setAdapter(adapter);
        recycleView.setRefreshing(true);
    }

    @Override
    public void onLoadding() {
        recycleView.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadData(false);
            }
        }, 3000);
    }

    @Override
    public void onRefresh() {
        recycleView.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadData(true);
            }
        }, 3000);
    }

    public void loadData(boolean isStart) {
        if (isStart) {
            adapter.clear();
            recycleView.getPageHelp().setPageInfo(1, 5);
            recycleView.setRefreshing(false);
            neibuData.clear();
            for (int i = 0; i < 10; i++) {
                neibuData.add(new NeibuData("我是第一种" + i));
            }
            adapter.addAdapter(new MyAdapter.AdapterItem1(this, neibuData));


            ArrayList<Neibu2Data> neibu2Data = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                neibu2Data.add(new Neibu2Data("我是第二种" + i));
            }
            adapter.addAdapter(new MyAdapter.AdapterItem2(this, neibu2Data));


            ArrayList<Neibu3Data> neibu3Data = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                neibu3Data.add(new Neibu3Data("我是第三种" + i));
            }
            adapter.addAdapter(new MyAdapter.AdapterItem3(this, neibu3Data).setViewType("end"));
//        adapter.addAdapter(new MyAdapter.AdapterItemSing(this));
        } else {
            recycleView.getPageHelp().nextPage();
            SingAdapter a = adapter.findAdapterByViewType("end");
            ArrayList<Neibu3Data> neibu3Data = new ArrayList<>();
            for (int i = a.getItemCount(); i < a.getItemCount() + 20; i++) {
                neibu3Data.add(new Neibu3Data("我是第三种" + i));
            }
            a.addDatas(neibu3Data);
            a.notifyDataSetChanged();
        }
    }
}
