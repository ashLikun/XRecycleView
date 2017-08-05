package com.ashlikun.xrecycleview;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * 作者　　: 李坤
 * 创建时间: 10:12 Administrator
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：带头部与底部的RecycleView
 */

public class RecyclerViewWithHeaderAndFooter extends RecyclerView {

    private static final String HEADERSIZE = "headerSize";
    private static final String FOOTERSIZE = "footerSize";
    private static final int TYPE_HEADER = -4;
    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_REFRESH_FOOTER = -3;
    private static final int TYPE_FOOTER = -2;
    protected ArrayList<View> mHeaderViews = new ArrayList<>();
    //最后一个为加载的布局
    protected ArrayList<View> mFootViews = new ArrayList<>();
    private Adapter mAdapter;
    private WrapAdapter mWrapAdapter;

    public boolean isHeader(ViewHolder viewHolder) {
        return viewHolder.getItemViewType() == TYPE_HEADER;
    }

    public boolean isFooter(ViewHolder viewHolder) {
        return viewHolder.getItemViewType() == TYPE_REFRESH_FOOTER || viewHolder.getItemViewType() == TYPE_FOOTER;
    }


    public RecyclerViewWithHeaderAndFooter(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerViewWithHeaderAndFooter(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public RecyclerViewWithHeaderAndFooter(Context context) {
        this(context, null);
    }


    public int getDataSize() {
        return mAdapter.getItemCount();
    }

    @Override
    public void setAdapter(Adapter adapter) {
        mAdapter = adapter;
        mWrapAdapter = new WrapAdapter();
        super.setAdapter(mWrapAdapter);
        try {
            mAdapter.registerAdapterDataObserver(mDataObserver);
        } catch (IllegalStateException e) {

        }

        mWrapAdapter.notifyDataSetChanged();
        setHeaderSize();
        setFooterSize();
    }

    public void addHeaderView(View view) {
        mHeaderViews.add(view);
        setHeaderSize();
    }

    public void removeHeaderView(View view) {
        mHeaderViews.remove(view);
        setHeaderSize();
    }

    public void removeFootView(View view) {
        mFootViews.remove(view);
        setFooterSize();
    }

    public void addFootView(final View view) {
        mFootViews.add(view);
        setFooterSize();
    }

    public void addFootView(int index, final View view) {
        mFootViews.add(index, view);
        setFooterSize();
    }

    protected void setFooterSize() {
        Class cls = getCommonAdapterClass(mAdapter.getClass());//应为CommonAdapter为抽象类
        if (cls == null) return;
        try {
            Field field = cls.getDeclaredField(FOOTERSIZE);
            field.setAccessible(true);
            try {
                field.set(mAdapter, getFootViewSize());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                Log.w("setFooterSize", "adapter设置" + FOOTERSIZE + "字段失败");
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            Log.w("setFooterSize", "adapter没有" + FOOTERSIZE + "字段");
        }

    }

    private void setHeaderSize() {
        Class cls = getCommonAdapterClass(mAdapter.getClass());//应为CommonAdapter为抽象类
        if (cls == null) return;
        try {
            Field field = cls.getDeclaredField(HEADERSIZE);
            field.setAccessible(true);
            try {
                field.set(mAdapter, getHeaderViewSize());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                Log.w("setHeaderSize", "adapter设置" + HEADERSIZE + "字段失败");
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            Log.w("setHeaderSize", "adapter没有" + HEADERSIZE + "字段");
        }

    }

    private Class<? extends Adapter> getCommonAdapterClass(Class cls) {
        if (cls.isAssignableFrom(Adapter.class) || cls.getSuperclass() == null) {
            return null;
        } else {
            try {
                cls.getDeclaredField(HEADERSIZE);
                cls.getDeclaredField(FOOTERSIZE);
                return cls;
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
                return getCommonAdapterClass(cls.getSuperclass());
            }
        }
    }


    public void scrollToPositionWithOffset(int postion) {
        LayoutManager layoutManager = getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            ((LinearLayoutManager) getLayoutManager()).scrollToPositionWithOffset(postion, 0);
        } else if (layoutManager instanceof GridLayoutManager) {
            ((GridLayoutManager) getLayoutManager()).scrollToPositionWithOffset(postion, 0);
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            ((StaggeredGridLayoutManager) getLayoutManager()).scrollToPositionWithOffset(postion, 0);
        }
    }


    public int getHeaderViewSize() {
        return mHeaderViews.size();
    }

    public int getFootViewSize() {
        return mFootViews.size();
    }

    public int getFootViewHeight() {
        int height = 0;
        for (View v : mFootViews) {
            height += v.getHeight();
        }
        return height;
    }

    public int getHeaderViewHeight() {
        int height = 0;
        for (View v : mHeaderViews) {
            height += v.getHeight();
        }
        return height;
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

    private final AdapterDataObserver mDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            if (mAdapter != null) {
                mWrapAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            mWrapAdapter.notifyItemMoved(fromPosition, toPosition);
        }
    };


    private class WrapAdapter extends RecyclerView.Adapter<ViewHolder> {
        private int headerPosition = 0;
        private int footerPosition = 0;

        public WrapAdapter() {
        }


        public boolean isHeader(int position) {
            return position >= 0 && position < mHeaderViews.size();
        }

        public boolean isFooter(int position) {
            return position < getItemCount() && position >= getItemCount() - mFootViews.size();
        }

        public boolean isFooterLoad(int position) {
            return mFootViews.size() > 0 && position == getItemCount() - 1 && mFootViews.get(mFootViews.size() - 1) instanceof FooterView;
        }

        public int getHeadersCount() {
            return mHeaderViews.size();
        }

        public int getFootersCount() {
            return mFootViews.size();
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            mAdapter.onAttachedToRecyclerView(recyclerView);
            LayoutManager manager = recyclerView.getLayoutManager();
            if (manager instanceof GridLayoutManager) {
                final GridLayoutManager gridManager = ((GridLayoutManager) manager);
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {

                        return (isHeader(position) || isFooter(position))
                                ? gridManager.getSpanCount() : 1;
                    }
                });
                gridManager.setSpanCount(gridManager.getSpanCount());
            }
        }

        @Override
        public void onViewAttachedToWindow(ViewHolder holder) {
            mAdapter.onViewAttachedToWindow(holder);
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null
                    && lp instanceof StaggeredGridLayoutManager.LayoutParams
                    && (isHeader(holder.getLayoutPosition()) || isFooter(holder.getLayoutPosition()))) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_REFRESH_FOOTER) {
                return new SimpleViewHolder(mFootViews.get(mFootViews.size() - 1));
            } else if (viewType == TYPE_HEADER) {
                if (headerPosition >= mHeaderViews.size()) {
                    headerPosition = 0;
                }

                return new SimpleViewHolder(mHeaderViews.get(headerPosition++));
            } else if (viewType == TYPE_FOOTER) {
                if (footerPosition >= mFootViews.size()) {
                    footerPosition = 0;
                }
                return new SimpleViewHolder(mFootViews.get(footerPosition++));
            }
            return mAdapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (isFooterLoad(position)) {

                return;
            }
            if (isHeader(position)) {
                return;
            }
            if (isFooter(position)) {
                return;
            }
            int adjPosition = position - getHeadersCount();
            if (mAdapter != null) {
                if (adjPosition < mAdapter.getItemCount()) {
                    mAdapter.onBindViewHolder(holder, adjPosition);
                    return;
                }
            }
        }

        @Override
        public int getItemCount() {
            if (mAdapter != null) {
                return getHeadersCount() + getFootersCount() + mAdapter.getItemCount();
            } else {
                return getHeadersCount() + getFootersCount();
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (isFooterLoad(position)) {
                return TYPE_REFRESH_FOOTER;
            }
            if (isHeader(position)) {
                return TYPE_HEADER;
            }
            if (isFooter(position)) {
                return TYPE_FOOTER;
            }
            int adjPosition = position - getHeadersCount();
            if (mAdapter != null) {
                int adapterCount = mAdapter.getItemCount();
                if (adjPosition < adapterCount) {
                    return mAdapter.getItemViewType(adjPosition);
                }
            }
            return TYPE_NORMAL;
        }

        @Override
        public long getItemId(int position) {
            if (mAdapter != null && position >= getHeadersCount()) {
                int adjPosition = position - getHeadersCount();
                int adapterCount = mAdapter.getItemCount();
                if (adjPosition < adapterCount) {
                    return mAdapter.getItemId(adjPosition);
                }
            }
            return -1;
        }

        @Override
        public void unregisterAdapterDataObserver(AdapterDataObserver observer) {

            if (mAdapter != null) {
                try {
                    mAdapter.unregisterAdapterDataObserver(observer);
                } catch (IllegalStateException e) {

                }
            }
        }

        @Override
        public void registerAdapterDataObserver(AdapterDataObserver observer) {
            if (mAdapter != null) {
                try {
                    mAdapter.registerAdapterDataObserver(observer);
                } catch (IllegalStateException e) {

                }
            }
        }


        private class SimpleViewHolder extends ViewHolder {
            View view;

            public SimpleViewHolder(View itemView) {
                super(itemView);
                this.view = itemView;
            }
        }
    }
}
