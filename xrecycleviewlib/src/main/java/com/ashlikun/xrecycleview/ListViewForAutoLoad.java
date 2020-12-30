package com.ashlikun.xrecycleview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/3/14.
 */
public class ListViewForAutoLoad extends ListView implements BaseSwipeInterface, PageHelpListener, ConfigChang {

    public PageHelp pageHelp = null;
    private OnLoaddingListener onLoaddingListener;
    private LoadView footerView;
    private ArrayList<OnScrollListener> scrollListeners = new ArrayList<>();
    private RefreshLayout refreshLayout;
    private OnItemClickListener itemClickListener;

    public ListViewForAutoLoad(Context context) {
        this(context, null);
    }

    public ListViewForAutoLoad(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListViewForAutoLoad(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addFooterView(footerView = new LoadView(context), null, false);
        init();
        super.setOnScrollListener(new ScrollListener());
        if (!isInEditMode()) {
            super.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(parent, view, position - getHeaderViewsCount(), id);
                    }
                }
            });
        }
    }

    public void addOnScrollListener(OnScrollListener scrollListeners) {
        this.scrollListeners.add(scrollListeners);
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        addOnScrollListener(l);
    }


    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }


    private final class ScrollListener implements OnScrollListener {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

            for (OnScrollListener s : scrollListeners) {
                s.onScrollStateChanged(view, scrollState);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            int lastItemIndex = getLastVisiblePosition(); // 获取当前屏幕最后Item的ID
            if ((refreshLayout == null || !refreshLayout.isRefreshing()) &&
                    totalItemCount - getFooterViewsCount() - getHeaderViewsCount() > 0) {
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
            for (OnScrollListener s : scrollListeners) {
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
        setState(state, null);
    }

    public void setState(LoadState state, String message) {
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
        pageHelp.addStatusChangListener(this);
    }


    @Override
    public void complete() {
        complete(null);
    }

    @Override
    public void complete(String message) {
        if (getState() != null && getState() != LoadState.NoData) {
            setState(LoadState.Complete, message);
        }
    }

    /**
     * 没有更多数据加载
     */
    @Override
    public void noData() {
        noData(null);
    }

    @Override
    public void noData(String message) {
        setState(LoadState.NoData, message);
    }

    /**
     * 初始化状态
     */
    @Override
    public void init() {
        init(null);
    }

    @Override
    public void init(String message) {
        setState(LoadState.Init, message);
    }

    @Override
    public void failure() {
        failure(null);
    }

    @Override
    public void failure(String message) {
        if (getState() != null && getState() != LoadState.NoData) {
            setState(LoadState.Failure, message);
        }
    }

    @Override
    public int getItemCount() {
        if (getAdapter() != null) {
            return getAdapter().getCount();
        }
        return 0;
    }

    @Override
    public void setNoDataFooterText(String autoloaddingNoData) {
        if (footerView != null) {
            footerView.setNoDataFooterText(autoloaddingNoData);
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
    public PageHelpListener getPageHelpListener() {
        return this;
    }
}
