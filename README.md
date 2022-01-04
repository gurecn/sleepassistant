# sleepassistant


睡眠助手


一个记录个人每天睡眠时间的工具。当前年轻人熬夜现象越来越普遍，熬夜对身体的危害越来越严重。但是普遍情况下，大家并没有意识到自己睡眠情况。该工具借助于Android技术，实现对用户睡眠情况采集、统计、展示，让用户意识到自己的睡眠规律。并且通过应用锁等强制逻辑，限制用户在相应时间内使用相关应用。以此引导用户有意识的改善自己的睡眠安排及生活质量。


#### 主要功能：


1. 日历组件使用，实现年、月循环滑动展示，可长按选择，设置不同日期显示效果；
2. 各类图标统计显示，支持折线图、曲线图、饼形图等图标显示；
3. 支持本地数据库存储，使用SqLite数据库实现本地数据持久化存储；
4. 支持本地数据导入、导出功能；
5. 支持图标长按显示图标；
6. 支持调用手机邮箱进行意见反馈功能；
7. 显示全部安装的应用；
8. 支持设置应用锁，在指定时间段限制使用应用（待完善）。
9. 支持今日诗词效果显示，支持自定义字体。
9. 支持文字转拼音显示，支持声调显示。
9. 支持View保存成图片，支持长图（超一屏）、大图保存。
9. 支持桌面小插件显示。
9. 支持下拉快捷方式定义。
9. 支持多任务桌面显示（不支持vivo）。
9. 支持对诗词进行系统放大镜显示。



#### 使用的组件：


项目中使用了很多第三方组件，主要组件如下：


1.  eventbus：实现事件发布与监听； 
2.  leakcanary：实现内存泄漏监测； 
3.  xclcharts：实现图标显示； 
4.  GridView：实现应用锁列表。 
5.  CalendarCard：实现自定义日历显示。 



#### 运行环境：


gradle:3.5.3


compileSdkVersion 26


minSdkVersion 26
targetSdkVersion 31


#### 应用结构如下：


[ProjectStructure](./ProjectStructure.md)


#### 项目依赖关系：


通过命令查看项目依赖树关系：gradlew :app:dependencies


```java
+--- com.android.support:appcompat-v7:26.0.0+ -> 26.0.0
|    +--- com.android.support:support-annotations:26.0.0
|    +--- com.android.support:support-v4:26.0.0
|    |    +--- com.android.support:support-compat:26.0.0
|    |    |    \--- com.android.support:support-annotations:26.0.0
|    |    +--- com.android.support:support-media-compat:26.0.0
|    |    |    +--- com.android.support:support-annotations:26.0.0
|    |    |    \--- com.android.support:support-compat:26.0.0 (*)
|    |    +--- com.android.support:support-core-utils:26.0.0
|    |    |    +--- com.android.support:support-annotations:26.0.0
|    |    |    \--- com.android.support:support-compat:26.0.0 (*)
|    |    +--- com.android.support:support-core-ui:26.0.0
|    |    |    +--- com.android.support:support-annotations:26.0.0
|    |    |    \--- com.android.support:support-compat:26.0.0 (*)
|    |    \--- com.android.support:support-fragment:26.0.0
|    |         +--- com.android.support:support-compat:26.0.0 (*)
|    |         +--- com.android.support:support-core-ui:26.0.0 (*)
|    |         \--- com.android.support:support-core-utils:26.0.0 (*)
|    +--- com.android.support:support-vector-drawable:26.0.0
|    |    +--- com.android.support:support-annotations:26.0.0
|    |    \--- com.android.support:support-compat:26.0.0 (*)
|    \--- com.android.support:animated-vector-drawable:26.0.0
|         +--- com.android.support:support-vector-drawable:26.0.0 (*)
|         \--- com.android.support:support-core-ui:26.0.0 (*)
+--- com.android.support:design:26.0.0+ -> 26.0.0
|    +--- com.android.support:multidex:1.0.1
|    +--- com.android.support:support-v4:26.0.0 (*)
|    +--- com.android.support:appcompat-v7:26.0.0 (*)
|    +--- com.android.support:recyclerview-v7:26.0.0
|    |    +--- com.android.support:support-annotations:26.0.0
|    |    +--- com.android.support:support-compat:26.0.0 (*)
|    |    \--- com.android.support:support-core-ui:26.0.0 (*)
|    \--- com.android.support:transition:26.0.0
|         +--- com.android.support:support-annotations:26.0.0
|         \--- com.android.support:support-v4:26.0.0 (*)
\--- com.squareup.leakcanary:leakcanary-android-no-op:1.5
```


#### 演示：


![](demo.gif#crop=0&crop=0&crop=1&crop=1&id=iPqa0&originalType=binary&ratio=1&rotation=0&showTitle=false&status=done&style=none&title=)
