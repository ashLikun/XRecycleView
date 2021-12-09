package com.ashlikun.xrecycleview.simple;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * @author　　: 李坤
 * 创建时间: 2018/8/29 11:05
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：按照比例缩放的ImageView
 */

public class ScaleImageView extends AppCompatImageView {
    /**
     * 大小比例，按照宽度
     */
    private float ratio = 16 / 9.0f;
    /**
     * 按照宽度（0）或者高度（1）为基础
     */
    private int orientation = 0;

    public ScaleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ScaleImageView);
        ratio = ta.getFloat(R.styleable.ScaleImageView_sci_ratio, ratio);
        orientation = ta.getInt(R.styleable.ScaleImageView_sci_orientation, orientation);
        ta.recycle();
    }

    public ScaleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScaleImageView(Context context) {
        this(context, null, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (ratio <= 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        if (orientation == 0) {
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int height = (int) (widthSize / ratio);
            super.onMeasure(MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        } else {
            int height = MeasureSpec.getSize(heightMeasureSpec);
            int width = (int) (height / ratio);
            super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        }
    }

    public float getRatio() {
        return ratio;
    }

    public void setRatio(float ratio) {
        if (ratio == this.ratio) {
            return;
        }
        this.ratio = ratio;
        requestLayout();
    }

    public int getOrientation() {
        return orientation;
    }

    /**
     * 按照宽度（0）或者高度（1）为基础
     */
    public void setOrientationHeight() {
        if (1 == this.orientation) {
            return;
        }
        this.orientation = 1;
        requestLayout();
    }

    /**
     * 按照宽度（0）或者高度（1）为基础
     */
    public void setOrientationWidth() {
        if (0 == this.orientation) {
            return;
        }
        this.orientation = 0;
        requestLayout();
    }
}
