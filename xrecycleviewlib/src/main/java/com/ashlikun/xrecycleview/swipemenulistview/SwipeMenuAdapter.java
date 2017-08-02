package com.ashlikun.xrecycleview.swipemenulistview;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;

import com.ashlikun.baseadapter.ISwipeAdapter;


/**
 * 作者　　: 李坤
 * 创建时间: 11:29 Administrator
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：listView侧滑菜单  adapter
 */

public class SwipeMenuAdapter implements WrapperListAdapter {

    private BaseAdapter mAdapter;
    private Context mContext;
    private SwipeMenuListView.OnMenuItemClickListener onMenuItemClickListener;

    public SwipeMenuAdapter(Context context, BaseAdapter adapter) {
        mAdapter = adapter;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mAdapter.getCount();
    }

    @Override
    public Object getItem(int position) {
        return mAdapter.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return mAdapter.getItemId(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SwipeMenuLayout layout = null;
        if (convertView == null) {
            View contentView = mAdapter.getView(position, convertView, parent);
            SwipeMenu menu = new SwipeMenu(mContext);
            menu.setViewType(getItemViewType(position));
            menu.setPosition(position);
            createMenu(menu);
            SwipeMenuView menuView = new SwipeMenuView(menu,
                    (SwipeMenuListView) parent);
            SwipeMenuListView listView = (SwipeMenuListView) parent;
            layout = new SwipeMenuLayout(contentView, menuView,
                    listView.getCloseInterpolator(),
                    listView.getOpenInterpolator());
            layout.setPosition(position);
            menuView.setOnSwipeItemClickListener(new MyOnSwipeItemClickListener(layout));
        } else {
            layout = (SwipeMenuLayout) convertView;
            layout.closeMenu();
            layout.setPosition(position);
            mAdapter.getView(position, layout.getContentView(),
                    parent);
        }
        if (mAdapter instanceof ISwipeAdapter) {
            layout.setSwipEnable((((ISwipeAdapter) mAdapter).getSwipEnableByPosition(position)));
        }
        return layout;
    }

    public void createMenu(SwipeMenu menu) {
        // Test Code
        SwipeMenuItem item = new SwipeMenuItem(mContext);
        item.setTitle("Item 1");
        item.setBackground(new ColorDrawable(Color.GRAY));
        item.setWidth(300);
        menu.addMenuItem(item);

        item = new SwipeMenuItem(mContext);
        item.setTitle("Item 2");
        item.setBackground(new ColorDrawable(Color.RED));
        item.setWidth(300);
        menu.addMenuItem(item);

    }

    private class MyOnSwipeItemClickListener implements SwipeMenuView.OnSwipeItemClickListener {
        SwipeMenuLayout layout = null;

        public MyOnSwipeItemClickListener(SwipeMenuLayout layout) {
            this.layout = layout;
        }

        @Override
        public void onItemClick(SwipeMenuView view, SwipeMenu menu, int index) {
            if (onMenuItemClickListener != null) {
                onMenuItemClickListener.onMenuItemClick(view.getPosition(), layout, menu,
                        index);
            }
        }
    }


    public void setOnSwipeItemClickListener(
            SwipeMenuListView.OnMenuItemClickListener onMenuItemClickListener) {
        this.onMenuItemClickListener = onMenuItemClickListener;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        mAdapter.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        mAdapter.unregisterDataSetObserver(observer);
    }

    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public boolean areAllItemsEnabled() {
        return mAdapter.areAllItemsEnabled();
    }

    @Override
    public boolean isEnabled(int position) {
        return mAdapter.isEnabled(position);
    }

    @Override
    public boolean hasStableIds() {
        return mAdapter.hasStableIds();
    }

    @Override
    public int getItemViewType(int position) {
        return mAdapter.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return mAdapter.getViewTypeCount();
    }

    @Override
    public boolean isEmpty() {
        return mAdapter.isEmpty();
    }

    @Override
    public ListAdapter getWrappedAdapter() {
        return mAdapter;
    }

}
