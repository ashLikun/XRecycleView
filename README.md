
[![Release](https://jitpack.io/v/ashLikun/XRecycleView.svg)](https://jitpack.io/#ashLikun/XRecycleView)

# **XRecycleView**
1:listview,gridview,recycleview  封装自动加载，下拉刷新
2:recycleview分割线,头部与底部
3:分页助手
## 使用方法

build.gradle文件中添加:
```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```
并且:

```gradle
dependencies {
    implementation 'com.github.ashLikun:XrecycleView:{latest version}'//XRecycleView
}
```
### 1.用法

```java
         /**
             * 下拉刷新是否用自定义的，
             * true:使用自定义的base_swipe_custom_recycle.xml
             * false:使用自定义的base_swipe_recycle.xml    google自己的
             */
        public static boolean REFRESH_IS_CUSTOM = false;

        listSwipeView = (SuperRecyclerView) findViewById(R.id.switchRoot);
        adapter = getAdapter();
        listSwipeView.getRecyclerView().addItemDecoration(getItemDecoration());
        listSwipeView.getRecyclerView().setLayoutManager(getLayoutManager());
        listSwipeView.setAdapter(adapter);
        listSwipeView.setOnRefreshListener(this);
        listSwipeView.setOnLoaddingListener(this);
        adapter.setOnItemClickListener(this);
```

### 混肴
    保证CommonAdapter的footerSize和headerSize字段不被混肴
    #某一变量不混淆
    -keepclasseswithmembers class com.xxx.xxx {
        private com.ashlikun.adapter.recyclerview.BaseAdapter footerSize;
        private com.ashlikun.adapter.recyclerview.BaseAdapter  headerSize;
    }

