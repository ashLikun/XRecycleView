package com.ashlikun.xrecycleview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;

/**
 * 作者　　: 李坤
 * 创建时间: 2017/4/12 0012 16:15
 * <p>
 * 方法功能：自动加载更多的RecyclerView
 * setRefreshLayout必须设置要不然无法下拉加载
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

    /**
     * 加载的位置
     * 上面还是下面
     * 1:上    2：下
     */
    private int loadLocation = 2;


    public RecyclerViewAutoLoadding(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerViewAutoLoadding(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }


    public RecyclerViewAutoLoadding(Context context) {
        this(context, null);
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.RecyclerViewAutoLoadding);
        loadLocation = a.getInt(R.styleable.RecyclerViewAutoLoadding_rv_load_location, 2);
        addLoadView();

    }

    private void addLoadView() {
        LoadView loadView = new LoadView(getContext());
        if (loadView.isLoadMoreEnabled()) {
            if (loadLocation == 1) {
                //加载第0个位置
                addHeaderView(0, loadView);
            } else {
                addFootView(loadView);
            }
            loadView.setVisibility(GONE);
            loadView.setStatus(LoadState.Init);
        }
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
        if (mFootViews.size() > 0 && mFootViews.get(mFootViews.size() - 1) instanceof LoadView) {
            mFootViews.add(mFootViews.size() - 1, view);
        } else {
            mFootViews.add(view);
        }
        setFooterSize();
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        boolean isCan = (refreshLayout == null || !refreshLayout.isRefreshing()) &&
                getState() != null && isLoadMoreEnabled() && getState() != LoadState.Loadding && getState() != LoadState.NoData;
        if (isCan) {
            LayoutManager layoutManager = getLayoutManager();
            if (loadLocation == 1) {
                //顶部加载
                int firstVisibleItemPosition;
                if (layoutManager instanceof GridLayoutManager) {
                    firstVisibleItemPosition = ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
                } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                    int[] into = ((StaggeredGridLayoutManager) layoutManager).findFirstVisibleItemPositions(null);
                    firstVisibleItemPosition = findMin(into);
                } else {
                    firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                }
                if (getItemCount(layoutManager) > 0 && layoutManager.getChildCount() > 0
                        && firstVisibleItemPosition <= 0
                        && layoutManager.getItemCount() >= layoutManager.getChildCount()
                        && (pageHelp != null && pageHelp.isNext())) {
                    setState(LoadState.Loadding);
                    if (onLoaddingListener != null) {
                        onLoaddingListener.onLoadding();
                    }
                }
            } else {
                //底部加载
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

    private int findMin(int[] lastPositions) {
        int min = lastPositions[0];
        for (int value : lastPositions) {
            if (value < min) {
                min = value;
            }
        }
        return min;
    }


    /**
     * 获取自动加载VIew
     */
    public LoadView getLoadView() {
        View view = null;
        if (loadLocation == 1) {
            view = getHeaderView(0);
        } else {
            view = getHeaderView(mFootViews.size() - 1);
        }
        if (view != null && view instanceof LoadView) {
            return (LoadView) view;
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
        //停止滚动
        stopScroll();
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
        LoadView f = getLoadView();
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
        LoadView f = getLoadView();
        if (f != null) {
            return f.getStates();
        } else {
            return null;
        }
    }

    @Override
    protected void onAdapterItemAnimChang() {
        super.onAdapterItemAnimChang();
        LoadView f = getLoadView();
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
        LoadView f = getLoadView();
        if (f != null) {
            f.setNoDataFooterText(autoloaddingNoData);
        }
    }

    /**
     * 设置底部加载中的文字
     * 建议使用String.xml  替换R.string.loadding变量
     */
    public void setLoaddingFooterText(String loaddingFooterText) {
        LoadView f = getLoadView();
        if (f != null) {
            f.setLoaddingFooterText(loaddingFooterText);
        }
    }


    @Override
    public boolean isLoadMoreEnabled() {
        LoadView footerView = getLoadView();
        return footerView == null ? false : footerView.isLoadMoreEnabled();
    }

    @Override
    public void setLoadMoreEnabled(boolean loadMoreEnabled) {
        LoadView loadView = getLoadView();
        if (loadView != null) {
            loadView.setLoadMoreEnabled(loadMoreEnabled);
            if (!loadMoreEnabled) {
                if (loadLocation == 1) {
                    removeHeaderView(loadView);
                } else {
                    removeFootView(loadView);
                }
            }
        } else if (loadMoreEnabled) {
            addLoadView();
        }
    }


    @Override
    public StatusChangListener getStatusChangListener() {
        return this;
    }


}
