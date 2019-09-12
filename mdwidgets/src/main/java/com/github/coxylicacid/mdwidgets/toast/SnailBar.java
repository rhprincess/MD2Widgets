package com.github.coxylicacid.mdwidgets.toast;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.coxylicacid.mdwidgets.R;
import com.google.android.material.button.MaterialButton;

/**
 * @author Krins
 * @version 0.0.1-alpha01
 */
@SuppressLint("StaticFieldLeak")
public class SnailBar {

    private static AppCompatActivity context;
    private static FrameLayout decorView;
    private static ViewGroup container;
    private static TextView content;
    private static MaterialButton btn_action;
    private static ViewGroup container_content;
    private static FrameLayout.LayoutParams layoutParams;
    private static SnailBarActionListener actionListener;
    private static SnailBarListener listener;
    private static ImageView _icon;

    public final static int LENGTH_LONG = -0;
    public final static int LENGTH_SHORT = -1;
    public final static int LENGTH_NEVER = -2;

    /**
     * Gravity 位置方向
     * <p>
     * TOP: 上面, TOP_LEFT: 左上, TOP_RIGHT: 右上, BOTTOM: 下面, BOTTOM_LEFT: 左下, BOTTOM_RIGHT: 右下, CENTER: 中间, CENTER_LEFT: 左中, CENTER_RIGHT: 右中
     */
    public enum Gravity {TOP, TOP_LEFT, TOP_RIGHT, BOTTOM, BOTTOM_LEFT, BOTTOM_RIGHT, CENTER, CENTER_LEFT, CENTER_RIGHT}

    private static Gravity defaultGravity = Gravity.BOTTOM;

    /**
     * Anime 过度动画
     * <p>
     * CIRCULAR_REVEAL: 揭露动画, SCALE: 缩放动画, ALPHA: 透明度动画, SLIDE: 从下边滑入, SLIDE_FROM_SIDE: 从旁边滑入
     */
    public enum Anime {CIRCULAR_REVEAL, SCALE, ALPHA, SLIDE, SLIDE_FROM_SIDE}

    private static Anime defaultAnime = Anime.SLIDE;
    private static int animeDuration = 100;

    static final Handler durationHandler = new Handler();
    private int _duration = 0;
    static SnailBar instance;
    static Runnable runnable = new Runnable() {
        @Override
        public void run() {
            closeAnimation(defaultAnime);
            durationHandler.removeCallbacks(this);
        }
    };


    /**
     * 获取SnailBar的实例
     *
     * @param activity 界面
     * @return {@link SnailBar}
     */
    public static SnailBar getIntance(AppCompatActivity activity) {
        if (instance == null) {
            instance = new SnailBar();
            context = activity;
            initViews(activity);
        }
        return instance;
    }

    /**
     * 生成 Snailbar
     *
     * @param activity 界面Activity
     * @param msg      消息
     * @param length   显示时长
     * @return {@link SnailBar}
     */
    public static SnailBar make(AppCompatActivity activity, String msg, int length) {
        SnailBar.getIntance(activity);
        initActions(instance);
        instance.msg(msg);
        instance.duration(length);
        return instance;
    }

    /**
     * 生成 Snailbar
     *
     * @param activity 界面Activity
     * @param msgId    消息资源值
     * @param length   显示时长
     * @return {@link SnailBar}
     */
    public static SnailBar make(AppCompatActivity activity, int msgId, int length) {
        SnailBar.getIntance(activity);
        initActions(instance);
        instance.msg(activity.getString(msgId));
        instance.duration(length);
        return instance;
    }

    @SuppressLint("SetTextI18n")
    private static void initViews(AppCompatActivity activity) {
        decorView = (FrameLayout) activity.getWindow().getDecorView();
        container = (ViewGroup) LayoutInflater.from(activity).inflate(R.layout.snailbar, null);
        layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        decorView.addView(container, layoutParams);
        container.setVisibility(View.GONE);

        _icon = container.findViewById(R.id.snailbar_icon);
        content = container.findViewById(R.id.snailbar_content);
        btn_action = container.findViewById(R.id.snailbar_action);
        container_content = container.findViewById(R.id.container_content);

        layoutParams.gravity = android.view.Gravity.BOTTOM;
        content.setText("内容");
        btn_action.setText("Action");
    }

    private static void initActions(final SnailBar snailBar) {
        btn_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (actionListener != null)
                    actionListener.onClick(view, snailBar);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setUpBehavior() {
        container_content.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        durationHandler.removeCallbacks(runnable);
                        break;
                    case MotionEvent.ACTION_UP:
                        durationHandler.postDelayed(runnable, 1850);
                        break;
                }
                return false;
            }
        });
    }

    private void showAnimation(Anime anime) {
        setUpBehavior();
        instance.duration(_duration);
        durationHandler.removeCallbacks(runnable);
        container.setVisibility(View.VISIBLE);
        AnimatorSet animatorSet = new AnimatorSet();

        switch (anime) {
            case ALPHA:
                ObjectAnimator alpha = ObjectAnimator.ofFloat(container, "alpha", 0, 0.55f, 1);
                animatorSet.play(alpha);
                animatorSet.setDuration(animeDuration);
                animatorSet.start();
                break;
            case SCALE:
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(container, "scaleX", 0.8f, 0.85f, 1);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(container, "scaleY", 0.8f, 0.85f, 1);
                alpha = ObjectAnimator.ofFloat(container, "alpha", 0, 0.55f, 1);
                animatorSet.playTogether(scaleX, scaleY, alpha);
                animatorSet.setDuration(animeDuration);
                animatorSet.start();
                break;
            case SLIDE:
                scaleX = ObjectAnimator.ofFloat(container, "scaleX", 0.8f, 0.85f, 1);
                scaleY = ObjectAnimator.ofFloat(container, "scaleY", 0.8f, 0.85f, 1);
                alpha = ObjectAnimator.ofFloat(container, "alpha", 0, 0.55f, 1);
                ObjectAnimator translationY = ObjectAnimator.ofFloat(container, "translationY", container.getHeight(), 0);
                animatorSet.playTogether(translationY, scaleX, scaleY, alpha);
                animatorSet.setDuration(animeDuration);
                animatorSet.start();
                break;
            case SLIDE_FROM_SIDE:
                ObjectAnimator translationX;
                Animator circularReveal;
                alpha = ObjectAnimator.ofFloat(container, "alpha", 0, 0.55f, 1);
                if (defaultGravity == Gravity.TOP_LEFT || defaultGravity == Gravity.BOTTOM_LEFT || defaultGravity == Gravity.CENTER_LEFT) {
                    translationX = ObjectAnimator.ofFloat(container, "translationX", -container.getWidth(), 0);
                    circularReveal = ViewAnimationUtils.createCircularReveal(container, 0, container.getHeight() / 2, 0, container.getWidth());
                } else if (defaultGravity == Gravity.TOP_RIGHT || defaultGravity == Gravity.BOTTOM_RIGHT || defaultGravity == Gravity.CENTER_RIGHT) {
                    translationX = ObjectAnimator.ofFloat(container, "translationX", container.getWidth(), 0);
                    circularReveal = ViewAnimationUtils.createCircularReveal(container, container.getWidth(), container.getHeight() / 2, 0, container.getWidth());
                } else {
                    translationX = ObjectAnimator.ofFloat(container, "translationX", -container.getWidth(), 0);
                    circularReveal = ViewAnimationUtils.createCircularReveal(container, 0, container.getHeight() / 2, 0, container.getWidth());
                }
                animatorSet.playTogether(circularReveal, translationX, alpha);
                animatorSet.setDuration(animeDuration * 2);
                animatorSet.start();
                break;
            case CIRCULAR_REVEAL:
                circularReveal = ViewAnimationUtils.createCircularReveal(container, container.getWidth() / 2, container.getHeight(), 0, container.getWidth());
                alpha = ObjectAnimator.ofFloat(container, "alpha", 0, 0.55f, 1);
                animatorSet.playTogether(circularReveal, alpha);
                animatorSet.setDuration(animeDuration * 3);
                animatorSet.start();
                break;
        }

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                switch (_duration) {
                    case LENGTH_LONG:
                        closeInSchedule(2750);
                        break;
                    case LENGTH_SHORT:
                        closeInSchedule(1850);
                        break;
                    case LENGTH_NEVER:
                        break;
                    default:
                        if (_duration > 0)
                            closeInSchedule(_duration);
                        break;
                }
                if (listener != null)
                    listener.onShown(instance);
            }
        });
    }

    private static void closeAnimation(Anime anime) {
        AnimatorSet animatorSet = new AnimatorSet();

        switch (anime) {
            case ALPHA:
                ObjectAnimator alpha = ObjectAnimator.ofFloat(container, "alpha", 1, 0.55f, 0);
                animatorSet.play(alpha);
                animatorSet.setDuration(animeDuration);
                animatorSet.start();
                break;
            case SCALE:
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(container, "scaleX", 1, 0.85f, 0.8f);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(container, "scaleY", 1, 0.85f, 0.8f);
                alpha = ObjectAnimator.ofFloat(container, "alpha", 1, 0.55f, 0);
                animatorSet.playTogether(scaleX, scaleY, alpha);
                animatorSet.setDuration(animeDuration);
                animatorSet.start();
                break;
            case SLIDE:
                scaleX = ObjectAnimator.ofFloat(container, "scaleX", 1, 0.85f, 0.8f);
                scaleY = ObjectAnimator.ofFloat(container, "scaleY", 1, 0.85f, 0.8f);
                alpha = ObjectAnimator.ofFloat(container, "alpha", 1, 0.55f, 0);
                ObjectAnimator translationY = ObjectAnimator.ofFloat(container, "translationY", 0, container.getHeight());
                animatorSet.playTogether(translationY, scaleX, scaleY, alpha);
                animatorSet.setDuration(animeDuration);
                animatorSet.start();
                break;
            case SLIDE_FROM_SIDE:
                ObjectAnimator translationX;
                Animator circularReveal;
                alpha = ObjectAnimator.ofFloat(container, "alpha", 1, 0.55f, 0);
                if (defaultGravity == Gravity.TOP_LEFT || defaultGravity == Gravity.BOTTOM_LEFT || defaultGravity == Gravity.CENTER_LEFT) {
                    translationX = ObjectAnimator.ofFloat(container, "translationX", 0, container.getWidth());
                    circularReveal = ViewAnimationUtils.createCircularReveal(container, container.getWidth(), container.getHeight() / 2, container.getWidth(), 0);
                } else if (defaultGravity == Gravity.TOP_RIGHT || defaultGravity == Gravity.BOTTOM_RIGHT || defaultGravity == Gravity.CENTER_RIGHT) {
                    translationX = ObjectAnimator.ofFloat(container, "translationX", 0, container.getWidth());
                    circularReveal = ViewAnimationUtils.createCircularReveal(container, 0, container.getHeight() / 2, container.getWidth(), 0);
                } else {
                    translationX = ObjectAnimator.ofFloat(container, "translationX", 0, container.getWidth() + container.getX());
                    circularReveal = ViewAnimationUtils.createCircularReveal(container, container.getWidth(), container.getHeight() / 2, container.getWidth(), 0);
                }
                animatorSet.playTogether(circularReveal, translationX, alpha);
                animatorSet.setDuration(animeDuration * 2);
                animatorSet.start();
                break;
            case CIRCULAR_REVEAL:
                circularReveal = ViewAnimationUtils.createCircularReveal(container, container.getWidth() / 2, container.getHeight(), container.getWidth(), 0);
                alpha = ObjectAnimator.ofFloat(container, "alpha", 1, 0.55f, 0);
                animatorSet.playTogether(circularReveal, alpha);
                animatorSet.setDuration(animeDuration * 3);
                animatorSet.start();
                break;
        }

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                container.setVisibility(View.GONE);
                if (listener != null)
                    listener.onDismissed(instance);
            }
        });
    }

    private void closeInSchedule(int _duration) {
        durationHandler.postDelayed(runnable, _duration);
    }

    /**
     * 设置 SnailBar 的重力方向
     *
     * @param gravity 界面Activity
     * @return {@link SnailBar}
     */
    public SnailBar gravity(Gravity gravity) {
        switch (gravity) {
            case TOP:
                layoutParams.gravity = android.view.Gravity.TOP | android.view.Gravity.CENTER;
                container.setY(getStatusBarHeight(context));
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                defaultGravity = Gravity.TOP;
                break;
            case TOP_LEFT:
                layoutParams.gravity = android.view.Gravity.START;
                container.setY(getStatusBarHeight(context));
                container_content.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                defaultGravity = Gravity.TOP_LEFT;
                break;
            case TOP_RIGHT:
                layoutParams.gravity = android.view.Gravity.END;
                container.setY(getStatusBarHeight(context));
                container_content.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                defaultGravity = Gravity.TOP_RIGHT;
                break;
            case BOTTOM:
                layoutParams.gravity = android.view.Gravity.BOTTOM | android.view.Gravity.CENTER;
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                defaultGravity = Gravity.BOTTOM;
                break;
            case BOTTOM_LEFT:
                layoutParams.gravity = android.view.Gravity.BOTTOM | android.view.Gravity.START;
                container_content.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                defaultGravity = Gravity.BOTTOM_LEFT;
                break;
            case BOTTOM_RIGHT:
                layoutParams.gravity = android.view.Gravity.BOTTOM | android.view.Gravity.END;
                container_content.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                defaultGravity = Gravity.BOTTOM_RIGHT;
                break;
            case CENTER:
                layoutParams.gravity = android.view.Gravity.CENTER;
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                defaultGravity = Gravity.CENTER;
                break;
            case CENTER_LEFT:
                layoutParams.gravity = android.view.Gravity.START | android.view.Gravity.CENTER;
                container_content.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                defaultGravity = Gravity.CENTER_LEFT;
                break;
            case CENTER_RIGHT:
                layoutParams.gravity = android.view.Gravity.END | android.view.Gravity.CENTER;
                container_content.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                defaultGravity = Gravity.CENTER_RIGHT;
                break;
        }
        return this;
    }

    /**
     * 强制自适应或不自适应SnailBar宽度
     *
     * @param wrap 是否自适应
     * @return {@link SnailBar}
     */
    public SnailBar wrapMode(boolean wrap) {
        if (wrap) {
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            container_content.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else {
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            container_content.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        return this;
    }

    /**
     * 设置默认的SnailBar过渡动画
     *
     * @param anime {@link Anime}
     * @return {@link SnailBar}
     */
    public SnailBar anime(Anime anime) {
        defaultAnime = anime;
        return this;
    }

    /**
     * 设置默认的SnailBar过渡动画
     *
     * @param anime    {@link Anime}
     * @param duration 动画时长
     * @return {@link SnailBar}
     */
    public SnailBar anime(Anime anime, int duration) {
        defaultAnime = anime;
        if (duration > 0)
            animeDuration = duration;
        return this;
    }

    /**
     * 将SnailBar绑定到一个View上面
     *
     * @param v 需要绑定的控件
     * @return {@link SnailBar}
     */
    public SnailBar attachTo(View v) {
        if ((v.getX() + (container.getWidth() * 0.36)) > decorView.getWidth()) {
            container.setX(decorView.getWidth() - container.getWidth());
            container.setY((float) (v.getY() + container.getHeight() * 0.36));
        } else if (v.getX() - (container.getWidth() * 0.36) < 0) {
            container.setX(0);
            container.setY((float) (v.getY() + container.getHeight() * 0.36));
        } else {
            container.setX((float) (v.getX() - (container.getWidth() * 0.36)));
            container.setY((float) (v.getY() + container.getHeight() * 0.36));
        }
        return this;
    }

    /**
     * 设置SnailBar的布局
     *
     * @param layout 布局Id值
     * @return {@link SnailBar}
     */
    public SnailBar contentView(int layout) {
        container_content = (ViewGroup) LayoutInflater.from(context).inflate(layout, null);
        if (container_content.findViewById(R.id.snailbar_action) == null) {
            throw new NullPointerException("The layout haven't has an action button with id 'snailbar_action'");
        } else if (container_content.findViewById(R.id.snailbar_content) == null) {
            throw new NullPointerException("The layout haven't has a content TextView with id 'snailbar_content'");
        } else {
            container.removeAllViews();
            container.addView(container_content);
            btn_action = container.findViewById(R.id.snailbar_action);
            content = container.findViewById(R.id.snailbar_content);
        }
        return this;
    }

    /**
     * 设置SnailBar的布局
     *
     * @param layout 布局控件
     * @return {@link SnailBar}
     */
    public SnailBar contentView(ViewGroup layout) {
        container_content = layout;
        if (container_content.findViewById(R.id.snailbar_action) == null) {
            throw new NullPointerException("The layout haven't has an action button with id 'snailbar_action'");
        } else if (container_content.findViewById(R.id.snailbar_content) == null) {
            throw new NullPointerException("The layout haven't has a content TextView with id 'snailbar_content'");
        } else {
            container.removeAllViews();
            container.addView(container_content);
            btn_action = container.findViewById(R.id.snailbar_action);
            content = container.findViewById(R.id.snailbar_content);
        }
        return this;
    }

    /**
     * 设置SnailBar上面要显示的Icon
     *
     * @param resId 资源Id值
     * @return {@link SnailBar}
     */
    public SnailBar icon(int resId) {
        _icon.setVisibility(View.VISIBLE);
        _icon.setImageDrawable(context.getResources().getDrawable(resId));
        return this;
    }

    /**
     * 设置SnailBar上面要显示的Icon
     *
     * @param bitmap Bitmap图片
     * @return {@link SnailBar}
     */
    public SnailBar icon(Bitmap bitmap) {
        _icon.setVisibility(View.VISIBLE);
        _icon.setImageBitmap(bitmap);
        return this;
    }

    /**
     * 设置 SnailBar 的背景
     *
     * @param drawable Drawable资源
     * @return {@link SnailBar}
     */
    public SnailBar background(Drawable drawable) {
        container_content.setBackground(drawable);
        return this;
    }

    /**
     * 设置 SnailBar 背景颜色
     *
     * @param color 颜色值
     * @return {@link SnailBar}
     */
    public SnailBar backgroundColor(int color) {
        Drawable drawable = context.getDrawable(R.drawable.snailbar_bg);
        drawable.setTint(color);
        container_content.setBackground(drawable);
        return this;
    }

    /**
     * 设置 SnailBar 背景颜色
     *
     * @param color 颜色值
     * @return {@link SnailBar}
     */
    public SnailBar backgroundColor(String color) {
        Drawable drawable = context.getDrawable(R.drawable.snailbar_bg);
        drawable.setTint(Color.parseColor(color));
        container_content.setBackground(drawable);
        return this;
    }

    /**
     * 设置 SnailBar 背景资源
     *
     * @param resId 资源值
     * @return {@link SnailBar}
     */
    public SnailBar backgroundResources(int resId) {
        container_content.setBackgroundResource(resId);
        return this;
    }

    /**
     * 设置要显示的消息
     *
     * @param s 消息
     * @return {@link SnailBar}
     */
    public SnailBar msg(String s) {
        content.setText(s);
        return this;
    }

    /**
     * 设置按钮的点击事件
     *
     * @param text     按钮的文本
     * @param listener {@link SnailBarActionListener}监听器
     * @return {@link SnailBar}
     */
    public SnailBar action(String text, SnailBarActionListener listener) {
        btn_action.setVisibility(View.VISIBLE);
        btn_action.setText(text);
        actionListener = listener;
        return this;
    }

    /**
     * 设置显示时长
     *
     * @param length 时长
     * @return {@link SnailBar}
     */
    public SnailBar duration(int length) {
        _duration = length;
        return this;
    }

    /**
     * 显示SnailBar
     */
    public void show() {
        showAnimation(defaultAnime);
    }

    /**
     * 取消SnailBar
     */
    public void dismiss() {
        closeAnimation(defaultAnime);
    }

    /**
     * 设置按钮的字体颜色
     *
     * @param color 颜色值
     * @return {@link SnailBar}
     */
    public SnailBar setActionButtonTextColor(int color) {
        btn_action.setTextColor(color);
        return this;
    }

    /**
     * 设置消息的文本颜色
     *
     * @param color 颜色值
     * @return {@link SnailBar}
     */
    public SnailBar setMsgColor(int color) {
        content.setTextColor(color);
        return this;
    }

    /**
     * SnailBar 按钮监听器
     */
    public interface SnailBarActionListener {
        /**
         * 按钮点击事件
         *
         * @param view     返回按钮
         * @param snailBar 返回SnailBar
         */
        void onClick(View view, SnailBar snailBar);
    }

    /**
     * SnailBar监听器
     */
    public interface SnailBarListener {
        /**
         * SnailBar已显示事件
         *
         * @param snailBar 返回SnailBar
         */
        void onShown(SnailBar snailBar);

        /**
         * SnailBar已取消事件
         *
         * @param snailBar 返回SnailBar
         */
        void onDismissed(SnailBar snailBar);
    }

    private static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

}
