package com.ashlikun.xrecycleview.divider;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.DimenRes;
import androidx.recyclerview.widget.RecyclerView;


/**
 * @author　　: 李坤
 * 创建时间: 2018/8/30 16:52
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：水平分割线
 */

public class HorizontalDivider extends FlexibleDivider {
    /**
     * 是否显示第一个顶部分割线
     */
    private boolean mShowFirstTopDivider = false;
    /**
     * 如果显示第一个顶部，那么第一个顶部大小
     */
    private int mFirstTopDividerSize = 0;
    private MarginProvider mMarginProvider;

    protected HorizontalDivider(Builder builder) {
        super(builder);
        mMarginProvider = builder.mMarginProvider;
        mShowFirstTopDivider = builder.mShowFirstTopDivider;
        mFirstTopDividerSize = builder.mFirstTopDividerSize;
    }

    @Override
    protected void onDrawDivider(Canvas c, RecyclerView parent, View child, int position, RecyclerView.State state) {
        int groupIndex = getGroupIndex(position, parent);
        boolean showFirstTopDivider = groupIndex == 0 && mShowFirstTopDivider && mFirstTopDividerSize != 0;
        //绘制第一个的顶部
        if (showFirstTopDivider) {
            Rect bounds = getDividerBound(position, parent, child, true);
            onDraw(c, bounds, -1, parent, mFirstTopDividerSize);
        }
        Rect bounds = getDividerBound(position, parent, child, false);
        onDraw(c, bounds, position, parent, getDividerSize(position, parent));
    }

    protected Rect getDividerBound(int position, RecyclerView parent, View child, boolean isTop) {
        Rect bounds = new Rect(0, 0, 0, 0);
        int transitionY = (int) child.getTranslationY();


        int dividerSize;
        if (isTop) {
            dividerSize = mFirstTopDividerSize;
        } else {
            dividerSize = getDividerSize(position, parent);
        }
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
        bounds.left = child.getLeft() - params.leftMargin + (isTop ? 0 : mMarginProvider.dividerLeftMargin(position, parent));
        bounds.right = child.getRight() + params.rightMargin + dividerSize - (isTop ? 0 : mMarginProvider.dividerRightMargin(position, parent));


        boolean isReverseLayout = isReverseLayout(parent);
        if (mDividerType == DividerType.DRAWABLE) {
            if (isReverseLayout || isTop) {
                bounds.bottom = child.getTop() - params.topMargin + transitionY;
                bounds.top = bounds.bottom - dividerSize;
            } else {
                bounds.top = child.getBottom() + params.bottomMargin + transitionY;
                bounds.bottom = bounds.top + dividerSize;
            }
        } else {
            int halfSize = dividerSize / 2;
            if (isReverseLayout || isTop) {
                bounds.top = child.getTop() - params.topMargin - halfSize + transitionY;
            } else {
                bounds.top = child.getBottom() + params.bottomMargin + halfSize + transitionY;
            }
            bounds.bottom = bounds.top;
        }

        if (mPositionInsideItem) {
            if (isReverseLayout) {
                bounds.top += dividerSize;
                bounds.bottom += dividerSize;
            } else {
                bounds.top -= dividerSize;
                bounds.bottom -= dividerSize;
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
        if (isReverseLayout(parent)) {
            outRect.set(0, 0, getDividerSize(position, parent), 0);
        }
        //第一行看看是否要显示顶部
        else if (mShowFirstDivider && mShowFirstTopDivider && getGroupIndex(position, parent) == 0) {
            outRect.set(0, mFirstTopDividerSize > 0 ? mFirstTopDividerSize : getDividerSize(position, parent), 0, getDividerSize(position, parent));
        } else {
            outRect.set(0, 0, 0, getDividerSize(position, parent));
        }
    }


    /**
     * Interface for controlling divider margin
     */
    public interface MarginProvider {

        /**
         * Returns left margin of divider.
         *
         * @param position Divider position (or group index for GridLayoutManager)
         * @param parent   RecyclerView
         * @return left margin
         */
        int dividerLeftMargin(int position, RecyclerView parent);

        /**
         * Returns right margin of divider.
         *
         * @param position Divider position (or group index for GridLayoutManager)
         * @param parent   RecyclerView
         * @return right margin
         */
        int dividerRightMargin(int position, RecyclerView parent);
    }

    public static class Builder extends FlexibleDivider.Builder<Builder> {
        /**
         * 是否显示第一个顶部分割线
         */
        private boolean mShowFirstTopDivider = false;
        /**
         * 如果显示第一个顶部，那么第一个顶部大小
         */
        private int mFirstTopDividerSize = 0;
        private MarginProvider mMarginProvider = new MarginProvider() {
            @Override
            public int dividerLeftMargin(int position, RecyclerView parent) {
                return 0;
            }

            @Override
            public int dividerRightMargin(int position, RecyclerView parent) {
                return 0;
            }
        };

        public Builder(Context context) {
            super(context);
        }

        public Builder margin(final int leftMargin, final int rightMargin) {
            return marginProvider(new MarginProvider() {
                @Override
                public int dividerLeftMargin(int position, RecyclerView parent) {
                    return leftMargin;
                }

                @Override
                public int dividerRightMargin(int position, RecyclerView parent) {
                    return rightMargin;
                }
            });
        }

        public Builder margin(int horizontalMargin) {
            return margin(horizontalMargin, horizontalMargin);
        }

        public Builder marginResId(@DimenRes int leftMarginId, @DimenRes int rightMarginId) {
            return margin(mResources.getDimensionPixelSize(leftMarginId),
                    mResources.getDimensionPixelSize(rightMarginId));
        }

        public Builder marginResId(@DimenRes int horizontalMarginId) {
            return marginResId(horizontalMarginId, horizontalMarginId);
        }

        public Builder marginProvider(MarginProvider provider) {
            mMarginProvider = provider;
            return this;
        }

        /**
         * 是否显示第一个view顶部分割线
         *
         * @return
         */
        public Builder showFirstTopDivider() {
            mShowFirstTopDivider = true;
            return this;
        }

        /**
         * 是否显示第一个view顶部分割线
         *
         * @return
         */
        public Builder showFirstTopDivider(int size) {
            mShowFirstTopDivider = true;
            mFirstTopDividerSize = size;
            return this;
        }

        public HorizontalDivider build() {
            checkBuilderParams();
            return new HorizontalDivider(this);
        }
    }
}