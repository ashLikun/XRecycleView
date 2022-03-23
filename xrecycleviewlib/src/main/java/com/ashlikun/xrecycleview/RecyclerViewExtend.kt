package com.ashlikun.xrecycleview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ashlikun.xrecycleview.nested.NestedOnChildTouch
import java.util.*

/**
 * @author　　: 李坤
 * 创建时间: 2021.12.9 16:29
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：功能介绍：扩展的RecyclerView
 * 1：带头部与底部的RecycleView
 * 2：上下加载更多
 * 3：和可以设置最大高度
 */

open class RecyclerViewExtend @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RecyclerView(context, attrs, defStyle) {
    companion object {
        const val HEADERSIZE = "headerSize"
        const val FOOTERSIZE = "footerSize"
        private const val TYPE_HEADER = -900004
        private const val TYPE_REFRESH_HEADER = -900005
        private const val TYPE_NORMAL = 0
        private const val TYPE_REFRESH_FOOTER = -900003
        private const val TYPE_FOOTER = -900002
    }

    //头
    var headerViews = ArrayList<View>()

    //尾
    var footViews = ArrayList<View>()

    protected var mAdapter: Adapter<ViewHolder>? = null
    protected var mWrapAdapter: WrapAdapter? = null

    //最大高度
    var maxHeight = 0f
        set(value) {
            field = value
            requestLayout()
        }

    //最大比例,相对于宽度
    var maxRatio = 0f
        set(value) {
            field = value
            requestLayout()
        }

    //是否可以触摸
    var isNoTouch = false

    //是否需要开启和外部的RecyclerView 嵌套滚动
    var nestedOpen = false
        set(value) {
            field = value
            if (field) childTouch = NestedOnChildTouch(this)
        }
    open val dataSize: Int
        get() = mAdapter?.itemCount ?: 0

    //开启和外部的RecyclerView 嵌套滚动 的处理
    protected open lateinit var childTouch: NestedOnChildTouch

    open val headerViewSize: Int
        get() = headerViews.size
    open val footViewSize: Int
        get() = footViews.size
    open val footViewHeight: Int
        get() {
            var height = 0
            footViews.forEach {
                height += it.height
            }
            return height
        }
    open val headerViewHeight: Int
        get() {
            var height = 0
            headerViews.forEach {
                height += it.height
            }
            return height
        }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.RecyclerViewExtend)
        maxRatio = a.getFloat(R.styleable.RecyclerViewExtend_rv_heightRatio, 0f)
        maxHeight = a.getDimension(R.styleable.RecyclerViewExtend_rv_maxHeight, 0f)
        isNoTouch = a.getBoolean(R.styleable.RecyclerViewExtend_rv_noTouch, false)
        nestedOpen = a.getBoolean(R.styleable.RecyclerViewExtend_rv_nested_open, false)
        a.recycle()
        overScrollMode = OVER_SCROLL_NEVER
    }

    fun isHeader(viewHolder: ViewHolder): Boolean {
        return viewHolder.itemViewType == TYPE_REFRESH_HEADER || viewHolder.itemViewType == TYPE_HEADER
    }

    fun isFooter(viewHolder: ViewHolder): Boolean {
        return viewHolder.itemViewType == TYPE_REFRESH_FOOTER || viewHolder.itemViewType == TYPE_FOOTER
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (maxHeight <= 0 && maxRatio <= 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        if (maxHeight <= 0) {
            maxHeight = maxRatio * widthSize
        } else if (maxRatio > 0) {
            maxHeight = Math.min(maxHeight, maxRatio * widthSize)
        }
        if (maxHeight <= 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        var heightSize = MeasureSpec.getSize(heightMeasureSpec)
        if (heightMode == MeasureSpec.EXACTLY) {
            heightSize = if (heightSize <= maxHeight) heightSize else maxHeight.toInt()
        }
        if (heightMode == MeasureSpec.UNSPECIFIED) {
            heightSize = if (heightSize <= maxHeight) heightSize else maxHeight.toInt()
        }
        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = if (heightSize <= maxHeight) heightSize else maxHeight.toInt()
        }
        val maxHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
            heightSize,
            heightMode
        )
        super.onMeasure(widthMeasureSpec, maxHeightMeasureSpec)
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        if (isNoTouch) {
            return false
        }
        return if (nestedOpen && childTouch.onTouchEvent(e)) false else super.onTouchEvent(
            e
        )
    }

    override fun setAdapter(adapter: Adapter<out ViewHolder>?) {
        try {
            mAdapter?.unregisterAdapterDataObserver(mDataObserver)
        } catch (e: IllegalStateException) {
        }
        mAdapter = adapter as Adapter<ViewHolder>?
        mWrapAdapter = WrapAdapter()
        super.setAdapter(mWrapAdapter)
        try {
            mAdapter?.registerAdapterDataObserver(mDataObserver)
        } catch (e: IllegalStateException) {
        }
        mWrapAdapter?.notifyDataSetChanged()
        setHeaderSize()
        setFooterSize()
    }

    override fun getAdapter(): Adapter<out ViewHolder>? {
        return mAdapter
    }

    open fun addHeaderView(view: View) {
        if (view.layoutParams == null) {
            view.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }
        headerViews.add(view)
        setHeaderSize()
    }

    open fun addHeaderView(index: Int, view: View) {
        if (view.layoutParams == null) {
            view.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }
        headerViews.add(index, view)
        setHeaderSize()
    }

    open fun putHeaderView(view: View) {
        if (!containsHeaderView(view)) {
            addHeaderView(view)
        }
    }

    open fun putHeaderView(index: Int, view: View) {
        if (!containsHeaderView(view)) {
            addHeaderView(index, view)
        }
    }

    open fun getHeaderView(index: Int) = headerViews.getOrNull(index)

    open fun removeHeaderView(view: View) {
        headerViews.remove(view)
        setHeaderSize()
        mWrapAdapter?.notifyDataSetChanged()
    }

    open fun containsHeaderView(view: View) = headerViews.contains(view)

    open fun removeFootView(view: View) {
        footViews.remove(view)
        setFooterSize()
        mWrapAdapter?.notifyDataSetChanged()
    }

    open fun containsFootView(view: View) = footViews.contains(view)

    open fun addFootView(view: View) {
        if (view.layoutParams == null) {
            view.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }
        footViews.add(view)
        setFooterSize()
    }

    open fun addFootView(index: Int, view: View) {
        if (view.layoutParams == null) {
            view.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }
        footViews.add(index, view)
        setFooterSize()
    }

    open fun putFootView(view: View) {
        if (!containsFootView(view)) {
            addFootView(view)
        }
    }

    open fun putFootView(index: Int, view: View) {
        if (!containsFootView(view)) {
            addFootView(index, view)
        }
    }

    open fun getFootView(index: Int) = footViews.getOrNull(index)

    protected open fun setFooterSize() {
        if (mAdapter != null) setHeaderFooterSize(mAdapter!!, FOOTERSIZE, footViewSize)
    }

    protected open fun setHeaderSize() {
        if (mAdapter != null) setHeaderFooterSize(mAdapter!!, HEADERSIZE, headerViewSize)
    }


    open fun scrollToPositionWithOffset(postion: Int) {
        when (val layoutManager = layoutManager) {
            is LinearLayoutManager -> layoutManager.scrollToPositionWithOffset(postion, 0)
            is StaggeredGridLayoutManager -> layoutManager.scrollToPositionWithOffset(postion, 0)
        }
    }


    /**
     * 当adapter不是调用全部刷新，并且有动画的时候
     */
    protected open fun onAdapterItemAnimChang() {}

    private val mDataObserver: AdapterDataObserver = object : AdapterDataObserver() {
        override fun onChanged() {
            mWrapAdapter?.notifyDataSetChanged()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            mWrapAdapter?.notifyItemRangeInserted(headerViewSize + positionStart, itemCount)
            if (itemAnimator != null) {
                onAdapterItemAnimChang()
            }
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            mWrapAdapter?.notifyItemRangeChanged(headerViewSize + positionStart, itemCount)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            mWrapAdapter?.notifyItemRangeChanged(headerViewSize + positionStart, itemCount, payload)
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            mWrapAdapter?.notifyItemRangeRemoved(headerViewSize + positionStart, itemCount)
            if (itemAnimator != null) {
                onAdapterItemAnimChang()
            }
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            mWrapAdapter?.notifyItemMoved(
                headerViewSize + fromPosition,
                headerViewSize + toPosition
            )
            if (itemAnimator != null) {
                onAdapterItemAnimChang()
            }
        }
    }

    protected inner class WrapAdapter : Adapter<ViewHolder>() {
        private var headerPosition = 0
        private var footerPosition = 0
        fun isHeader(position: Int) = position in 0 until headerViews.size
        fun isHeaderLoad(position: Int) =
            headerViews.size > 0 && position == 0 && headerViews[0] is LoadView

        fun isFooter(position: Int): Boolean =
            position in itemCount - footViews.size until itemCount

        fun isFooterLoad(position: Int) =
            footViews.size > 0 && position == itemCount - 1 && footViews[footViews.size - 1] is LoadView

        override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
            mAdapter?.onAttachedToRecyclerView(recyclerView)
            val manager = recyclerView.layoutManager
            if (manager is GridLayoutManager) {
                manager.spanSizeLookup = SpanSizeLookupGroup(manager, headerViewSize) {
                    isHeader(it) || isFooter(it)
                }
                manager.spanCount = manager.spanCount
            }
        }

        override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
            mAdapter?.onDetachedFromRecyclerView(recyclerView)
        }

        override fun onViewAttachedToWindow(holder: ViewHolder) {
            try {
                if (isHeader(holder) || isFooter(holder)) {
                    val lp = holder.itemView.layoutParams
                    if (lp != null && lp is StaggeredGridLayoutManager.LayoutParams
                        && (isHeader(holder.layoutPosition) || isFooter(holder.layoutPosition))
                    ) {
                        //StaggeredGridLayoutManager 只能这样设置一整行
                        lp.isFullSpan = true
                    }
                } else {
                    mAdapter?.onViewAttachedToWindow(holder)
                }
            } catch (e: Exception) {
            }
        }

        override fun onViewDetachedFromWindow(holder: ViewHolder) {
            try {
                if (!isHeader(holder) && !isFooter(holder))
                    mAdapter?.onViewDetachedFromWindow(holder)
            } catch (e: Exception) {
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            if (viewType == TYPE_REFRESH_FOOTER) {
                return object : ViewHolder(getFootView(footViews.size - 1)!!) {}
            } else if (viewType == TYPE_REFRESH_HEADER) {
                return object : ViewHolder(getHeaderView(0)!!) {}
            } else if (viewType == TYPE_HEADER) {
                if (headerPosition >= headerViews.size) {
                    headerPosition = 0
                }
                return object : ViewHolder(headerViews[headerPosition++]) {}
            } else if (viewType == TYPE_FOOTER) {
                if (footerPosition >= footViews.size) {
                    footerPosition = 0
                }
                return object : ViewHolder(footViews[footerPosition++]) {}
            }
            return mAdapter?.onCreateViewHolder(parent, viewType)
                ?: throw RuntimeException("请设置Adapter")
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: List<Any>) {
            super.onBindViewHolder(holder, position, payloads)
            if (isFooterLoad(position) || isHeaderLoad(position)
                || isHeader(position) || isFooter(position)
            ) return
            val adjPosition = position - headerViewSize
            if (mAdapter != null) {
                if (adjPosition < mAdapter?.itemCount ?: 0) {
                    mAdapter?.onBindViewHolder(holder, adjPosition, payloads)
                    return
                }
            }
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {}

        override fun getItemCount() = headerViewSize + footViewSize + (mAdapter?.itemCount ?: 0)

        override fun getItemViewType(position: Int): Int {
            when {
                isFooterLoad(position) -> return TYPE_REFRESH_FOOTER
                isHeaderLoad(position) -> return TYPE_REFRESH_HEADER
                isHeader(position) -> return TYPE_HEADER
                isFooter(position) -> return TYPE_FOOTER
                else -> {
                    val adjPosition = position - headerViewSize
                    val adapterCount = mAdapter?.itemCount ?: 0
                    if (adjPosition < adapterCount) {
                        return mAdapter?.getItemViewType(adjPosition) ?: TYPE_NORMAL
                    }
                    return TYPE_NORMAL
                }
            }
        }

        override fun getItemId(position: Int): Long {
            if (mAdapter != null && position >= headerViewSize) {
                val adjPosition = position - headerViewSize
                val adapterCount = mAdapter!!.itemCount
                if (adjPosition < adapterCount) {
                    return mAdapter!!.getItemId(adjPosition)
                }
            }
            return super.getItemId(position)
        }
    }


}