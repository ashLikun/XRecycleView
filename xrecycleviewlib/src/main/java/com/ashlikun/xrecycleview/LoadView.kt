package com.ashlikun.xrecycleview

import kotlin.jvm.JvmOverloads
import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.widget.LinearLayout
import com.ashlikun.circleprogress.CircleProgressView
import android.widget.TextView
import com.ashlikun.xrecycleview.LoadState
import com.ashlikun.xrecycleview.R
import com.ashlikun.xrecycleview.LoadView.MyHandler
import androidx.recyclerview.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.MeasureSpec
import android.view.MotionEvent
import java.lang.ref.WeakReference

/**
 * @author　　: 李坤
 * 创建时间: 2018/12/14 16:56
 * 邮箱　　：496546144@qq.com
 *
 *
 * 功能介绍：加载更多的view
 */
class LoadView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    layouId: Int? = null
) : LinearLayout(
    context, attrs, defStyleAttr
) {
    var progressBar: CircleProgressView? = null
    var textView: TextView? = null
    var states = LoadState.Init
        private set

    /**
     * 设置底部的没有数据时候的文字
     * 建议使用String.xml  替换R.string.autoloadding_no_data变量
     */
    var noDataFooterText = resources.getString(R.string.autoloadding_no_data)
    private var loaddingFooterText = resources.getString(R.string.autoloadding_loadding)
    private val loaddingFailureText = resources.getString(R.string.autoloadding_failure)
    var isLoadMoreEnabled = true
    var textViewHandler: MyHandler? = null

    //加载更多的布局,控件Id一定要是指定的2个
    private var loadFootlayoutId = R.layout.base_autoloadding_footer

    init {
        baseLayout()
        loaddingLayout(layouId)
    }

    protected fun baseLayout() {
        layoutParams = RecyclerView.LayoutParams(LayoutParams.MATCH_PARENT, dip2px(50f))
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
    }

    /**
     * 加载一个布局
     *
     * @param layouId
     */
    public fun loaddingLayout(layouId: Int?) {
        removeAllViews()
        if (layouId != null && layouId != -1 && layouId != 0) {
            loadFootlayoutId = layouId
        }
        LayoutInflater.from(context).inflate(loadFootlayoutId, this)
        textView = findViewById<View>(R.id.tvLoadMore) as TextView
        textViewHandler = MyHandler(textView!!)
        progressBar = findViewById<View>(R.id.progressbar) as CircleProgressView
        setStatus(states)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (states == LoadState.Hint) {
            //状态隐藏
            super.onMeasure(
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY)
            )
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return true
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
    fun setStatus(status: LoadState) {
        setStatus(status, "")
    }

    fun setStatus(status: LoadState, message: String) {
        var message = message
        states = status
        if (message.isEmpty()) {
            message = when (status) {
                LoadState.NoData -> noDataFooterText
                LoadState.Loadding -> loaddingFooterText
                LoadState.Failure -> loaddingFailureText
                else -> ""
            }
        }
        setTextViewText(message, false)
        when (status) {
            LoadState.NoData -> {
                progressBar?.visibility = GONE
                visibility = VISIBLE
            }
            LoadState.Loadding -> {
                progressBar?.visibility = VISIBLE
                visibility = VISIBLE
            }
            LoadState.Init -> visibility = GONE
            LoadState.Hint -> visibility = GONE
            LoadState.Failure -> {
                progressBar?.visibility = GONE
                visibility = VISIBLE
            }
            LoadState.Complete -> visibility = GONE
        }
    }

    /**
     * @param text
     * @param isTextNeedDelayed 是否需要延迟赋值，动画的时候
     */
    fun setTextViewText(text: String?, isTextNeedDelayed: Boolean) {
        if (!isTextNeedDelayed) {
            textView?.text = text
            return
        }
        textView?.text = ""
        val msg = textViewHandler?.obtainMessage(1)
        msg?.obj = text
        textViewHandler?.sendMessageDelayed(msg, 100)
    }

    /**
     * 设置底部加载中的文字
     * 建议使用String.xml  替换R.string.loadding变量
     */
    fun setLoaddingFooterText(loaddingFooterText: String) {
        this.loaddingFooterText = loaddingFooterText
    }

    /**
     * 作者　　: 李坤
     * 创建时间: 2017/8/3 17:45
     * 邮箱　　：496546144@qq.com
     *
     *
     * 方法功能：是否正在加载更多
     */
    val isLoadMore: Boolean
        get() = states == LoadState.Loadding

    private fun dip2px(dipValue: Float): Int {
        val scale = resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }

    fun setRecycleAniming() {
        setTextViewText(textView!!.text.toString(), true)
    }

    fun setColor(color: Int) {
        textView!!.setTextColor(color)
        progressBar!!.setColor(color)
    }

    inner class MyHandler(textView: TextView) : Handler() {
        var mWeakReference: WeakReference<TextView> = WeakReference(textView)
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val text = msg.obj as String
            mWeakReference.get()?.text = text
        }

    }


}