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
 * 方法功能：自动加载更多的RecyclerView ,setRefreshLayout必须设置要不然无法下拉加载
 */

public class RecyclerViewAutoLoadding extends RecyclerViewExtend implements BaseSwipeInterface,
        StatusChangListener, ConfigChang {

    public PageHelp pageHelp;
    private RefreshLayout refreshLayout;

    private OnLoaddingListener onLoaddingListener;
    /**
     * 初始是否刷新开启
     */
    private boolean isInitEnableRefresh = false;
    /**
     * 记录初始是否刷新
     */
    private boolean isOneEnableRefresh = true;


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
        if (pageHelp == null) {
            pageHelp = new PageHelp(getContext());
        } else {
            pageHelp.clear();
        }
        pageHelp.setStatusChangListener(this);
    }

    @Override
    public PageHelp getPageHelp() {
        return pageHelp;
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
                int[] into = ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(null);
                lastVisibleItemPosition = findMax(into);
            } else {
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            }
            if (getItemCount(layoutManager) > 0 && layoutManager.getChildCount() > 0
                    && lastVisibleItemPosition >= layoutManager.getItemCount() - 1
                    && layoutManager.getItemCount() >= layoutManager.getChildCount()
                    && (pageHelp != null && pageHelp.isNext())) {
                setState(LoadState.Loadding);
                if (onLoaddingListener != null) {
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
     * 方法功能：设置刷新布局，必须设置要不然无法下拉加载
     */

    @Override
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
            //停止滚动
            stopScroll();
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
            //如果正在加载更多，就禁用下拉刷新
            if (refreshLayout != null) {
                //这里要注意如果默认时候就不可以刷新那不能把他设置成可以刷新
                if (f.isLoadMore()) {
                    if (isOneEnableRefresh) {
                        isOneEnableRefresh = false;
                        //记录初始是否刷新
                        isInitEnableRefresh = refreshLayout.isEnabled();
                    }
                    refreshLayout.setEnabled(false);
                } else {
                    //其他状态下 还原成初始
                    if (isInitEnableRefresh) {
                        refreshLayout.setEnabled(isInitEnableRefresh);
                    }
                    isOneEnableRefresh = true;
                }
            }
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
    protected void onAdapterItemAnimChang() {
        super.onAdapterItemAnimChang();
        FooterView f = getLoaddFooterView();
        if (f != null) {
            f.setRecycleAniming();
        }
    }

    /**
     * 设置底部的没有数据时候的文字
     * 建议使用String.xml  替换R.string.autoloadding_no_data变量
     */
    @Override
    public void setNoDataFooterText(String autoloaddingNoData) {
        FooterView f = getLoaddFooterView();
        if (f != null) {
            f.setNoDataFooterText(autoloaddingNoData);
        }
    }

    /**
     * 设置底部加载中的文字
     * 建议使用String.xml  替换R.string.loadding变量
     */
    public void setLoaddingFooterText(String loaddingFooterText) {
        FooterView f = getLoaddFooterView();
        if (f != null) {
            f.setLoaddingFooterText(loaddingFooterText);
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
            if (!loadMoreEnabled) {
                mFootViews.remove(footerView);
                setFooterSize();
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


}
