package com.riseapps.xmusic.view.Activity;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;
import com.riseapps.xmusic.R;
import com.riseapps.xmusic.component.SharedPreferenceSingelton;
import com.riseapps.xmusic.model.MusicService;

public class AppSettingActivity extends AppCompatActivity implements View.OnClickListener {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    InterstitialAd interstitial;
    private Dialog dialog;
    private EditText min,hrs;
    private static EqualizerPresetListener equalizerPresetListener;
    private RadioGroup radioButtonGroup;
    private AdView mAdView;
    AdRequest adRequest;
    SharedPreferenceSingelton sharedPreferenceSingelton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        adRequest = new AdRequest.Builder().build();
        init();
        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId("ca-app-pub-2454061641779517/5252774182");
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-2454061641779517~3507282989");
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mAdView.setVisibility(View.VISIBLE);
                super.onAdLoaded();
            }
        });
    }

    public int getLayoutId() {
        return R.layout.activity_app_setting;
    }

    private void init() {
        // Toolbar
        sharedPreferenceSingelton=new SharedPreferenceSingelton();

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        CardView setting_pro = (CardView) findViewById(R.id.setting_shake);
        CardView setting_equalizer = (CardView) findViewById(R.id.setting_equalizer);
        CardView setting_sleep = (CardView) findViewById(R.id.setting_sleep);
        CardView setting_share_app = (CardView) findViewById(R.id.setting_share);
        CardView setting_unlock = (CardView) findViewById(R.id.setting_unlocked);
        CardView setting_rate = (CardView) findViewById(R.id.setting_rate);

        if (!this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_PROXIMITY)){
            setting_pro.setVisibility(View.GONE);
            //Toast.makeText(this, "Has", Toast.LENGTH_SHORT).show();
        }

        Switch pro = (Switch) findViewById(R.id.setting_pro);
        if(sharedPreferenceSingelton.getSavedBoolean(this,"Pro_Controls"))
            pro.setChecked(true);
        else
            pro.setChecked(false);
        pro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sharedPreferenceSingelton.getSavedBoolean(AppSettingActivity.this,"Pro_Controls")) {
                    sharedPreferenceSingelton.saveAs(AppSettingActivity.this,"Pro_Controls",false);
                    MainActivity.mSensorManager.unregisterListener(MainActivity.proximityDetector);
                    Toast.makeText(AppSettingActivity.this, "Pro Controls Deactivated", Toast.LENGTH_SHORT).show();
                }
                else {
                    sharedPreferenceSingelton.saveAs(AppSettingActivity.this,"Pro_Controls",true);
                    MainActivity.mSensorManager.registerListener(MainActivity.proximityDetector, MainActivity.mProximity, 2 * 1000 * 1000);
                    Toast.makeText(AppSettingActivity.this, "Pro Controls Activated", Toast.LENGTH_SHORT).show();
                }
            }
        });

        setting_equalizer.setOnClickListener(this);
        setting_sleep.setOnClickListener(this);
        setting_share_app.setOnClickListener(this);
        setting_unlock.setOnClickListener(this);
        setting_rate.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.setting_equalizer:

                interstitial.loadAd(adRequest);
                openEqualizerDialog();
                break;

            case R.id.setting_sleep:
                interstitial.loadAd(adRequest);
                openSleepDialog();
                break;

            case R.id.setting_share:
                shareAppLink();
                break;

            case R.id.setting_unlocked:
                gotoUnlockedVersion();
                break;

            case R.id.setting_rate:
                rateApp();
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
                    Toast.makeText(AppSettingActivity.this, getString(R.string.Invalid_Time_Toast), Toast.LENGTH_SHORT).show();
                }
                else if(h.equalsIgnoreCase("")||m.equalsIgnoreCase("")){
                    Toast.makeText(AppSettingActivity.this, getString(R.string.Invalid_Time_Toast), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    d=System.currentTimeMillis()+(Integer.parseInt(h)*60*60*1000)+(Integer.parseInt(m)*60*1000);
                    Intent intent = new Intent("Stop");
                    PendingIntent pi = PendingIntent.getBroadcast(AppSettingActivity.this, 5, intent, 0);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                    alarmManager.cancel(pi);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, d, pi);
                    else
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, d, pi);
                    dialog.dismiss();
                }
                if(interstitial.isLoaded())
                    showInterstitial();

            }
        });
        Button cancel=(Button) dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if(interstitial.isLoaded())
                    showInterstitial();
            }
        });

    }

    private void openEqualizerDialog(){
        dialog=new Dialog(this);
        dialog.setContentView(R.layout.equalizer_dialog);
        dialog.show();
        radioButtonGroup= (RadioGroup) dialog.findViewById(R.id.radio_group);

        int x=sharedPreferenceSingelton.getSavedInt(AppSettingActivity.this,"Preset");
        if(x!=0){
            radioButtonGroup.check((radioButtonGroup.getChildAt(x)).getId());
        }
        Button done= (Button) dialog.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int radioButtonID = radioButtonGroup.getCheckedRadioButtonId();
                View radioButton = radioButtonGroup.findViewById(radioButtonID);
                int idx = radioButtonGroup.indexOfChild(radioButton);
                equalizerPresetListener.OnEqualizerPresetChanged((short)idx);
                sharedPreferenceSingelton.saveAs(AppSettingActivity.this,"Preset",idx);
                dialog.dismiss();
                if(interstitial.isLoaded())
                    showInterstitial();
            }
        });

        //

    }

    private void gotoUnlockedVersion(){
        final Uri uri = Uri.parse("market://details?id=com.riseapps.xmusicunlocked");
        final Intent rateAppIntent = new Intent(Intent.ACTION_VIEW, uri);

        if (getPackageManager().queryIntentActivities(rateAppIntent, 0).size() > 0)
        {
            startActivity(rateAppIntent);
        }

    }

    private void shareAppLink(){
        String message = "https://play.google.com/store/apps/details?id=com.riseapps.xmusic";
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(share, "Share via.."));
    }

    private void rateApp() {
        final Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
        final Intent rateAppIntent = new Intent(Intent.ACTION_VIEW, uri);

        if (getPackageManager().queryIntentActivities(rateAppIntent, 0).size() > 0)
        {
            startActivity(rateAppIntent);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public interface EqualizerPresetListener{
        void OnEqualizerPresetChanged(short value);
    }

    public static void setEqualizerPresetListener(EqualizerPresetListener listener) {   // Sets a callback to execute when we switch songs.. ie: update UI
        equalizerPresetListener = listener;
    }

    private void showInterstitial() {
        if (interstitial.isLoaded()) {
            interstitial.show();
        }
    }

}
