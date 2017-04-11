package com.riseapps.xmusic.view.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.riseapps.xmusic.R;
import com.riseapps.xmusic.component.SharedPreferenceSingelton;
import com.riseapps.xmusic.executor.MyApplication;
import com.riseapps.xmusic.executor.UpdateSongs;
import com.riseapps.xmusic.model.Pojo.Album;
import com.riseapps.xmusic.model.Pojo.Artist;
import com.riseapps.xmusic.model.Pojo.Song;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Walkthrough extends AppCompatActivity {

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private int[] layouts;
    private Button btnSkip, btnNext,btn1,btn2,btn3;

    Async async = new Async();
    final int textLimit = 26;
    private static final int REQUEST_PERMISSION = 0;

    ArrayList<Song> songList = new ArrayList<>();
    ArrayList<Album> albumList = new ArrayList<>();
    ArrayList<Artist> artistList = new ArrayList<>();
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21)
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(R.layout.activity_walkthrough);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        btnSkip = (Button) findViewById(R.id.btn_skip);
        btnNext = (Button) findViewById(R.id.btn_next);
        btn1= (Button) findViewById(R.id.bt1);
        btn2= (Button) findViewById(R.id.bt2);
        btn3= (Button) findViewById(R.id.bt3);

        layouts = new int[]{
                R.layout.walkthrough1,
                R.layout.walkthrough2,
                R.layout.walkthrough3};

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchHomeScreen();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page
                // if last page home screen will be launched
                int current = getItem(+1);
                if (current < layouts.length) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                } else {
                    launchHomeScreen();
                }
            }
        });
        checkPermission();


    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        if (async.getStatus() == AsyncTask.Status.FINISHED) {
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        } else {
            Snackbar.make(viewPager, R.string.fetched,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            finish();
                        }
                    }).show();
        }

    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            if (position == layouts.length - 1) {
                btn3.setBackground(getResources().getDrawable(R.drawable.walkthrough_selected));
                btn2.setBackground(getResources().getDrawable(R.drawable.walkthrough_unselected));
                btnNext.setText(getString(R.string.start));
                btnSkip.setVisibility(View.GONE);
            }
            else if(position==1){
                // still pages are left
                btn1.setBackground(getResources().getDrawable(R.drawable.walkthrough_unselected));
                btn3.setBackground(getResources().getDrawable(R.drawable.walkthrough_unselected));
                btn2.setBackground(getResources().getDrawable(R.drawable.walkthrough_selected));
                btnNext.setText(getString(R.string.next));
                btnSkip.setVisibility(View.VISIBLE);
            }
            else {
                btn1.setBackground(getResources().getDrawable(R.drawable.walkthrough_selected));
                btn2.setBackground(getResources().getDrawable(R.drawable.walkthrough_unselected));
                btnNext.setText(getString(R.string.next));
                btnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

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
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Snackbar.make(findViewById(android.R.id.content),
                            "Permission needed to Access Songs",
                            Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ActivityCompat.requestPermissions(Walkthrough.this,
                                            new String[]{Manifest.permission
                                                    .WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                            REQUEST_PERMISSION);
                                }
                            }).show();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
                }
            } else {
                async.execute();
            }
        } else {
            async.execute();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //updateSongs.fetchSongs();
                    async.execute();
                } else {
                    Snackbar.make(viewPager, R.string.permission_rationale,
                            Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ActivityCompat.requestPermissions(Walkthrough.this,
                                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                            REQUEST_PERMISSION);
                                }
                            }).show();
                }
                break;
        }
    }

    private class Async extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            String adString = getResources().getString(R.string.adStringPlaceholder);
            new UpdateSongs(Walkthrough.this).getSongList();
            songList = new MyApplication(Walkthrough.this).getWritableDatabase().readSongs();
            artistList = new MyApplication(Walkthrough.this).getWritableDatabase().readArtists();
            albumList = new MyApplication(Walkthrough.this).getWritableDatabase().readAlbums();
            if(albumList.size()>13&&artistList.size()>17) {
                // Place 3 ads for album fragment
                Album albumAdOne = new Album();
                albumAdOne.setName(adString);
                albumAdOne.setImagepath("NoImage");
                albumAdOne.setViewType(2);
                albumList.add(3, albumAdOne);

                Album albumAdTwo = new Album();
                albumAdTwo.setName(adString);
                albumAdTwo.setImagepath("NoImage");
                albumAdTwo.setViewType(2);
                albumList.add(8, albumAdTwo);

                Album albumAdThree = new Album();
                albumAdThree.setName(adString);
                albumAdThree.setImagepath("NoImage");
                albumAdThree.setViewType(2);
                albumList.add(13, albumAdThree);

                // Place 3 ads for artist fragment
                Artist artistAdOne = new Artist();
                artistAdOne.setName(adString);
                artistAdOne.setImagepath("NoImage");
                artistAdOne.setViewType(2);
                artistList.add(4, artistAdOne);

                Artist artistAdTwo = new Artist();
                artistAdTwo.setName(adString);
                artistAdTwo.setImagepath("NoImage");
                artistAdTwo.setViewType(2);
                artistList.add(11, artistAdTwo);

                Artist artistAdThree = new Artist();
                artistAdThree.setName(adString);
                artistAdThree.setImagepath("NoImage");
                artistAdThree.setViewType(2);
                artistList.add(17, artistAdThree);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            new SharedPreferenceSingelton().saveAs(Walkthrough.this, "opened_before", true);
            intent = new Intent(Walkthrough.this, MainActivity.class);
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Song>>() {
            }.getType();
            String songJson = gson.toJson(songList, type);

            type = new TypeToken<ArrayList<Album>>() {
            }.getType();
            String albumJson = gson.toJson(albumList, type);

            type = new TypeToken<ArrayList<Artist>>() {
            }.getType();
            String artistJson = gson.toJson(artistList, type);

            intent.putExtra("songList", songJson);
            intent.putExtra("albumList", albumJson);
            intent.putExtra("artistList", artistJson);
        }
    }
}