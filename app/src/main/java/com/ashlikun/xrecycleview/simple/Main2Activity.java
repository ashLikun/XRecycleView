package com.ashlikun.xrecycleview.simple;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ashlikun.adapter.ViewHolder;
import com.ashlikun.adapter.recyclerview.CommonAdapter;
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
    RecyclerView recycleView;
    CommonAdapter adapter = new CommonAdapter<String>(this, R.layout.item_view_main2, list) {
        @Override
        public void convert(ViewHolder holder, String s) {
            ScaleImageView imageView = holder.getView(R.id.image);
//            if (holder.getPositionInside() >= getItemCount() - 1) {
//                imageView.setRatio(1 / 10f);
//            } else {
//                imageView.setRatio((float) (Math.max(Math.random() * 2, 0.1)));
//            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for (int i = 0; i < 30; i++) {
            list.add(i + "");
        }
        setContentView(R.layout.activity_main2);
        recycleView = findViewById(R.id.recycleView);
        recycleView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .size(20)
                .showFirstTopDivider()
                .showFirstTopDivider(100)
                .color(0xffff0000)
                .build());
        recycleView.addItemDecoration(new VerticalDividerItemDecoration.Builder(this)
                .size(20)
                .color(0xffff0000)
                .build());
//        recycleView.addItemDecoration(new DividerGridItemDecoration.Builder(this)
//                .size(50)
//                .color(0xffff0000)
//                .build());
//        recycleView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        recycleView.setLayoutManager(new GridLayoutManager(this, 2));
//        recycleView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recycleView.setAdapter(adapter);
    }

    @Override
    public void onLoadding() {
        recycleView.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadData(true);
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
