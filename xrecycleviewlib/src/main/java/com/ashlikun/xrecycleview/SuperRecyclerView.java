package com.ashlikun.xrecycleview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.ashlikun.animmenu.AnimMenuItem;

/**
 * 作者　　: 李坤
 * 创建时间: 16:32 Administrator
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：带有下拉刷新和自动分页的RecyclerView
 */

public class SuperRecyclerView extends RelativeLayout {
    public RefreshLayout refreshLayout;
    RecyclerViewAutoLoadding recyclerView;

    public SuperRecyclerView(Context context) {
        this(context, null);
    }

    public SuperRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SuperRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            initView();
        }
    }


    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.base_swipe_recycle, this, true);
        refreshLayout = (RefreshLayout) findViewById(R.id.swipe);
        recyclerView = (RecyclerViewAutoLoadding) findViewById(R.id.list_swipe_target);
        /**
         * 设置集合view的刷新view
         */
        recyclerView.setRefreshLayout(refreshLayout);
        setColorSchemeResources(refreshLayout);
    }

    public void setColorSchemeResources(RefreshLayout refreshLayout) {
        TypedArray array = getContext().getTheme().obtainStyledAttributes(new int[]{R.attr.SwipeRefreshLayout_Color1, R.attr.SwipeRefreshLayout_Color2, R.attr.SwipeRefreshLayout_Color3, R.attr.SwipeRefreshLayout_Color4});
        refreshLayout.setColorSchemeColors(array.getColor(0, 0xff0000), array.getColor(1, 0xff0000), array.getColor(2, 0xff0000), array.getColor(3, 0xff0000));
        array.recycle();
    }

    /**
     * 设置加载更多的回调
     *
     * @param onLoaddingListener
     */
    public void setOnLoaddingListener(OnLoaddingListener onLoaddingListener) {
        recyclerView.setOnLoaddingListener(onLoaddingListener);

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
    public void setAdapter(RecyclerView.Adapter adapter) {
        recyclerView.setAdapter(adapter);
    }

    /**
     * 获取pagingHelp
     */
    public PageHelp getPageHelp() {
        return recyclerView.getPageHelp();
    }

    /**
     * 设置分页数据为下一页
     * 为下次加载做准备
     */
    public void nextPage() {
        recyclerView.getPageHelp().nextPage();
    }


    public RefreshLayout getRefreshLayout() {
        return refreshLayout;
    }

    public ConfigChang getConfigChang() {
        if (recyclerView instanceof ConfigChang) {
            return (ConfigChang) recyclerView;
        } else {
            return null;
        }
    }

    /**
     * 刷新
     *
     * @return
     */
    public void setRefreshing(boolean refreshing) {
        refreshLayout.setRefreshing(refreshing);
    }


    public RecyclerViewAutoLoadding getRecyclerView() {
        return recyclerView;
    }


    public StatusChangListener getStatusChangListener() {
        return recyclerView.getStatusChangListener();
    }

    public void addItemDecoration(RecyclerView.ItemDecoration decor) {
        recyclerView.addItemDecoration(decor);
    }

    public void setLayoutManager(RecyclerView.LayoutManager layout) {
        recyclerView.setLayoutManager(layout);
    }

    //设置是否可以下拉刷新
    public void setLoadMoreEnabled(boolean loadMoreEnabled) {
        recyclerView.setLoadMoreEnabled(loadMoreEnabled);
    }

    /**
     * 设置底部加载中的文字
     * 建议使用String.xml  替换R.string.loadding变量
     */
    public void setLoaddingFooterText(String loaddingFooterText) {
        recyclerView.setLoaddingFooterText(loaddingFooterText);
    }

    /**
     * 作者　　: 李坤
     * 创建时间: 16:46 Administrator
     * 邮箱　　：496546144@qq.com
     * <p>
     * 功能介绍：下拉和加载更多的集合借口
     */

    public interface ListSwipeViewListener extends RefreshLayout.OnRefreshListener, OnLoaddingListener {

    }


}
