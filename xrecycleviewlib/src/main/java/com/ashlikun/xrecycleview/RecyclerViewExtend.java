package com.ashlikun.xrecycleview;

import android.content.Context;
import android.content.res.TypedArray;
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
import java.util.List;

/**
 * 作者　　: 李坤
 * 创建时间: 10:12 Administrator
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：扩展的RecyclerView
 * 1：带头部与底部的RecycleView
 * 2：上下加载更多
 * 3：和可以设置最大高度
 */

public class RecyclerViewExtend extends RecyclerView {

    private static final String HEADERSIZE = "headerSize";
    private static final String FOOTERSIZE = "footerSize";
    private static final int TYPE_HEADER = -900004;
    private static final int TYPE_REFRESH_HEADER = -900005;
    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_REFRESH_FOOTER = -900003;
    private static final int TYPE_FOOTER = -900002;
    /**
     * 第一个加载的布局
     */
    protected ArrayList<View> mHeaderViews = new ArrayList<>();
    /**
     * 最后一个为加载的布局
     */
    protected ArrayList<View> mFootViews = new ArrayList<>();
    private Adapter mAdapter;
    private WrapAdapter mWrapAdapter;
    /**
     * 最大高度
     */
    protected float maxHeight = 0;
    /**
     * 最大比例,相对于宽度
     */
    protected float maxRatio = 0;

    public boolean isHeader(ViewHolder viewHolder) {
        return viewHolder.getItemViewType() == TYPE_REFRESH_HEADER || viewHolder.getItemViewType() == TYPE_HEADER;
    }

    public boolean isFooter(ViewHolder viewHolder) {
        return viewHolder.getItemViewType() == TYPE_REFRESH_FOOTER || viewHolder.getItemViewType() == TYPE_FOOTER;
    }

    public RecyclerViewExtend(Context context) {
        this(context, null);
    }

    public RecyclerViewExtend(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerViewExtend(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }


    private void initView(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.RecyclerViewExtend);
        maxRatio = a.getFloat(R.styleable.RecyclerViewExtend_rv_heightRatio, 0);
        maxHeight = a.getDimension(R.styleable.RecyclerViewExtend_rv_heightDimen, 0);
        a.recycle();
    }

    public int getDataSize() {
        return mAdapter.getItemCount();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (maxHeight <= 0 && maxRatio <= 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if (maxHeight <= 0) {
            maxHeight = maxRatio * widthSize;
        } else {
            maxHeight = Math.min(maxHeight, maxRatio * widthSize);
        }
        if (maxHeight <= 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (heightMode == MeasureSpec.EXACTLY) {
            heightSize = heightSize <= maxHeight ? heightSize
                    : (int) maxHeight;
        }

        if (heightMode == MeasureSpec.UNSPECIFIED) {
            heightSize = heightSize <= maxHeight ? heightSize
                    : (int) maxHeight;
        }
        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = heightSize <= maxHeight ? heightSize
                    : (int) maxHeight;
        }
        int maxHeightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize,
                heightMode);
        super.onMeasure(widthMeasureSpec, maxHeightMeasureSpec);
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

    public void addHeaderView(int index, View view) {
        mHeaderViews.add(index, view);
        setHeaderSize();
    }

    public View getHeaderView(int index) {
        if (mHeaderViews.size() > index && index >= 0) {
            return mHeaderViews.get(index);
        }
        return null;
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

    public View getFootView(int index) {
        if (mFootViews.size() > index && index >= 0) {
            return mFootViews.get(index);
        }
        return null;
    }

    protected void setFooterSize() {
        if (mAdapter == null) {
            return;
        }
        //应为CommonAdapter为抽象类
        Class cls = getCommonAdapterClass(mAdapter.getClass());
        if (cls == null) {
            return;
        }
        try {
            Field field = cls.getDeclaredField(FOOTERSIZE);
            field.setAccessible(true);
            try {
                field.set(mAdapter, getFootViewSize());
            } catch (IllegalAccessException e) {
                Log.w("setFooterSize", "adapter设置" + FOOTERSIZE + "字段失败");
            }
        } catch (NoSuchFieldException e) {
            Log.w("setFooterSize", "adapter没有" + FOOTERSIZE + "字段");
        }

    }

    private void setHeaderSize() {
        if (mAdapter == null) {
            return;
        }
        Class cls = getCommonAdapterClass(mAdapter.getClass());//应为CommonAdapter为抽象类
        if (cls == null) {
            return;
        }
        try {
            Field field = cls.getDeclaredField(HEADERSIZE);
            field.setAccessible(true);
            try {
                field.set(mAdapter, getHeaderViewSize());
            } catch (IllegalAccessException e) {
                Log.w("setHeaderSize", "adapter设置" + HEADERSIZE + "字段失败");
            }
        } catch (NoSuchFieldException e) {
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

    /**
     * 当adapter不是调用全部刷新，并且有动画的时候
     */
    protected void onAdapterItemAnimChang() {

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
            mWrapAdapter.notifyItemRangeInserted(getHeaderViewSize() + positionStart, itemCount);
            if (getItemAnimator() != null) {
                onAdapterItemAnimChang();
            }
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeChanged(getHeaderViewSize() + positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            mWrapAdapter.notifyItemRangeChanged(getHeaderViewSize() + positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeRemoved(getHeaderViewSize() + positionStart, itemCount);
            if (getItemAnimator() != null) {
                onAdapterItemAnimChang();
            }
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            mWrapAdapter.notifyItemMoved(getHeaderViewSize() + fromPosition, getHeaderViewSize() + toPosition);
            if (getItemAnimator() != null) {
                onAdapterItemAnimChang();
            }
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

        public boolean isHeaderLoad(int position) {
            return mHeaderViews.size() > 0 && position == 0 && mHeaderViews.get(0) instanceof LoadView;
        }

        public boolean isFooter(int position) {
            return position < getItemCount() && position >= getItemCount() - mFootViews.size();
        }

        public boolean isFooterLoad(int position) {
            return mFootViews.size() > 0 && position == getItemCount() - 1 && mFootViews.get(mFootViews.size() - 1) instanceof LoadView;
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
                //可能之前已经有自定义的了,这里不能直接替换
                final GridLayoutManager gridManager = ((GridLayoutManager) manager);
                final GridLayoutManager.SpanSizeLookup old = gridManager.getSpanSizeLookup();
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        if (isHeader(position) || isFooter(position)) {
                            //占满全行
                            return gridManager.getSpanCount();
                        } else {
                            if (old != null) {
                                //使用之前设置过的 并且positions是去除头部后的位置
                                return old.getSpanSize(position - getHeadersCount());
                            } else {
                                return 1;
                            }
                        }
                    }
                });
                gridManager.setSpanCount(gridManager.getSpanCount());
            }
        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            mAdapter.onDetachedFromRecyclerView(recyclerView);
        }

        @Override
        public void onViewAttachedToWindow(ViewHolder holder) {
            try {
                if (RecyclerViewExtend.this.isHeader(holder) || RecyclerViewExtend.this.isFooter(holder)) {
                    ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
                    if (lp != null
                            && lp instanceof StaggeredGridLayoutManager.LayoutParams
                            && (isHeader(holder.getLayoutPosition()) || isFooter(holder.getLayoutPosition()))) {
                        StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                        //StaggeredGridLayoutManager 只能这样设置一整行
                        p.setFullSpan(true);
                    }
                } else {
                    mAdapter.onViewAttachedToWindow(holder);
                }
            } catch (Exception e) {
            }
        }

        @Override
        public void onViewDetachedFromWindow(ViewHolder holder) {
            try {
                if (RecyclerViewExtend.this.isHeader(holder) || RecyclerViewExtend.this.isFooter(holder)) {

                } else {
                    mAdapter.onViewDetachedFromWindow(holder);
                }
            } catch (Exception e) {

            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_REFRESH_FOOTER) {
                return new ViewHolder(getFootView(mFootViews.size() - 1)) {
                };
            } else if (viewType == TYPE_REFRESH_HEADER) {
                return new ViewHolder(getHeaderView(0)) {
                };
            } else if (viewType == TYPE_HEADER) {
                if (headerPosition >= mHeaderViews.size()) {
                    headerPosition = 0;
                }
                return new ViewHolder(mHeaderViews.get(headerPosition++)) {
                };
            } else if (viewType == TYPE_FOOTER) {
                if (footerPosition >= mFootViews.size()) {
                    footerPosition = 0;
                }
                return new ViewHolder(mFootViews.get(footerPosition++)) {
                };
            }
            return mAdapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
            super.onBindViewHolder(holder, position, payloads);
            if (isFooterLoad(position) || isHeaderLoad(position)) {
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
                    mAdapter.onBindViewHolder(holder, adjPosition, payloads);
                    return;
                }
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

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
            if (isHeaderLoad(position)) {
                return TYPE_REFRESH_HEADER;
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
    }

    public void setMaxHeight(float maxHeight) {
        this.maxHeight = maxHeight;
        requestLayout();
    }

    public void setMaxRatio(float maxRatio) {
        this.maxRatio = maxRatio;
        requestLayout();
    }
}
