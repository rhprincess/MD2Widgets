package com.github.coxylicacid.md2demo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.github.coxylicacid.mdwidgets.dialog.MD2Dialog;
import com.github.coxylicacid.mdwidgets.toast.SnailBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button show = findViewById(R.id.show);
        final FloatingActionButton fab = findViewById(R.id.fab);

        show.setOnClickListener(new View.OnClickListener() {
            int n = 0;

            @Override
            public void onClick(View view) {

                MD2Dialog.create(view.getContext())
                        .buttonStyle(MD2Dialog.ButtonStyle.AGREEMENT)
                        .title("同意许可")
                        .msg("是否允许以下协议？")
                        .enableCheckBox("《BlahBlah服务协议》")
                        .simpleCancel("不同意")
                        .onConfirmClick("同意", new MD2Dialog.OptionsButtonCallBack() {
                            @Override
                            public void onClick(View view, MD2Dialog dialog) {
                                SnailBar.make(MainActivity.this, "你点击了确定按钮", SnailBar.LENGTH_SHORT).show();
                            }
                        }).show();

//                SnailBar.make(MainActivity.this, "我是显示的消息 " + n, SnailBar.LENGTH_SHORT)
//                        .gravity(SnailBar.Gravity.BOTTOM)
//                        .wrapMode(false)
//                        .attachToFab(fab)
//                        .useExpandMode()
//                        .icon(R.mipmap.ic_launcher_round)
//                        .show();
                n++;
            }
        });
    }
}
