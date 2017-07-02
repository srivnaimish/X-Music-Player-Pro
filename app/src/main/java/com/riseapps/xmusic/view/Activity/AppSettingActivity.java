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
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private int[] images;
    private String[] texts;

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
        images = new int[]{R.drawable.theme1,R.drawable.theme2,R.drawable.theme4,R.drawable.theme5,R.drawable.theme8,
        R.drawable.theme7,R.drawable.theme6,R.drawable.theme3};

        texts = new String[]{"Theme 1","Theme 2","Theme 3","Theme 4","Theme 5","Theme 6","Theme 7","Theme 8"};
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
       ViewPager viewpager= (ViewPager) dialog.findViewById(R.id.view_pager);
       MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter();
       viewpager.setAdapter(myViewPagerAdapter);
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

    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = layoutInflater.inflate(R.layout.pager_item, container, false);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
            imageView.setImageResource(images[position]);

            TextView textView = (TextView) itemView.findViewById(R.id.text);
            textView.setText(texts[position]);

            ImageView tick = (ImageView) itemView.findViewById(R.id.tick);
            int x=sharedPreferenceSingelton.getSavedInt(AppSettingActivity.this,"Theme");
            if(x==position){
                tick.setImageResource(R.drawable.ic_check);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(position==0){
                        sharedPreferenceSingelton.saveAs(AppSettingActivity.this,"Theme",0);
                    }else if(position==1){
                        sharedPreferenceSingelton.saveAs(AppSettingActivity.this,"Theme",1);
                    }else if(position==2){
                        sharedPreferenceSingelton.saveAs(AppSettingActivity.this,"Theme",2);
                    }else if(position==3){
                        sharedPreferenceSingelton.saveAs(AppSettingActivity.this,"Theme",3);
                    }else if(position==4){
                        sharedPreferenceSingelton.saveAs(AppSettingActivity.this,"Theme",4);
                    }else if(position==5){
                        sharedPreferenceSingelton.saveAs(AppSettingActivity.this,"Theme",5);
                    }else if(position==6){
                        sharedPreferenceSingelton.saveAs(AppSettingActivity.this,"Theme",6);
                    }else if(position==7){
                        sharedPreferenceSingelton.saveAs(AppSettingActivity.this,"Theme",7);
                    }
                    dialog.dismiss();
                    recreate();
                    /*finish();
                    Intent intent = IntentCompat.makeMainActivity(new ComponentName(
                            AppSettingActivity.this, SplashScreen.class));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);*/
                }
            });

            container.addView(itemView);

            return itemView;
        }

        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

}
