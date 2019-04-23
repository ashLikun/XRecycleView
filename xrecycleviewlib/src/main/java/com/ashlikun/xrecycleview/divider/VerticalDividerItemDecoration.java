package com.ashlikun.xrecycleview.divider;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import androidx.annotation.DimenRes;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
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
    protected void onDrawDivider(Canvas c, RecyclerView parent, View child, int position, RecyclerView.State state) {
        int divSize = getDividerSize(position, parent);
        Rect bounds = getDividerBound(position, parent, child, false);
        onDraw(c, bounds, position, parent, divSize);
    }

    protected Rect getDividerBound(int position, RecyclerView parent, View child, boolean isLeft) {

        Rect bounds = new Rect(0, 0, 0, 0);
        int transitionX = (int) child.getTranslationX();
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
        bounds.top = child.getTop() - params.topMargin + (isLeft ? 0 : mMarginProvider.dividerTopMargin(position, parent));
        bounds.bottom = child.getBottom() + params.bottomMargin - (isLeft ? 0 : mMarginProvider.dividerBottomMargin(position, parent));


        int dividerSize = getDividerSize(position, parent);
        if (isLeft) {
            dividerSize = dividerSize / 2;
        }
        boolean isReverseLayout = isReverseLayout(parent);
        if (mDividerType == DividerType.DRAWABLE) {
            if (isReverseLayout | isLeft) {
                bounds.right = child.getLeft() - params.leftMargin + transitionX;
                bounds.left = bounds.right - dividerSize;
            } else {
                bounds.left = child.getRight() + params.rightMargin + transitionX;
                bounds.right = bounds.left + dividerSize;
            }
        } else {
            int halfSize = dividerSize / 2;
            if (isReverseLayout | isLeft) {
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
        //当前第几列 0开始
        int spanIndex = getIndexColum(parent, v, position, spanCount);
        int dividerSize = getDividerSize(position, parent);
        int dividerSizeAll = 0;
        //总大小
        for (int i = 0; i < spanCount - 1; i++) {
            int cp = position + i - spanIndex;
            if (cp >= 0) {
                dividerSizeAll += getDividerSize(cp, parent);
            }
        }
        //每列大小
        int eachWidth = dividerSizeAll / spanCount;
        int left = spanIndex * (dividerSize - eachWidth);
        int right = eachWidth - left;
        if (spanIndex == spanCount - 1) {
            // 如果是最后一列，则不需要绘制右边
            outRect.set(left, 0, 0, 0);
        } else if (spanIndex == 0) {
            //第一列不绘制左边
            outRect.set(0, 0, right, 0);
        } else {
            //中间的左右都绘制
            outRect.set(left, 0, right, 0);
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
            showLastDivider();
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
}