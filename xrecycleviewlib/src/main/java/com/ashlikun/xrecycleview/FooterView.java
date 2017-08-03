package com.ashlikun.xrecycleview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;


/**
 * Created by Administrator on 2016/3/14.
 */
public class FooterView extends RelativeLayout {
    private MaterialProgressBar progressBar;
    private TextView textView;
    private Context context;
    private LoadState state = LoadState.Init;
    private int dataSize = 0;
    private String autoloaddingNoData = getResources().getString(R.string.autoloadding_no_data);
    private String autoloaddingCompleData = getResources().getString(R.string.autoloadding_comple_data);
    private boolean loadMoreEnabled = true;

    public FooterView(Context context) {
        this(context, null);
    }

    public FooterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        LayoutInflater.from(context).inflate( R.layout.base_autoloadding_footer, this);
        textView = (TextView) findViewById(R.id.tvLoadMore);
        progressBar = (MaterialProgressBar) findViewById(R.id.progressbar);
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
            setVisibility(VISIBLE);
            textView.setText(dataSize > 0 ? String.format(autoloaddingCompleData, dataSize) : autoloaddingNoData);
        } else if (status == LoadState.Loadding) {
            progressBar.setVisibility(VISIBLE);
            textView.setText(context.getString(R.string.loadding));
            setVisibility(VISIBLE);
        } else if (status == LoadState.Init) {
            setVisibility(GONE);
        } else if (status == LoadState.Hint) {
            setVisibility(GONE);
        } else if (status == LoadState.Failure) {
            textView.setText(context.getString(R.string.autoloadding_failure));
            progressBar.setVisibility(GONE);
            setVisibility(VISIBLE);
        } else if (status == LoadState.Complete) {
            setVisibility(GONE);
        }
    }

    public LoadState getStates() {
        return state;
    }

    public String getAutoloaddingNoData() {
        return autoloaddingNoData;
    }

    public String getAutoloaddingCompleData() {
        return autoloaddingCompleData;
    }

    public void setAutoloaddingCompleData(String autoloaddingCompleData) {
        this.autoloaddingCompleData = autoloaddingCompleData;
    }

    public void setAutoloaddingNoData(String autoloaddingNoData) {
        this.autoloaddingNoData = autoloaddingNoData;
    }

    public int getDataSize() {
        return dataSize;
    }

    public void setDataSize(int dataSize) {
        this.dataSize = dataSize;
        setStatus(state);
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
}
