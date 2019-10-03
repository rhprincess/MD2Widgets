package com.github.coxylicacid.md2demo;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.coxylicacid.mdwidgets.toast.SnailBar;
import com.github.coxylicacid.mdwidgets.widget.SnailActionBar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    private final int TRANSLATE = -10086;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        final MaterialButton show = findViewById(R.id.show);
        final MaterialButton add = findViewById(R.id.add);
        final MaterialButton delete = findViewById(R.id.delete);
        final TextInputEditText editTitle = findViewById(R.id.edit_title);
        final TextInputEditText editNum = findViewById(R.id.edit_num);
        final TextView errorMsg = findViewById(R.id.errorMsg);
        final SnailActionBar actionBar = new SnailActionBar(MainActivity.this, "操作");

        actionBar.setMaxItemOnSurface(4);
        actionBar.addItem(android.R.id.copy, "复制", R.drawable.ic_copy);
        actionBar.addItem(android.R.id.paste, "粘贴", R.drawable.ic_paste);
        actionBar.addItem(android.R.id.cut, "剪贴", R.drawable.ic_cut);
        actionBar.addItem(android.R.id.selectAll, "全选", R.drawable.ic_select_all);
        actionBar.addItem(TRANSLATE, "翻译", R.drawable.ic_translate);
        actionBar.setGravity(Gravity.TOP);
        actionBar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        actionBar.addClickListener(new SnailActionBar.OnItemClickListener() {
            @Override
            public void onClick(SnailActionBar bar, int pos) {
                switch (bar.getItemId(pos)) {
                    case android.R.id.copy:
                        bar.removeItem(pos);
                        break;
                    case android.R.id.paste:
                        SnailBar.make(MainActivity.this, "Paste", SnailBar.LENGTH_SHORT).gravity(SnailBar.Gravity.CENTER).show();
                        break;
                    case android.R.id.cut:
                        SnailBar.make(MainActivity.this, "Cut", SnailBar.LENGTH_SHORT).gravity(SnailBar.Gravity.CENTER).show();
                        break;
                    case android.R.id.selectAll:
                        SnailBar.make(MainActivity.this, "SelectAll", SnailBar.LENGTH_SHORT).gravity(SnailBar.Gravity.CENTER).show();
                        break;
                    case TRANSLATE:
                        SnailBar.make(MainActivity.this, "Translate", SnailBar.LENGTH_SHORT).gravity(SnailBar.Gravity.CENTER).show();
                        break;
                }
            }
        });

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (actionBar.isShowing())
                    actionBar.close();
                else
                    actionBar.show();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!TextUtils.isEmpty(editTitle.getText()))
                        actionBar.addItem(editTitle.getText().toString(), R.mipmap.ic_launcher);
                } catch (Exception e) {
                    errorMsg.setText(e.getMessage());
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!TextUtils.isEmpty(editNum.getText()))
                        actionBar.removeItem(Integer.valueOf(editNum.getText().toString()) - 1);
                } catch (Exception e) {
                    errorMsg.setText(e.getMessage());
                }
            }
        });
    }
}
