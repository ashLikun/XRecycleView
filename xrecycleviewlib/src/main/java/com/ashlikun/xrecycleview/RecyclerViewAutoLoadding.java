package com.ashlikun.xrecycleview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

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
     * 没有数据的时候是否显示LoadView
     */
    protected boolean noDataIsShow = true;

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
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RecyclerViewAutoLoadding);
        int footLayouId = a.getInt(R.styleable.RecyclerViewAutoLoadding_rv_footLoadLayoutId, -1);
        noDataIsShow = a.getBoolean(R.styleable.RecyclerViewAutoLoadding_rv_noDataIsShow, noDataIsShow);
        boolean isFootLoadColor = false;
        int footLoadColor = 0;
        if (a.hasValue(R.styleable.RecyclerViewAutoLoadding_rv_footLoadColor)) {
            isFootLoadColor = true;
            footLoadColor = a.getColor(R.styleable.RecyclerViewAutoLoadding_rv_footLoadColor, 0);
        }
        a.recycle();
        addLoadView(footLayouId);
        if (isFootLoadColor) {
            setLoadFootColor(footLoadColor);
        }
    }

    /**
     * 添加加载控件
     */
    public void addLoadView() {
        addLoadView(-1);
    }

    /**
     * 添加加载控件
     */
    public void addLoadView(int loadFootlayoutId) {
        LoadView loadView = getLoadView();
        if (loadView == null) {
            //没有，就新增
            loadView = new LoadView(getContext(), loadFootlayoutId);
            addFootView(loadView);
            loadView.setVisibility(GONE);
            loadView.setStatus(LoadState.Init);
        } else {
            //如果已经有加载布局了，就更新
            loadView.loaddingLayout(loadFootlayoutId);
        }
    }

    /**
     * 移除加载控件
     */
    public void removeLoadView() {
        LoadView loadView = getLoadView();
        if (loadView != null) {
            removeFootView(loadView);
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
        pageHelp.addStatusChangListener(this);
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
    public LoadView getLoadView() {
        View view = getFootView(mFootViews.size() - 1);
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
        if (!noDataIsShow) {
            setState(LoadState.Hint);
        } else {
            setState(LoadState.NoData);
        }
        //停止滚动
        stopScroll();
    }

    /**
     * 初始化状态
     */
    @Override
    public void init() {
        setState(LoadState.Init);
    }

    @Override
    public void failure() {
        if (getState() != null && getState() != LoadState.NoData) {
            setState(LoadState.Failure);
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public void hint() {
        if (getState() != null) {
            setState(LoadState.Hint);
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
     * 没有数据的时候是否显示LoadView
     */
    public void setNoDataIsShow(boolean noDataIsShow) {
        this.noDataIsShow = noDataIsShow;
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
                removeFootView(loadView);
            }
        } else if (loadMoreEnabled) {
            addLoadView();
        }
    }


    /**
     * 底部加载布局
     *
     * @param loadFootlayoutId
     */
    public void setLoadFootlayoutId(int loadFootlayoutId) {
        removeLoadView();
        addLoadView(loadFootlayoutId);
    }

    /**
     * 底部加载主颜色
     */
    public void setLoadFootColor(int color) {
        LoadView view = getLoadView();
        if (view != null) {
            view.setColor(color);
        }
    }

    @Override
    public StatusChangListener getStatusChangListener() {
        return this;
    }


}

