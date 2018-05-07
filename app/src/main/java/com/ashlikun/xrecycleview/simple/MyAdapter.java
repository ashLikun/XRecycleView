package com.ashlikun.xrecycleview.simple;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.FloatLayoutHelper;
import com.alibaba.android.vlayout.layout.GridLayoutHelper;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.ashlikun.adapter.ViewHolder;
import com.ashlikun.adapter.animation.SlideInBottomAnimation;
import com.ashlikun.adapter.recyclerview.multiltem.SimpleSingAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者　　: 李坤
 * 创建时间: 2018/4/9 0009　下午 5:17
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */

public class MyAdapter {
    public static class AdapterItem1 extends SimpleSingAdapter<NeibuData> {

        public AdapterItem1(Context context, List<NeibuData> datas) {
            super(context);
            setDatas(datas);
        }

        @Override
        public void onViewAttachedToWindow(ViewHolder holder) {
            super.onViewAttachedToWindow(holder);
        }

        @Override
        public void onViewDetachedFromWindow(ViewHolder holder) {
            super.onViewDetachedFromWindow(holder);
        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            super.onDetachedFromRecyclerView(recyclerView);
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public int getLayoutId() {
            return R.layout.item_view;
        }

        @Override
        public LayoutHelper onCreateLayoutHelper() {
            GridLayoutHelper helper = new GridLayoutHelper(3);
            return helper;
        }

        @Override
        public void convert(ViewHolder holder, NeibuData neibuData) {
            holder.setText(R.id.textView, neibuData.name);
        }
    }

    public static class AdapterItem2 extends SimpleSingAdapter<Neibu2Data> {

        public AdapterItem2(Context context, List<Neibu2Data> datas) {
            super(context);
            setDatas(datas);
        }

        @Override
        public int getLayoutId() {
            return R.layout.item_view1;
        }

        @Override
        public LayoutHelper onCreateLayoutHelper() {
            LinearLayoutHelper helper = new LinearLayoutHelper(20);
            return helper;
        }

        @Override
        public void convert(ViewHolder holder, Neibu2Data neibuData) {
            holder.setText(R.id.textView, neibuData.name);
        }
    }

    public static class AdapterItem3 extends SimpleSingAdapter<Neibu3Data> {

        public AdapterItem3(Context context, List<Neibu3Data> datas) {
            super(context);
            setDatas(datas);
            setCustomAnimation(new SlideInBottomAnimation());
        }

        @Override
        public int getLayoutId() {
            return R.layout.item_view2;
        }

        @Override
        public LayoutHelper onCreateLayoutHelper() {
            LinearLayoutHelper helper = new LinearLayoutHelper(20);
            return helper;
        }

        @Override
        public void convert(ViewHolder holder, Neibu3Data neibuData) {
            holder.setText(R.id.textView, neibuData.name);
        }
    }

    public static class AdapterItemSing extends SimpleSingAdapter<String> {

        public AdapterItemSing(Context context) {

            super(context);

            ArrayList a = new ArrayList();
            a.add("我啊");
            setDatas(a);
            setCustomAnimation(new SlideInBottomAnimation());
        }

        @Override
        public int getLayoutId() {
            return R.layout.item_viewsing;
        }

        @Override
        public LayoutHelper onCreateLayoutHelper() {
            FloatLayoutHelper helper = new FloatLayoutHelper();
            return helper;
        }

        @Override
        public void convert(ViewHolder holder, String neibuData) {
            holder.setText(R.id.textView, neibuData);
        }
    }


//    @Override
//    public int getLayoutId(int itemType) {
//        if (itemType == 1) {
//            return R.layout.item_view;
//        } else if (itemType == 2) {
//            return R.layout.item_view1;
//        } else if (itemType == 3) {
//            return R.layout.item_view2;
//        }
//        return 0;
//    }
//
//    @Override
//    public int getItemViewType(int position, Data data) {
//        return data.type;
//    }
//
//    @Override
//    public MultiltemViewHolder createViewHolder(Context context, View view, int itemType) {
//        if (itemType == 1) {
//            return new Neibu1ViewHolder(context, view);
//        } else if (itemType == 2) {
//            return new Neibu2ViewHolder(context, view);
//        } else if (itemType == 3) {
//            return new Neibu3ViewHolder(context, view);
//        }
//        return null;
//    }
}
