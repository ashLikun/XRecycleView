package com.ashlikun.xrecycleview

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ashlikun.animmenu.AnimMenu
import com.ashlikun.animmenu.AnimMenuItem
import com.ashlikun.animmenu.OnMenuItemClickListener
import com.ashlikun.xrecycleview.listener.OnGoTopClickListener

/**
 * @author　　: 李坤
 * 创建时间: 2021.12.9 19:22
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：带有下拉刷新和自动分页的RecyclerView
 */

class SuperRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        /**
         * 下拉刷新是否用自定义的，
         * true:使用自定义的base_swipe_custom_recycle.xml
         * false:使用自定义的base_swipe_recycle.xml    google自己的
         */
        var REFRESH_IS_CUSTOM = false

        //返回顶部的animMenu的Tag
        const val TAG_ANIMMENU_GO_TOP = "TAG_ANIMMENU_GO_TOP"
        const val DEFAULT_ANIM_MENU_POSITION = 5
    }

    var refreshLayout: RefreshLayout? = null
        //替换下拉刷新
        set(value) {
            if (field !== value) {
                field?.setEnabled(false)
                field = value
            }
            recyclerView.refreshLayout = field
        }
    lateinit var recyclerView: RecyclerViewAutoLoadding
    var animMenu: AnimMenu? = null

    //第几个的时候打开AnimMenu
    private var openAnimMenuPosition = DEFAULT_ANIM_MENU_POSITION
    private var goTopIconStrokeColor = -0xcececf
    private var goTopIconStrokeWidth = dip2px(context, 1)
    private var isGoTop = false
    private var goTopIcon = 0

    //设置返回顶部的事件
    var goTopClickListener: OnGoTopClickListener? = null
    var menuItemClickListener: OnMenuItemClickListener? = null

    //获取pagingHelp
    val pageHelp: PageHelp
        get() = recyclerView.pageHelp
    val configChang: ConfigChang?
        get() = if (recyclerView is ConfigChang) recyclerView else null

    //分页监听
    val pageHelpListener: PageHelpListener
        get() = recyclerView.pageHelpListener


    val myScroll: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (animMenu != null) {
                val layoutManager = recyclerView.layoutManager
                var firstVisiblePosition = 0
                if (layoutManager is LinearLayoutManager) {
                    firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
                } else if (layoutManager is GridLayoutManager) {
                    firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
                } else if (layoutManager is StaggeredGridLayoutManager) {
                    val into = layoutManager.findFirstVisibleItemPositions(null)
                    firstVisiblePosition = findMax(into)
                }
                if (firstVisiblePosition >= openAnimMenuPosition) {
                    animMenu!!.openMenu()
                } else {
                    animMenu!!.closeMenu()
                }
            }
        }
    }

    init {
        if (!isInEditMode) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SuperRecyclerView)
            isGoTop = a.getBoolean(R.styleable.SuperRecyclerView_srv_isGoTop, false)
            goTopIcon =
                a.getResourceId(
                    R.styleable.SuperRecyclerView_srv_goTopIcon,
                    R.drawable.icon_go_top
                )
            openAnimMenuPosition =
                a.getInteger(
                    R.styleable.SuperRecyclerView_srv_goTopOnPosition,
                    openAnimMenuPosition
                )
            goTopIconStrokeWidth = a.getDimensionPixelSize(
                R.styleable.SuperRecyclerView_srv_goTopIcon_strokeColor,
                goTopIconStrokeWidth
            )
            if (!REFRESH_IS_CUSTOM) {
                LayoutInflater.from(getContext())
                    .inflate(R.layout.base_swipe_recycle, this, true)
            } else {
                LayoutInflater.from(getContext())
                    .inflate(R.layout.base_swipe_custom_recycle, this, true)
            }
            recyclerView = findViewById(R.id.list_swipe_target)
            refreshLayout = findViewById(R.id.swipe)

            recyclerView.noDataIsShow =
                a.getBoolean(R.styleable.SuperRecyclerView_rv_noDataIsShow, true)
            recyclerView.maxHeight =
                a.getDimension(R.styleable.SuperRecyclerView_rv_maxHeight, 0f)
            recyclerView.maxRatio = a.getFloat(R.styleable.SuperRecyclerView_rv_heightRatio, 0f)
            recyclerView.isNoTouch =
                a.getBoolean(R.styleable.SuperRecyclerView_rv_noTouch, false)
            recyclerView.nestedOpen =
                a.getBoolean(R.styleable.SuperRecyclerView_rv_nested_open, false)
            setLoadFootlayoutId(a.getInt(R.styleable.SuperRecyclerView_rv_footLoadLayoutId, -1))
            if (a.hasValue(R.styleable.SuperRecyclerView_rv_footLoadColor)) {
                recyclerView.setLoadFootColor(
                    a.getColor(
                        R.styleable.SuperRecyclerView_rv_footLoadColor,
                        0
                    )
                )
            }
            if (a.hasValue(R.styleable.SuperRecyclerView_rv_neibuBackground)) {
                recyclerView.setBackgroundResource(
                    a.getResourceId(
                        R.styleable.SuperRecyclerView_rv_neibuBackground,
                        0
                    )
                )
            }
            if (isGoTop) {
                addGoTopView()
            }
            setColorSchemeResources(context, refreshLayout!!)
            recyclerView.addOnScrollListener(myScroll)
            a.recycle()
        }
    }

    fun addGoTopView() {
        initAnimMenu()
        if (topAnimMenuItem != null) {
            //已经有了
            return
        }
        animMenu?.addView(
            animMenu!!
                .getDefaultItem()
                .tag(TAG_ANIMMENU_GO_TOP)
                .strokeWidth(goTopIconStrokeWidth.toFloat())
                .strokeColor(goTopIconStrokeColor)
                .iconId(goTopIcon)
        )
    }

    val topAnimMenuItem: AnimMenuItem?
        get() {
            for (i in 0 until (animMenu?.childCount ?: 0)) {
                try {
                    val item = animMenu?.getChildAt(i) as AnimMenuItem?
                    if (TAG_ANIMMENU_GO_TOP == item?.itemTag) {
                        return item
                    }
                } catch (e: Exception) {
                }
            }
            return null
        }

    /**
     * 清空返回顶部的view
     */
    fun cleanGoTopView() {
        if (animMenu != null) {
            val item = topAnimMenuItem
            if (item != null) {
                //已经有了
                animMenu!!.removeView(item)
            }
        }
    }

    private fun initAnimMenu() {
        if (animMenu != null) {
            return
        }
        animMenu = AnimMenu(context)
        animMenu?.run {
            setAutoOpen(false)
            setNormalColor(-0x1)
            setPressColor(-0x666667)
            isClickable = true
            setItemClickListener { index, tag ->
                if (TAG_ANIMMENU_GO_TOP == tag) {
                    recyclerView.scrollToPosition(0)
                    if (goTopClickListener != null) {
                        goTopClickListener!!.onGoTopListener()
                    }
                }
                menuItemClickListener?.onItemClick(index, tag)
            }
            setItemAnimListener { isOpen ->
                if (!isOpen) {
                    visibility = GONE
                }
            }
            alpha = 0.8f
            val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            params.gravity = Gravity.RIGHT or Gravity.BOTTOM
            params.bottomMargin = dip2px(20)
            params.rightMargin = dip2px(3)
            addView(this, params)
            visibility = GONE
        }

    }


    /**
     * 设置加载更多的回调
     */
    fun setOnLoaddingListener(onLoaddingListener: OnLoaddingListener) {
        recyclerView.onLoaddingListener = onLoaddingListener
    }

    /**
     * 设置下拉刷新的回调
     */
    fun setOnRefreshListener(listener: RefreshLayout.OnRefreshListener) {
        refreshLayout?.setOnRefreshCallback(listener)
    }

    /**
     * 设置适配器
     */
    fun setAdapter(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>?) {
        recyclerView.setAdapter(adapter)
    }


    /**
     * 设置分页数据为下一页
     * 为下次加载做准备
     */
    fun nextPage() {
        pageHelp.nextPage()
    }

    /**
     * 设置页数信息
     * 会自动下一页+1
     */
    fun setPageInfo(currentPage: Int, recordPage: Int) {
        pageHelp.setPageInfo(currentPage, recordPage)
    }

    /**
     * 设置页数信息
     * 不会自动下一页
     */
    fun setPageInfoNoNext(currentPage: Int, recordPage: Int) {
        pageHelp.setPageInfoNoNext(currentPage, recordPage)
    }


    /**
     * 刷新
     */
    fun setRefreshing(refreshing: Boolean) {
        refreshLayout?.setRefreshing(refreshing)
    }


    fun addItemDecoration(decor: ItemDecoration) {
        recyclerView.addItemDecoration(decor)
    }

    fun setLayoutManager(layoutManager: LayoutManager) {
        recyclerView.layoutManager = layoutManager
    }

    /**
     * 设置是否可以下拉刷新
     */
    fun setLoadMoreEnabled(loadMoreEnabled: Boolean) {
        recyclerView.isLoadMoreEnabled = loadMoreEnabled
    }

    /**
     * 设置底部加载中的文字
     * 建议使用String.xml  替换R.string.loadding变量
     */
    fun setLoaddingFooterText(loaddingFooterText: String) {
        recyclerView.setLoaddingFooterText(loaddingFooterText)
    }

    fun setOpenAnimMenuPosition(openAnimMenuPosition: Int) {
        this.openAnimMenuPosition = openAnimMenuPosition
    }


    /**
     * 没有数据的时候是否显示LoadView
     */
    fun setNoDataIsShow(noDataIsShow: Boolean) {
        recyclerView.noDataIsShow = noDataIsShow
    }

    /**
     * 底部加载布局
     *
     * @param loadFootlayoutId
     */
    fun setLoadFootlayoutId(loadFootlayoutId: Int) {
        recyclerView.setLoadFootlayoutId(loadFootlayoutId)
    }


    fun setGoTop(goTop: Boolean) {
        isGoTop = goTop
        if (isGoTop) {
            addGoTopView()
        } else {
            cleanGoTopView()
        }
    }

    fun setGoTopIcon(@DrawableRes icon: Int) {
        goTopIcon = icon
        cleanGoTopView()
        if (isGoTop) {
            addGoTopView()
        }
    }

    fun setGoTopIconStrokeColor(goTopIconStrokeColor: Int) {
        this.goTopIconStrokeColor = goTopIconStrokeColor
        cleanGoTopView()
        if (isGoTop) {
            addGoTopView()
        }
    }

    fun setGoTopIconStrokeWidth(goTopIconStrokeWidth: Int) {
        this.goTopIconStrokeWidth = goTopIconStrokeWidth
        cleanGoTopView()
        if (isGoTop) {
            addGoTopView()
        }
    }

    /**
     * 添加Anim菜单
     */
    fun addAnimMenu(builder: AnimMenuItem.Builder) {
        initAnimMenu()
        animMenu?.addView(builder)
    }

    fun isGoTop(): Boolean {
        return isGoTop
    }

    fun getGoTopIcon(): Int {
        return goTopIcon
    }

    fun getGoTopIconStrokeColor(): Int {
        return goTopIconStrokeColor
    }

    fun getGoTopIconStrokeWidth(): Int {
        return goTopIconStrokeWidth
    }


}