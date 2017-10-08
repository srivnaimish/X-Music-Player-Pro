package com.riseapps.xmusic.view.Activity;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.IntentCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
    RelativeLayout theme_dialog;

    private ArrayList<PlaylistSelect> folders;
    private LinearLayout movie_dialog;

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
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.theme_select_dialog);
        try {
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }catch (Exception e){}
        ViewPager viewpager = (ViewPager) dialog.findViewById(R.id.view_pager);
        theme_dialog = (RelativeLayout) dialog.findViewById(R.id.theme_dialog);

        viewpager.setClipToPadding(false);
        viewpager.setPadding(40, 0, 70, 0);
        viewpager.setPageMargin(20);
        viewpager.addOnPageChangeListener(viewPagerPageChangeListener);
        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter();
        viewpager.setAdapter(myViewPagerAdapter);
        try {
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        } catch (NullPointerException e) {
        }
        dialog.show();

    }

    public void changeMovieTheme(View v) {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.movie_theme_dialog);
        try {
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }catch (Exception e){}
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
                    case R.id.inception:
                        sharedPreferenceSingelton.saveAs(AppSettingActivity.this, "Themes", 12);
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
        dialog.findViewById(R.id.inception).setOnClickListener(clickListener);
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

    public void helpUsTranslate(View v) {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.translation);
        try {
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        } catch (NullPointerException e) {
        }
        dialog.show();

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
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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

    public void tryTaplor(View view) {

        final Uri uri = Uri.parse("market://details?id=" + "com.riseapps.taplor");
        final Intent rateAppIntent = new Intent(Intent.ACTION_VIEW, uri);

        if (getPackageManager().queryIntentActivities(rateAppIntent, 0).size() > 0) {
            startActivity(rateAppIntent);
        }
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
            Button preview = (Button) itemView.findViewById(R.id.show);
            int x = sharedPreferenceSingelton.getSavedInt(AppSettingActivity.this, "Themes");
            if (x == position) {
                tick.setImageResource(R.drawable.ic_check);
            }

            if (position == 0)
                preview.setVisibility(View.GONE);

            if (!AppConstants.themesPurchased && position!=0 && position!=1 ) {
                button.setVisibility(View.GONE);
                buyButton.setVisibility(View.VISIBLE);
            }

            buyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (billinSupported) {
                        try {
                            mHelper.launchPurchaseFlow(AppSettingActivity.this, AppConstants.ITEM_SKU[8], 10001, mPurchaseFinishedListener, "mypurchaseToken");
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
                            AppSettingActivity.this, Splash2Activity.class));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });

            preview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position == 1) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(AppConstants.THEME_URL + "#Black"));
                        startActivity(browserIntent);
                    } else if (position == 2) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(AppConstants.THEME_URL + "#Walnut"));
                        startActivity(browserIntent);
                    } else if (position == 3) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(AppConstants.THEME_URL + "#Matterhorn"));
                        startActivity(browserIntent);
                    } else if (position == 4) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(AppConstants.THEME_URL + "#Orchid"));
                        startActivity(browserIntent);
                    } else if (position == 5) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(AppConstants.THEME_URL + "#bluewood"));
                        startActivity(browserIntent);
                    } else if (position == 6) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(AppConstants.THEME_URL + "#Lockmara"));
                        startActivity(browserIntent);
                    } else if (position == 7) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(AppConstants.THEME_URL + "#Karry"));
                        startActivity(browserIntent);
                    }
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
            for (int i = 0; i < AppConstants.backgroundColors.length; i++) {
                dialog.findViewById(AppConstants.buttonId[i]).setBackground(getResources().getDrawable(R.drawable.walkthrough_unselected));
            }
            dialog.findViewById(AppConstants.buttonId[position]).setBackground(getResources().getDrawable(R.drawable.walkthrough_selected));
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
            if (info.getSku().equalsIgnoreCase(AppConstants.ITEM_SKU[8])) {
                AppConstants.themesPurchased = true;
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
