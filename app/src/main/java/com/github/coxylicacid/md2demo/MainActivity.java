package com.github.coxylicacid.md2demo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.github.coxylicacid.mdwidgets.toast.SnailBar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button show = findViewById(R.id.show);
        final FloatingActionButton fab = findViewById(R.id.fab);
        final MaterialButton btn = findViewById(R.id.button);

        show.setOnClickListener(new View.OnClickListener() {
            int n = 0;

            @Override
            public void onClick(View view) {
                SnailBar.make(MainActivity.this, "测试优化程度\n测试优化程度\n测试优化程度\n测试优化程度\n测试优化程度\n测试优化程度\n", SnailBar.LENGTH_SHORT)
                        .attachTo(btn)
                        .wrapMode(true)
                        .anime(SnailBar.Anime.SLIDE_FROM_SIDE)
                        .useExpandMode()
                        .icon(R.mipmap.ic_launcher_round)
                        .show();
                n++;
            }
        });
    }
}
