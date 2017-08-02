package com.ashlikun.xrecycleview;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;

import com.hbung.xrecycleview.swipemenulistview.SwipeMenuListView;

import java.util.Collection;

import static android.R.attr.mode;

/**
 * Created by Administrator on 2016/4/28.
 */
public class SuperSwipeMenuListView extends RelativeLayout {
    public RefreshLayout refreshLayout;
    SwipeMenuListView swipeMenuListView;

    public SuperSwipeMenuListView(Context context) {
        this(context, null);
    }

    public SuperSwipeMenuListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SuperSwipeMenuListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            initView();
        }

    }


    public boolean isRecycleView() {
        return mode == 2;
    }

    public boolean isListleView() {
        return mode == 2;
    }


    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.base_swipe_recycle, this, true);
        refreshLayout = (RefreshLayout) findViewById(R.id.swipe);
        swipeMenuListView = (SwipeMenuListView) findViewById(R.id.list_swipe_target);
        /**
         * 设置集合view的刷新view
         */
        swipeMenuListView.setRefreshLayout(refreshLayout);
    }


    /**
     * 设置item点击事件 listView he GridViewForAutoLoadding
     *
     * @param onItemClickListen
     */
    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListen) {
        swipeMenuListView.setOnItemClickListener(onItemClickListen);
    }


    /**
     * 设置加载更多的回调
     *
     * @param onLoaddingListener
     */
    public void setOnLoaddingListener(OnLoaddingListener onLoaddingListener) {
        swipeMenuListView.setOnLoaddingListener(onLoaddingListener);

    }

    /**
     * 设置下拉刷新的回调
     *
     * @param listener
     */
    public void setOnRefreshListener(RefreshLayout.OnRefreshListener listener) {
        if (refreshLayout != null) {
            refreshLayout.setOnRefreshListener(listener);
        }
    }

    /**
     * 设置适配器
     */

    public void setAdapter(BaseAdapter adapter) {
        swipeMenuListView.setAdapter(adapter);
    }

    public void setDividerHeight(float dp) {
        swipeMenuListView.setDividerHeight(dip2px(getContext(), dp));
    }


    /**
     * 获取pagingHelp
     */
    public PagingHelp getPagingHelp() {
        return swipeMenuListView.getPagingHelp();
    }

    /**
     * 获取分页的有效数据
     */
    public <T> Collection<T> getValidData(Collection<T> c) {
        return getPagingHelp().getValidData(c);
    }

    /**
     * 获取容器view
     *
     * @return
     */
    public SwipeMenuListView getListView() {
        return swipeMenuListView;
    }

    public RefreshLayout getRefreshLayout() {
        return refreshLayout;
    }

    public ConfigChang getConfigChang() {
        if (swipeMenuListView instanceof ConfigChang) {
            return (ConfigChang) swipeMenuListView;
        } else {
            return null;
        }
    }

    /**
     * 刷新Swp
     *
     * @return
     */
    public void setRefreshing(boolean refreshing) {
        refreshLayout.setRefreshing(refreshing);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue （DisplayMetrics类中属�?�density�?
     * @return
     */
    private int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public StatusChangListener getStatusChangListener() {
        return swipeMenuListView.getStatusChangListener();
    }

    public interface ListSwipeViewListener extends SwipeRefreshLayout.OnRefreshListener, OnLoaddingListener {

    }
}
