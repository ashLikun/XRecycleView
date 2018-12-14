package com.ashlikun.xrecycleview;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ashlikun.circleprogress.CircleProgressView;

import java.lang.ref.WeakReference;


/**
 * @author　　: 李坤
 * 创建时间: 2018/12/14 16:56
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：加载更多的view
 */

public class LoadView extends LinearLayout {
    private CircleProgressView progressBar;
    private TextView textView;
    private Context context;
    private LoadState state = LoadState.Init;
    private String noDataFooterText = getResources().getString(R.string.autoloadding_no_data);
    private String loaddingFooterText = getResources().getString(R.string.autoloadding_loadding);
    private boolean loadMoreEnabled = true;
    MyHandler textViewHandler;

    public LoadView(Context context) {
        this(context, null);
    }

    public LoadView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.base_autoloadding_footer, this);
        setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, dip2px(50)));
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
        textView = (TextView) findViewById(R.id.tvLoadMore);
        textViewHandler = new MyHandler(textView);
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
    public void setStatus(LoadState status) {
        this.state = status;
        if (status == LoadState.NoData) {
            progressBar.setVisibility(GONE);
            setTextViewText(noDataFooterText, false);
            setVisibility(VISIBLE);
        } else if (status == LoadState.Loadding) {
            progressBar.setVisibility(VISIBLE);
            setTextViewText(loaddingFooterText, false);
            setVisibility(VISIBLE);
        } else if (status == LoadState.Init) {
            setVisibility(GONE);
        } else if (status == LoadState.Hint) {
            setVisibility(GONE);
        } else if (status == LoadState.Failure) {
            setTextViewText(context.getString(R.string.autoloadding_failure), false);
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
        Message msg = textViewHandler.obtainMessage(1);
        msg.obj = text;
        textViewHandler.sendMessageDelayed(msg, 100);
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

    public void setRecycleAniming() {
        setTextViewText(textView.getText().toString(), true);
    }

    public class MyHandler extends Handler {
        WeakReference<TextView> mWeakReference;

        public MyHandler(TextView textView) {
            this.mWeakReference = new WeakReference<>(textView);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mWeakReference.get() != null) {
                String text = (String) msg.obj;
                mWeakReference.get().setText(text);
            }
        }
    }
}