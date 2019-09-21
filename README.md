### MD2Widgets

[ ![下载](https://api.bintray.com/packages/coxylicacid-official/MD2Widgets/mdwidgets/images/download.svg?version=0.0.1-alpha06) ](https://bintray.com/coxylicacid-official/MD2Widgets/mdwidgets/0.0.1-alpha06/link)

[SnailBar使用方法](SnailBar 使用文档.html)

##### 使用方法

```gradle
api "com.google.android.material:material:1.1.0-alpha10" //这个是必须引入的，官方的库，且需要使用api引入
implementation 'com.github.coxylicacid:mdwidgets:0.0.1-alpha06' //该扩展库
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

##### 常见问题

###### 1.为什么我使用了MD2Dialog的initWithAppTheme方法无效?
`如果你有设置buttonStyle的话，请把这个方法放在其后，这样就能解决了`

###### 2.有其它方法更改对话框的颜色属性嘛?
`有的。在您App的colors.xml中加入如下代码：`

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    ...
    
    <!-- SnailBar的背景颜色. -->
    <color name="md2widgets_toast_snailbar_color">#323232</color>
    <!-- 对话框的强调色. -->
    <color name="md2widgets_dialog_accent">#1871EF</color>
    <!-- 对话框的深强调色. -->
    <color name="md2widgets_dialog_accent_deep">#0C65EB</color>
    <!-- 对话框按钮的边框颜色（只有当您设置了buttonStyle为OUTLINE的时候才会生效嗷）. -->
    <color name="md2widgets_dialog_button_stroke">#751871EF</color>
    
    ...
</resources>
```

##### 引入问题

###### 如果您在引用的过程中Gradle Sync出现问题，您可以先把 implementation 先改为 compile,然后再改回 implementation 就好了
