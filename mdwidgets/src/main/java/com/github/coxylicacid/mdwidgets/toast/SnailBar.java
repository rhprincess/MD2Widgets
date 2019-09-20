package com.github.coxylicacid.mdwidgets.toast;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.coxylicacid.mdwidgets.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * @author Krins
 * @version 0.0.1-alpha06
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
    private static ImageView _expand;
    private static FloatingActionButton fabButton;

    private static LinearLayout expanderFront;
    private static LinearLayout expanderBehind;
    private static TextView textFront;
    private static TextView textBehind;

    private static View targetAttachView;

    public final static int LENGTH_LONG = -0;
    public final static int LENGTH_SHORT = -1;
    public final static int LENGTH_NEVER = -2;

    private static boolean isUsingExpandMode = false;
    private static boolean isExpanderOnTop = false;
    private static boolean isExpanded = false;

    private static boolean isAttachToFab = false;
    private static boolean isFirstShow = true;
    private static boolean isTargetViewOnTop = false;

    private static float attachY = 0;

    private static int maxExpandedMsg = 10;

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
    private static String _msg = "";
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
        _expand = container.findViewById(R.id.snailbar_expand);
        content = container.findViewById(R.id.snailbar_content);
        btn_action = container.findViewById(R.id.snailbar_action);
        container_content = container.findViewById(R.id.container_content);

        expanderFront = container.findViewById(R.id.snailbar_expander_front);
        expanderBehind = container.findViewById(R.id.snailbar_expander_behind);
        textFront = container.findViewById(R.id.snailbar_front_text);
        textBehind = container.findViewById(R.id.snailbar_behind_text);

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

        btn_action.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (actionListener != null)
                    return actionListener.onLongClick(view, snailBar);
                else return false;
            }
        });

        _expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isExpanded)
                    closeExpander();
                else
                    openExpander();
            }
        });
    }

    private static void openExpander() {
        durationHandler.removeCallbacks(runnable); // 暂时关闭计时器
        if (listener != null)
            listener.onPaused(instance);

        container_content.setOnTouchListener(null);

        isExpanded = true;

        //判断SnailBar的方位，根据方位显示消息扩展器
        if (isExpanderOnTop) {
            _expand.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_expand));
            expanderBehind.setVisibility(View.VISIBLE);
            textBehind.setText(_msg);
        } else {
            _expand.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_fold));
            expanderFront.setVisibility(View.VISIBLE);
            textFront.setText(_msg);
        }

        if (isTargetViewOnTop) {
            container.setY(attachY + container.getHeight() * 2.5f);
        }
    }

    private static void closeExpander() {
        durationHandler.postDelayed(runnable, 1850);
        if (listener != null)
            listener.onResumed(instance);

        isExpanded = false;
        //判断SnailBar的方位，根据方位隐藏消息扩展器
        if (isExpanderOnTop) {
            _expand.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_fold));
            expanderBehind.setVisibility(View.GONE);
        } else {
            _expand.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_expand));
            expanderFront.setVisibility(View.GONE);
        }

        if (isTargetViewOnTop) {
            container.setY(targetAttachView.getY());
        }
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
                        if (listener != null)
                            listener.onPaused(instance);
                        break;
                    case MotionEvent.ACTION_UP:
                        durationHandler.postDelayed(runnable, 1850);
                        if (listener != null)
                            listener.onResumed(instance);
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

        if (targetAttachView != null && isFirstShow) {
            if ((targetAttachView.getY() + container.getHeight() * 0.36 < decorView.getHeight() * 0.3) && isUsingExpandMode) {
                defaultGravity = Gravity.TOP;
                useExpandMode();
                isTargetViewOnTop = true;
            }
        }

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setExpandMode();
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

                isFirstShow = false;
            }
        });

        if (isAttachToFab) {
            ObjectAnimator fabTrans = ObjectAnimator.ofFloat(fabButton, "translationY", 0, -container.getHeight() + dp2px(10));
            fabTrans.setDuration(150);
            fabTrans.start();
        }
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

        if (isAttachToFab) {
            ObjectAnimator fabTrans = ObjectAnimator.ofFloat(fabButton, "translationY", -container.getHeight() + dp2px(10), 0);
            fabTrans.setDuration(150);
            fabTrans.start();
            fabTrans.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    isAttachToFab = false;
                }
            });
        }
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
     * 使用消息扩展模式，当消息大于一定长度时自动折叠消息
     *
     * @return {@link SnailBar}
     */
    public SnailBar useExpandMode() {
        isUsingExpandMode = true;
        if (defaultGravity == Gravity.TOP_LEFT || defaultGravity == Gravity.TOP || defaultGravity == Gravity.TOP_RIGHT) {
            _expand.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_fold));
            isExpanderOnTop = true;
        } else {
            _expand.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_expand));
            isExpanderOnTop = false;
        }
        setExpandMode();
        return this;
    }

    @SuppressLint("SetTextI18n")
    private static void setExpandMode() {
        if (isUsingExpandMode && (content.getLineCount() > 3 || container.getHeight() > dp2px(40) * 2.5)) {
            _expand.setVisibility(View.VISIBLE);
            content.setText((_msg.substring(0, maxExpandedMsg) + "...").replace("\n", ""));
        }

        if (isAttachToFab && isFirstShow) {
            ObjectAnimator fabTrans = ObjectAnimator.ofFloat(fabButton, "translationY", 0, -container.getHeight() + dp2px(10));
            fabTrans.setDuration(150);
            fabTrans.start();
        }
    }

    /**
     * 监听SnailBar的相关事件
     *
     * @param lis 监听器
     * @return {@link SnailBar}
     */
    public SnailBar listenSnail(SnailBarListener lis) {
        listener = lis;
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
        targetAttachView = v;
        attachY = v.getY() + container.getHeight() * 0.36f;
        if ((v.getX() + (container.getWidth() * 0.36)) > decorView.getWidth()) {
            container.setX(decorView.getWidth() - container.getWidth());
            container.setY(v.getY() + container.getHeight() * 0.36f);
        } else if (v.getX() - (container.getWidth() * 0.36) < 0) {
            container.setX(0);
            container.setY(v.getY() + container.getHeight() * 0.36f);
        } else {
            container.setX(v.getX() - (container.getWidth() * 0.36f));
            container.setY(v.getY() + container.getHeight() * 0.36f);
        }
        return this;
    }

    /**
     * 绑定到Fab上（针对性）
     *
     * @param fab FloatingActionButton
     * @return {@link SnailBar}
     */
    public SnailBar attachToFab(FloatingActionButton fab) {
        fabButton = fab;
        isAttachToFab = true;
        if (fab.getX() > decorView.getWidth() * 0.65) {
            gravity(Gravity.BOTTOM_RIGHT);
        } else if (fab.getX() < decorView.getWidth() * 0.35) {
            gravity(Gravity.BOTTOM_LEFT);
        } else {
            gravity(Gravity.BOTTOM);
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
     * 给Icon着色
     *
     * @param color 颜色值
     * @return {@link SnailBar}
     */
    public SnailBar iconTint(int color) {
        _icon.setImageTintList(ColorStateList.valueOf(color));
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
        if (!isUsingExpandMode) {
            content.setText(s);
        } else {
            try {
                if (isExpanderOnTop) {
                    if (isExpanded) {
                        textBehind.setText(s);
                        content.setText(String.format("%s...", s.substring(0, maxExpandedMsg).replace("\n", "")));
                    } else {
                        updateMsgOnExpandMode(content, s);
                    }
                } else {
                    if (isExpanded) {
                        textFront.setText(s);
                        content.setText(String.format("%s...", s.substring(0, maxExpandedMsg).replace("\n", "")));
                    } else {
                        updateMsgOnExpandMode(content, s);
                    }
                }
            } catch (StringIndexOutOfBoundsException ignored) {
            }
        }
        _msg = s;
        return this;
    }

    /**
     * 设置要显示的消息
     *
     * @param resId 字符串资源Id
     * @return {@link SnailBar}
     */
    public SnailBar msg(int resId) {
        if (!isUsingExpandMode) {
            content.setText(context.getString(resId));
        } else {
            try {
                if (isExpanderOnTop) {
                    if (isExpanded) {
                        textBehind.setText(context.getString(resId));
                        content.setText(String.format("%s...", context.getString(resId).substring(0, maxExpandedMsg).replace("\n", "")));
                    } else {
                        updateMsgOnExpandMode(content, context.getString(resId));
                    }
                } else {
                    if (isExpanded) {
                        textFront.setText(context.getString(resId));
                        content.setText(String.format("%s...", context.getString(resId).substring(0, maxExpandedMsg).replace("\n", "")));
                    } else {
                        updateMsgOnExpandMode(content, context.getString(resId));
                    }
                }
            } catch (StringIndexOutOfBoundsException ignored) {
            }
        }
        _msg = context.getString(resId);
        return this;
    }

    private void updateMsgOnExpandMode(TextView v, String s) {
        if (s.length() > maxExpandedMsg) {
            v.setText(String.format("%s...", s.substring(0, maxExpandedMsg).replace("\n", "")));
        } else {
            v.setText(s);
        }
    }

    /**
     * 消息最大折叠数
     *
     * @param max 最大折叠值
     * @return {@link SnailBar}
     */
    public SnailBar expandMax(int max) {
        maxExpandedMsg = max;
        return this;
    }

    /**
     * 设置消息可复制性
     *
     * @param selectable 是否可复制
     * @return {@link SnailBar}
     */
    public SnailBar msgSelectable(boolean selectable) {
        if (selectable) {
            content.setEnabled(true);
            content.setTextIsSelectable(true);
            content.setLongClickable(true);

            textFront.setEnabled(true);
            textFront.setTextIsSelectable(true);
            textFront.setLongClickable(true);

            textBehind.setEnabled(true);
            textBehind.setTextIsSelectable(true);
            textBehind.setLongClickable(true);
        } else {
            content.setEnabled(false);
            content.setTextIsSelectable(false);
            content.setLongClickable(false);

            textFront.setEnabled(false);
            textFront.setTextIsSelectable(false);
            textFront.setLongClickable(false);

            textBehind.setEnabled(false);
            textBehind.setTextIsSelectable(false);
            textBehind.setLongClickable(false);
        }
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
    public SnailBar actionTextColor(int color) {
        btn_action.setTextColor(color);
        return this;
    }

    /**
     * 设置消息的文本颜色
     *
     * @param color 颜色值
     * @return {@link SnailBar}
     */
    public SnailBar msgColor(int color) {
        content.setTextColor(color);
        textFront.setTextColor(color);
        textBehind.setTextColor(color);
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
         * @param snailBar 返回{@link SnailBar}
         */
        void onClick(View view, SnailBar snailBar);

        /**
         * 按钮长按事件
         *
         * @param view     返回按钮
         * @param snailBar 返回{@link SnailBar}
         * @return 是否完成了长按
         */
        boolean onLongClick(View view, SnailBar snailBar);
    }

    /**
     * {@link SnailBarActionListener}的适配器，选择性实现接口方法
     */
    public static class SnailBarActionListenerAdapter implements SnailBarActionListener {
        @Override
        public void onClick(View view, SnailBar snailBar) {

        }

        @Override
        public boolean onLongClick(View view, SnailBar snailBar) {
            return false;
        }
    }

    /**
     * SnailBar监听器
     */
    public interface SnailBarListener {
        /**
         * SnailBar已显示事件
         *
         * @param snailBar 返回{@link SnailBar}
         */
        void onShown(SnailBar snailBar);

        /**
         * SnailBar已取消事件
         *
         * @param snailBar 返回{@link SnailBar}
         */
        void onDismissed(SnailBar snailBar);

        /**
         * SnailBar暂时停止事件（当触碰SnailBar的时候激活）
         *
         * @param snailBar 返回{@link SnailBar}
         */
        void onPaused(SnailBar snailBar);

        /**
         * Snailbar恢复事件（当触碰取消的时候激活）
         *
         * @param snailBar 返回{@link SnailBar}
         */
        void onResumed(SnailBar snailBar);
    }

    /**
     * {@link SnailBarListener}的适配器，选择性实现接口方法
     */
    public static class SnailBarListenerAdapter implements SnailBarListener {
        @Override
        public void onShown(SnailBar snailBar) {

        }

        @Override
        public void onDismissed(SnailBar snailBar) {

        }

        @Override
        public void onPaused(SnailBar snailBar) {

        }

        @Override
        public void onResumed(SnailBar snailBar) {

        }
    }

    private static float getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        if (height != 0) {
            return height;
        } else {
            return dp2px(20);
        }
    }

    private static int dp2px(float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 限制消息扩展栏的高度
     */
    public static class SnailLimitedHeightView extends LinearLayout {

        private static final float DEFAULT_MAX_RATIO = 0.6f;
        private static final float DEFAULT_MAX_HEIGHT = 0f;

        private float mMaxRatio = DEFAULT_MAX_RATIO;// 优先级高
        private float mMaxHeight = DEFAULT_MAX_HEIGHT;// 优先级低

        public SnailLimitedHeightView(Context context) {
            super(context);
            init();
        }

        public SnailLimitedHeightView(Context context, AttributeSet attrs) {
            super(context, attrs);
            initAttrs(context, attrs);
            init();
        }

        public SnailLimitedHeightView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            initAttrs(context, attrs);
            init();
        }

        private void initAttrs(Context context, AttributeSet attrs) {
            TypedArray a = context.obtainStyledAttributes(attrs,
                    R.styleable.SnailLimitedHeightView);

            final int count = a.getIndexCount();
            for (int i = 0; i < count; ++i) {
                int attr = a.getIndex(i);
                if (attr == R.styleable.SnailLimitedHeightView_heightRatio) {
                    mMaxRatio = a.getFloat(attr, DEFAULT_MAX_RATIO);
                } else if (attr == R.styleable.SnailLimitedHeightView_heightDimen) {
                    mMaxHeight = a.getDimension(attr, DEFAULT_MAX_HEIGHT);
                }
            }
            a.recycle();
        }

        private void init() {
            if (mMaxHeight <= 0) {
                mMaxHeight = mMaxRatio * (float) getScreenHeight(getContext());
            } else {
                mMaxHeight = Math.min(mMaxHeight, mMaxRatio * (float) getScreenHeight(getContext()));
            }
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);

            if (heightMode == MeasureSpec.EXACTLY) {
                heightSize = heightSize <= mMaxHeight ? heightSize
                        : (int) mMaxHeight;
            }

            if (heightMode == MeasureSpec.UNSPECIFIED) {
                heightSize = heightSize <= mMaxHeight ? heightSize
                        : (int) mMaxHeight;
            }
            if (heightMode == MeasureSpec.AT_MOST) {
                heightSize = heightSize <= mMaxHeight ? heightSize
                        : (int) mMaxHeight;
            }
            int maxHeightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, heightMode);
            super.onMeasure(widthMeasureSpec, maxHeightMeasureSpec);
        }

        private int getScreenHeight(Context context) {
            DisplayMetrics outMetrics = new DisplayMetrics();
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (wm != null) {
                wm.getDefaultDisplay().getMetrics(outMetrics);
            }
            return outMetrics.heightPixels;
        }

    }

}
