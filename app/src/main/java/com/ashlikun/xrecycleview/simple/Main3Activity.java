package com.ashlikun.xrecycleview.simple;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.ashlikun.adapter.ViewHolder;
import com.ashlikun.adapter.recyclerview.CommonAdapter;
import com.ashlikun.xrecycleview.RecyclerViewAutoLoadding;
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
public class Main3Activity extends AppCompatActivity implements RecycleViewSwipeListener {

    List<String> list = new ArrayList<>();
    RecyclerViewAutoLoadding recycleView;
    CommonAdapter adapter = new CommonAdapter<String>(this, R.layout.item_view_main2, list) {
        @Override
        public void convert(ViewHolder holder, String s) {
            ScaleImageView imageView = holder.getView(R.id.image);
            holder.setText(R.id.textView, s);
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
            list.add(list.size() + "");
        }
        setContentView(R.layout.activity_main3);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recycleView = findViewById(R.id.recycleView);
//        recycleView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        recycleView.setLayoutManager(layoutManager);
        layoutManager.setReverseLayout(true);
//        recycleView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recycleView.setAdapter(adapter);
        recycleView.setOnLoaddingListener(this);
        recycleView.getPageHelp().setPageInfo(1, 100);
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
            list.add(list.size() + "");
        }
        adapter.notifyDataSetChanged();
        recycleView.getPageHelp().nextPage();
    }

    public void onClick(View view) {
        list.clear();
        recycleView.getPageHelp().setPageInfo(1, 100);
        for (int i = 0; i < 10; i++) {
            list.add(0, list.size() + "");
        }
        adapter.notifyDataSetChanged();
    }
}
