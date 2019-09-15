package com.github.coxylicacid.md2demo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

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
                SnailBar.make(MainActivity.this, "Emmmmmmmmmmm", SnailBar.LENGTH_SHORT)
                        .attachToFab(fab)
                        .wrapMode(true)
                        .anime(SnailBar.Anime.SLIDE_FROM_SIDE)
                        .icon(R.mipmap.ic_launcher_round)
                        .show();
                n++;
            }
        });
    }
}
