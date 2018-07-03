package com.ashlikun.xrecycleview.simple;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.ashlikun.adapter.ViewHolder;
import com.ashlikun.adapter.recyclerview.CommonAdapter;
import com.ashlikun.xrecycleview.RecyclerViewExtend;
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
    RecyclerViewExtend recycleView;
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
        recycleView.setLayoutManager(new LinearLayoutManager(this));
        recycleView.setAdapter(adapter);
    }

    @Override
    public void onLoadding() {

    }

    @Override
    public void onRefresh() {

    }

    public void onClick(View view) {
        list.clear();
        for (int i = 0; i < 10; i++) {
            list.add("");
        }
        adapter.notifyDataSetChanged();
    }
}
