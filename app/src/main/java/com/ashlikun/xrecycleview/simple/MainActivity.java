package com.ashlikun.xrecycleview.simple;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.ashlikun.adapter.recyclerview.multiltem.MultipleAdapter;
import com.ashlikun.xrecycleview.SuperRecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    MultipleAdapter adapter;
    ArrayList<NeibuData> neibuData = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SuperRecyclerView recycleView = (SuperRecyclerView) findViewById(R.id.recycleView);
        recycleView.setLayoutManager(new VirtualLayoutManager(this));
        adapter = new MultipleAdapter((VirtualLayoutManager) recycleView.getRecyclerView().getLayoutManager());
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
        adapter.addAdapter(new MyAdapter.AdapterItem3(this, neibu3Data));
//        adapter.addAdapter(new MyAdapter.AdapterItemSing(this));

        recycleView.setAdapter(adapter);

    }
}
