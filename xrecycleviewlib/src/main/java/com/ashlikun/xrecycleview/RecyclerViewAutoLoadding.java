package com.ashlikun.xrecycleview;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;

/**
 * 作者　　: 李坤
 * 创建时间: 2017/4/12 0012 16:15
 * <p>
 * 方法功能：自动加载更多的RecyclerView ,setRefreshLayout必须设置要不然无法加载更多
 */

public class RecyclerViewAutoLoadding extends RecyclerViewWithHeaderAndFooter implements BaseSwipeInterface,
        StatusChangListener, ConfigChang {

    public PagingHelp pagingHelp;
    private RefreshLayout refreshLayout;

    private OnLoaddingListener onLoaddingListener;

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        try {
            adapter.registerAdapterDataObserver(mDataObserver);
        } catch (IllegalStateException e) {
        }

    }

    public RecyclerViewAutoLoadding(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerViewAutoLoadding(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }


    public RecyclerViewAutoLoadding(Context context) {
        this(context, null);
    }

    private void initView() {
        addFooterView();
    }


    @Override
    public void setOnLoaddingListener(OnLoaddingListener onLoaddingListener) {
        this.onLoaddingListener = onLoaddingListener;
        if (pagingHelp == null) {
            pagingHelp = new PagingHelp(getContext());
        } else {
            pagingHelp.clear();
        }
        pagingHelp.setStatusChangListener(this);
    }

    @Override
    public PagingHelp getPagingHelp() {
        return pagingHelp;
    }


    @Override
    public void addFootView(final View view) {
        //放在自动加载的前面
        if (mFootViews.size() > 0 && mFootViews.get(mFootViews.size() - 1) instanceof FooterView) {
            mFootViews.add(mFootViews.size() - 1, view);
        } else {
            mFootViews.add(view);
        }
        setFooterSize();
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);

        if ((refreshLayout == null || !refreshLayout.isRefreshing()) && getState() != null && isLoadMoreEnabled() && getState() != LoadState.Loadding && getState() != LoadState.NoData) {
            LayoutManager layoutManager = getLayoutManager();
            int lastVisibleItemPosition;
            if (layoutManager instanceof GridLayoutManager) {
                lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                int[] into = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(into);
                lastVisibleItemPosition = findMax(into);
            } else {
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            }
            if (getItemCount(layoutManager) > 0 && layoutManager.getChildCount() > 0
                    && lastVisibleItemPosition >= layoutManager.getItemCount() - 1 && layoutManager.getItemCount() >= layoutManager.getChildCount()) {
                setState(LoadState.Loadding);
                if(onLoaddingListener != null) {
                    onLoaddingListener.onLoadding();
                }
            }
        }
    }

    private int getItemCount(LayoutManager layoutManager) {
        return layoutManager.getItemCount() - getHeaderViewSize() - getFootViewSize();
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

    public void setRefreshLayout(RefreshLayout refreshLayout) {
        this.refreshLayout = refreshLayout;
    }

    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }


    /**
     * 获取自动加载VIew
     */
    public FooterView getLoaddFooterView() {
        if (mFootViews.size() > 0 && mFootViews.get(mFootViews.size() - 1) instanceof FooterView) {
            return ((FooterView) mFootViews.get(mFootViews.size() - 1));
        }
        return null;
    }

    @Override
    public void complete() {

        if (getState() != null && getState() != LoadState.NoData) {
            setState(LoadState.Complete);
        }
    }

    /**
     * 没有更多数据加载
     */
    @Override
    public void noData() {
        setState(LoadState.NoData);
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
        if (getState() != null && getState() != LoadState.NoData) {
            setState(LoadState.Failure);
        }
    }

    public void setState(LoadState state) {
        FooterView f = getLoaddFooterView();
        if (f != null) {
            f.setStatus(state);
            refreshLayout.setEnabled(!f.isLoadMore());//如果正在加载更多，就禁用下拉刷新
        }
    }


    public void setDataSize(int count) {
        FooterView f = getLoaddFooterView();
        if (f != null) {
            f.setDataSize(count);
        }
    }

    public LoadState getState() {
        FooterView f = getLoaddFooterView();
        if (f != null) {
            return f.getStates();
        } else {
            return null;
        }
    }

    @Override
    public void setAutoloaddingNoData(String autoloaddingNoData) {
        FooterView f = getLoaddFooterView();
        if (f != null) {
            f.setAutoloaddingNoData(autoloaddingNoData);
        }

    }

    @Override
    public void setAutoloaddingCompleData(String autoloaddingCompleData) {
        FooterView f = getLoaddFooterView();
        if (f != null) {
            f.setAutoloaddingCompleData(autoloaddingCompleData);
        }

    }

    @Override
    public boolean isLoadMoreEnabled() {
        FooterView footerView = getLoaddFooterView();
        return footerView == null ? false : footerView.isLoadMoreEnabled();
    }

    @Override
    public void setLoadMoreEnabled(boolean loadMoreEnabled) {
        FooterView footerView = getLoaddFooterView();
        if (footerView != null) {
            footerView.setLoadMoreEnabled(loadMoreEnabled);
            if (!loadMoreEnabled && mFootViews.size() > 0 && mFootViews.get(mFootViews.size() - 1) instanceof FooterView) {
                mFootViews.remove(mFootViews.size() - 1);
            }
        } else if (loadMoreEnabled) {
            addFooterView();
        }
    }

    private void addFooterView() {
        FooterView footerView = new FooterView(getContext());
        if (footerView.isLoadMoreEnabled()) {
            addFootView(footerView);
            footerView.setVisibility(GONE);
            footerView.setStatus(LoadState.Init);
        }
    }


    @Override
    public StatusChangListener getStatusChangListener() {
        return this;
    }


    private final AdapterDataObserver mDataObserver = new AdapterDataObserver() {
        public int getItemCount() {
            return getAdapter().getItemCount() - getHeaderViewSize() - getFootViewSize();
        }

        @Override
        public void onChanged() {
            setDataSize(getItemCount());
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            setDataSize(getItemCount());
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            setDataSize(getItemCount());
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            setDataSize(getItemCount());
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            setDataSize(getItemCount());
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            setDataSize(getItemCount());
        }
    };
}
