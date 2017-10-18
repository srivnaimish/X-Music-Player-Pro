package com.riseapps.xmusic.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;

import com.riseapps.xmusic.R;

public class SplashScreen extends AppCompatActivity {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this, Splash2Activity.class));
                overridePendingTransition(R.anim.real_fade_in, R.anim.real_fade_out);
                finish();
            }
        }, 400);
    }



}