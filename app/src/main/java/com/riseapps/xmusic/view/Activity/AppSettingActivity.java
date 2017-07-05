package com.riseapps.xmusic.view.Activity;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.jesusm.holocircleseekbar.lib.HoloCircleSeekBar;
import com.riseapps.xmusic.R;
import com.riseapps.xmusic.component.AppConstants;
import com.riseapps.xmusic.component.SharedPreferenceSingelton;
import com.riseapps.xmusic.billing.IabHelper;
import com.riseapps.xmusic.billing.IabResult;
import com.riseapps.xmusic.billing.Inventory;
import com.riseapps.xmusic.billing.Purchase;
import com.riseapps.xmusic.component.ThemeSelector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppSettingActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private static final String TAG = "In-App Billing";

    IabHelper mHelper;

    boolean billinSupported = false;

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
    LinearLayout theme_dialog;
    int buttonId[] = {R.id.bt1, R.id.bt2, R.id.bt3, R.id.bt4, R.id.bt5, R.id.bt6, R.id.bt7, R.id.bt8};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferenceSingelton = new SharedPreferenceSingelton();

        new ThemeSelector().setAppTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        init();

        String output=AppConstants.decrypt(AppConstants.encrypted);
        mHelper = new IabHelper(this, output);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.d(TAG, "In-app billing failed " + result);
                    billinSupported = false;
                } else {
                    Log.d(TAG, "In-app billing setup OK ");
                    billinSupported = true;
                    if (mHelper == null) return;
                    List<String> st = new ArrayList<String>();
                    st.addAll(Arrays.asList(AppConstants.ITEM_SKU).subList(2, 8));
                    try {
                        mHelper.queryInventoryAsync(true, st, mGotInventoryListener);
                    } catch (IabHelper.IabAsyncInProgressException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
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
        short_time = (TextView) findViewById(R.id.time_for_short_music);
        previous_set = sharedPreferenceSingelton.getSavedInt(this, "Short_music_time");
        String time = previous_set + " seconds";
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
    public void changeTheme(View v) {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.theme_select_dialog);
        ViewPager viewpager = (ViewPager) dialog.findViewById(R.id.view_pager);
        theme_dialog = (LinearLayout) dialog.findViewById(R.id.theme_dialog);
        viewpager.addOnPageChangeListener(viewPagerPageChangeListener);
        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter();
        viewpager.setAdapter(myViewPagerAdapter);
        dialog.show();
    }

    public void changeSkipieSwitch(View v) {
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
        final CardView fifteen = (CardView) dialog.findViewById(R.id.fifteen);
        final CardView thirty = (CardView) dialog.findViewById(R.id.thirty);
        final CardView fortyfive = (CardView) dialog.findViewById(R.id.fortyfive);
        final CardView sixty = (CardView) dialog.findViewById(R.id.sixty);
        min = (EditText) dialog.findViewById(R.id.minutes);
        final Button done = (Button) dialog.findViewById(R.id.done);
        final Button cancel = (Button) dialog.findViewById(R.id.cancel);
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == fifteen.getId())
                    setTimer(15);
                else if (v.getId() == thirty.getId())
                    setTimer(30);
                else if (v.getId() == fortyfive.getId())
                    setTimer(45);
                else if (v.getId() == sixty.getId())
                    setTimer(60);
                else if (v.getId() == done.getId()) {
                    String minutes = min.getText().toString();
                    if (minutes.equalsIgnoreCase("")) {
                        Toast.makeText(AppSettingActivity.this, getString(R.string.Invalid_Time_Toast), Toast.LENGTH_SHORT).show();
                    } else if (minutes.equalsIgnoreCase("0")) {
                        Toast.makeText(AppSettingActivity.this, getString(R.string.Invalid_Time_Toast), Toast.LENGTH_SHORT).show();
                    } else {
                        int m = Integer.parseInt(minutes);
                        setTimer(m);
                    }
                } else if (v.getId() == cancel.getId())
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

    void setTimer(int minutes) {
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

    public void hide_short(View v) {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.short_music_hide_dialog);
        dialog.show();
        Button done = (Button) dialog.findViewById(R.id.done);
        Button cancel = (Button) dialog.findViewById(R.id.cancel);
        seekBar = (HoloCircleSeekBar) dialog.findViewById(R.id.seekBar);
        previous_set = sharedPreferenceSingelton.getSavedInt(this, "Short_music_time");
        if (previous_set != 0) {
            seekBar.setValue(previous_set);
        }
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time = seekBar.getValue() + " Seconds";
                short_time.setText(time);
                sharedPreferenceSingelton.saveAs(AppSettingActivity.this, "Short_music_time", seekBar.getValue());
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
            CardView cardView = (CardView) itemView.findViewById(R.id.card);
            cardView.setCardBackgroundColor(getResources().getColor(AppConstants.backgroundColors[position]));

            TextView textView = (TextView) itemView.findViewById(R.id.text);
            textView.setText(AppConstants.texts[position]);
            textView.setTextColor(getResources().getColor(AppConstants.textColors[position]));
            ImageView tick = (ImageView) itemView.findViewById(R.id.tick);

            Button button = (Button) itemView.findViewById(R.id.button);
            Button buyButton = (Button) itemView.findViewById(R.id.buttonBuy);
            int x = sharedPreferenceSingelton.getSavedInt(AppSettingActivity.this, "Themes");
            if (x == position) {
                tick.setImageResource(R.drawable.ic_check);
            }

            if ((position == 2 && !AppConstants.theme3) || (position == 3 && !AppConstants.theme4) || (position == 4 && !AppConstants.theme5) ||
                    (position == 5 && !AppConstants.theme6) || (position == 6 && !AppConstants.theme7) || (position == 7 && !AppConstants.theme8)) {
                button.setVisibility(View.GONE);
                buyButton.setVisibility(View.VISIBLE);
            }
            buyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (billinSupported) {
                        try {
                            mHelper.launchPurchaseFlow(AppSettingActivity.this, AppConstants.ITEM_SKU[position], 10001, mPurchaseFinishedListener, "mypurchaseToken");
                        } catch (IabHelper.IabAsyncInProgressException e) {
                            e.printStackTrace();
                        }
                    } else
                        Toast.makeText(AppSettingActivity.this, "Billing Not Supported on Your Device", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position == 0) {
                        sharedPreferenceSingelton.saveAs(AppSettingActivity.this, "Themes", 0);
                    } else if (position == 1) {
                        sharedPreferenceSingelton.saveAs(AppSettingActivity.this, "Themes", 1);
                    } else if (position == 2) {
                        sharedPreferenceSingelton.saveAs(AppSettingActivity.this, "Themes", 2);
                    } else if (position == 3) {
                        sharedPreferenceSingelton.saveAs(AppSettingActivity.this, "Themes", 3);
                    } else if (position == 4) {
                        sharedPreferenceSingelton.saveAs(AppSettingActivity.this, "Themes", 4);
                    } else if (position == 5) {
                        sharedPreferenceSingelton.saveAs(AppSettingActivity.this, "Themes", 5);
                    } else if (position == 6) {
                        sharedPreferenceSingelton.saveAs(AppSettingActivity.this, "Themes", 6);
                    } else if (position == 7) {
                        sharedPreferenceSingelton.saveAs(AppSettingActivity.this, "Themes", 7);
                    }
                    dialog.dismiss();
                    // recreate();
                    finish();
                    Intent intent = IntentCompat.makeMainActivity(new ComponentName(
                            AppSettingActivity.this, MainActivity.class));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });

            container.addView(itemView);

            return itemView;
        }

        @Override
        public int getCount() {
            return AppConstants.backgroundColors.length;
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

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            theme_dialog.setBackgroundColor(getResources().getColor(AppConstants.backgroundColors[position]));
            for (int i = 0; i < AppConstants.backgroundColors.length; i++) {
                dialog.findViewById(buttonId[i]).setBackground(getResources().getDrawable(R.drawable.walkthrough_unselected));
            }
            dialog.findViewById(buttonId[position]).setBackground(getResources().getDrawable(R.drawable.walkthrough_selected));
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mHelper.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        @Override
        public void onIabPurchaseFinished(IabResult result, Purchase info) {
            if (result.isFailure()) {
                Toast.makeText(AppSettingActivity.this, getString(R.string.aborted), Toast.LENGTH_SHORT).show();
                return;
            }
            if (info.getSku().equalsIgnoreCase(AppConstants.ITEM_SKU[2])) {
                AppConstants.theme3 = true;
                Toast.makeText(AppSettingActivity.this, getString(R.string.thanks), Toast.LENGTH_SHORT).show();
            } else if (info.getSku().equalsIgnoreCase(AppConstants.ITEM_SKU[3])) {
                AppConstants.theme4 = true;
                Toast.makeText(AppSettingActivity.this, getString(R.string.thanks), Toast.LENGTH_SHORT).show();
            } else if (info.getSku().equalsIgnoreCase(AppConstants.ITEM_SKU[4])) {
                AppConstants.theme5 = true;
                Toast.makeText(AppSettingActivity.this, getString(R.string.thanks), Toast.LENGTH_SHORT).show();
            } else if (info.getSku().equalsIgnoreCase(AppConstants.ITEM_SKU[5])) {
                AppConstants.theme6 = true;
                Toast.makeText(AppSettingActivity.this, getString(R.string.thanks), Toast.LENGTH_SHORT).show();
            } else if (info.getSku().equalsIgnoreCase(AppConstants.ITEM_SKU[6])) {
                AppConstants.theme7 = true;
                Toast.makeText(AppSettingActivity.this, getString(R.string.thanks), Toast.LENGTH_SHORT).show();
            } else if (info.getSku().equalsIgnoreCase(AppConstants.ITEM_SKU[7])) {
                AppConstants.theme8 = true;
                Toast.makeText(AppSettingActivity.this, getString(R.string.thanks), Toast.LENGTH_SHORT).show();
            }
        }
    };

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {

            if (mHelper == null) return;

            if (result.isFailure()) {
                return;
            }

            if (inventory.hasPurchase(AppConstants.ITEM_SKU[2])) {
                AppConstants.theme3 = true;
            }
            if (inventory.hasPurchase(AppConstants.ITEM_SKU[3])) {
                AppConstants.theme4 = true;
            }
            if (inventory.hasPurchase(AppConstants.ITEM_SKU[4])) {
                AppConstants.theme5 = true;
            }
            if (inventory.hasPurchase(AppConstants.ITEM_SKU[5])) {
                AppConstants.theme6 = true;
            }
            if (inventory.hasPurchase(AppConstants.ITEM_SKU[6])) {
                AppConstants.theme7 = true;
            }
            if (inventory.hasPurchase(AppConstants.ITEM_SKU[7])) {
                AppConstants.theme8 = true;
            }

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (billinSupported) {
            if (mHelper != null) try {
                mHelper.dispose();
            } catch (IabHelper.IabAsyncInProgressException e) {
                e.printStackTrace();
            }
            mHelper = null;
        }
    }
}
