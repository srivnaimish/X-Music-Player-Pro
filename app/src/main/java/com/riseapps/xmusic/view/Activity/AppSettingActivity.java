package com.riseapps.xmusic.view.Activity;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.riseapps.xmusic.R;
import com.riseapps.xmusic.component.SharedPreferenceSingelton;

public class AppSettingActivity extends AppCompatActivity implements View.OnClickListener {

    private Switch pro;
    private Dialog dialog;
    private EditText min,hrs;
    private Toolbar mToolbar;
    private CoordinatorLayout coordinatorLayout;
    private CardView setting_equalizer,setting_sleep,setting_theme,setting_share,setting_rate,setting_pro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       // setTheme(R.style.AppTheme_Dark);
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        init();

       // switchToDarkTheme();
    }

    public int getLayoutId() {
        return R.layout.activity_app_setting;
    }

    private void init() {

        // Toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setting_pro= (CardView) findViewById(R.id.setting_shake);
        setting_equalizer = (CardView) findViewById(R.id.setting_equalizer);
        setting_sleep = (CardView) findViewById(R.id.setting_sleep);
        setting_theme = (CardView) findViewById(R.id.setting_theme);
        setting_share = (CardView) findViewById(R.id.setting_share_app);
        setting_rate = (CardView) findViewById(R.id.setting_rate);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.back);

        pro = (Switch) findViewById(R.id.setting_pro);
        if(new SharedPreferenceSingelton().getSavedBoolean(this,"Pro_Controls"))
            pro.setChecked(true);
        else
            pro.setChecked(false);
        pro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(new SharedPreferenceSingelton().getSavedBoolean(AppSettingActivity.this,"Pro_Controls")) {
                    new SharedPreferenceSingelton().saveAs(AppSettingActivity.this,"Pro_Controls",false);
                    Toast.makeText(AppSettingActivity.this, "Pro Controls Deactivated", Toast.LENGTH_SHORT).show();
                }
                else {
                    new SharedPreferenceSingelton().saveAs(AppSettingActivity.this,"Pro_Controls",true);
                    Toast.makeText(AppSettingActivity.this, "Pro Controls Activated", Toast.LENGTH_SHORT).show();
                }
            }
        });

        setting_equalizer.setOnClickListener(this);
        setting_sleep.setOnClickListener(this);
        setting_theme.setOnClickListener(this);
        setting_share.setOnClickListener(this);
        setting_rate.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.setting_equalizer:

                break;

            case R.id.setting_sleep:
                openSleepDialog();
                break;

            case R.id.setting_theme:

                break;

            case R.id.setting_share_app:

                break;

            case R.id.setting_rate:

                break;

        }
    }

    private void openSleepDialog(){
        dialog=new Dialog(this);
        dialog.setContentView(R.layout.sleep_timer_dialog);
        dialog.show();
        hrs= (EditText) dialog.findViewById(R.id.hours);
        min= (EditText) dialog.findViewById(R.id.minutes);
        Button done= (Button) dialog.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long d=0;
                String h=hrs.getText().toString();
                String m=min.getText().toString();
                if((h+m).equalsIgnoreCase("00")||(h+m).equalsIgnoreCase("0")||(h+m).equalsIgnoreCase("")){
                    Toast.makeText(AppSettingActivity.this, "Invalid Time", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    d=System.currentTimeMillis()+(Integer.parseInt(h)*60*60*1000)+(Integer.parseInt(m)*60*1000);
                    Intent intent = new Intent("Stop");
                    PendingIntent pi = PendingIntent.getBroadcast(AppSettingActivity.this, 5, intent, 0);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, d, pi);
                    else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, d, pi);
                    dialog.dismiss();
                }

            }
        });
        Button cancel=(Button) dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }


}
