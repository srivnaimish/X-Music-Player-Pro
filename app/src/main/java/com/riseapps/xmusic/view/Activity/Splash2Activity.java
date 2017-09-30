package com.riseapps.xmusic.view.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.riseapps.xmusic.R;
import com.riseapps.xmusic.component.SharedPreferenceSingelton;
import com.riseapps.xmusic.component.ThemeSelector;

public class Splash2Activity extends AppCompatActivity {

    private SharedPreferenceSingelton sharedPreferenceSingleton=new SharedPreferenceSingelton();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash2);

        if (sharedPreferenceSingleton.getSavedInt(this, "Themes") == 8) {
            ThemeSelector.theme_like_drawable=R.drawable.ic_harry_potter;
        } else if (sharedPreferenceSingleton.getSavedInt(this, "Themes") == 9) {
            ThemeSelector.theme_like_drawable=R.drawable.ic_batman;
        } else if (new SharedPreferenceSingelton().getSavedInt(this, "Themes") == 10) {
            ThemeSelector.theme_like_drawable=R.drawable.ic_iron_man;
        } else if (new SharedPreferenceSingelton().getSavedInt(this, "Themes") == 11) {
            ThemeSelector.theme_like_drawable=R.drawable.ic_deadpool;
        }else if (new SharedPreferenceSingelton().getSavedInt(this, "Themes") == 12) {
            ThemeSelector.theme_like_drawable=R.drawable.ic_inception;
        }else {
            ThemeSelector.theme_like_drawable=R.drawable.ic_like;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (new SharedPreferenceSingelton().getSavedBoolean(Splash2Activity.this, "opened_before")) {
                    startActivity(new Intent(Splash2Activity.this, MainActivity.class));
                    overridePendingTransition(R.anim.real_fade_in, R.anim.real_fade_out);
                    finish();
                } else {
                    startActivity(new Intent(Splash2Activity.this, Walkthrough.class));
                    overridePendingTransition(R.anim.real_fade_in, R.anim.real_fade_out);
                    finish();
                }
            }
        }, 1000);


    }

}
