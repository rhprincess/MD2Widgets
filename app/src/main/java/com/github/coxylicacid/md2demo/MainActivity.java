package com.github.coxylicacid.md2demo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.github.coxylicacid.mdwidgets.toast.SnailBar;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button show = findViewById(R.id.show);
        show.setOnClickListener(new View.OnClickListener() {
            int n = 0;

            @Override
            public void onClick(View view) {
                SnailBar.make(MainActivity.this, "你好", SnailBar.LENGTH_SHORT)
                        .gravity(SnailBar.Gravity.BOTTOM)
                        .wrapMode(true)
                        .icon(R.mipmap.ic_launcher_round)
                        .anime(SnailBar.Anime.CIRCULAR_REVEAL)
                        .action("确定", new SnailBar.SnailBarActionListenerAdapter() {
                            @Override
                            public void onClick(View view, SnailBar snailBar) {
                                snailBar.msg("你点了确定");
                            }
                        }).show();
                n++;
            }
        });
    }
}
