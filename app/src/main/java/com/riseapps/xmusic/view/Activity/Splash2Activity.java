package com.riseapps.xmusic.view.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.riseapps.xmusic.R;
import com.riseapps.xmusic.component.SharedPreferenceSingelton;

public class Splash2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash2);
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
