package com.riseapps.xmusic.view.activity;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.jesusm.holocircleseekbar.lib.HoloCircleSeekBar;
import com.riseapps.xmusic.R;
import com.riseapps.xmusic.billing.IabHelper;
import com.riseapps.xmusic.billing.IabResult;
import com.riseapps.xmusic.billing.Inventory;
import com.riseapps.xmusic.billing.Purchase;
import com.riseapps.xmusic.component.AppConstants;
import com.riseapps.xmusic.component.SharedPreferenceSingelton;
import com.riseapps.xmusic.component.ThemeSelector;
import com.riseapps.xmusic.executor.RecycleViewAdapters.FoldersAdapter;
import com.riseapps.xmusic.model.Pojo.PlaylistSelect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppSettingActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    Button buy;

    private static final String TAG = "In-App Billing";
    IabHelper mHelper;

    boolean billinSupported = false;

    private Dialog dialog;
    private EditText min;
    SharedPreferenceSingelton sharedPreferenceSingelton;
    CoordinatorLayout back;
    Switch pro;
    private TextView short_time;
    private int previous_set;
    private HoloCircleSeekBar seekBar;
    CardView theme_dialog;

    private ArrayList<PlaylistSelect> folders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferenceSingelton = new SharedPreferenceSingelton();

        new ThemeSelector().setAppTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        init();

        String output = AppConstants.decrypt(AppConstants.encrypted);
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
                    st.addAll(Arrays.asList(AppConstants.ITEM_SKU).subList(2, 9));
                    try {
                        mHelper.queryInventoryAsync(true, st, mGotInventoryListener);
                    } catch (IabHelper.IabAsyncInProgressException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        theme_dialog = findViewById(R.id.theme_dialog);
        int x = sharedPreferenceSingelton.getSavedInt(AppSettingActivity.this, "Themes");
        switch (x){
            case 0:
                ImageView im=findViewById(AppConstants.checkImages[0]);
                im.setVisibility(View.VISIBLE);
                break;
            case 1:
                im=findViewById(AppConstants.checkImages[1]);
                im.setVisibility(View.VISIBLE);
                break;
            case 2:
                im=findViewById(AppConstants.checkImages[7]);
                im.setVisibility(View.VISIBLE);
                break;
            case 3:
                im=findViewById(AppConstants.checkImages[4]);
                im.setVisibility(View.VISIBLE);
                break;
            case 4:
                im=findViewById(AppConstants.checkImages[3]);
                im.setVisibility(View.VISIBLE);
                break;
            case 5:
                im=findViewById(AppConstants.checkImages[5]);
                im.setVisibility(View.VISIBLE);
                break;
            case 6:
                im=findViewById(AppConstants.checkImages[2]);
                im.setVisibility(View.VISIBLE);
                break;
            case 7:
                im=findViewById(AppConstants.checkImages[6]);
                im.setVisibility(View.VISIBLE);
                break;
        }
        buy=findViewById(R.id.buttonBuy);
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

        short_time = (TextView) findViewById(R.id.time_for_short_music);
        previous_set = sharedPreferenceSingelton.getSavedInt(this, "Short_music_time");
        String time = previous_set + " seconds";
        short_time.setText(time);

    }

    public void changeTheme(View v) {
        theme_dialog.startAnimation(AnimationUtils.loadAnimation(this,android.R.anim.slide_in_left));
        theme_dialog.setVisibility(View.VISIBLE);
    }

    public void changeMovieTheme(View v) {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.movie_theme_dialog);
        try {
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        } catch (Exception e) {
        }
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.harry:
                        sharedPreferenceSingelton.saveAs(AppSettingActivity.this, "Themes", 8);
                        break;
                    case R.id.batman:
                        sharedPreferenceSingelton.saveAs(AppSettingActivity.this, "Themes", 9);
                        break;
                    case R.id.iron:
                        sharedPreferenceSingelton.saveAs(AppSettingActivity.this, "Themes", 10);
                        break;
                    case R.id.deadpool:
                        sharedPreferenceSingelton.saveAs(AppSettingActivity.this, "Themes", 11);
                        break;

                }
                dialog.dismiss();
                finish();
                Intent intent = IntentCompat.makeMainActivity(new ComponentName(
                        AppSettingActivity.this, Splash2Activity.class));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        };
        dialog.findViewById(R.id.harry).setOnClickListener(clickListener);
        dialog.findViewById(R.id.batman).setOnClickListener(clickListener);
        dialog.findViewById(R.id.iron).setOnClickListener(clickListener);
        dialog.findViewById(R.id.deadpool).setOnClickListener(clickListener);
        try {
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        } catch (NullPointerException e) {
        }
        dialog.show();

    }

    public void openSleepDialog(View v) {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.sleep_timer_dialog);
        try {
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        } catch (NullPointerException e) {
        }
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

    public void hide_short(View v) {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.short_music_hide_dialog);
        try {
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        } catch (NullPointerException e) {
            Log.d("NullPointer", "yes");
        }
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
        if (theme_dialog.getVisibility() == View.VISIBLE) {
            theme_dialog.startAnimation(AnimationUtils.loadAnimation(this,android.R.anim.slide_out_right));
            theme_dialog.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }

    }

    public void openFolderDialog(View view) {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.folder_select);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        try {
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        } catch (NullPointerException e) {
        }
        dialog.show();

        Button done = (Button) dialog.findViewById(R.id.done);
        Button cancel = (Button) dialog.findViewById(R.id.cancel);

        RecyclerView recyclerView;
        FoldersAdapter foldersAdapter;
        recyclerView = (RecyclerView) dialog.findViewById(R.id.foldersList);

        folders = AppConstants.getFolderNames(this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        foldersAdapter = new FoldersAdapter(this, folders, recyclerView);
        recyclerView.setAdapter(foldersAdapter);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String unselected = "";
                int x = 0;
                for (int i = 0; i < folders.size(); i++) {
                    if (!folders.get(i).isSelected()) {
                        x++;
                        unselected += folders.get(i).getName() + ",";
                    }
                }
                if (x == folders.size()) {
                    Toast.makeText(AppSettingActivity.this, "Cannot unselect All Folders", Toast.LENGTH_SHORT).show();
                } else {
                    sharedPreferenceSingelton.saveAs(AppSettingActivity.this, "SkipFolders", unselected);
                    dialog.dismiss();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void hide_anim(View view) {
        boolean hidden = sharedPreferenceSingelton.getSavedBoolean(this, "Waves");
        if (hidden) {
            MainActivity.waveView.setVisibility(View.VISIBLE);
            sharedPreferenceSingelton.saveAs(this, "Waves", false);
            Toast.makeText(this, "Showing Wave Animation", Toast.LENGTH_SHORT).show();
        } else {
            MainActivity.waveView.setVisibility(View.GONE);
            sharedPreferenceSingelton.saveAs(this, "Waves", true);
            Toast.makeText(this, "Wave Animation Hidden", Toast.LENGTH_SHORT).show();
        }
    }

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
            if (info.getSku().equalsIgnoreCase(AppConstants.ITEM_SKU[8])) {
                AppConstants.themesPurchased = true;
                buy.setVisibility(View.GONE);
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

            if (inventory.hasPurchase(AppConstants.ITEM_SKU[2]) ||
                    inventory.hasPurchase(AppConstants.ITEM_SKU[3]) ||
                    inventory.hasPurchase(AppConstants.ITEM_SKU[4]) ||
                    inventory.hasPurchase(AppConstants.ITEM_SKU[5]) ||
                    inventory.hasPurchase(AppConstants.ITEM_SKU[6]) ||
                    inventory.hasPurchase(AppConstants.ITEM_SKU[7]) ||
                    inventory.hasPurchase(AppConstants.ITEM_SKU[8])) {
                AppConstants.themesPurchased = true;
                buy.setVisibility(View.GONE);
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

    public void themeClick(View view) {
        switch (view.getId()) {
            case R.id.card1:
                sharedPreferenceSingelton.saveAs(AppSettingActivity.this, "Themes", 0);
                restartApp();
                break;
            case R.id.card2:
                sharedPreferenceSingelton.saveAs(AppSettingActivity.this, "Themes", 1);
                restartApp();
                break;
            case R.id.card8:
                if (AppConstants.themesPurchased) {
                    sharedPreferenceSingelton.saveAs(AppSettingActivity.this, "Themes", 2);
                    restartApp();
                }else {
                    Toast.makeText(this, getString(R.string.buy), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.card5:
                if (AppConstants.themesPurchased) {
                    sharedPreferenceSingelton.saveAs(AppSettingActivity.this, "Themes", 3);
                    restartApp();
                }else {
                    Toast.makeText(this, getString(R.string.buy), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.card4:
                if (AppConstants.themesPurchased) {
                    sharedPreferenceSingelton.saveAs(AppSettingActivity.this, "Themes", 4);
                    restartApp();
                }else {
                    Toast.makeText(this, getString(R.string.buy), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.card6:
                if (AppConstants.themesPurchased) {
                    sharedPreferenceSingelton.saveAs(AppSettingActivity.this, "Themes", 5);
                    restartApp();
                }else {
                    Toast.makeText(this, getString(R.string.buy), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.card3:
                if (AppConstants.themesPurchased) {
                    sharedPreferenceSingelton.saveAs(AppSettingActivity.this, "Themes", 6);
                    restartApp();
                }else {
                    Toast.makeText(this, getString(R.string.buy), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.card7:
                if (AppConstants.themesPurchased) {
                    sharedPreferenceSingelton.saveAs(AppSettingActivity.this, "Themes", 7);
                    restartApp();
                }else {
                    Toast.makeText(this, getString(R.string.buy), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.buttonBuy:
                if (billinSupported) {
                    try {
                        mHelper.launchPurchaseFlow(AppSettingActivity.this, AppConstants.ITEM_SKU[8], 10001, mPurchaseFinishedListener, "mypurchaseToken");
                    } catch (IabHelper.IabAsyncInProgressException e) {
                        e.printStackTrace();
                    }
                } else
                    Toast.makeText(AppSettingActivity.this, "Billing Not Supported on Your Device", Toast.LENGTH_SHORT).show();
                break;

            case R.id.preview:
                openThemeInBrowser();
                break;
        }

    }

    public void restartApp() {
        Intent i = getBaseContext().getPackageManager().
                getLaunchIntentForPackage(getBaseContext().getPackageName());
        assert i != null;
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    public void openThemeInBrowser() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(AppConstants.THEME_URL));
        startActivity(browserIntent);
    }
}
