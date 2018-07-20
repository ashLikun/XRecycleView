package com.ashlikun.xrecycleview.simple;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.ashlikun.adapter.ViewHolder;
import com.ashlikun.adapter.recyclerview.CommonAdapter;
import com.ashlikun.xrecycleview.SuperRecyclerView;
import com.ashlikun.xrecycleview.divider.HorizontalDividerItemDecoration;
import com.ashlikun.xrecycleview.divider.VerticalDividerItemDecoration;
import com.ashlikun.xrecycleview.listener.RecycleViewSwipeListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者　　: 李坤
 * 创建时间: 2018/6/22 0022　下午 3:06
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */
public class Main2Activity extends AppCompatActivity implements RecycleViewSwipeListener {

    List<String> list = new ArrayList<>();
    SuperRecyclerView recycleView;
    CommonAdapter adapter = new CommonAdapter<String>(this, R.layout.item_view_main2, list) {
        @Override
        public void convert(ViewHolder holder, String s) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        recycleView = findViewById(R.id.recycleView);
        recycleView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .size(30)
                .showFirstTopDivider(60)
                .color(0xffff0000)
                .build());
        recycleView.addItemDecoration(new VerticalDividerItemDecoration.Builder(this)
                .size(30)
                .color(0xffff0000)
                .build());
        recycleView.setOnLoaddingListener(this);
        recycleView.setOnRefreshListener(this);
        recycleView.setLayoutManager(new GridLayoutManager(this, 5));
        recycleView.setAdapter(adapter);
    }

    @Override
    public void onLoadding() {
        recycleView.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadData(true);
                recycleView.setRefreshing(false);
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

    private void loadData(boolean b) {
        for (int i = 0; i < 10; i++) {
            list.add("");
        }
        adapter.notifyDataSetChanged();
    }

    public void onClick(View view) {
        list.clear();
        for (int i = 0; i < 10; i++) {
            list.add("");
        }
        adapter.notifyDataSetChanged();
    }
}
