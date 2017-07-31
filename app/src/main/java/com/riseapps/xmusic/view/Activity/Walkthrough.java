package com.riseapps.xmusic.view.Activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.riseapps.xmusic.R;
import com.riseapps.xmusic.component.SharedPreferenceSingelton;

public class Walkthrough extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private ViewPager viewPager;
    private int[] layouts;
    private Button btn1, btn2, btn3;
    boolean found;

    private static final int REQUEST_PERMISSION = 0;
    String[] permissionsRequired = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_walkthrough);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setExitTransition(new Fade());
        }
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        RelativeLayout btnSkip = (RelativeLayout) findViewById(R.id.btn_skip);
        btn1 = (Button) findViewById(R.id.bt1);
        btn2 = (Button) findViewById(R.id.bt2);
        btn3 = (Button) findViewById(R.id.bt3);


        layouts = new int[]{
                R.layout.walkthrough1,
                R.layout.walkthrough2,
                R.layout.walkthrough3};

        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(found)
                moveToMain();
                else {
                    Dialog dialog = new Dialog(Walkthrough.this);
                    dialog.setContentView(R.layout.empty_state_dialog);
                    dialog.show();
                }
            }
        });
        checkPermission();

    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            if (position == layouts.length - 1) {
                btn3.setBackground(getResources().getDrawable(R.drawable.walkthrough_selected));
                btn2.setBackground(getResources().getDrawable(R.drawable.walkthrough_unselected));
            } else if (position == 1) {
                // still pages are left
                btn1.setBackground(getResources().getDrawable(R.drawable.walkthrough_unselected));
                btn3.setBackground(getResources().getDrawable(R.drawable.walkthrough_unselected));
                btn2.setBackground(getResources().getDrawable(R.drawable.walkthrough_selected));

            } else {
                btn1.setBackground(getResources().getDrawable(R.drawable.walkthrough_selected));
                btn2.setBackground(getResources().getDrawable(R.drawable.walkthrough_unselected));
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        return new CursorLoader(this,musicUri,null,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        found = data.getCount() != 0;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
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

    public void checkPermission() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED|| ActivityCompat.checkSelfPermission(this, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[1])) {
                    Snackbar.make(findViewById(android.R.id.content),
                            "Permissions needed to Access Songs and to stop/play music during call",
                            Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ActivityCompat.requestPermissions(Walkthrough.this,
                                            permissionsRequired,
                                            REQUEST_PERMISSION);
                                }
                            }).show();
                } else {
                    ActivityCompat.requestPermissions(this, permissionsRequired, REQUEST_PERMISSION);
                }
            }
            else{
                getSupportLoaderManager().initLoader(6,null,this);
            }
        }
        else {
            getSupportLoaderManager().initLoader(6,null,this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED ) {
                    getSupportLoaderManager().initLoader(6,null,this);
                } else {
                    Snackbar.make(viewPager, R.string.permission_rationale,
                            Snackbar.LENGTH_INDEFINITE)
                            .setAction(android.R.string.ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ActivityCompat.requestPermissions(Walkthrough.this,
                                            permissionsRequired,
                                            REQUEST_PERMISSION);
                                }
                            }).show();
                }
                break;
        }
    }

    public void moveToMain(){
        new SharedPreferenceSingelton().saveAs(Walkthrough.this, "opened_before", true);
        startActivity(new Intent(Walkthrough.this, MainActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}