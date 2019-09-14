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
                String msg = "Hello World!\n\n" + "Hello World!\n\n" + "Hello World!\n\n" + "Hello World!\n\n"
                        + "Hello World!\n\n" + "Hello World!\n\n" + "Hello World!\n\n" + "Hello World!\n\n";

                SnailBar.make(MainActivity.this, msg, SnailBar.LENGTH_SHORT)
                        .gravity(SnailBar.Gravity.BOTTOM)
                        .anime(SnailBar.Anime.CIRCULAR_REVEAL)
                        .wrapMode(true)
                        .useExpandMode()
                        .icon(R.mipmap.ic_launcher_round)
                        .action("??", new SnailBar.SnailBarActionListenerAdapter() {
                            @Override
                            public void onClick(View view, SnailBar snailBar) {
                                snailBar.msg("What?");
                            }
                        }).show();
                n++;
            }
        });
    }
}
