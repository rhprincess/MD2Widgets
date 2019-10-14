package com.github.coxylicacid.mdwidgets.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.TooltipCompat;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Krins (CoxylicAcid)
 * @version 0.0.1-beta04
 */
public class SnailActionBar {

    private AppCompatActivity mActivity;
    private RelativeLayout container;
    private ImageView actionMore;
    private ImageView actionClose;
    private TextView title;
    private LinearLayout menuItemContainer;
    private RelativeLayout menuContainer;
    private FrameLayout decorView;
    private FrameLayout.LayoutParams layoutParams;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private PopupMenu menuMore;
    private boolean isShown = false;
    private int maxItemOnSurface = 0;
    private int iconTintColor = 0;
    private int backgroundTintColor = 0;

    private List<SnailActionItem> actionItems = new ArrayList<>();
    private int actionItemsSize = 0;

    public SnailActionBar(AppCompatActivity activity) {
        this.mActivity = activity;
        initViews();
    }

    public SnailActionBar(AppCompatActivity activity, String title) {
        this.mActivity = activity;
        initViews();
        setTitle(title);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initViews() {
        container = (RelativeLayout) LayoutInflater.from(mActivity).inflate(com.github.coxylicacid.mdwidgets.R.layout.snail_action_bar, null);
        actionClose = container.findViewById(com.github.coxylicacid.mdwidgets.R.id.snail_action_close);
        actionMore = container.findViewById(com.github.coxylicacid.mdwidgets.R.id.snail_action_more);
        title = container.findViewById(com.github.coxylicacid.mdwidgets.R.id.snail_action_title);
        menuItemContainer = container.findViewById(com.github.coxylicacid.mdwidgets.R.id.action_icon_container);
        menuContainer = container.findViewById(com.github.coxylicacid.mdwidgets.R.id.snail_action_menu_container);
        menuMore = new PopupMenu(mActivity, actionMore);

        decorView = (FrameLayout) mActivity.getWindow().getDecorView();
        layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.bottomMargin = getSoftButtonsBarHeight(mActivity);
        decorView.addView(container, layoutParams);
        container.setVisibility(View.GONE);

        TooltipCompat.setTooltipText(actionClose, mActivity.getString(com.github.coxylicacid.mdwidgets.R.string.close));
        actionClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });

        TooltipCompat.setTooltipText(actionMore, mActivity.getString(com.github.coxylicacid.mdwidgets.R.string.more));
        actionMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuMore.show();
            }
        });
        actionMore.setOnTouchListener(menuMore.getDragToOpenListener());

        try {
            Field field = menuMore.getClass().getDeclaredField("mPopup");
            field.setAccessible(true);
            MenuPopupHelper helper = (MenuPopupHelper) field.get(menuMore);
            helper.setForceShowIcon(true);
        } catch (NullPointerException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void animateIn() {
        container.setVisibility(View.VISIBLE);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(container, "alpha", 0, 0.55f, 1);
        alpha.setDuration(150);
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isShown = true;
            }
        });
        if (!isShown)
            alpha.start();
    }

    private void animateOut() {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(container, "alpha", 1, 0.55f, 0);
        alpha.setDuration(150);
        alpha.start();
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isShown = false;
                container.setVisibility(View.GONE);
            }
        });
    }

    private void animateUpdateItems() {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(menuContainer, "alpha", 0, 0.55f, 1);
        alpha.setDuration(250);
        alpha.start();
    }

    private int dp(float dpValue) {
        final float scale = mActivity.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private Object addItemPrivatly(String title, Drawable icon) {
        if (menuItemContainer.getChildCount() + 1 <= maxItemOnSurface) {
            LinearLayout parent = (LinearLayout) LayoutInflater.from(mActivity).inflate(com.github.coxylicacid.mdwidgets.R.layout.action_icon, null);
            ImageView iconView = (ImageView) parent.getChildAt(0);
            iconView.setContentDescription(title == null ? "" : title);
            final int id = actionItemsSize;
            icon.setTint(iconTintColor == 0 ? 0xFFFFFFFF : iconTintColor);
            iconView.setImageDrawable(icon);
            iconView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null)
                        onItemClickListener.onClick(SnailActionBar.this, id);
                }
            });
            if (onItemLongClickListener != null) {
                iconView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        return onItemLongClickListener.onLongClick(SnailActionBar.this, id);
                    }
                });
            } else {
                if (!TextUtils.isEmpty(iconView.getContentDescription()))
                    TooltipCompat.setTooltipText(iconView, iconView.getContentDescription());
            }
            menuContainer.getLayoutParams().width += dp(38);
            menuItemContainer.addView(parent);
            actionItems.add(new SnailActionItem(parent));
            actionItemsSize++;
            return iconView;
        } else {
            if (actionMore.getVisibility() == View.GONE)
                actionMore.setVisibility(View.VISIBLE);

            final int id = actionItems.size();

            menuMore.getMenu().add(title).setIcon(icon).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onClick(SnailActionBar.this, id);
                    }
                    return false;
                }
            });
            icon.setTint(0xFF959595);
            SnailActionItem actionItem = new SnailActionItem(title, icon);
            actionItem.setId(id);
            actionItems.add(actionItem);
            actionItemsSize++;
            return actionItem;
        }
    }

    /**
     * 添加Item
     *
     * @param title 标题
     * @param icon  图标
     */
    public void addItem(String title, @DrawableRes int icon) {
        addItemPrivatly(title, mActivity.getDrawable(icon));
    }

    /**
     * 添加Item
     *
     * @param title 标题
     * @param icon  图标
     */
    public void addItem(@StringRes int title, @DrawableRes int icon) {
        addItemPrivatly(mActivity.getString(title), mActivity.getDrawable(icon));
    }

    /**
     * 添加Item
     *
     * @param id    Item的Id
     * @param title 标题
     * @param icon  图标
     */
    public void addItem(@IdRes int id, String title, @DrawableRes int icon) {
        Object o = addItemPrivatly(title, mActivity.getDrawable(icon));
        if (o instanceof ImageView)
            ((ImageView) o).setId(id);
        else if (o instanceof SnailActionItem)
            ((SnailActionItem) o).setId(id);
    }

    /**
     * 添加Item
     *
     * @param id    Item的Id
     * @param title 标题
     * @param icon  图标
     */
    public void addItem(@IdRes int id, @StringRes int title, @DrawableRes int icon) {
        addItem(id, mActivity.getString(title), icon);
    }

    /**
     * 添加Item集合
     *
     * @param items 标题数组
     * @param icons 图标数组
     */
    public void addItems(String[] items, @DrawableRes int[] icons) {
        if (items.length == icons.length) {
            for (int n = 0; n <= items.length - 1; n++) {
                addItemPrivatly(items[n], mActivity.getDrawable(icons[n]));
            }
        } else {
            throw new IllegalArgumentException("items数组与icons数组长度不匹配!");
        }
    }

    /**
     * 移除Item
     *
     * @param pos 位置
     */
    public void removeItem(int pos) {
        menuItemContainer.removeAllViews();
        menuMore.getMenu().clear();
        menuContainer.getLayoutParams().width = dp(35);
        actionItems.remove(pos);
        actionItemsSize = 0;
        for (int k = 0; k <= actionItems.size() - 1; k++) { //遍历所有Item
            final int id = k;
            SnailActionItem item = actionItems.get(k);
            if (menuItemContainer.getChildCount() + 1 <= maxItemOnSurface) { //判断是否可直接显示在操作栏上
                if (item.getView() != null) { //如果Item里已经包含了原来的View，覆盖事件
                    LinearLayout ll = item.getView();
                    ll.getChildAt(0).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onItemClickListener != null)
                                onItemClickListener.onClick(SnailActionBar.this, id);
                        }
                    });
                    if (onItemLongClickListener != null) {
                        ll.getChildAt(0).setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                return onItemLongClickListener.onLongClick(SnailActionBar.this, id);
                            }
                        });
                    } else {
                        if (!TextUtils.isEmpty(ll.getChildAt(0).getContentDescription()))
                            TooltipCompat.setTooltipText(ll.getChildAt(0), ll.getChildAt(0).getContentDescription());
                    }
                    menuItemContainer.addView(ll);
                    menuContainer.getLayoutParams().width += dp(38);
                    System.gc();
                } else { //Item里面不包含原来的View，创建并添加事件
                    LinearLayout parent = (LinearLayout) LayoutInflater.from(mActivity).inflate(com.github.coxylicacid.mdwidgets.R.layout.action_icon, null);
                    ImageView iconView = (ImageView) parent.getChildAt(0);
                    iconView.setContentDescription(title == null ? "" : item.getTitle());
                    iconView.setImageDrawable(item.getIcon());
                    iconView.setImageTintList(ColorStateList.valueOf(iconTintColor == 0 ? 0xFFFFFFFF : iconTintColor));
                    iconView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onItemClickListener != null)
                                onItemClickListener.onClick(SnailActionBar.this, id);
                        }
                    });
                    if (onItemLongClickListener != null) {
                        iconView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                return onItemLongClickListener.onLongClick(SnailActionBar.this, id);
                            }
                        });
                    } else {
                        if (!TextUtils.isEmpty(iconView.getContentDescription()))
                            TooltipCompat.setTooltipText(iconView, iconView.getContentDescription());
                    }
                    menuContainer.getLayoutParams().width += dp(38);
                    menuItemContainer.addView(parent);
                    System.gc();
                }
            } else { //显示在PopupMenu上的Item
                if (actionMore.getVisibility() == View.GONE)
                    actionMore.setVisibility(View.VISIBLE);

                menuMore.getMenu().add(item.getTitle()).setIcon(item.getIcon()).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (onItemClickListener != null) {
                            onItemClickListener.onClick(SnailActionBar.this, id);
                        }
                        return false;
                    }
                });
            }
        }
        if (actionItems.size() == maxItemOnSurface)
            actionMore.setVisibility(View.GONE);
        System.gc();
        animateUpdateItems();
    }

    /**
     * 获取Item
     *
     * @param pos 位置
     * @return SnailAction项
     */
    public SnailActionItem getItem(int pos) {
        return actionItems.get(pos);
    }

    /**
     * 获取Item的Id
     *
     * @param pos 位置
     * @return Item的Id
     */
    public int getItemId(int pos) {
        return actionItems.get(pos).getId();
    }

    /**
     * 设置边距
     *
     * @param top    上边距
     * @param left   左边距
     * @param bottom 底边距
     * @param right  右边距
     */
    public void setMargin(int top, int left, int bottom, int right) {
        layoutParams.topMargin += top;
        layoutParams.leftMargin += left;
        layoutParams.bottomMargin += bottom;
        layoutParams.rightMargin += right;
    }

    /**
     * 设置重心位置
     *
     * @param gravity {@link Gravity}
     */
    public void setGravity(int gravity) {
        layoutParams.gravity = gravity;
        if (gravity == Gravity.TOP)
            layoutParams.topMargin = getStatusBarHeight(mActivity);
    }

    /**
     * 设置标题颜色
     *
     * @param color 颜色值
     */
    public void setTitleColor(int color) {
        title.setTextColor(color);
    }

    /**
     * 设置Icon的颜色
     *
     * @param color 颜色值
     */
    public void setIconColor(int color) {
        iconTintColor = color;
        actionClose.setImageTintList(ColorStateList.valueOf(color));
        actionMore.setImageTintList(ColorStateList.valueOf(color));
        for (SnailActionItem i : actionItems) {
            ((ImageView) i.getView().getChildAt(0)).setImageTintList(ColorStateList.valueOf(color));
        }
    }

    /**
     * 设置背景颜色
     *
     * @param color 颜色值
     */
    public void setBackgroundColor(int color) {
        backgroundTintColor = color;
        container.getChildAt(0).setBackgroundTintList(ColorStateList.valueOf(color));
    }

    /**
     * 设置标题
     *
     * @param s 标题
     */
    public void setTitle(String s) {
        title.setText(s);
    }

    /**
     * 设置最多可见的项
     *
     * @param n 可见项
     */
    public void setMaxItemOnSurface(int n) {
        this.maxItemOnSurface = n;
    }

    /**
     * 添加单击事件
     *
     * @param listener 监听器
     */
    public void addClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    /**
     * 添加长按事件
     *
     * @param listener 监听器
     */
    public void addLongClickListener(OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
    }

    /**
     * 返回显示状态
     *
     * @return 显示状态
     */
    public boolean isShowing() {
        return isShown;
    }

    /**
     * 显示
     */
    public void show() {
        if (!isShown)
            animateIn();
    }

    /**
     * 关闭
     */
    public void close() {
        if (isShown)
            animateOut();
    }

    /**
     * 底部虚拟按键栏的高度
     *
     * @return 虚拟按键高度
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static int getSoftButtonsBarHeight(AppCompatActivity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        //这个方法获取可能不是真实屏幕的高度
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        //获取当前屏幕的真实高度
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }

    private int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        if (height != 0) {
            return height;
        } else {
            return dp(20);
        }
    }

    public class SnailActionItem {
        private String title;
        private Drawable icon2;
        private ImageView icon;
        private LinearLayout ll;
        private int id;
        private boolean isPopupMode = false;

        public SnailActionItem(LinearLayout ll) {
            this.icon = (ImageView) ll.getChildAt(0);
            this.title = (String) icon.getContentDescription();
        }

        public SnailActionItem(String title, Drawable icon) {
            isPopupMode = true;
            this.title = title;
            this.icon2 = icon;
        }

        public void setIcon(Drawable icon) {
            if (isPopupMode)
                this.icon2 = icon;
            else
                this.icon.setImageDrawable(icon);
        }

        public Drawable getIcon() {
            if (isPopupMode)
                return icon2;
            else
                return this.icon.getDrawable();
        }

        public void setTitle(String title) {
            if (isPopupMode)
                this.title = title;
            else
                icon.setContentDescription(title);
        }

        public String getTitle() {
            return title;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getId() {
            if (isPopupMode)
                return id;
            else
                return icon.getId();
        }

        public LinearLayout getView() {
            return ll;
        }

        public void setView(LinearLayout layout) {
            this.ll = layout;
        }
    }

    /**
     * Item点击事件
     */
    public interface OnItemClickListener {
        void onClick(SnailActionBar snailActionBar, int pos);
    }

    /**
     * Item长按事件
     */
    public interface OnItemLongClickListener {
        boolean onLongClick(SnailActionBar snailActionBar, int pos);
    }

}
