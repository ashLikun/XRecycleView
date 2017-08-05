# **XRecycleView**
1:listview,gridview,recycleview  封装自动加载，下拉刷新
2:recycleview分割线,头部与底部
3:分页助手

### 1.用法
使用前，对于Android Studio的用户，可以选择添加:
     compile 'com.github.ashLikun:XrecycleView:1.0.2'//XRecycleView
        listSwipeView = (SuperRecyclerView) findViewById(R.id.switchRoot);
        adapter = getAdapter();
        listSwipeView.getRecyclerView().addItemDecoration(getItemDecoration());
        listSwipeView.getRecyclerView().setLayoutManager(getLayoutManager());
        listSwipeView.setAdapter(adapter);
        listSwipeView.setOnRefreshListener(this);
        listSwipeView.setOnLoaddingListener(this);
        adapter.setOnItemClickListener(this);
### 混肴
####
    保证CommonAdapter的footerSize和headerSize字段不被混肴
    #某一变量不混淆
    -keepclasseswithmembers class com.xxx.xxx {
        private com.ashlikun.adapter.recyclerview.CommonAdapter footerSize;
        private com.ashlikun.adapter.recyclerview.CommonAdapter headerSize;
    }

