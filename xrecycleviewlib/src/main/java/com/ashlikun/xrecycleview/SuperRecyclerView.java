package com.ashlikun.xrecycleview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.DrawableRes;
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

    /**
     * 下拉刷新是否用自定义的，
     * true:使用自定义的base_swipe_custom_recycle.xml
     * false:使用自定义的base_swipe_recycle.xml    google自己的
     */
    public static boolean REFRESH_IS_CUSTOM = false;

    //返回顶部的animMenu的Tag
    public static final String TAG_ANIMMENU_GO_TOP = "TAG_ANIMMENU_GO_TOP";
    public static final int DEFAULT_ANIM_MENU_POSITION = 5;
    public RefreshLayout refreshLayout;
    RecyclerViewAutoLoadding recyclerView;
    AnimMenu animMenu;
    //第几个的时候打开AnimMenu
    private int openAnimMenuPosition = DEFAULT_ANIM_MENU_POSITION;
    private int goTopIconStrokeColor = 0xff313131;
    private int goTopIconStrokeWidth = dip2px(1);
    private boolean isGoTop;
    private int goTopIcon;
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
        if (!isInEditMode()) {
            initView(context, attrs);
        }
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SuperRecyclerView);
        isGoTop = a.getBoolean(R.styleable.SuperRecyclerView_srv_isGoTop, true);
        goTopIcon = a.getResourceId(R.styleable.SuperRecyclerView_srv_goTopIcon, R.drawable.icon_go_top);
        openAnimMenuPosition = a.getInteger(R.styleable.SuperRecyclerView_srv_goTopOnPosition, openAnimMenuPosition);
        goTopIconStrokeWidth = a.getDimensionPixelSize(R.styleable.SuperRecyclerView_srv_goTopIcon_strokeColor, goTopIconStrokeWidth);
        if (!REFRESH_IS_CUSTOM) {
            LayoutInflater.from(getContext()).inflate(R.layout.base_swipe_recycle, this, true);
        } else {
            LayoutInflater.from(getContext()).inflate(R.layout.base_swipe_custom_recycle, this, true);
        }
        refreshLayout = findViewById(R.id.swipe);
        recyclerView = findViewById(R.id.list_swipe_target);
        recyclerView.noDataIsShow = a.getBoolean(R.styleable.SuperRecyclerView_rv_noDataIsShow, true);
        recyclerView.maxHeight = a.getDimension(R.styleable.SuperRecyclerView_rv_heightDimen, 0);
        recyclerView.maxRatio = a.getFloat(R.styleable.SuperRecyclerView_rv_heightRatio, 0);
        setLoadFootlayoutId(a.getInt(R.styleable.SuperRecyclerView_rv_footLoadLayoutId, -1));
        if (a.hasValue(R.styleable.SuperRecyclerView_rv_footLoadColor)) {
            recyclerView.setLoadFootColor(a.getColor(R.styleable.SuperRecyclerView_rv_footLoadColor, 0));
        }

        if (isGoTop) {
            addGoTopView();
        }
        //设置集合view的刷新view
        setRefreshLayout(refreshLayout);
        setColorSchemeResources(refreshLayout);
        recyclerView.addOnScrollListener(myScroll);
        a.recycle();
    }

    public void addGoTopView() {
        initAnimMenu();
        if (getTopAnimMenuItem() != null) {
            //已经有了
            return;
        }
        animMenu.addView(animMenu
                .getDefaultItem()
                .tag(TAG_ANIMMENU_GO_TOP)
                .strokeWidth(goTopIconStrokeWidth)
                .strokeColor(goTopIconStrokeColor)
                .iconId(goTopIcon));

    }

    public AnimMenuItem getTopAnimMenuItem() {

        for (int i = 0; i < animMenu.getChildCount(); i++) {
            try {
                AnimMenuItem item = (AnimMenuItem) animMenu.getChildAt(i);

                if (item != null && TAG_ANIMMENU_GO_TOP.equals(item.getItemTag())) {
                    return item;
                }
            } catch (Exception e) {

            }
        }
        return null;
    }

    /**
     * 清空返回顶部的view
     */
    public void cleanGoTopView() {
        if (animMenu != null) {
            AnimMenuItem item = getTopAnimMenuItem();
            if (item != null) {
                //已经有了
                animMenu.removeView(item);
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
        params.rightMargin = dip2px(3);
        addView(animMenu, params);
        animMenu.setVisibility(GONE);
    }

    public int dip2px(int dip) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
    }


    public void setColorSchemeResources(RefreshLayout refreshLayout) {
        TypedArray array = getContext().getTheme().obtainStyledAttributes(new int[]{R.attr.SwipeRefreshLayout_Color1, R.attr.SwipeRefreshLayout_Color2, R.attr.SwipeRefreshLayout_Color3, R.attr.SwipeRefreshLayout_Color4});
        refreshLayout.setColorSchemeColors(array.getColor(0, 0xffff0000), array.getColor(1, 0xffff0000), array.getColor(2, 0xffff0000), array.getColor(3, 0xffff0000));
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
            refreshLayout.setOnRefreshCallback(listener);
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

    /**
     * 获取下拉刷新
     *
     * @return
     */
    public RefreshLayout getRefreshLayout() {
        return refreshLayout;
    }

    /**
     * 替换下拉刷新
     */
    public void setRefreshLayout(RefreshLayout refreshLayout) {
        if (refreshLayout == null) {
            return;
        }
        if (this.refreshLayout != refreshLayout) {
            if (this.refreshLayout != null) {
                this.refreshLayout.setEnabled(false);
            }
            this.refreshLayout = refreshLayout;
        }
        recyclerView.setRefreshLayout(refreshLayout);
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

    public void setGoTopIcon(@DrawableRes int icon) {
        goTopIcon = icon;
        cleanGoTopView();
        if (isGoTop) {
            addGoTopView();
        }
    }

    public void setGoTopIconStrokeColor(int goTopIconStrokeColor) {
        this.goTopIconStrokeColor = goTopIconStrokeColor;
        cleanGoTopView();
        if (isGoTop) {
            addGoTopView();
        }
    }

    public void setGoTopIconStrokeWidth(int goTopIconStrokeWidth) {
        this.goTopIconStrokeWidth = goTopIconStrokeWidth;
        cleanGoTopView();
        if (isGoTop) {
            addGoTopView();
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

    /**
     * 没有数据的时候是否显示LoadView
     */
    public void setNoDataIsShow(boolean noDataIsShow) {
        recyclerView.setNoDataIsShow(noDataIsShow);
    }

    /**
     * 底部加载布局
     *
     * @param loadFootlayoutId
     */
    public void setLoadFootlayoutId(int loadFootlayoutId) {
        recyclerView.setLoadFootlayoutId(loadFootlayoutId);
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

    public boolean isGoTop() {
        return isGoTop;
    }

    public int getGoTopIcon() {
        return goTopIcon;
    }

    public int getGoTopIconStrokeColor() {
        return goTopIconStrokeColor;
    }

    public int getGoTopIconStrokeWidth() {
        return goTopIconStrokeWidth;
    }

    public OnGoTopClickListener getGoTopClickListener() {
        return goTopClickListener;
    }

    public AnimMenu getAnimMenu() {
        return animMenu;
    }
}
