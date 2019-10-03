package com.github.coxylicacid.mdwidgets.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.github.coxylicacid.mdwidgets.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Krins
 * @version 0.0.1-beta01
 */

public class MD2Dialog {

    private Context context;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private TextView _title;
    private TextView content;
    private MaterialButton confirm;
    private MaterialButton cancel;
    private MaterialButton negative;
    private CheckBox checkbox;
    private LinearLayout progressContent;
    private LinearLayout loadingField;
    private TextView _percent;
    private TextView _kbs;
    private TextView loadingText;
    private ProgressBar progressBar;
    private View rootView;
    private View buttonViews;
    private FrameLayout buttonsContainer;
    private OptionsButtonCallBack _lis_confirm;
    private OptionsButtonCallBack _lis_cancel;
    private OptionsButtonCallBack _lis_negative;
    private OptionsCallBack _callback;
    private String[] choice_list;
    private ListView listView;
    private boolean isSingleChoiceMode = false;
    private SingleChoiceAdapter adapter;
    private SingleChoiceListener choiceLis;
    private boolean isLoadingMode = false;

    private ButtonStyle defaultButtonStyle = ButtonStyle.FILL;

    /**
     * 按钮的样式
     * FLAT : 透明的按钮
     * OUTLINE : 有边框的按钮
     * FILL : 默认按钮样式
     */
    public enum ButtonStyle {
        AGREEMENT, FLAT, OUTLINE, FILL
    }

    /**
     * 错误颜色
     */
    public static final int COLOR_ERROR = 0xFFF44336;
    /**
     * 成功颜色
     */
    public static final int COLOR_SUCCESSFUL = 0xFF4CAF50;
    /**
     * 警告颜色
     */
    public static final int COLOR_WARNING = 0xFFFF5722;

    /**
     * @param context 上下文
     */
    public MD2Dialog(@NonNull Context context) {
        this.context = context;
        rootView = LayoutInflater.from(context).inflate(R.layout.md2dialog, null);
        buttonViews = LayoutInflater.from(context).inflate(R.layout.button_fill, null);
        builder = new AlertDialog.Builder(context);
        builder.setView(rootView);
        dialog = builder.create();
        initView();
    }

    /**
     * 直接获取Dialog实例
     *
     * @param context 上下文
     * @return MD2dialog
     */
    public static MD2Dialog create(Context context) {
        return new MD2Dialog(context);
    }

    private void initView() {
        buttonsContainer = rootView.findViewById(R.id.buttons_container);
        loadingField = rootView.findViewById(R.id.md2_dialog_loading_field);
        loadingText = rootView.findViewById(R.id.md2_dialog_loading_msg);
        listView = rootView.findViewById(R.id.md2_dialog_list);
        progressContent = rootView.findViewById(R.id.md2_dialog_progress_content);
        _percent = rootView.findViewById(R.id.md2_dialog_percent);
        _kbs = rootView.findViewById(R.id.md2_dialog_kbs);
        progressBar = rootView.findViewById(R.id.md2_dialog_progressbar);
        _title = rootView.findViewById(R.id.md2_dialog_title);
        content = rootView.findViewById(R.id.md2_dialog_content);
        confirm = buttonViews.findViewById(R.id.md2_dialog_confirm);
        cancel = buttonViews.findViewById(R.id.md2_dialog_cancel);
        checkbox = rootView.findViewById(R.id.md2_dialog_check1);

        buttonsContainer.addView(buttonViews);
        initButtons();

        _title.setText("标题");
        content.setText("");
        checkbox.setChecked(false);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg_inset);
        dialog.getWindow().setDimAmount(0.35f);

        adapter = new SingleChoiceAdapter(context);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                adapter.select(i);
                if (choiceLis != null)
                    choiceLis.onChoice(MD2Dialog.this, i, adapter.getItem(i).isSelected());
            }
        });
    }

    private void initButtons() {
        confirm = buttonViews.findViewById(R.id.md2_dialog_confirm);
        cancel = buttonViews.findViewById(R.id.md2_dialog_cancel);

        confirm.setText("确定");
        cancel.setText("取消");

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (_lis_confirm != null)
                    _lis_confirm.onClick(view, MD2Dialog.this);
                if (_callback != null)
                    _callback.onConfirm(MD2Dialog.this);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (_lis_cancel != null)
                    _lis_cancel.onClick(view, MD2Dialog.this);
                if (_callback != null)
                    _callback.onCancel(MD2Dialog.this);
            }
        });

        if (defaultButtonStyle != ButtonStyle.AGREEMENT) {
            negative = buttonViews.findViewById(R.id.md2_dialog_negative);
            negative.setText("隐藏");

            negative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (_lis_negative != null)
                        _lis_negative.onClick(view, MD2Dialog.this);
                    if (_callback != null)
                        _callback.onNegative(MD2Dialog.this);
                }
            });
        }
    }

    private int dp2px(float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 设置对话框按钮的Style
     *
     * @param style 主题
     * @return {@link MD2Dialog}
     */
    public MD2Dialog buttonStyle(ButtonStyle style) {
        defaultButtonStyle = style;
        switch (style) {
            case AGREEMENT:
                buttonsContainer.removeAllViews();
                buttonViews = LayoutInflater.from(context).inflate(R.layout.button_agreement, null);
                buttonsContainer.addView(buttonViews);
                initButtons();
                break;
            case FLAT:
                buttonsContainer.removeAllViews();
                buttonViews = LayoutInflater.from(context).inflate(R.layout.button_flat, null);
                buttonsContainer.addView(buttonViews);
                initButtons();
                break;
            case FILL:
                buttonsContainer.removeAllViews();
                buttonViews = LayoutInflater.from(context).inflate(R.layout.button_fill, null);
                buttonsContainer.addView(buttonViews);
                initButtons();
                break;
            case OUTLINE:
                buttonsContainer.removeAllViews();
                buttonViews = LayoutInflater.from(context).inflate(R.layout.button_outline, null);
                buttonsContainer.addView(buttonViews);
                initButtons();
                break;
        }
        return this;
    }

    /**
     * 设置标题的颜色
     *
     * @param color 颜色值
     * @return {@link MD2Dialog}
     */
    public MD2Dialog titleColor(int color) {
        _title.setTextColor(color);
        return this;
    }

    /**
     * 设置消息颜色
     *
     * @param color 颜色值
     * @return {@link MD2Dialog}
     */
    public MD2Dialog msgColor(int color) {
        content.setTextColor(color);
        return this;
    }

    /**
     * 设置确认按钮的颜色
     *
     * @param color 颜色值
     * @return {@link MD2Dialog}
     */
    public MD2Dialog confirmColor(int color) {
        confirm.setTextColor(color);
        return this;
    }

    /**
     * 设置取消按钮的颜色
     *
     * @param color 颜色值
     * @return {@link MD2Dialog}
     */
    public MD2Dialog cancelColor(int color) {
        cancel.setTextColor(color);
        return this;
    }

    /**
     * 设置消极按钮的的颜色
     *
     * @param color 颜色值
     * @return {@link MD2Dialog}
     */
    public MD2Dialog negativeColor(int color) {
        negative.setTextColor(color);
        return this;
    }

    /**
     * 设置所有按钮的统一颜色
     *
     * @param color 颜色值
     * @return {@link MD2Dialog}
     */
    public MD2Dialog allButtonColor(int color) {
        confirm.setTextColor(color);
        cancel.setTextColor(color);
        negative.setTextColor(color);
        return this;
    }

    /**
     * 通过当前Activity的主题来配置对话框的一些颜色
     *
     * @return {@link MD2Dialog}
     */
    public MD2Dialog initWithAppTheme() {
        TypedArray array = context.getTheme().obtainStyledAttributes(new int[]{
                android.R.attr.textColorPrimary,
                android.R.attr.colorAccent,
        });
        int textColor = array.getColor(0, 0xFF000000);
        int colorAccent = array.getColor(1, context.getResources().getColor(R.color.coxylicDialogAccent));
        array.recycle();

        msgColor(getBrighterColor(textColor));
        titleColor(getDarkerColor(textColor));
        allButtonColor(colorAccent);
        return this;
    }

    /**
     * 设置Dialog的显示视图
     *
     * @param resId 布局资源Id
     */
    public void setContentView(int resId) {
        builder.setView(resId);
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg_inset);
        dialog.getWindow().setDimAmount(0.35f);
    }

    /**
     * 设置Dialog的显示视图
     *
     * @param view 要显示的View
     */
    public void setContentView(View view) {
        builder.setView(view);
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg_inset);
        dialog.getWindow().setDimAmount(0.35f);
    }

    /**
     * 设置启动监听器
     *
     * @param listener 监听器
     */
    public void onShow(DialogInterface.OnShowListener listener) {
        dialog.setOnShowListener(listener);
    }

    /**
     * 通过资源Id寻找Dialog中的View
     *
     * @param resId 资源Id
     * @return View
     */
    public View findView(int resId) {
        return dialog.getWindow().findViewById(resId);
    }

    /**
     * 获取对话框中的CheckBox的选中情况
     *
     * @return {@link MD2Dialog}
     */
    public boolean getCheckBoxStatus() {
        return checkbox.isChecked();
    }

    /**
     * 设置Dialog为加载中模式
     *
     * @return {@link MD2Dialog}
     */
    public MD2Dialog onLoading() {
        isLoadingMode = true;
        content.setVisibility(View.GONE);
        loadingField.setVisibility(View.VISIBLE);
        _title.setVisibility(View.GONE);
        return this;
    }


    /**
     * 设置Dialog为加载中模式
     *
     * @param hasTitle 是否含有标题
     * @return {@link MD2Dialog}
     */
    public MD2Dialog onLoading(boolean hasTitle) {
        isLoadingMode = true;
        content.setVisibility(View.GONE);
        loadingField.setVisibility(View.VISIBLE);
        if (!hasTitle)
            _title.setVisibility(View.GONE);
        return this;
    }


    /**
     * 监听单选情况
     *
     * @param defaultChoice 默认选中某个选项
     * @param lis           监听者
     * @return {@link MD2Dialog}
     */
    public MD2Dialog listenChoices(int defaultChoice, SingleChoiceListener lis) {
        if (isSingleChoiceMode) {
            choiceLis = lis;
            adapter.getItem(defaultChoice).setSelected(true);
        }
        return this;
    }

    /**
     * 移除单选列表的分割线
     *
     * @return {@link MD2Dialog}
     */
    public MD2Dialog removeDivider() {
        listView.setDividerHeight(0);
        return this;
    }

    /**
     * 设置是否为单选模式
     *
     * @param b 确认与否
     * @return {@link MD2Dialog}
     */
    public MD2Dialog singleChoiceMode(boolean b) {
        isSingleChoiceMode = b;
        listView.setVisibility(View.VISIBLE);
        content.setVisibility(View.GONE);
        return this;
    }

    /**
     * 获取单选的选中项
     *
     * @return 返回选中项
     */
    public int getSelectedItem() {
        return adapter.getSelected();
    }

    /**
     * 根据位置获取单选项的内容
     *
     * @param pos 位置
     * @return 内容
     */
    public String getItemValue(int pos) {
        return adapter.getItem(pos).getContent();
    }

    /**
     * 设置单选的项目（数据）
     *
     * @param resId values目录下的arrays.xml文件所定义的数组的id
     * @return {@link MD2Dialog}
     */
    public MD2Dialog items(int resId) {
        String[] lists = context.getResources().getStringArray(resId);
        List<Choicer> choicers = new ArrayList<>();
        if (isSingleChoiceMode) {
            for (String s : lists) {
                choicers.add(new Choicer(s, false));
            }
            adapter.setList(choicers);
            listView.setAdapter(adapter);
        }
        return this;
    }

    /**
     * 设置单选的项目（数据）
     *
     * @param resId         values目录下的arrays.xml文件所定义的数组的id
     * @param defaultChoice 默认选中某个选项
     * @return {@link MD2Dialog}
     */
    public MD2Dialog items(int resId, int defaultChoice) {
        String[] lists = context.getResources().getStringArray(resId);
        List<Choicer> choicers = new ArrayList<>();
        if (isSingleChoiceMode) {
            for (String s : lists) {
                choicers.add(new Choicer(s, false));
            }
            adapter.setList(choicers);
            listView.setAdapter(adapter);
            adapter.getItem(defaultChoice).setSelected(true);
        }
        return this;
    }

    /**
     * 设置单选项目（数据）
     *
     * @param lists 数据数组
     * @return {@link MD2Dialog}
     */
    public MD2Dialog items(String[] lists) {
        List<Choicer> choicers = new ArrayList<>();
        if (isSingleChoiceMode) {
            for (String s : lists) {
                choicers.add(new Choicer(s, false));
            }
            adapter.setList(choicers);
            listView.setAdapter(adapter);
        }
        return this;
    }

    /**
     * 设置单选项目（数据）
     *
     * @param lists         数据数组
     * @param defaultChoice 默认选中某个选项
     * @return {@link MD2Dialog}
     */
    public MD2Dialog items(String[] lists, int defaultChoice) {
        List<Choicer> choicers = new ArrayList<>();
        if (isSingleChoiceMode) {
            for (String s : lists) {
                choicers.add(new Choicer(s, false));
            }
            adapter.setList(choicers);
            listView.setAdapter(adapter);
            adapter.getItem(defaultChoice).setSelected(true);
        }
        return this;
    }


    /**
     * 响应按钮的兼并监听模式
     *
     * @param callback 监听回调
     * @return {@link MD2Dialog}
     */
    public MD2Dialog call(OptionsCallBack callback) {
        this._callback = callback;
        return this;
    }

    /**
     * 响应确认按钮
     *
     * @param callBack 监听回调
     * @return {@link MD2Dialog}
     */
    public MD2Dialog callConfirm(OptionsButtonCallBack callBack) {
        this._lis_confirm = callBack;
        return this;
    }

    /**
     * 响应取消按钮
     *
     * @param callBack 监听回调
     * @return {@link MD2Dialog}
     */
    public MD2Dialog callCancel(OptionsButtonCallBack callBack) {
        this._lis_cancel = callBack;
        return this;
    }

    /**
     * 响应消极按钮
     *
     * @param callBack 监听回调
     * @return {@link MD2Dialog}
     */
    public MD2Dialog callNegative(OptionsButtonCallBack callBack) {
        return this;
    }

    /**
     * 设置对话框模式为进度条模式
     *
     * @return {@link MD2Dialog}
     */
    public MD2Dialog progress() {
        progressContent.setVisibility(View.VISIBLE);
        return this;
    }

    /**
     * 是否显示为进度条模式
     *
     * @param indeterminate 是否将进度条处于循环加载状态
     * @param max           最大值
     * @return {@link MD2Dialog}
     */
    public MD2Dialog progress(boolean indeterminate, int max) {
        progressBar.setIndeterminate(indeterminate);
        progressBar.setMax(max);
        progressContent.setVisibility(View.VISIBLE);
        return this;
    }

    /**
     * 设置进度条的最大值
     *
     * @param max 最大值
     * @return {@link MD2Dialog}
     */
    public MD2Dialog progressMax(int max) {
        progressBar.setMax(max);
        return this;
    }

    /**
     * 获取进度条控件
     *
     * @return ProgressBar
     */
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    /**
     * 更新进度条的进度
     *
     * @param progress 更新的进度值
     * @return {@link MD2Dialog}
     */
    public MD2Dialog updateProgress(int progress) {
        progressBar.setProgress(progress);
        return this;
    }

    /**
     * 设置进度条显示的百分比值文字
     *
     * @param i 数值
     * @return {@link MD2Dialog}
     */
    @SuppressLint("SetTextI18n")
    public MD2Dialog percent(int i) {
        if (_percent.getVisibility() == View.GONE) {
            _percent.setVisibility(View.VISIBLE);
            _percent.setText(String.valueOf(i) + "%");
        } else {
            _percent.setText(String.valueOf(i) + "%");
        }
        return this;
    }

    /**
     * 设置进度条显示的百分比文字
     *
     * @param i 文字
     * @return {@link MD2Dialog}
     */
    @SuppressLint("SetTextI18n")
    public MD2Dialog percent(String i) {
        if (_percent.getVisibility() == View.GONE) {
            _percent.setVisibility(View.VISIBLE);
            _percent.setText(i + "%");
        } else {
            _percent.setText(i + "%");
        }
        return this;
    }

    /**
     * 设置进度条显示的百分比值文字
     *
     * @param i 数值
     * @return {@link MD2Dialog}
     */
    @SuppressLint("SetTextI18n")
    public MD2Dialog percent(float i) {
        if (_percent.getVisibility() == View.GONE) {
            _percent.setVisibility(View.VISIBLE);
            _percent.setText(String.valueOf(i) + "%");
        } else {
            _percent.setText(String.valueOf(i) + "%");
        }
        return this;
    }

    /**
     * 显示进度条当中的加载值文字
     *
     * @param s 字符串
     * @return {@link MD2Dialog}
     */
    public MD2Dialog kbs(String s) {
        if (_kbs.getVisibility() == View.GONE) {
            _kbs.setVisibility(View.VISIBLE);
            _kbs.setText(s);
        } else {
            _kbs.setText(s);
        }
        return this;
    }

    /**
     * 是否允许触碰对话框外后关闭对话框
     *
     * @param b 确认与否
     * @return {@link MD2Dialog}
     */
    public MD2Dialog canceledOnTouchOutside(boolean b) {
        dialog.setCanceledOnTouchOutside(b);
        return this;
    }

    /**
     * 启用对话框中的CheckBox （可用于协议的同意等等）
     *
     * @param describe CheckBox的描述文字
     * @return {@link MD2Dialog}
     */
    public MD2Dialog enableCheckBox(String describe) {
        checkbox.setVisibility(View.VISIBLE);
        checkbox.setText(describe);
        return this;
    }

    /**
     * 启用对话框中的CheckBox （可用于协议的同意等等）
     *
     * @param resId CheckBox的描述文字 （字符串资源id）
     * @return {@link MD2Dialog}
     */
    public MD2Dialog enableCheckBox(int resId) {
        checkbox.setVisibility(View.VISIBLE);
        checkbox.setText(context.getString(resId));
        return this;
    }

    /**
     * 启用对话框中的CheckBox （可用于协议的同意等等）
     *
     * @param describe       CheckBox的描述文字
     * @param defaultChecked 是否默认选中
     * @return {@link MD2Dialog}
     */
    public MD2Dialog enableCheckBox(String describe, boolean defaultChecked) {
        checkbox.setVisibility(View.VISIBLE);
        checkbox.setChecked(defaultChecked);
        checkbox.setText(describe);
        return this;
    }

    /**
     * 启用对话框中的CheckBox （可用于协议的同意等等）
     *
     * @param resId          CheckBox的描述文字 （字符串资源id）
     * @param defaultChecked 是否默认选中
     * @return {@link MD2Dialog}
     */
    public MD2Dialog enableCheckBox(int resId, boolean defaultChecked) {
        checkbox.setVisibility(View.VISIBLE);
        checkbox.setChecked(defaultChecked);
        checkbox.setText(context.getString(resId));
        return this;
    }

    /**
     * 确认按钮图标 （仅适用于ButtonStyle为AGREENMENT模式时）
     *
     * @param iconId Drawable资源id
     * @return {@link MD2Dialog}
     */
    public MD2Dialog confirmIcon(@DrawableRes int iconId) {
        if (defaultButtonStyle == ButtonStyle.AGREEMENT) {
            confirm.setIconResource(iconId);
        }
        return this;
    }

    /**
     * 确认按钮图标 （仅适用于ButtonStyle为AGREENMENT模式时）
     *
     * @param iconDrawable Drawable
     * @return {@link MD2Dialog}
     */
    public MD2Dialog confirmIcon(Drawable iconDrawable) {
        if (defaultButtonStyle == ButtonStyle.AGREEMENT) {
            confirm.setIcon(iconDrawable);
        }
        return this;
    }

    /**
     * 取消按钮图标 （仅适用于ButtonStyle为AGREENMENT模式时）
     *
     * @param iconId Drawable资源id
     * @return {@link MD2Dialog}
     */
    public MD2Dialog cancelIcon(@DrawableRes int iconId) {
        if (defaultButtonStyle == ButtonStyle.AGREEMENT) {
            cancel.setIconResource(iconId);
        }
        return this;
    }

    /**
     * 取消按钮图标 （仅适用于ButtonStyle为AGREENMENT模式时）
     *
     * @param iconDrawable Drawable
     * @return {@link MD2Dialog}
     */
    public MD2Dialog cancelIcon(Drawable iconDrawable) {
        if (defaultButtonStyle == ButtonStyle.AGREEMENT) {
            cancel.setIcon(iconDrawable);
        }
        return this;
    }

    /**
     * 设置确认按钮的监听
     *
     * @param listener 监听器
     * @return {@link MD2Dialog}
     */
    public MD2Dialog onConfirmClick(OptionsButtonCallBack listener) {
        confirm.setVisibility(View.VISIBLE);
        _lis_confirm = listener;
        return this;
    }

    /**
     * 设置确认按钮的监听
     *
     * @param text     确认按钮的文字
     * @param listener 监听器
     * @return {@link MD2Dialog}
     */
    public MD2Dialog onConfirmClick(String text, OptionsButtonCallBack listener) {
        confirm.setVisibility(View.VISIBLE);
        confirm.setText(text);
        _lis_confirm = listener;
        return this;
    }

    /**
     * 设置确认按钮的监听
     *
     * @param resId    确认按钮文字 （字符串资源Id）
     * @param listener 监听器
     * @return {@link MD2Dialog}
     */
    public MD2Dialog onConfirmClick(int resId, OptionsButtonCallBack listener) {
        confirm.setVisibility(View.VISIBLE);
        confirm.setText(context.getString(resId));
        _lis_confirm = listener;
        return this;
    }

    /**
     * 设置对话框中的确认按钮事件为默认关闭对话框
     *
     * @return {@link MD2Dialog}
     */
    public MD2Dialog simpleConfirm() {
        confirm.setVisibility(View.VISIBLE);
        _lis_confirm = new OptionsButtonCallBack() {
            @Override
            public void onClick(View view, MD2Dialog dialog) {
                MD2Dialog.this.dismiss();
            }
        };
        return this;
    }

    /**
     * 设置对话框中的确认按钮事件为默认关闭对话框
     *
     * @param s 确认按钮的文字
     * @return {@link MD2Dialog}
     */
    public MD2Dialog simpleConfirm(String s) {
        confirm.setVisibility(View.VISIBLE);
        confirm.setText(s);
        _lis_confirm = new OptionsButtonCallBack() {
            @Override
            public void onClick(View view, MD2Dialog dialog) {
                MD2Dialog.this.dismiss();
            }
        };
        return this;
    }

    /**
     * 设置对话框中的确认按钮事件为默认关闭对话框
     *
     * @param resId 确认按钮的文字
     * @return {@link MD2Dialog}
     */
    public MD2Dialog simpleConfirm(int resId) {
        confirm.setVisibility(View.VISIBLE);
        confirm.setText(context.getString(resId));
        _lis_confirm = new OptionsButtonCallBack() {
            @Override
            public void onClick(View view, MD2Dialog dialog) {
                MD2Dialog.this.dismiss();
            }
        };
        return this;
    }

    /**
     * 设置对话框中的取消按钮事件为默认关闭对话框
     *
     * @return {@link MD2Dialog}
     */
    public MD2Dialog simpleCancel() {
        cancel.setVisibility(View.VISIBLE);
        _lis_cancel = new OptionsButtonCallBack() {
            @Override
            public void onClick(View view, MD2Dialog dialog) {
                MD2Dialog.this.dismiss();
            }
        };
        return this;
    }

    /**
     * 设置对话框中的取消按钮事件为默认关闭对话框
     *
     * @param s 取消按钮的文字
     * @return {@link MD2Dialog}
     */
    public MD2Dialog simpleCancel(String s) {
        cancel.setVisibility(View.VISIBLE);
        cancel.setText(s);
        _lis_cancel = new OptionsButtonCallBack() {
            @Override
            public void onClick(View view, MD2Dialog dialog) {
                MD2Dialog.this.dismiss();
            }
        };
        return this;
    }

    /**
     * 设置对话框中的取消按钮事件为默认关闭对话框
     *
     * @param resId 取消按钮的文字
     * @return {@link MD2Dialog}
     */
    public MD2Dialog simpleCancel(int resId) {
        cancel.setVisibility(View.VISIBLE);
        cancel.setText(context.getString(resId));
        _lis_cancel = new OptionsButtonCallBack() {
            @Override
            public void onClick(View view, MD2Dialog dialog) {
                MD2Dialog.this.dismiss();
            }
        };
        return this;
    }

    /**
     * 设置对话框中的消极按钮事件为默认关闭对话框
     *
     * @return {@link MD2Dialog}
     */
    public MD2Dialog simpleNegative() {
        if (defaultButtonStyle != ButtonStyle.AGREEMENT) {
            negative.setVisibility(View.VISIBLE);
            _lis_negative = new OptionsButtonCallBack() {
                @Override
                public void onClick(View view, MD2Dialog dialog) {
                    MD2Dialog.this.dismiss();
                }
            };
        }
        return this;
    }

    /**
     * 设置对话框中的消极按钮事件为默认关闭对话框
     *
     * @param s 消极按钮的文字
     * @return {@link MD2Dialog}
     */
    public MD2Dialog simpleNegative(String s) {
        if (defaultButtonStyle != ButtonStyle.AGREEMENT) {
            negative.setVisibility(View.VISIBLE);
            negative.setText(s);
            _lis_negative = new OptionsButtonCallBack() {
                @Override
                public void onClick(View view, MD2Dialog dialog) {
                    MD2Dialog.this.dismiss();
                }
            };
        }
        return this;
    }

    /**
     * 设置对话框中的消极按钮事件为默认关闭对话框
     *
     * @param resId 消极按钮的文字
     * @return {@link MD2Dialog}
     */
    public MD2Dialog simpleNegative(int resId) {
        if (defaultButtonStyle != ButtonStyle.AGREEMENT) {
            negative.setVisibility(View.VISIBLE);
            negative.setText(context.getString(resId));
            _lis_negative = new OptionsButtonCallBack() {
                @Override
                public void onClick(View view, MD2Dialog dialog) {
                    MD2Dialog.this.dismiss();
                }
            };
        }
        return this;
    }

    /**
     * 设置取消按钮的监听
     *
     * @param listener 监听器
     * @return {@link MD2Dialog}
     */
    public MD2Dialog onCancelClick(OptionsButtonCallBack listener) {
        cancel.setVisibility(View.VISIBLE);
        _lis_cancel = listener;
        return this;
    }

    /**
     * 设置取消按钮的监听
     *
     * @param text     取消按钮的文字
     * @param listener 监听器
     * @return {@link MD2Dialog}
     */
    public MD2Dialog onCancelClick(String text, OptionsButtonCallBack listener) {
        cancel.setVisibility(View.VISIBLE);
        cancel.setText(text);
        _lis_cancel = listener;
        return this;
    }

    /**
     * 设置取消按钮的监听
     *
     * @param resId    取消按钮的文字 （字符串资源Id）
     * @param listener 监听器
     * @return {@link MD2Dialog}
     */
    public MD2Dialog onCancelClick(int resId, OptionsButtonCallBack listener) {
        cancel.setVisibility(View.VISIBLE);
        cancel.setText(context.getString(resId));
        _lis_cancel = listener;
        return this;
    }

    /**
     * 设置消极按钮的监听
     *
     * @param listener 监听器
     * @return {@link MD2Dialog}
     */
    public MD2Dialog onNegativeClick(OptionsButtonCallBack listener) {
        if (defaultButtonStyle != ButtonStyle.AGREEMENT) {
            negative.setVisibility(View.VISIBLE);
            _lis_negative = listener;
        }
        return this;
    }

    /**
     * 设置消极按钮的监听
     *
     * @param s        消极按钮的文字
     * @param listener 监听器
     * @return {@link MD2Dialog}
     */
    public MD2Dialog onNegativeClick(String s, OptionsButtonCallBack listener) {
        if (defaultButtonStyle != ButtonStyle.AGREEMENT) {
            negative.setVisibility(View.VISIBLE);
            negative.setText(s);
            _lis_negative = listener;
        }
        return this;
    }

    /**
     * 设置消极按钮的监听
     *
     * @param resId    消极按钮的文字 （字符串资源Id）
     * @param listener 监听器
     * @return {@link MD2Dialog}
     */
    public MD2Dialog onNegativeClick(int resId, OptionsButtonCallBack listener) {
        if (defaultButtonStyle != ButtonStyle.AGREEMENT) {
            negative.setVisibility(View.VISIBLE);
            negative.setText(context.getString(resId));
            _lis_negative = listener;
        }
        return this;
    }

    /**
     * 设置对话框标题
     *
     * @param s 标题
     * @return {@link MD2Dialog}
     */
    public MD2Dialog title(String s) {
        _title.setVisibility(View.VISIBLE);
        _title.setText(s);
        return this;
    }

    /**
     * 设置对话框标题
     *
     * @param resId 字符串资源id
     * @return {@link MD2Dialog}
     */
    public MD2Dialog title(int resId) {
        _title.setVisibility(View.VISIBLE);
        _title.setText(context.getString(resId));
        return this;
    }

    /**
     * 设置对话框内容
     *
     * @param s 内容
     * @return {@link MD2Dialog}
     */
    public MD2Dialog msg(String s) {
        if (isLoadingMode) {
            loadingText.setText(s);
        } else {
            content.setText(s);
        }
        return this;
    }

    /**
     * 设置对话框内容
     *
     * @param resId 字符串资源id
     * @return {@link MD2Dialog}
     */
    public MD2Dialog msg(int resId) {
        if (isLoadingMode) {
            loadingText.setText(context.getString(resId));
        } else {
            content.setText(context.getString(resId));
        }
        return this;
    }

    /**
     * 设置对话框内容
     *
     * @param fromHtml Spanned文本
     * @return {@link MD2Dialog}
     */
    public MD2Dialog msg(Spanned fromHtml) {
        if (isLoadingMode) {
            loadingText.setText(fromHtml);
        } else {
            content.setText(fromHtml);
        }
        return this;
    }

    /**
     * 显示对话框
     *
     * @return {@link MD2Dialog}
     */
    public MD2Dialog show() {
        dialog.show();
        return this;
    }

    /**
     * 取消对话框
     *
     * @return {@link MD2Dialog}
     */
    public MD2Dialog dismiss() {
        dialog.dismiss();
        return this;
    }

    // 获取更深颜色
    private int getDarkerColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv); // convert to hsv
        // make darker
        hsv[1] = hsv[1] + 0.1f; // 饱和度更高
        hsv[2] = hsv[2] - 0.1f; // 明度降低
        return Color.HSVToColor(hsv);
    }

    // 获取更浅的颜色
    private int getBrighterColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv); // convert to hsv

        hsv[1] = hsv[1] - 0.2f; // less saturation
        hsv[2] = hsv[2] + 0.2f; // more brightness
        return Color.HSVToColor(hsv);
    }


    /**
     * 按钮回调接口
     */
    public interface OptionsButtonCallBack {
        /**
         * 点击事件
         *
         * @param view   返回被点中的按钮
         * @param dialog 返回Dialog窗体
         */
        void onClick(View view, MD2Dialog dialog);
    }

    /**
     * 按钮兼并回调接口
     */
    public interface OptionsCallBack {
        /**
         * 确认按钮事件
         *
         * @param dialog 返回Dialog窗体
         */
        void onConfirm(MD2Dialog dialog);

        /**
         * 取消按钮事件
         *
         * @param dialog 返回Dialog窗体
         */
        void onCancel(MD2Dialog dialog);

        /**
         * 消极按钮事件
         *
         * @param dialog 返回Dialog窗体
         */
        void onNegative(MD2Dialog dialog);
    }

    /**
     * 单选按钮选中回调接口
     */
    public interface SingleChoiceListener {
        /**
         * 选项被选中
         *
         * @param dialog 返回Dialog窗体
         * @param pos    返回选项位置
         * @param choice 是否被选中
         */
        void onChoice(MD2Dialog dialog, int pos, boolean choice);
    }

    //选择器
    private class Choicer {
        private String content;
        private boolean selected = false;

        public Choicer(String content, boolean selected) {
            this.content = content;
            this.selected = selected;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public String getContent() {
            return content;
        }

        public boolean isSelected() {
            return selected;
        }
    }

    //单选框适配器
    private class SingleChoiceAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater mInflater;
        private ViewHolder holder;
        private List<Choicer> list;
        private Choicer choicer;
        private int isSelectedItem = 0;

        public SingleChoiceAdapter(Context context) {
            this.context = context;
            this.mInflater = LayoutInflater.from(context);
        }

        public void setList(List<Choicer> choicers) {
            this.list = choicers;
        }

        // 选中当前选项时，让其他选项不被选中
        public void select(int position) {
            if (!list.get(position).isSelected()) {
                list.get(position).setSelected(true);
                isSelectedItem = position;
                for (int i = 0; i < list.size(); i++) {
                    if (i != position) {
                        list.get(i).setSelected(false);
                    }
                }
            }
            notifyDataSetChanged();
        }

        public int getSelected() {
            return isSelectedItem;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Choicer getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                holder = new ViewHolder();
                view = mInflater.inflate(R.layout.list_single_choice, null);
                holder.radioBtn = (RadioButton) view
                        .findViewById(R.id.choice_btn);
                holder.radioBtn.setClickable(false);
                holder.textView = (TextView) view
                        .findViewById(R.id.choice_content);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            choicer = (Choicer) getItem(i);
            holder.radioBtn.setChecked(choicer.isSelected());
            holder.textView.setText(choicer.getContent());
            return view;
        }

        private class ViewHolder {
            RadioButton radioBtn;
            TextView textView;
        }

    }

}
