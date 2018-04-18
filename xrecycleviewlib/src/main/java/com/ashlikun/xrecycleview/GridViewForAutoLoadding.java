package com.ashlikun.xrecycleview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/3/14.
 */
public class GridViewForAutoLoadding extends GridViewWithHeaderAndFooter implements BaseSwipeInterface, StatusChangListener, ConfigChang {
    public PageHelp pageHelp = null;
    private OnLoaddingListener onLoaddingListener;
    private FooterView footerView;
    private ArrayList<AbsListView.OnScrollListener> scrollListeners = new ArrayList<>();
    private RefreshLayout refreshLayout;
    private AdapterView.OnItemClickListener itemClickListener;

    public GridViewForAutoLoadding(Context context) {
        this(context, null);
    }

    public GridViewForAutoLoadding(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        addFooterView(footerView = new FooterView(context));
        footerView.setStatus(LoadState.Init);
        super.setOnScrollListener(new ScrollListener());
        super.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(parent, view, position - getHeaderViewsCount(), id);
                }
            }
        });
    }

    public GridViewForAutoLoadding(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void addOnScrollListener(AbsListView.OnScrollListener scrollListeners) {
        this.scrollListeners.add(scrollListeners);
    }

    @Override
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    @Override
    public void setOnScrollListener(AbsListView.OnScrollListener l) {
        addOnScrollListener(l);
    }

    private final class ScrollListener implements AbsListView.OnScrollListener {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

            for (AbsListView.OnScrollListener s : scrollListeners) {
                s.onScrollStateChanged(view, scrollState);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            int lastItemIndex = getLastVisiblePosition(); // 获取当前屏幕最后Item的ID
            if ((refreshLayout == null || !refreshLayout.isRefreshing()) && totalItemCount - getFooterViewsCount() - getHeaderViewsCount() > 0) {
                if (lastItemIndex + 1 >= totalItemCount) {// 达到数据的最后一条记录
                    if (footerView.isLoadMoreEnabled()
                            && getItemCount(totalItemCount) > 0
                            && footerView.getStates() != LoadState.Loadding
                            && footerView.getStates() != LoadState.NoData
                            && onLoaddingListener != null
                            && pageHelp != null && pageHelp.isNext()) {//可以加载
                        setState(LoadState.Loadding);
                        onLoaddingListener.onLoadding();
                    }
                }
            } else {
                setState(LoadState.Hint);
            }
            for (AbsListView.OnScrollListener s : scrollListeners) {
                s.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
        }
    }

    private int getItemCount(int totalItemCount) {
        return totalItemCount - getFooterViewsCount() - getHeaderViewsCount();
    }

    @Override
    public RefreshLayout getRefreshLayout() {
        return refreshLayout;
    }

    /**
     * 作者　　: 李坤
     * 创建时间: 2017/4/12 0012 16:14
     * <p>
     * 方法功能：设置刷新布局，必须设置要不然无法加载更多
     */

    @Override
    public void setRefreshLayout(RefreshLayout refreshLayout) {
        this.refreshLayout = refreshLayout;
    }

    public LoadState getState() {
        return footerView.getStates();
    }

    public void setState(LoadState state) {
        footerView.setStatus(state);
    }

    public OnLoaddingListener getOnLoaddingListener() {
        return onLoaddingListener;
    }

    @Override
    public void setOnLoaddingListener(OnLoaddingListener onLoaddingListener) {
        this.onLoaddingListener = onLoaddingListener;
        if (pageHelp == null) {
            pageHelp = new PageHelp(getContext());
        } else {
            pageHelp.clear();
        }
        pageHelp.setStatusChangListener(this);
    }


    @Override
    public void complete() {
        if (footerView.getStates() != LoadState.NoData) {
            setState(LoadState.Complete);
        }
    }

    /**
     * 没有更多数据加载
     */
    @Override
    public void noData() {
        setDataSize();
        setState(LoadState.NoData);
    }

    public void setDataSize() {
        if (footerView != null) {
            footerView.setDataSize(0);
        }
    }

    @Override
    public void setAutoloaddingCompleData(String autoloaddingCompleData) {
        if (footerView != null) {
            footerView.setAutoloaddingCompleData(autoloaddingCompleData);
        }

    }

    @Override
    /**
     * 初始化状态
     */
    public void init() {
        setState(LoadState.Init);
    }

    @Override
    public void failure() {
        if (footerView.getStates() != LoadState.NoData) {
            setState(LoadState.Failure);
        }
    }

    @Override
    public void setAutoloaddingNoData(String autoloaddingNoData) {
        if (footerView != null) {
            footerView.setAutoloaddingNoData(autoloaddingNoData);
        }
    }

    @Override
    public boolean isLoadMoreEnabled() {
        return footerView == null ? false : footerView.isLoadMoreEnabled();
    }

    @Override
    public void setLoadMoreEnabled(boolean loadMoreEnabled) {
        if (footerView != null) {
            footerView.setLoadMoreEnabled(loadMoreEnabled);
        }
    }

    @Override
    public PageHelp getPageHelp() {
        return pageHelp;
    }

    @Override
    public StatusChangListener getStatusChangListener() {
        return this;
    }
}
