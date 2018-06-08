package com.ashlikun.xrecycleview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.ashlikun.animmenu.AnimMenu;
import com.ashlikun.animmenu.AnimMenuItem;
import com.ashlikun.animmenu.OnMenuItemClickListener;
import com.ashlikun.xrecycleview.listener.OnGoTopClickListener;

/**
 * 作者　　: 李坤
 * 创建时间: 16:32 Administrator
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：带有下拉刷新和自动分页的RecyclerView
 */

public class SuperRecyclerView extends FrameLayout {
    //返回顶部的animMenu的Tag
    public static final String TAG_ANIMMENU_GO_TOP = "TAG_ANIMMENU_GO_TOP";
    public static final int DEFAULT_ANIM_MENU_POSITION = 5;
    public RefreshLayout refreshLayout;
    RecyclerViewAutoLoadding recyclerView;
    AnimMenu animMenu;
    //第几个的时候打开AnimMenu
    private int openAnimMenuPosition = DEFAULT_ANIM_MENU_POSITION;
    private boolean isGoTop;
    OnGoTopClickListener goTopClickListener;
    OnMenuItemClickListener menuItemClickListener;

    public SuperRecyclerView(Context context) {
        this(context, null);
    }

    public SuperRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SuperRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAtt(context, attrs);
        if (!isInEditMode()) {
            initView();
        }

    }

    private void initAtt(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressView);
        isGoTop = a.getBoolean(R.styleable.SuperRecyclerView_srv_isGoTop, true);
        openAnimMenuPosition = a.getInteger(R.styleable.SuperRecyclerView_srv_goTopOnPosition, openAnimMenuPosition);
        a.recycle();
    }

    public void addGoTopView() {
        initAnimMenu();
        for (int i = 0; i < animMenu.getChildCount(); i++) {
            try {
                AnimMenuItem item = (AnimMenuItem) animMenu.getChildAt(i);
                //已经有了
                if (item != null && TAG_ANIMMENU_GO_TOP.equals(item.getItemTag())) {
                    return;
                }
            } catch (Exception e) {

            }
        }
        animMenu.addView(animMenu
                .getDefaultItem()
                .tag(TAG_ANIMMENU_GO_TOP)
                .strokeWidth(3)
                .strokeColor(0xff313131)
                .iconId(R.drawable.icon_go_top));

    }

    /**
     * 清空返回顶部的view
     */
    public void cleanGoTopView() {
        if (animMenu != null) {
            for (int i = 0; i < animMenu.getChildCount(); i++) {
                try {
                    AnimMenuItem item = (AnimMenuItem) animMenu.getChildAt(i);
                    if (item != null && TAG_ANIMMENU_GO_TOP.equals(item.getItemTag())) {
                        animMenu.removeView(item);
                        return;
                    }
                } catch (Exception e) {

                }
            }
        }
    }


    private void initAnimMenu() {
        if (animMenu != null) {
            return;
        }
        animMenu = new AnimMenu(getContext());
        animMenu.setAutoOpen(false);
        animMenu.setNormalColor(0xffffffff);
        animMenu.setPressColor(0xff999999);
        animMenu.setClickable(true);
        animMenu.setItemClickListener(new OnMenuItemClickListener() {
            @Override
            public void onItemClick(int index, String tag) {
                if (TAG_ANIMMENU_GO_TOP.equals(tag)) {
                    recyclerView.scrollToPosition(0);
                    if (goTopClickListener != null) {
                        goTopClickListener.onGoTopListener();
                    }
                }
                if (menuItemClickListener != null) {
                    menuItemClickListener.onItemClick(index, tag);
                }
            }
        });
        animMenu.setItemAnimListener(new OnMenuItemClickListener.OnMenuItemAnimListener() {
            @Override
            public void onAnimationEnd(boolean isOpen) {
                if (!isOpen) {
                    animMenu.setVisibility(GONE);
                }
            }
        });
        animMenu.setAlpha(0.8f);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        params.bottomMargin = dip2px(20);
        params.rightMargin = dip2px(20);
        addView(animMenu, params);
        animMenu.setVisibility(GONE);
    }

    public int dip2px(int dip) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.base_swipe_recycle, this, true);
        refreshLayout = (RefreshLayout) findViewById(R.id.swipe);
        recyclerView = (RecyclerViewAutoLoadding) findViewById(R.id.list_swipe_target);
        if (isGoTop) {
            addGoTopView();
        }
        /**
         * 设置集合view的刷新view
         */
        recyclerView.setRefreshLayout(refreshLayout);
        setColorSchemeResources(refreshLayout);
        recyclerView.addOnScrollListener(myScroll);
    }

    public void setColorSchemeResources(RefreshLayout refreshLayout) {
        TypedArray array = getContext().getTheme().obtainStyledAttributes(new int[]{R.attr.SwipeRefreshLayout_Color1, R.attr.SwipeRefreshLayout_Color2, R.attr.SwipeRefreshLayout_Color3, R.attr.SwipeRefreshLayout_Color4});
        refreshLayout.setColorSchemeColors(array.getColor(0, 0xff0000), array.getColor(1, 0xff0000), array.getColor(2, 0xff0000), array.getColor(3, 0xff0000));
        array.recycle();
    }

    /**
     * 设置加载更多的回调
     *
     * @param onLoaddingListener
     */
    public void setOnLoaddingListener(OnLoaddingListener onLoaddingListener) {
        recyclerView.setOnLoaddingListener(onLoaddingListener);

    }

    /**
     * 设置下拉刷新的回调
     *
     * @param listener
     */
    public void setOnRefreshListener(RefreshLayout.OnRefreshListener listener) {
        if (refreshLayout != null) {
            refreshLayout.setOnRefreshListener(listener);
        }
    }


    /**
     * 设置适配器
     */
    public void setAdapter(RecyclerView.Adapter adapter) {
        recyclerView.setAdapter(adapter);
    }

    /**
     * 获取pagingHelp
     */
    public PageHelp getPageHelp() {
        return recyclerView.getPageHelp();
    }

    /**
     * 设置分页数据为下一页
     * 为下次加载做准备
     */
    public void nextPage() {
        recyclerView.getPageHelp().nextPage();
    }

    /**
     * 设置页数信息
     * 会自动下一页+1
     */
    public void setPageInfo(int currentPage, int recordPage) {
        recyclerView.getPageHelp().setPageInfo(currentPage, recordPage);
    }

    /**
     * 设置页数信息
     * 不会自动下一页
     */
    public void setPageInfoNoNext(int currentPage, int recordPage) {
        recyclerView.getPageHelp().setPageInfoNoNext(currentPage, recordPage);
    }


    public RefreshLayout getRefreshLayout() {
        return refreshLayout;
    }

    public ConfigChang getConfigChang() {
        if (recyclerView instanceof ConfigChang) {
            return (ConfigChang) recyclerView;
        } else {
            return null;
        }
    }

    /**
     * 刷新
     *
     * @return
     */
    public void setRefreshing(boolean refreshing) {
        refreshLayout.setRefreshing(refreshing);
    }


    public RecyclerViewAutoLoadding getRecyclerView() {
        return recyclerView;
    }


    public StatusChangListener getStatusChangListener() {
        return recyclerView.getStatusChangListener();
    }

    public void addItemDecoration(RecyclerView.ItemDecoration decor) {
        recyclerView.addItemDecoration(decor);
    }

    public void setLayoutManager(RecyclerView.LayoutManager layout) {
        recyclerView.setLayoutManager(layout);
    }

    //设置是否可以下拉刷新
    public void setLoadMoreEnabled(boolean loadMoreEnabled) {
        recyclerView.setLoadMoreEnabled(loadMoreEnabled);
    }

    /**
     * 设置底部加载中的文字
     * 建议使用String.xml  替换R.string.loadding变量
     */
    public void setLoaddingFooterText(String loaddingFooterText) {
        recyclerView.setLoaddingFooterText(loaddingFooterText);
    }

    /**
     * 设置返回顶部的事件
     *
     * @param goTopClickListener
     */
    public void setGoTopClickListener(OnGoTopClickListener goTopClickListener) {
        this.goTopClickListener = goTopClickListener;
    }

    public void setOpenAnimMenuPosition(int openAnimMenuPosition) {
        this.openAnimMenuPosition = openAnimMenuPosition;
    }

    public void setGoTop(boolean goTop) {
        isGoTop = goTop;
        if (isGoTop) {
            addGoTopView();
        } else {
            cleanGoTopView();
        }
    }

    /**
     * 设置菜单点击事件
     *
     * @param menuItemClickListener
     */
    public void setMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {
        this.menuItemClickListener = menuItemClickListener;
    }

    /**
     * 添加Anim菜单
     *
     * @param builder
     */
    public void addAnimMenu(AnimMenuItem.Builder builder) {
        initAnimMenu();
        animMenu.addView(builder);
    }

    RecyclerView.OnScrollListener myScroll = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (animMenu != null) {
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int firstVisiblePosition = 0;
                if (layoutManager instanceof LinearLayoutManager) {
                    firstVisiblePosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                } else if (layoutManager instanceof GridLayoutManager) {
                    firstVisiblePosition = ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
                } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                    int[] into = ((StaggeredGridLayoutManager) layoutManager).findFirstVisibleItemPositions(null);
                    firstVisiblePosition = findMax(into);
                }
                if (firstVisiblePosition >= openAnimMenuPosition) {
                    animMenu.openMenu();
                } else {
                    animMenu.closeMenu();
                }
            }
        }

        private int findMax(int[] lastPositions) {
            int max = lastPositions[0];
            for (int value : lastPositions) {
                if (value > max) {
                    max = value;
                }
            }
            return max;
        }
    };

}
