### MD2Widgets

##### 使用方法

```gradle
api "com.google.android.material:material:1.1.0-alpha10" //这个是必须引入的，官方的库，且需要使用api引入
implementation 'com.github.coxylicacid:mdwidgets:0.0.1-alpha02' //该扩展库
```

##### 修改您App的主题

```xml
<resources>

    <!-- 选择 Theme.MaterialComponents.XXX 作为主题. -->
    <style name="AppTheme" parent="Theme.MaterialComponents.Light">
        <!-- Customize your theme here. -->
        <item name="colorPrimary">@color/colorPrimary</item>
        <item name="colorPrimaryDark">@color/colorPrimaryDark</item>
        <item name="colorAccent">@color/colorAccent</item>
    </style>

</resources>
```

##### 引入问题

###### 如果您在引用的过程中Gradle Sync出现问题，您可以先把 implementation 先改为 compile,然后再改回 implementation 就好了