package com.riseapps.xmusic.view.Activity;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.DragEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.riseapps.xmusic.R;
import com.riseapps.xmusic.component.SharedPreferenceSingelton;
import com.riseapps.xmusic.model.MusicService;

public class AppSettingActivity extends AppCompatActivity{

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private Dialog dialog;
    private EditText min, hrs;
    private static EqualizerPresetListener equalizerPresetListener;
    private RadioGroup radioButtonGroup;
    SharedPreferenceSingelton sharedPreferenceSingelton;
    CoordinatorLayout back;
    Switch theme,pro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferenceSingelton = new SharedPreferenceSingelton();
        if (sharedPreferenceSingelton.getSavedBoolean(AppSettingActivity.this, "Theme")) {
            setTheme(R.style.AppTheme_Dark);
        }
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        init();
    }

    public int getLayoutId() {
        return R.layout.activity_app_setting;
    }

    private void init() {
        // Toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        CardView setting_pro = (CardView) findViewById(R.id.setting_shake);
        back = (CoordinatorLayout) findViewById(R.id.back);
        if (!this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_PROXIMITY)) {
            setting_pro.setVisibility(View.GONE);
        }
        pro = (Switch) findViewById(R.id.setting_pro);
        if (sharedPreferenceSingelton.getSavedBoolean(this, "Pro_Controls"))
            pro.setChecked(true);
        else
            pro.setChecked(false);

        theme = (Switch) findViewById(R.id.switch_theme);
        if (sharedPreferenceSingelton.getSavedBoolean(this, "Theme"))
            theme.setChecked(true);
        else
            theme.setChecked(false);

        pro.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (sharedPreferenceSingelton.getSavedBoolean(AppSettingActivity.this, "Pro_Controls")) {
                    sharedPreferenceSingelton.saveAs(AppSettingActivity.this, "Pro_Controls", false);
                    MainActivity.mSensorManager.unregisterListener(MainActivity.proximityDetector);
                    Toast.makeText(AppSettingActivity.this, "Deactivated", Toast.LENGTH_SHORT).show();
                } else {
                    sharedPreferenceSingelton.saveAs(AppSettingActivity.this, "Pro_Controls", true);
                    MainActivity.mSensorManager.registerListener(MainActivity.proximityDetector, MainActivity.mProximity, 2 * 1000 * 1000);
                    Toast.makeText(AppSettingActivity.this, "Activated", Toast.LENGTH_SHORT).show();
                }
            }
        });

        theme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (sharedPreferenceSingelton.getSavedBoolean(AppSettingActivity.this, "Theme")) {
                    sharedPreferenceSingelton.saveAs(AppSettingActivity.this, "Theme", false);
                } else {
                    sharedPreferenceSingelton.saveAs(AppSettingActivity.this, "Theme", true);
                }
                finish();
                final Intent intent = IntentCompat.makeMainActivity(new ComponentName(
                        AppSettingActivity.this, SplashScreen.class));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

    }

    public void changeThemeSwitch(View v){
        if (theme.isChecked()) {
            theme.setChecked(false);
        } else {
            theme.setChecked(true);
        }
    }

    public void changeSkipieSwitch(View v){
        if (pro.isChecked()) {
            pro.setChecked(false);
        } else {
            pro.setChecked(true);
        }
    }

    public void openSleepDialog(View v) {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.sleep_timer_dialog);
        dialog.show();
        hrs = (EditText) dialog.findViewById(R.id.hours);
        min = (EditText) dialog.findViewById(R.id.minutes);
        Button done = (Button) dialog.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long d = 0;
                String h = hrs.getText().toString();
                String m = min.getText().toString();
                if ((h + m).equalsIgnoreCase("00") || (h + m).equalsIgnoreCase("0") || (h + m).equalsIgnoreCase("")) {
                    Toast.makeText(AppSettingActivity.this, getString(R.string.Invalid_Time_Toast), Toast.LENGTH_SHORT).show();
                } else if (h.equalsIgnoreCase("") || m.equalsIgnoreCase("")) {
                    Toast.makeText(AppSettingActivity.this, getString(R.string.Invalid_Time_Toast), Toast.LENGTH_SHORT).show();
                } else {
                    d = System.currentTimeMillis() + (Integer.parseInt(h) * 60 * 60 * 1000) + (Integer.parseInt(m) * 60 * 1000);
                    Intent intent = new Intent("Stop");
                    PendingIntent pi = PendingIntent.getBroadcast(AppSettingActivity.this, 5, intent, 0);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                    alarmManager.cancel(pi);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, d, pi);
                    else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, d, pi);
                    else
                        alarmManager.set(AlarmManager.RTC_WAKEUP, d, pi);
                    dialog.dismiss();
                }

            }
        });
        Button cancel = (Button) dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    public void openEqualizerDialog(View v) {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.equalizer_dialog);
        dialog.show();
        radioButtonGroup = (RadioGroup) dialog.findViewById(R.id.radio_group);

        int x = sharedPreferenceSingelton.getSavedInt(AppSettingActivity.this, "Preset");
        if (x != 0) {
            radioButtonGroup.check((radioButtonGroup.getChildAt(x)).getId());
        }
        Button done = (Button) dialog.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int radioButtonID = radioButtonGroup.getCheckedRadioButtonId();
                View radioButton = radioButtonGroup.findViewById(radioButtonID);
                int idx = radioButtonGroup.indexOfChild(radioButton);
                equalizerPresetListener.OnEqualizerPresetChanged((short) idx);
                sharedPreferenceSingelton.saveAs(AppSettingActivity.this, "Preset", idx);
                dialog.dismiss();
            }
        });

    }

    public void shareAppLink(View v) {
        String message = "https://play.google.com/store/apps/details?id=com.riseapps.xmusic";
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(share, "Share via.."));
    }

    public void rateApp(View v) {
        final Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
        final Intent rateAppIntent = new Intent(Intent.ACTION_VIEW, uri);

        if (getPackageManager().queryIntentActivities(rateAppIntent, 0).size() > 0) {
            startActivity(rateAppIntent);
        }
    }

    public void helpUsTranslate(View v) {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.translation);
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public interface EqualizerPresetListener {
        void OnEqualizerPresetChanged(short value);
    }

    public static void setEqualizerPresetListener(EqualizerPresetListener listener) {   // Sets a callback to execute when we switch songs.. ie: update UI
        equalizerPresetListener = listener;
    }


}
