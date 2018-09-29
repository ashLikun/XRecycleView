package com.ashlikun.xrecycleview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ashlikun.circleprogress.CircleProgressView;


/**
 * Created by Administrator on 2016/3/14.
 */
public class FooterView extends LinearLayout {
    private CircleProgressView progressBar;
    private TextView textView;
    private Context context;
    private LoadState state = LoadState.Init;
    private String noDataFooterText = getResources().getString(R.string.autoloadding_no_data);
    private String loaddingFooterText = getResources().getString(R.string.autoloadding_loadding);
    private boolean loadMoreEnabled = true;
    /**
     * 动画之前的状态
     */
    private int animBeforeVisibility = -100;
    /**
     * 当前recycleview正在动画,就隐藏当前view
     */
    private boolean isRecycleAniming = false;

    public FooterView(Context context) {
        this(context, null);
    }

    public FooterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.base_autoloadding_footer, this);
        setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, dip2px(50)));
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
        textView = (TextView) findViewById(R.id.tvLoadMore);
        progressBar = (CircleProgressView) findViewById(R.id.progressbar);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return true;
    }


    /**
     * Init,//初始化
     * Loadding,//加载中
     * Complete,//加载完成
     * NoData,// 没有更多数据加载
     * Hint//隐藏
     *
     * @param status
     */
    public void setStatus(LoadState status, boolean isTextNeedDelayed) {
        this.state = status;
        if (status == LoadState.NoData) {
            progressBar.setVisibility(GONE);
            setTextViewText(noDataFooterText, isTextNeedDelayed);
            setVisibility(VISIBLE);
        } else if (status == LoadState.Loadding) {
            progressBar.setVisibility(VISIBLE);
            setTextViewText(loaddingFooterText, isTextNeedDelayed);
            setVisibility(VISIBLE);
        } else if (status == LoadState.Init) {
            setVisibility(GONE);
        } else if (status == LoadState.Hint) {
            setVisibility(GONE);
        } else if (status == LoadState.Failure) {
            setTextViewText(context.getString(R.string.autoloadding_failure), isTextNeedDelayed);
            progressBar.setVisibility(GONE);
            setVisibility(VISIBLE);
        } else if (status == LoadState.Complete) {
            setVisibility(GONE);
        }
    }

    public void setTextViewText(final String text, boolean isTextNeedDelayed) {
        if (!isTextNeedDelayed) {
            textView.setText(text);
            return;
        }
        textView.setText("");
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (textView != null) {
                    textView.setText(text);
                }
            }
        }, 100);
    }

    @Override
    public void setVisibility(int visibility) {
        if (isRecycleAniming) {
            //记录之前的
            animBeforeVisibility = visibility;
            super.setVisibility(GONE);
        } else {
            super.setVisibility(visibility);
        }
    }

    public LoadState getStates() {
        return state;
    }

    public String getNoDataFooterText() {
        return noDataFooterText;
    }

    /**
     * 设置底部的没有数据时候的文字
     * 建议使用String.xml  替换R.string.autoloadding_no_data变量
     */
    public void setNoDataFooterText(String noDataFooterText) {
        this.noDataFooterText = noDataFooterText;
    }

    /**
     * 设置底部加载中的文字
     * 建议使用String.xml  替换R.string.loadding变量
     */
    public void setLoaddingFooterText(String loaddingFooterText) {
        this.loaddingFooterText = loaddingFooterText;
    }


    public boolean isLoadMoreEnabled() {
        return loadMoreEnabled;
    }

    public void setLoadMoreEnabled(boolean loadMoreEnabled) {
        this.loadMoreEnabled = loadMoreEnabled;
    }

    /**
     * 作者　　: 李坤
     * 创建时间: 2017/8/3 17:45
     * 邮箱　　：496546144@qq.com
     * <p>
     * 方法功能：是否正在加载更多
     */

    public boolean isLoadMore() {
        return state == LoadState.Loadding;
    }

    private int dip2px(float dipValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 当前recycleview正在动画,就隐藏当前view
     */
    @SuppressLint("WrongConstant")
    public void setRecycleAniming(boolean isIng) {
        if (isRecycleAniming != isIng) {
            isRecycleAniming = isIng;
            if (!isIng && animBeforeVisibility != -100) {
                setVisibility(animBeforeVisibility);
                animBeforeVisibility = -100;
            }
        }
    }
}
