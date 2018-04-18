package com.ashlikun.xrecycleview.progress;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.util.Property;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;


/**
 * By cindy on 12/22/14 3:55 PM.
 */
public class CircleProgressDrawable extends Drawable implements Animatable {

    private static final Interpolator ANGLE_INTERPOLATOR = new LinearInterpolator();

    private static final Interpolator SWEEP_INTERPOLATOR = new DecelerateInterpolator();

    private int angleAnimatorDuration = 2000;

    private int sweepAnimatorDuration = 600;

    private int minSweepAngle = 30;

    private final RectF fBounds = new RectF();

    private ObjectAnimator mObjectAnimatorSweep;

    private ObjectAnimator mObjectAnimatorAngle;

    private boolean mModeAppearing;
    private Paint mPaint;

    private float mCurrentGlobalAngleOffset;

    private float mCurrentGlobalAngle;

    private float mCurrentSweepAngle;

    private float mBorderWidth;

    private boolean mRunning;

    public CircleProgressDrawable(int color, float borderWidth) {
        mBorderWidth = borderWidth;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(borderWidth);
        mPaint.setColor(color);

        setupAnimations();
    }

    @Override
    public void draw(Canvas canvas) {
        float startAngle = mCurrentGlobalAngle - mCurrentGlobalAngleOffset;
        float sweepAngle = mCurrentSweepAngle;
        if (mModeAppearing) {
            sweepAngle += minSweepAngle;
        } else {
            startAngle = startAngle + sweepAngle;
            sweepAngle = 360 - sweepAngle - minSweepAngle;
        }
        canvas.drawArc(fBounds, startAngle, sweepAngle, false, mPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSPARENT;
    }

    private void toggleAppearingMode() {
        mModeAppearing = !mModeAppearing;
        if (mModeAppearing) {
            mCurrentGlobalAngleOffset = (mCurrentGlobalAngleOffset + minSweepAngle * 2) % 360;
        }
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        fBounds.left = bounds.left + mBorderWidth / 2f + .5f;
        fBounds.right = bounds.right - mBorderWidth / 2f - .5f;
        fBounds.top = bounds.top + mBorderWidth / 2f + .5f;
        fBounds.bottom = bounds.bottom - mBorderWidth / 2f - .5f;
    }


    private Property<CircleProgressDrawable, Float> mAngleProperty =
            new Property<CircleProgressDrawable, Float>(Float.class, "angle") {
                @Override
                public Float get(CircleProgressDrawable object) {
                    return object.getCurrentGlobalAngle();
                }

                @Override
                public void set(CircleProgressDrawable object, Float value) {
                    object.setCurrentGlobalAngle(value);
                }
            };

    private Property<CircleProgressDrawable, Float> mSweepProperty =
            new Property<CircleProgressDrawable, Float>(Float.class, "arc") {
                @Override
                public Float get(CircleProgressDrawable object) {
                    return object.getCurrentSweepAngle();
                }

                @Override
                public void set(CircleProgressDrawable object, Float value) {
                    object.setCurrentSweepAngle(value);
                }
            };

    private void setupAnimations() {
        mObjectAnimatorAngle = ObjectAnimator.ofFloat(this, mAngleProperty, 360f);
        mObjectAnimatorAngle.setInterpolator(ANGLE_INTERPOLATOR);
        mObjectAnimatorAngle.setDuration(angleAnimatorDuration);
        mObjectAnimatorAngle.setRepeatMode(ValueAnimator.RESTART);
        mObjectAnimatorAngle.setRepeatCount(ValueAnimator.INFINITE);

        mObjectAnimatorSweep = ObjectAnimator.ofFloat(this,
                mSweepProperty, 360f - minSweepAngle * 2);
        mObjectAnimatorSweep.setInterpolator(SWEEP_INTERPOLATOR);
        mObjectAnimatorSweep.setDuration(sweepAnimatorDuration);
        mObjectAnimatorSweep.setRepeatMode(ValueAnimator.RESTART);
        mObjectAnimatorSweep.setRepeatCount(ValueAnimator.INFINITE);
        mObjectAnimatorSweep.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                toggleAppearingMode();
            }
        });
    }

    @Override
    public void start() {
        if (isRunning()) {
            return;
        }
        mRunning = true;
        mObjectAnimatorAngle.start();
        mObjectAnimatorSweep.start();
        invalidateSelf();
    }

    @Override
    public void stop() {
        if (!isRunning()) {
            return;
        }
        mRunning = false;
        mObjectAnimatorAngle.cancel();
        mObjectAnimatorSweep.cancel();
        invalidateSelf();
    }

    @Override
    public boolean isRunning() {
        return mRunning;
    }

    public void setCurrentGlobalAngle(float currentGlobalAngle) {
        mCurrentGlobalAngle = currentGlobalAngle;
        invalidateSelf();
    }

    public float getCurrentGlobalAngle() {
        return mCurrentGlobalAngle;
    }

    public void setCurrentSweepAngle(float currentSweepAngle) {
        mCurrentSweepAngle = currentSweepAngle;
        invalidateSelf();
    }

    public float getCurrentSweepAngle() {
        return mCurrentSweepAngle;
    }

    public static final class Builder {

        private CircleProgressDrawable drawable;
        private int angleAnimatorDuration = 2000;
        private int sweepAnimatorDuration = 600;
        private int minSweepAngle = 30;
        private int borderWidth = 4;
        private int color = 0x00ff00;

        public CircleProgressDrawable build() {
            CircleProgressDrawable drawable = new CircleProgressDrawable(color, borderWidth);
            drawable.angleAnimatorDuration = angleAnimatorDuration;
            drawable.sweepAnimatorDuration = sweepAnimatorDuration;
            drawable.minSweepAngle = minSweepAngle;
            return drawable;
        }

        public void angleAnimatorDuration(int millis) {
            this.angleAnimatorDuration = millis;
        }

        public void sweepAnimatorDuration(int millis) {
            this.sweepAnimatorDuration = millis;
        }

        public void borderWidth(int px) {
            this.borderWidth = px;
        }

        public void color(int color) {
            this.color = color;
        }
    }
}
