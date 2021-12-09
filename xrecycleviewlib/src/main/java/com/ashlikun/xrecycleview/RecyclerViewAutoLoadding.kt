package com.ashlikun.xrecycleview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * 作者　　: 李坤
 * 创建时间: 2017/4/12 0012 16:15
 *
 *
 * 方法功能：自动加载更多的RecyclerView
 * setRefreshLayout必须设置要不然无法下拉加载
 */
open class RecyclerViewAutoLoadding @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RecyclerViewExtend(context, attrs, defStyle), BaseSwipeInterface, PageHelpListener,
    ConfigChang {


    override var refreshLayout: RefreshLayout? = null
    override val pageHelpListener: PageHelpListener = this
    override var onLoaddingListener: OnLoaddingListener? = null
        set(value) {
            field = value
            pageHelp.clear()
            pageHelp.addStatusChangListener(this)
        }

    /**
     * 初始是否刷新开启
     */
    private var isInitEnableRefresh = false

    /**
     * 记录初始是否刷新
     */
    private var isOneEnableRefresh = true

    /**
     * 没有数据的时候是否显示LoadView
     */
    var noDataIsShow = true

    /**
     * 获取自动加载VIew
     */
    val loadView: LoadView?
        get() {
            val view = getFootView(footViewSize - 1)
            return if (view is LoadView) view else null
        }

    var state: LoadState?
        get() {
            return loadView?.states
        }
        set(state) {
            if (state != null) {
                setState(state, "")
            }
        }

    override var pageHelp = PageHelp(context)


    override val itemCount: Int
        get() = dataSize

    override var isLoadMoreEnabled: Boolean
        get() = loadView?.isLoadMoreEnabled ?: false
        set(value) {
            if (loadView != null) {
                loadView?.isLoadMoreEnabled = value
                if (!value) {
                    removeFootView(loadView!!)
                }
            } else if (value) {
                addLoadView()
            }
        }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.RecyclerViewAutoLoadding)
        val footLayouId = a.getInt(R.styleable.RecyclerViewAutoLoadding_rv_footLoadLayoutId, -1)
        noDataIsShow =
            a.getBoolean(R.styleable.RecyclerViewAutoLoadding_rv_noDataIsShow, noDataIsShow)
        var isFootLoadColor = false
        var footLoadColor = 0
        if (a.hasValue(R.styleable.RecyclerViewAutoLoadding_rv_footLoadColor)) {
            isFootLoadColor = true
            footLoadColor = a.getColor(R.styleable.RecyclerViewAutoLoadding_rv_footLoadColor, 0)
        }
        a.recycle()
        addLoadView(if (footLayouId == -1) null else footLayouId)
        if (isFootLoadColor) {
            setLoadFootColor(footLoadColor)
        }
    }

    /**
     * 添加加载控件
     */
    fun addLoadView(loadFootlayoutId: Int? = null) {
        var loadView = loadView
        if (loadView == null) {
            //没有，就新增
            loadView = LoadView(context, layouId = loadFootlayoutId)
            addFootView(loadView)
            loadView.visibility = GONE
            loadView.setStatus(LoadState.Init)
        } else {
            //如果已经有加载布局了，就更新
            loadView.loaddingLayout(loadFootlayoutId)
        }
    }

    /**
     * 移除加载控件
     */
    fun removeLoadView() {
        loadView?.let { removeFootView(it) }
    }


    override fun addFootView(view: View) {
        //放在自动加载的前面
        if (footViews.getOrNull(footViewSize - 1) is LoadView) footViews.add(footViewSize - 1, view)
        else footViews.add(view)
        setFooterSize()
    }

    override fun onScrolled(dx: Int, dy: Int) {
        super.onScrolled(dx, dy)
        val isCan =
            (refreshLayout == null || !refreshLayout!!.isRefreshing()) && state != null && isLoadMoreEnabled && state != LoadState.Loadding && state != LoadState.NoData
        if (isCan) {
            layoutManager?.run {
                val lastVisibleItemPosition = when (this) {
                    is GridLayoutManager -> findLastVisibleItemPosition()
                    is StaggeredGridLayoutManager -> findMax(findLastVisibleItemPositions(null))
                    else -> (layoutManager as LinearLayoutManager?)!!.findLastVisibleItemPosition()
                }
                if (getItemCount(this) > 0 && childCount > 0
                    && lastVisibleItemPosition >= itemCount - 1 && itemCount >= childCount && pageHelp.isNext
                ) {
                    state = LoadState.Loadding
                    onLoaddingListener?.onLoadding()
                }
            }

        }
    }

    private fun getItemCount(layoutManager: LayoutManager): Int {
        return layoutManager!!.itemCount - headerViewSize - footViewSize
    }


    override fun complete(message: String) {
        if (state != null && state != LoadState.NoData) {
            setState(LoadState.Complete, message)
            //停止滚动
            stopScroll()
        }
    }

    /**
     * 没有更多数据加载
     */

    override fun noData(message: String) {
        if (!noDataIsShow) {
            setState(LoadState.Hint, message)
        } else {
            setState(LoadState.NoData, message)
        }
        //停止滚动
        stopScroll()
    }

    /**
     * 初始化状态
     */
    override fun init(message: String) {
        setState(LoadState.Init, message)
    }

    /**
     * 加载失败
     */
    override fun failure(message: String) {
        if (state != null && state != LoadState.NoData) {
            setState(LoadState.Failure, message)
        }
    }

    open fun hint() {
        if (state != null) {
            state = LoadState.Hint
        }
    }

    fun setState(state: LoadState, message: String) {
        val f = loadView
        if (f != null) {
            f.setStatus(state, message)
            //如果正在加载更多，就禁用下拉刷新
            refreshLayout?.also { refreshLayout ->
                //这里要注意如果默认时候就不可以刷新那不能把他设置成可以刷新
                if (f.isLoadMore) {
                    if (isOneEnableRefresh) {
                        isOneEnableRefresh = false
                        //记录初始是否刷新
                        isInitEnableRefresh = refreshLayout.isEnabled()
                    }
                    refreshLayout.setEnabled(false)
                } else {
                    //其他状态下 还原成初始
                    if (isInitEnableRefresh) {
                        refreshLayout.setEnabled(isInitEnableRefresh)
                    }
                    isOneEnableRefresh = true
                }
            }
        }
    }


    override fun onAdapterItemAnimChang() {
        super.onAdapterItemAnimChang()
        loadView?.setRecycleAniming()
    }


    /**
     * 设置底部的没有数据时候的文字
     * 建议使用String.xml  替换R.string.autoloadding_no_data变量
     */
    override fun setNoDataFooterText(autoloaddingNoData: String) {
        loadView?.noDataFooterText = autoloaddingNoData
    }

    /**
     * 设置底部加载中的文字
     * 建议使用String.xml  替换R.string.loadding变量
     */
    fun setLoaddingFooterText(loaddingFooterText: String) {
        loadView?.setLoaddingFooterText(loaddingFooterText)
    }


    /**
     * 底部加载布局
     *
     * @param loadFootlayoutId
     */
    fun setLoadFootlayoutId(loadFootlayoutId: Int) {
        removeLoadView()
        addLoadView(loadFootlayoutId)
    }

    /**
     * 底部加载主颜色
     */
    fun setLoadFootColor(color: Int) {
        loadView?.setColor(color)
    }
}