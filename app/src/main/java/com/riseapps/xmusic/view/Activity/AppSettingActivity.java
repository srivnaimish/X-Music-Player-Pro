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
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.jesusm.holocircleseekbar.lib.HoloCircleSeekBar;
import com.riseapps.xmusic.R;
import com.riseapps.xmusic.component.SharedPreferenceSingelton;
import com.riseapps.xmusic.model.MusicService;

public class AppSettingActivity extends AppCompatActivity{

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private Dialog dialog;
    private EditText min;
    private static EqualizerPresetListener equalizerPresetListener;
    private RadioGroup radioButtonGroup;
    SharedPreferenceSingelton sharedPreferenceSingelton;
    CoordinatorLayout back;
    Switch pro;
    private TextView short_time;
    private int previous_set;
    private HoloCircleSeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferenceSingelton = new SharedPreferenceSingelton();
        if (sharedPreferenceSingelton.getSavedInt(this,"Theme")==1) {
            setTheme(R.style.AppTheme_Dark);
        }else if (sharedPreferenceSingelton.getSavedInt(this,"Theme")==2) {
            setTheme(R.style.AppTheme_Dark2);
        }
        else if (sharedPreferenceSingelton.getSavedInt(this,"Theme")==3) {
            setTheme(R.style.AppTheme_Dark3);
        }
        else if (sharedPreferenceSingelton.getSavedInt(this,"Theme")==4) {
            setTheme(R.style.AppTheme_Dark4);
        }
        else if (sharedPreferenceSingelton.getSavedInt(this,"Theme")==5) {
            setTheme(R.style.AppTheme_Dark5);
        }
        else if (sharedPreferenceSingelton.getSavedInt(this,"Theme")==6) {
            setTheme(R.style.AppTheme_Dark6);
        }
        else if (sharedPreferenceSingelton.getSavedInt(this,"Theme")==7) {
            setTheme(R.style.AppTheme_Dark7);
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
                AppSettingActivity.this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        CardView setting_pro = (CardView) findViewById(R.id.setting_shake);
        back = (CoordinatorLayout) findViewById(R.id.back);
        if (!this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_PROXIMITY)) {
            setting_pro.setVisibility(View.GONE);
        }
        pro = (Switch) findViewById(R.id.setting_pro);
        short_time= (TextView) findViewById(R.id.time_for_short_music);
        previous_set=sharedPreferenceSingelton.getSavedInt(this,"Short_music_time");
        String time=previous_set+" seconds";
        short_time.setText(time);
        
        if (sharedPreferenceSingelton.getSavedBoolean(this, "Pro_Controls"))
            pro.setChecked(true);
        else
            pro.setChecked(false);

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

    }

   /* */
   public void changeTheme(View v){
       dialog=new Dialog(this);
       dialog.setContentView(R.layout.theme_select_dialog);
       dialog.show();
       ImageView tick1= (ImageView) dialog.findViewById(R.id.tick1);
       ImageView tick2= (ImageView) dialog.findViewById(R.id.tick2);
       ImageView tick3= (ImageView) dialog.findViewById(R.id.tick3);
       ImageView tick4= (ImageView) dialog.findViewById(R.id.tick4);
       ImageView tick5= (ImageView) dialog.findViewById(R.id.tick5);
       ImageView tick6= (ImageView) dialog.findViewById(R.id.tick6);
       ImageView tick7= (ImageView) dialog.findViewById(R.id.tick7);
       ImageView tick8= (ImageView) dialog.findViewById(R.id.tick8);

       final CardView cardView1= (CardView) dialog.findViewById(R.id.theme1);
       final CardView cardView2= (CardView) dialog.findViewById(R.id.theme2);
       final CardView cardView3= (CardView) dialog.findViewById(R.id.theme3);
       final CardView cardView4= (CardView) dialog.findViewById(R.id.theme4);
       final CardView cardView5= (CardView) dialog.findViewById(R.id.theme5);
       final CardView cardView6= (CardView) dialog.findViewById(R.id.theme6);
       final CardView cardView7= (CardView) dialog.findViewById(R.id.theme7);
       final CardView cardView8= (CardView) dialog.findViewById(R.id.theme8);

       if (sharedPreferenceSingelton.getSavedInt(this,"Theme")==0){
           tick1.setImageResource(R.drawable.ic_check);
       }else if (sharedPreferenceSingelton.getSavedInt(this,"Theme")==1){
           tick2.setImageResource(R.drawable.ic_check);
       }else if (sharedPreferenceSingelton.getSavedInt(this,"Theme")==2){
           tick3.setImageResource(R.drawable.ic_check);
       }else if (sharedPreferenceSingelton.getSavedInt(this,"Theme")==3){
           tick4.setImageResource(R.drawable.ic_check);
       }else if (sharedPreferenceSingelton.getSavedInt(this,"Theme")==4){
           tick5.setImageResource(R.drawable.ic_check);
       }else if (sharedPreferenceSingelton.getSavedInt(this,"Theme")==5){
           tick6.setImageResource(R.drawable.ic_check);
       }else if (sharedPreferenceSingelton.getSavedInt(this,"Theme")==6){
           tick7.setImageResource(R.drawable.ic_check);
       }else if (sharedPreferenceSingelton.getSavedInt(this,"Theme")==7){
           tick8.setImageResource(R.drawable.ic_check);
       }
       View.OnClickListener clickListener=new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(v.getId()==cardView1.getId()){
                   sharedPreferenceSingelton.saveAs(AppSettingActivity.this,"Theme",0);
               }else if(v.getId()==cardView2.getId()){
                   sharedPreferenceSingelton.saveAs(AppSettingActivity.this,"Theme",1);
               }else if(v.getId()==cardView3.getId()){
                   sharedPreferenceSingelton.saveAs(AppSettingActivity.this,"Theme",2);
               }else if(v.getId()==cardView4.getId()){
                   sharedPreferenceSingelton.saveAs(AppSettingActivity.this,"Theme",3);
               }else if(v.getId()==cardView5.getId()){
                   sharedPreferenceSingelton.saveAs(AppSettingActivity.this,"Theme",4);
               }else if(v.getId()==cardView6.getId()){
                   sharedPreferenceSingelton.saveAs(AppSettingActivity.this,"Theme",5);
               }else if(v.getId()==cardView7.getId()){
                   sharedPreferenceSingelton.saveAs(AppSettingActivity.this,"Theme",6);
               }else if(v.getId()==cardView8.getId()){
                   sharedPreferenceSingelton.saveAs(AppSettingActivity.this,"Theme",7);
               }
               dialog.dismiss();
               finish();
               Intent intent = IntentCompat.makeMainActivity(new ComponentName(
                       AppSettingActivity.this, SplashScreen.class));
               intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
               startActivity(intent);
           }
       };
       cardView1.setOnClickListener(clickListener);
       cardView2.setOnClickListener(clickListener);
       cardView3.setOnClickListener(clickListener);
       cardView4.setOnClickListener(clickListener);
       cardView5.setOnClickListener(clickListener);
       cardView6.setOnClickListener(clickListener);
       cardView7.setOnClickListener(clickListener);
       cardView8.setOnClickListener(clickListener);
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
        final CardView fifteen= (CardView) dialog.findViewById(R.id.fifteen);
        final CardView thirty= (CardView) dialog.findViewById(R.id.thirty);
        final CardView fortyfive= (CardView) dialog.findViewById(R.id.fortyfive);
        final CardView sixty= (CardView) dialog.findViewById(R.id.sixty);
        min = (EditText) dialog.findViewById(R.id.minutes);
        final Button done = (Button) dialog.findViewById(R.id.done);
        final Button cancel = (Button) dialog.findViewById(R.id.cancel);
        View.OnClickListener clickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId()==fifteen.getId())
                    setTimer(15);
                else if(v.getId()==thirty.getId())
                    setTimer(30);
                else if(v.getId()==fortyfive.getId())
                    setTimer(45);
                else if(v.getId()==sixty.getId())
                    setTimer(60);
                else if(v.getId()==done.getId()){
                    String minutes=min.getText().toString();
                    if(minutes.equalsIgnoreCase("")){
                        Toast.makeText(AppSettingActivity.this, getString(R.string.Invalid_Time_Toast), Toast.LENGTH_SHORT).show();
                    }else if (minutes.equalsIgnoreCase("0")) {
                        Toast.makeText(AppSettingActivity.this, getString(R.string.Invalid_Time_Toast), Toast.LENGTH_SHORT).show();
                    } else {
                        int m=Integer.parseInt(minutes);
                        setTimer(m);
                    }
                }
                else if(v.getId()==cancel.getId())
                    dialog.dismiss();
            }
        };
        fifteen.setOnClickListener(clickListener);
        thirty.setOnClickListener(clickListener);
        fortyfive.setOnClickListener(clickListener);
        sixty.setOnClickListener(clickListener);
        done.setOnClickListener(clickListener);
        cancel.setOnClickListener(clickListener);

    }

    void setTimer(int minutes){
        long d = System.currentTimeMillis() + (minutes * 60 * 1000);
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

    public void hide_short(View v){
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.short_music_hide_dialog);
        dialog.show();
        Button done = (Button) dialog.findViewById(R.id.done);
        Button cancel = (Button) dialog.findViewById(R.id.cancel);
        seekBar= (HoloCircleSeekBar) dialog.findViewById(R.id.seekBar);
        previous_set=sharedPreferenceSingelton.getSavedInt(this,"Short_music_time");
        if(previous_set!=0){
            seekBar.setValue(previous_set);
        }
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time=seekBar.getValue()+" Seconds";
                short_time.setText(time);
                sharedPreferenceSingelton.saveAs(AppSettingActivity.this,"Short_music_time",seekBar.getValue());
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


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
