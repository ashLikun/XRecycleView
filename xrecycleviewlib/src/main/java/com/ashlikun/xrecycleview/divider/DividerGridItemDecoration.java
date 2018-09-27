package com.ashlikun.xrecycleview.divider;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * 作者　　: 李坤
 * 创建时间:2016/9/12　17:38
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：网格布局用的
 */

public class DividerGridItemDecoration extends RecyclerView.ItemDecoration {
    private Drawable mDivider;


    private DividerGridItemDecoration(Context context, Drawable drawable) {
        mDivider = drawable;
    }

    public static class Builder {
        Context context;
        int color;
        int size;
        Drawable drawable;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder colorRes(@ColorRes int colorRes) {
            color = context.getResources().getColor(colorRes);
            return this;
        }

        public Builder color(int color) {
            this.color = color;
            return this;
        }

        public Builder sizeRes(@DimenRes int sizeRes) {
            size = context.getResources().getDimensionPixelOffset(sizeRes);
            return this;
        }

        public Builder size(int size) {
            this.size = size;
            return this;
        }

        public void drawable(Drawable drawable) {
            this.drawable = drawable;
        }

        public void drawableRes(@DrawableRes int drawableRes) {
            this.drawable = context.getResources().getDrawable(drawableRes);
        }

        public DividerGridItemDecoration build() {
            if (drawable == null) {
                GradientDrawable drawable = new GradientDrawable();
                drawable.setSize(size, size);
                drawable.setColor(color);
                this.drawable = drawable;
            }
            return new DividerGridItemDecoration(context, drawable);
        }

    }


    /**
     * Sets the {@link Drawable} for this divider.
     *
     * @param drawable Drawable that should be used as a divider.
     */
    public void setDrawable(@NonNull Drawable drawable) {
        if (drawable == null) {
            throw new IllegalArgumentException("Drawable cannot be null.");
        }
        mDivider = drawable;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (parent.getLayoutManager() == null) {
            return;
        }
        drawVertical(c, parent);
        drawHorizontal(c, parent);
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getLeft() - params.leftMargin;
            final int right = child.getRight() + params.rightMargin
                    + mDivider.getIntrinsicWidth();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getTop() - params.topMargin;
            final int bottom = child.getBottom() + params.bottomMargin;
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDivider.getIntrinsicWidth();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int itemCount = parent.getAdapter().getItemCount();
        if (position >= itemCount - getLastDividerOffset(parent)) {
            // 如果是最后一行，则不需要绘制底部
            outRect.set(0, 0, 0, 0);
        } else {
            outRect.bottom = mDivider.getIntrinsicHeight();
        }
        int spanCount = getSpanCount(parent, position);
        //当前第几列
        if (spanCount > 1) {
            int spanIndex = getIndexColum(parent, view, position, spanCount);
            int dividerSize = mDivider.getIntrinsicHeight();
            //每列大小
            int eachWidth = (spanCount - 1) * dividerSize / spanCount;
            int left = position % spanCount * (dividerSize - eachWidth);
            int right = eachWidth - left;
            if (spanIndex == spanCount - 1) {
                // 如果是最后一列，则不需要绘制右边
                outRect.right = 0;
                outRect.left = left;
            } else if (spanIndex == 0) {
                //第一列不绘制左边
                outRect.right = right;
                outRect.left = 0;
            } else {//中间的左右都绘制
                outRect.left = left;
                outRect.right = right;
            }
        }
    }


    /**
     * 当前是第几列
     *
     * @param parent
     * @param pos
     * @param spanCount
     * @return
     */
    protected int getIndexColum(RecyclerView parent, View view, int pos, int spanCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            return ((GridLayoutManager) layoutManager).getSpanSizeLookup().getSpanIndex(pos, spanCount);
        } else if (layoutManager instanceof LinearLayoutManager) {
            //水平布局
            if (((LinearLayoutManager) layoutManager).getOrientation() == RecyclerView.HORIZONTAL) {
                return pos % spanCount;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            //瀑布流专属
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
            return params.getSpanIndex();
        }
        return spanCount;
    }

    /**
     * 一共多少列
     *
     * @param parent
     * @param position
     * @return
     */
    protected int getSpanCount(RecyclerView parent, int position) {
        // 列数
        int spanCount = 1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount() + 1;
            spanCount = Math.abs(spanCount - ((GridLayoutManager) layoutManager).getSpanSizeLookup().getSpanSize(position));
        } else if (layoutManager instanceof LinearLayoutManager) {
            //水平布局
            if (((LinearLayoutManager) layoutManager).getOrientation() == RecyclerView.HORIZONTAL) {
                return parent.getAdapter().getItemCount();
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) layoutManager)
                    .getSpanCount();
        }
        return spanCount;
    }

    private int getLastDividerOffset(RecyclerView parent) {
        if (parent.getLayoutManager() instanceof GridLayoutManager) {
            GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
            GridLayoutManager.SpanSizeLookup spanSizeLookup = layoutManager.getSpanSizeLookup();
            int spanCount = layoutManager.getSpanCount();
            int itemCount = parent.getAdapter().getItemCount();
            for (int i = itemCount - 1; i >= 0; i--) {
                if (spanSizeLookup.getSpanIndex(i, spanCount) == 0) {
                    return itemCount - i;
                }
            }
        }
        return 1;
    }
}