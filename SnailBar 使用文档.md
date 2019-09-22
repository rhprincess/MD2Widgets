# SnailBar 使用文档  

------

`作者：Krins（CoxylicAcid）`

[Github地址](https://github.com/coxylicacid/MD2Widgets)

- [函数方法](#函数方法)  
  [获取实例](#intance)  
  [make](#make)  
  [gravity](#gravity)  
  [useExpandMode](#useExpandMode)  
  [listenSnail](#listenSnail)  
  [wrapMode](#wrapMode)  
  [anime](#anime)  
  [attachTo](#attachTo)  
  [attachToFab](#attachToFab)  
  [contentView](#contentView)  
  [icon](#icon)  
  [iconTint](#iconTint)  
  [background](#background)  
  [msg](#msg)  
  [expandMax](#expandMax)  
  [msgSelectable](#msgSelectable)  
  [action](#action)  
  [duration](#duration)  
  [show](#show)  
  [dismiss](#dismiss)  
  [actionTextColor](#actionTextColor)  
  [msgColor](#msgColor)  

- [监听器以及接口](#监听以及接口)
  [SnailBarListener](#SnailBarListener)  
  [SnailBarListenerAdapter](#SnailBarListenerAdapter)  
  [SnailBarActionListener](#SnailBarActionListener)  
  [SnailBarActionListenerAdapter](#SnailBarActionListenerAdapter)  
- [其它](#其它)

## 函数方法

------

### <span id="intance">获取实例的方法  getIntance(AppCompatActivity activity)  </span>
```java
SnailBar.getIntance(activity);
```

### <span id="make">1. make(AppCompatActivity activity, String msg, int length)   </span>
> 该方法用于创建并返回SnailBar实例
> 第一个参数为SnailBar所在的Activity活动，它必须是AppCompatActivity
> 第二个参数是传入的消息字符串
> 然后第三个参数就是消息显示的时长了，他在SnailBar里的定义是这样的：
>
> ```java
> public final static int LENGTH_LONG = -0; // 消息的时长较长
> public final static int LENGTH_SHORT = -1; // 消息的时长较短
> public final static int LENGTH_NEVER = -2; // 消息点开后不再消失
> ```
> 当然你还可以自定义时长，单位为毫秒，但时长必须大于0，如：5000（5秒），3500（3.5秒）以此类推
>
> 拓展用法：
> `make(AppCompatActivity activity, int msgId, int length)`
> 基本上和原方法一致，不过该方法的第二个消息值可以是资源文件 <font color="#F44336">strings.xml</font> 中所设置的字符串值
> 例如: <font color="#2196F3">R.srings.hello_world</font>

### <span id="gravity">2. gravity(Gravity gravity)  </span>
>该方法用于设置SnailBar的重心位置
>```java
>/**
> * Gravity 重心方向
> * TOP: 上面, TOP_LEFT: 左上, TOP_RIGHT: 右上, BOTTOM: 下面,
> * BOTTOM_LEFT: 左下, BOTTOM_RIGHT: 右下, CENTER: 中间,
> * CENTER_LEFT: 左中, CENTER_RIGHT: 右中
> */
> public enum Gravity {
> 	TOP, TOP_LEFT, TOP_RIGHT, BOTTOM, BOTTOM_LEFT,
> 	BOTTOM_RIGHT, CENTER, CENTER_LEFT, CENTER_RIGHT
> }
> private static Gravity defaultGravity = Gravity.BOTTOM; //默认的重心方向为底部且居中
> ```

### <span id="useExpandMode">3. useExpandMode()  </span>
>使用消息扩展模式
>具体来讲就是，当你的消息内容超过某一最大限度值时，自动折叠掉，并且允许用户自行展开
>设置折叠后可显示的字数可以使用 [expandMax](#expandMax) 方法
>
>![1569071282728](https://github.com/coxylicacid/MD2Widgets/blob/master/snail_img/1569071282728.png?raw=true)
>
><div align="center";><font size="2">消息折叠</font></div>
>
>![1569071759531](https://github.com/coxylicacid/MD2Widgets/blob/master/snail_img/1569071759531.png?raw=true)
>
><div align="center";><font size="2">消息展开</font></div>

### <span id="listenSnail">4. listenSnail(SnailBarListener lis)  </span>
>监听SnailBar的各种事件，参数为SnailBar的监听器
>目前有两种SnailBar的监听器，一种是需要实现监听器接口所有方法的 [SnailBarListener](#SnailBarListener)，另一种则是不需要全部实现的适配器 [SnailBarListenerAdapter](#SnailBarListenerAdapter)
>
>示例：
>
>```java
>//需要实现所有方法
>SnailBar.make(MainActivity.this, "Hello World", SnailBar.LENGTH_LONG)
>                .listenSnail(new SnailBar.SnailBarListener() {
>                    @Override
>                    public void onShown(SnailBar snailBar) {/*TODO Add some event here*/}
>                    @Override
>                    public void onDismissed(SnailBar snailBar) {/*TODO Add some event here*/}
>                    @Override
>                    public void onPaused(SnailBar snailBar) {/*TODO Add some event here*/}
>                    @Override
>                    public void onResumed(SnailBar snailBar) {/*TODO Add some event here*/}
>                }).show();
>
>//可以实现部分方法
>SnailBar.make(MainActivity.this, "Hello World", SnailBar.LENGTH_LONG)
>                .listenSnail(new SnailBar.SnailBarListenerAdapter() {
>                    @Override
>                    public void onShown(SnailBar snailBar) {
>                        super.onShown(snailBar);
>                        //TODO Add some event here
>                    }
>                    //省略其它方法，按自己需求继承需要实现的方法
>                }).show();
>```
>
>

### <span id="wrapMode">5.wrapMode(boolean wrap)  </span>

> 强制自适应或不自适应SnailBar宽度
> 参数值：是否自适应
>
> ![1569071845219](https://github.com/coxylicacid/MD2Widgets/blob/master/snail_img/1569071845219.png?raw=true)
>
> <div align="center";><font size="2">自适应</font></div>
>
> ![1569071899094](https://github.com/coxylicacid/MD2Widgets/blob/master/snail_img/1569071899094.png?raw=true)
>
> <div align="center";><font size="2">不自适应</font></div>

### <span id="anime">6. anime(Anime anime)  </span>

> 设置默认的SnailBar过渡动画（显示和关闭的动画）
> 拓展用法：
> `anime(Anime anime, int duration)`
> 与原方法一致，不过多出了一个duration参数，该参数可以设置动画显示的时长，单位为毫秒，如需设置0.5s的话，参数填500就可以了
>
> ```java
> /**
>      * Anime 过度动画
>      * <p>
>      * CIRCULAR_REVEAL: 揭露动画, SCALE: 缩放动画, ALPHA: 透明度动画, SLIDE: 从下边滑入, SLIDE_FROM_SIDE: 从旁边滑入
>      */
>     public enum Anime {CIRCULAR_REVEAL, SCALE, ALPHA, SLIDE, SLIDE_FROM_SIDE}
>     private static Anime defaultAnime = Anime.SLIDE; // 默认的过渡动画为从下边滑入
> ```

### <span id="attachTo">7. attachTo(View v)  </span>

> 将SnailBar绑定到一个View上面，显示的时候将会悬浮在View上方
> 目前较不稳定，可能会出现一些错误，但在大部分情况下是能正常使用的
> 参数v为需要绑定的控件

### <span id="attachToFab">8. attachToFab(FloatingActionButton fab)  </span>

> 针对性地将SnailBar绑定到一个FloatingActionButton（浮动按钮）上
> 主要是用于SnailBar将浮动按钮遮挡住的情况，用了该方法后，浮动按钮会自动躲避SnailBar
>
> ![1569071955196](https://github.com/coxylicacid/MD2Widgets/blob/master/snail_img/1569071955196.png?raw=true)
>
> <div align="center";><font size="2">自动闪避</font></div>

### <span id="contentView">9. contentView(int layout)  </span>

> 设置SnailBar的布局
> 参数layout：布局资源的id值，如：<font color="#2196F3">R.layout.snailbar_layout</font>
>
> 拓展用法：
> `contentView(ViewGroup layout)`
> 参数layout：指定的ViewGroup

### <span id="icon">10. icon(int resId)  </span>

> 设置SnailBar消息上显示的图标
> 参数resId：drawable资源id值，如：<font color="#2196F3">R.drawable.snail_icon</font>
>
> 拓展用法：
> `icon(Bitmap bitmap)`
> 参数bitmap：设置图标的bitmap
>
> ![1569072054552](https://github.com/coxylicacid/MD2Widgets/blob/master/snail_img/1569072054552.png?raw=true)
>
> <div align="center";><font size="2">设置图标</font></div>

### <span id="iconTint">11. iconTint(int color)  </span>

> 给SnailBar上的图标染色（着色）
> 参数color：颜色值
>
> ![1569072147728](https://github.com/coxylicacid/MD2Widgets/blob/master/snail_img/1569072147728.png?raw=true)
>
> <div align="center";><font size="2">图标着色</font></div>

### <span id="background">12. background(Drawable drawable)  </span>

> 设置SnailBar的背景
> 参数drawable：Drawable背景
>
> 拓展用法：
> `1. backgroundColor(int color)`
> 设置背景颜色，输入参数为颜色值
>
> `2. backgroundColor(String color)`
> 设置背景颜色，输入参数为颜色字符串，例如：<font color="#4CAF50">#4CAF50</font>
>
> `3. backgroundResources(int resId)`
> 设置背景，输入参数为drawable资源id值，例如：<font color="#2196F3">R.drawable.snail_bg</font>
>
> ![1569072330606](https://github.com/coxylicacid/MD2Widgets/blob/master/snail_img/1569072330606.png?raw=true)
>
> <div align="center";><font size="2">设置背景颜色</font></div>

### <span id="msg">13. msg(String s)  </span>

> 设置消息字符串，一般不怎么用到，但可用于随时更改弹出的SnailBar的消息
> 例：
>
> ```java
> snail = SnailBar.make(MainActivity.this, "Hello World!", SnailBar.LENGTH_LONG).show();
> snail.msg("World Hello!");
> snail.msg("H E L L O - W O R L D !");
> ```
>
> 在SnailBar未消失之前，都可以看到消息的变化
>
> 拓展用法：
> `msg(int resId)`
> 你可以使用此方法用字符串资源id设置消息
> 例如：
>
> ```java
> SnailBar.make(MainActivity.this, R.string.sail_msg, SnailBar.LENGTH_LONG).show();
> ```

### <span id="expandMax">14. expandMax(int max)  </span>

>设置SnailBar消息的最大折叠字数

### <span id="msgSelectable">15. msgSelectable(boolean selectable)  </span>

> 设置消息的可复制性
> 如果参数selectable为true的话，可以让消息变得可以复制
>
> ![1569072410457](https://github.com/coxylicacid/MD2Widgets/blob/master/snail_img/1569072410457.png?raw=true)
>
> <div align="center";><font size="2">复制消息</font></div>

### <span id="action">16. action(String text, SnailBarActionListener listener)  </span>

> 设置SnailBar上的按钮并且设置点击事件
>
> 目前有两种SnailBar按钮的监听器，一种是需要实现监听器接口所有方法的 [SnailBarActionListener](#SnailBarActionListener)，另一种则是不需要全部实现的适配器 [SnailBarActionListenerAdapter](#SnailBarActionListenerAdapter)
>
> 示例用法：
>
> ```java
> //使用SnailBarActionListener需要实现其所有方法
> SnailBar.make(MainActivity.this, "Hello World", SnailBar.LENGTH_LONG)
>                 .action("Confirm", new SnailBar.SnailBarActionListener() {
>                     @Override
>                     public void onClick(View view, SnailBar snailBar) {
>                         //点击事件
>                     }
> 
>                     @Override
>                     public boolean onLongClick(View view, SnailBar snailBar) {
>                         //长按事件
>                         return false;
>                     }
>                 }).show();
> 
> //使用SnailBarActionListenerAdapter可以选择性地实现方法，例如我这里实现了单击事件
> SnailBar.make(MainActivity.this, "Hello World", SnailBar.LENGTH_LONG)
>                 .action("Confirm", new SnailBar.SnailBarActionListenerAdapter() {
>                     @Override
>                     public void onClick(View view, SnailBar snailBar) {
>                         super.onClick(view, snailBar);
>                         //点击事件
>                     }
>                 }).show();
> ```
>

> ![1569072589245](https://github.com/coxylicacid/MD2Widgets/blob/master/snail_img/1569072589245.png?raw=true)
>
> <div align="center";><font size="2">设置按钮</font></div>

### <span id="duration">17. duration(int length)  </span>

> 设置SnailBar的时长，用得较少，可在SnailBar显示一段时间后更改其显示事件
>
> 参数length见 [make](#make) 方法中的介绍

### <span id="show">18. show()</span>

> 显示SnailBar

### <span id="dismiss">19. dismiss()</span>

> 关闭SnailBar

### <span id="actionTextColor">20. actionTextColor(int color)</span>

> 设置SnailBar按钮的颜色
> 参数color为颜色值
>
> ![1569072689674](https://github.com/coxylicacid/MD2Widgets/blob/master/snail_img/1569072689674.png?raw=true)
>
> <div align="center";><font size="2">设置按钮颜色为红色</font></div>

### <span id="msgColor">21. msgColor(int color)</span>

> 设置SnailBar消息的颜色
> 参数color为颜色值
>
> ![1569072740412](https://github.com/coxylicacid/MD2Widgets/blob/master/snail_img/1569072740412.png?raw=true)
>
> <div align="center";><font size="2">设置消息颜色为红色</font></div>



## 监听以及接口

------

### SnailBarListener  

>SnailBar监听接口
>```java
>//SnailBar监听器
>public interface SnailBarListener {
>	//SnailBar已显示事件
>	void onShown(SnailBar snailBar);
>	//SnailBar已取消事件
>	void onDismissed(SnailBar snailBar);
>	//SnailBar暂时停止事件（当触碰SnailBar的时候激活）
>	void onPaused(SnailBar snailBar);
>	//Snailbar恢复事件（当触碰取消的时候激活）
>	void onResumed(SnailBar snailBar);
>}
>```

### SnailBarListenerAdapter  

> SnailBar监听接口的适配器，可以选择性地实现某一方法
>
> ```java
> public static class SnailBarListenerAdapter implements SnailBarListener {
>         @Override
>         public void onShown(SnailBar snailBar) {}
> 
>         @Override
>         public void onDismissed(SnailBar snailBar) {}
> 
>         @Override
>         public void onPaused(SnailBar snailBar) {}
> 
>         @Override
>         public void onResumed(SnailBar snailBar) {}
> }
> ```

### SnailBarActionListener  

>SnailBar按钮的监听接口
>
>```java
>/**
> * SnailBar 按钮监听器
> */
>public interface SnailBarActionListener {
>    // 按钮点击事件
>    void onClick(View view, SnailBar snailBar);
>    // 按钮长按事件
>    boolean onLongClick(View view, SnailBar snailBar);
>}
>```

### SnailBarActionListenerAdapter  

>SnailBar按钮的监听接口适配器，可选择性实现方法
>
>```java
>public static class SnailBarActionListenerAdapter implements SnailBarActionListener {
>        @Override
>        public void onClick(View view, SnailBar snailBar) {}
>
>        @Override
>        public boolean onLongClick(View view, SnailBar snailBar) {
>            return false;
>        }
>    }
>```

### SnailLimitedHeightView

> 该控件继承自LinearLayout，作用是限制SnailBar消息扩展栏的高度，无实际其他用途
>
> ヾ(•ω•`)o



## 其它

------

### 布局表中设置Snailbar背景颜色

> 在您App的<font color="#F44336">colors.xml</font>当中加入下面的代码就可以了
>
> ```xml
> <?xml version="1.0" encoding="utf-8"?>
> <resources>
>     ...
>     <color name="md2widgets_toast_snailbar_color">#323232</color>
>     ...
> </resources>
> ```
