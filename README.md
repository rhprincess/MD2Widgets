### MD2Widgets

[ ![下载](https://api.bintray.com/packages/coxylicacid-official/MD2Widgets/mdwidgets/images/download.svg?version=0.0.1-alpha10) ](https://bintray.com/coxylicacid-official/MD2Widgets/mdwidgets/0.0.1-alpha10/link)

##### [SnailBar使用方法](https://github.com/coxylicacid/MD2Widgets/blob/master/SnailBar%20%E4%BD%BF%E7%94%A8%E6%96%87%E6%A1%A3.md)

##### 使用方法

```gradle
api "com.google.android.material:material:1.1.0-alpha10" //这个是必须引入的，官方的库，且需要使用api引入
implementation 'com.github.coxylicacid:mdwidgets:0.0.1-beta01' //该扩展库
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
    <!--    SnailBar背景颜色-->
    <color name="coxylicSnailBarColor">#FF102027</color>
    <!--    SnailBar消息颜色-->
    <color name="coxylicSnailBarMsgColor">#FFF</color>
    <!--    SnailBar按钮颜色-->
    <color name="coxylicSnailBarActionTextColor">#FFF</color>

    <!--    对话框强调色-->
    <color name="coxylicDialogAccent">#37474f</color>
    <!--    对话框深强调色-->
    <color name="coxylicDialogAccentDeep">#102027</color>
    <!--    对话框按钮边框颜色-->
    <color name="coxylicDialogButtonStroke">#7537474f</color>
    <!--    对话框背景颜色-->
    <color name="coxylicDialogBackground">#FFF</color>
    ...
</resources>
```

###### 3.其它属性配置?
`在您App的dimens.xml中加入如下代码：`

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>

    <!--    SnailBar圆角-->
    <dimen name="coxylicSnailBarRadius">6dp</dimen>
    <!--    SnailBar最低高度-->
    <dimen name="coxylicSnailBarMinHeight">40dp</dimen>
    <!--    对话框边距-->
    <dimen name="coxylicDialogMargin">10dp</dimen>
    <!--    对话框圆角-->
    <dimen name="coxylicDialogRadius">5dp</dimen>

</resources>
```

##### 引入问题

###### 如果您在引用的过程中Gradle Sync出现问题，您可以先把 implementation 先改为 compile,然后再改回 implementation 就好了
