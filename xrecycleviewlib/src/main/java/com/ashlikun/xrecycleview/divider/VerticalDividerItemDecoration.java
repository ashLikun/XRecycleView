package com.ashlikun.xrecycleview.divider;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;


/**
 * @author　　: 李坤
 * 创建时间: 2018/8/30 16:51
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：垂直的分割线
 */

public class VerticalDividerItemDecoration extends FlexibleDividerDecoration {

    private MarginProvider mMarginProvider;

    protected VerticalDividerItemDecoration(Builder builder) {
        super(builder);
        mMarginProvider = builder.mMarginProvider;
    }

    @Override
    protected Rect getDividerBound(int position, RecyclerView parent, View child, boolean isTop) {

        Rect bounds = new Rect(0, 0, 0, 0);
        int transitionX = (int) child.getTranslationX();
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
        bounds.top = child.getTop() - params.topMargin + mMarginProvider.dividerTopMargin(position, parent);
        bounds.bottom = child.getBottom() + params.bottomMargin - mMarginProvider.dividerBottomMargin(position, parent);


        int dividerSize = getDividerSize(position, parent);
        boolean isReverseLayout = isReverseLayout(parent);
        if (mDividerType == DividerType.DRAWABLE) {
            // set left and right position of divider
            if (isReverseLayout) {
                bounds.right = child.getLeft() - params.leftMargin + transitionX;
                bounds.left = bounds.right - dividerSize;
            } else {
                bounds.left = child.getRight() + params.rightMargin + transitionX;
                bounds.right = bounds.left + dividerSize;
            }
        } else {
            // set center point of divider
            int halfSize = dividerSize / 2;
            if (isReverseLayout) {
                bounds.left = child.getLeft() - params.leftMargin - halfSize + transitionX;
            } else {
                bounds.left = child.getRight() + params.rightMargin + halfSize + transitionX;
            }
            bounds.right = bounds.left;
        }

        if (mPositionInsideItem) {
            if (isReverseLayout) {
                bounds.left += dividerSize;
                bounds.right += dividerSize;
            } else {
                bounds.left -= dividerSize;
                bounds.right -= dividerSize;
            }
        }

        return bounds;
    }


    @Override
    protected void setItemOffsets(Rect outRect, View v, int position, int childCount, RecyclerView parent) {
        if (mPositionInsideItem) {
            outRect.set(0, 0, 0, 0);
            return;
        }
        //多少列
        int spanCount = getSpanCount(parent, position);
        if (spanCount <= 1) {
            outRect.set(0, 0, 0, 0);
            return;
        }
        //当前第几列
        int spanIndex = getIndexColum(parent, v, position, spanCount);
        //总共大小
        int dividerAllSize = 0;
        for (int i = 0; i < spanCount - 1; i++) {
            //当前列开始的第一个
            int startPoi = position - spanIndex + 1;
            dividerAllSize += getDividerSize(startPoi + i, parent);
        }
        //每个item应该大小
        int itemDivSize = Math.round(dividerAllSize / ((spanCount - 1) * 1f));

        if (spanIndex == spanCount - 1) {
            // 如果是最后一列，则不需要绘制右边
            outRect.set(itemDivSize / 2, 0, 0, 0);
        } else if (spanIndex == 0) {
            //第一列不绘制左边
            outRect.set(0, 0, itemDivSize / 2, 0);
        } else {
            //中间的左右都绘制
            outRect.set(Math.round(itemDivSize / 2f), 0, Math.round(itemDivSize / 2f), 0);
        }
    }

    /**
     * Interface for controlling divider margin
     */
    public interface MarginProvider {

        /**
         * Returns top margin of divider.
         *
         * @param position Divider position (or group index for GridLayoutManager)
         * @param parent   RecyclerView
         * @return top margin
         */
        int dividerTopMargin(int position, RecyclerView parent);

        /**
         * Returns bottom margin of divider.
         *
         * @param position Divider position (or group index for GridLayoutManager)
         * @param parent   RecyclerView
         * @return bottom margin
         */
        int dividerBottomMargin(int position, RecyclerView parent);
    }

    public static class Builder extends FlexibleDividerDecoration.Builder<Builder> {

        private MarginProvider mMarginProvider = new MarginProvider() {
            @Override
            public int dividerTopMargin(int position, RecyclerView parent) {
                return 0;
            }

            @Override
            public int dividerBottomMargin(int position, RecyclerView parent) {
                return 0;
            }
        };

        public Builder(Context context) {
            super(context);
        }

        public Builder margin(final int topMargin, final int bottomMargin) {
            return marginProvider(new MarginProvider() {
                @Override
                public int dividerTopMargin(int position, RecyclerView parent) {
                    return topMargin;
                }

                @Override
                public int dividerBottomMargin(int position, RecyclerView parent) {
                    return bottomMargin;
                }
            });
        }

        public Builder margin(int verticalMargin) {
            return margin(verticalMargin, verticalMargin);
        }

        public Builder marginResId(@DimenRes int topMarginId, @DimenRes int bottomMarginId) {
            return margin(mResources.getDimensionPixelSize(topMarginId),
                    mResources.getDimensionPixelSize(bottomMarginId));
        }

        public Builder marginResId(@DimenRes int verticalMarginId) {
            return marginResId(verticalMarginId, verticalMarginId);
        }

        public Builder marginProvider(MarginProvider provider) {
            mMarginProvider = provider;
            return this;
        }

        public VerticalDividerItemDecoration build() {
            checkBuilderParams();
            return new VerticalDividerItemDecoration(this);
        }
    }

    /**
     * 当前是第几列
     *
     * @param parent
     * @param view
     * @param pos
     * @param spanCount
     * @return
     */
    protected int getIndexColum(RecyclerView parent, View view, int pos, int spanCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        int posSpan = pos % spanCount;
        if (layoutManager instanceof GridLayoutManager) {
            return posSpan;
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            //瀑布流专属
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
            return params.getSpanIndex();
        }
        return spanCount;
    }
}