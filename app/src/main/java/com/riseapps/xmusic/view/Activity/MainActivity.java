package com.riseapps.xmusic.view.Activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gelitenight.waveview.library.WaveView;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.riseapps.xmusic.R;
import com.riseapps.xmusic.component.CustomAnimation;
import com.riseapps.xmusic.component.SharedPreferenceSingelton;
import com.riseapps.xmusic.executor.FilePathFromId;
import com.riseapps.xmusic.executor.Interfaces.AdapterToActivityListener;
import com.riseapps.xmusic.executor.Interfaces.PlaylistRefreshListener;
import com.riseapps.xmusic.executor.Interfaces.SongRefreshListener;
import com.riseapps.xmusic.executor.MyApplication;
import com.riseapps.xmusic.executor.OnSwipeTouchListener;
import com.riseapps.xmusic.executor.PlaySongExec;
import com.riseapps.xmusic.executor.ProximityDetector;
import com.riseapps.xmusic.model.MusicService;
import com.riseapps.xmusic.model.Pojo.Song;
import com.riseapps.xmusic.utils.ZoomOutPageTransformer;
import com.riseapps.xmusic.widgets.EqualizerView;
import com.riseapps.xmusic.utils.WaveHelper;
import com.riseapps.xmusic.view.Fragment.AlbumFragment;
import com.riseapps.xmusic.view.Fragment.ArtistFragment;
import com.riseapps.xmusic.view.Fragment.PlaylistFragment;
import com.riseapps.xmusic.view.Fragment.ScrollingFragment;
import com.riseapps.xmusic.view.Fragment.SongsFragment;
import com.riseapps.xmusic.widgets.MainTextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements ScrollingFragment.OnFragmentInteractionListener, PlaylistFragment.OnFragmentInteractionListener, AdapterToActivityListener {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    AppBarLayout appBarLayout;
    public EqualizerView equalizerView;
    public boolean musicPlaying, isMusicShuffled = false;
    public ImageButton prev, next, repeat, shuffle;
    FloatingActionButton play_pause;
    //MiniPlayer items
    TextView title_mini, artist_mini;
    ImageView play_pause_mini;
    ImageView album_art_mini;
    //MainPlayer items
    TextView title, artist, currentPosition, totalDuration;
    ImageView album_art, liked;
    private ArrayList<Song> songList = new ArrayList<>();
    private MusicService musicService;
    private Intent playIntent;
    //Mini & Main player layouts
    private CardView miniPlayer;
    private ConstraintLayout mainPlayer;
    private SeekBar seekBar;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private Toolbar toolbarPlayer, toolbarContext, mToolbar;
    private WaveHelper mWaveHelper;
    private int mBorderColor = Color.parseColor("#e74c3c");
    private SongRefreshListener songRefreshListener;
    private PlaylistRefreshListener playlistRefreshListener;
    private MainTextView toolbar_context_title;
    String[] titles;
    private static final int REQUEST_PERMISSION = 0;
    String[] permissionsRequired = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private ArrayList<Long> selectedID = new ArrayList<>();

    public static SensorManager mSensorManager;
    @SuppressLint("StaticFieldLeak")
    public static ProximityDetector proximityDetector;
    public static Sensor mProximity;
    private Dialog dialog;
    private SharedPreferenceSingelton sharedPreferenceSingleton;

    public ArrayList<Song> completeList;
    Toolbar searchBar;
    AutoCompleteTextView autoComplete;
    private ImageButton searchClear;

    private View.OnClickListener togglePlayBtn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            musicService.togglePlay();
        }
    };
    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(final ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();
            musicService.setSongs(songList);
            musicService.setUIControls(seekBar, currentPosition, totalDuration);
            musicService.setOnSongChangedListener(new MusicService.OnSongChangedListener() {
                @Override
                public void onSongChanged(Song song) {
                    Glide
                            .with(MainActivity.this)
                            .load(song.getImagepath())
                            .crossFade()
                            .error(ContextCompat.getDrawable(MainActivity.this, R.drawable.empty))
                            .into(album_art);
                    Glide
                            .with(MainActivity.this)
                            .load(song.getImagepath())
                            .crossFade()
                            .error(ContextCompat.getDrawable(MainActivity.this, R.drawable.empty))
                            .into(album_art_mini);
                    title.setText(song.getName());
                    title_mini.setText(song.getName());

                    artist.setText(song.getArtist());
                    artist_mini.setText(song.getArtist());

                    long time = song.getDuration();
                    totalDuration.setText(String.format(Locale.getDefault(), "%d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(time),
                            TimeUnit.MILLISECONDS.toSeconds(time) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time))));
                }

                @Override
                public void onPlayerStatusChanged(int status) {
                    switch (status) {
                        case MusicService.PLAYING:
                            play_pause.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.play_pause));
                            play_pause.setImageResource(R.drawable.pause);
                            play_pause_mini.setImageResource(R.drawable.pause);
                            musicPlaying = true;
                            mWaveHelper.start();
                            equalizerView.animateBars();
                            equalizerView.setVisibility(View.VISIBLE);
                            break;
                        case MusicService.PAUSED:
                            play_pause.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.play_pause));
                            play_pause.setImageResource(R.drawable.play);
                            play_pause_mini.setImageResource(R.drawable.play);
                            musicPlaying = false;
                            mWaveHelper.cancel();
                            equalizerView.setVisibility(View.GONE);
                            equalizerView.stopBars();
                            break;
                    }
                }
            });
            int x = sharedPreferenceSingleton.getSavedInt(MainActivity.this, "Last_Song");
            musicService.setSong(x);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            play_pause.setImageResource(R.drawable.play);
            play_pause_mini.setImageResource(R.drawable.play);
            musicPlaying = false;
            mWaveHelper.cancel();
        }
    };


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferenceSingleton = new SharedPreferenceSingelton();
        if (sharedPreferenceSingleton.getSavedInt(this, "Theme") == 1) {
            setTheme(R.style.AppTheme_Dark);
        } else if (sharedPreferenceSingleton.getSavedInt(this, "Theme") == 2) {
            setTheme(R.style.AppTheme_Dark2);
        } else if (sharedPreferenceSingleton.getSavedInt(this, "Theme") == 3) {
            setTheme(R.style.AppTheme_Dark3);
        } else if (sharedPreferenceSingleton.getSavedInt(this, "Theme") == 4) {
            setTheme(R.style.AppTheme_Dark4);
        } else if (sharedPreferenceSingleton.getSavedInt(this, "Theme") == 5) {
            setTheme(R.style.AppTheme_Dark5);
        } else if (sharedPreferenceSingleton.getSavedInt(this, "Theme") == 6) {
            setTheme(R.style.AppTheme_Dark6);
        } else if (sharedPreferenceSingleton.getSavedInt(this, "Theme") == 7) {
            setTheme(R.style.AppTheme_Dark7);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        toolbarsInitiallize();
        initiallize();

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToPreviousSong();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToNextSong();
            }
        });
        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedPreferenceSingleton.getSavedBoolean(MainActivity.this, "Shuffle")) {
                    sharedPreferenceSingleton.saveAs(MainActivity.this, "Shuffle", false);
                    int a=sharedPreferenceSingleton.getSavedInt(MainActivity.this, "Theme");
                    if(!(a==1||a==2||a==4||a==5||a==7))
                        shuffle.setColorFilter(Color.argb(255, 0, 0, 0));
                    else
                        shuffle.setColorFilter(Color.argb(255, 255, 255, 255));
                } else {
                    sharedPreferenceSingleton.saveAs(MainActivity.this, "Shuffle", true);
                    shuffle.setColorFilter(Color.argb(255,236, 100, 75));
                }
            }
        });

        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedPreferenceSingleton.getSavedBoolean(MainActivity.this, "Repeat")) {
                    sharedPreferenceSingleton.saveAs(MainActivity.this, "Repeat", false);
                    Toast.makeText(MainActivity.this, getString(R.string.song_repeat_off_toast), Toast.LENGTH_SHORT).show();
                    DrawableCompat.setTint(repeat.getDrawable(), ContextCompat.getColor(MainActivity.this, R.color.colorBlack));
                } else {
                    sharedPreferenceSingleton.saveAs(MainActivity.this, "Repeat", true);
                    Toast.makeText(MainActivity.this, getString(R.string.song_repeat_on_toast), Toast.LENGTH_SHORT).show();
                    DrawableCompat.setTint(repeat.getDrawable(), ContextCompat.getColor(MainActivity.this, R.color.colorAccent));
                }
            }
        });

        miniPlayer.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeRight() {
                changeToPreviousSong();
            }

            public void onSwipeLeft() {
                changeToNextSong();
            }

            public void onTap() {
                showMainPlayer();
            }

        });

        album_art.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeRight() {
                changeToPreviousSong();
            }

            public void onSwipeLeft() {
                changeToNextSong();
            }

            public void onDoubleTapping() {
                Song song = songList.get(musicService.getCurrentIndex());
                if (song.getFavourite()) {
                    new MyApplication(MainActivity.this).getWritableDatabase().deleteFavourite(song.getID());
                    song.setFavourite(false);
                    Toast.makeText(MainActivity.this, "Song Removed from Favourites", Toast.LENGTH_SHORT).show();
                } else {
                    new MyApplication(MainActivity.this).getWritableDatabase().insertFavourite(song.getID());
                    liked.setVisibility(View.VISIBLE);
                    liked.startAnimation(new CustomAnimation().likeAnimation(MainActivity.this));
                    liked.setVisibility(View.INVISIBLE);
                    Toast.makeText(MainActivity.this, "Song Added to Favourites", Toast.LENGTH_SHORT).show();
                    song.setFavourite(true);
                }
            }

            public void onSwipeDown(){
                hideMainPlayer();
            }

        });

        mainPlayer.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this){
            public void onSwipeDown(){
                hideMainPlayer();
            }
        });

        final WaveView waveView = (WaveView) findViewById(R.id.wave);
        int mBorderWidth = 5;
        waveView.setBorder(mBorderWidth, mBorderColor);

        // set wave view
        mWaveHelper = new WaveHelper(waveView);
        waveView.setShapeType(WaveView.ShapeType.CIRCLE);
        waveView.setWaveColor(
                Color.parseColor("#D32F2F"),
                Color.parseColor("#F44336"));
        mWaveHelper.start();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        proximityDetector = new ProximityDetector(this);
        proximityDetector.setOnProximityListener(new ProximityDetector.OnProximityListener() {
            @Override
            public void onProximity() {
                ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(100);
                if (musicPlaying)
                    changeToNextSong();
                else
                    musicService.togglePlay();
            }
        });
    }

    private void changeToPreviousSong() {
        int current = musicService.getCurrentIndex();
        int previous = current - 1;
        if (previous < 0)            // If current was 0, then play the last song in the list
            previous = songList.size() - 1;
        musicService.setSong(previous);
        musicService.togglePlay();
    }

    private void changeToNextSong() {
        int next;
        int current = musicService.getCurrentIndex();
        next = current + 1;
        if (next == songList.size())// If current was the last song, then play the first song in the list
            next = 0;
        musicService.setSong(next);
        musicService.togglePlay();
    }

    private void initiallize() {
        appBarLayout= (AppBarLayout) findViewById(R.id.appbar);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("Stop");
        registerReceiver(stopReceiver, intentFilter);

        searchBar = (Toolbar) findViewById(R.id.search_bar);
        searchClear = (ImageButton) findViewById(R.id.search_bar_clear);
        autoComplete = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        searchClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(autoComplete.getText().toString().length()>0){
                    autoComplete.setText("");
                }else {
                    doExitReveal(searchBar);
                }

            }
        });

        autoComplete.setThreshold(3);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            assert tab != null;
            tab.setCustomView(mSectionsPagerAdapter.getTabView(i));
        }

        title = (TextView) findViewById(R.id.name);
        artist = (TextView) findViewById(R.id.artist);
        liked = (ImageView) findViewById(R.id.liked);
        album_art = (ImageView) findViewById(R.id.album_art);
        play_pause = (FloatingActionButton) findViewById(R.id.play_pause);
        next = (ImageButton) findViewById(R.id.next);
        prev = (ImageButton) findViewById(R.id.prev);
        shuffle = (ImageButton) findViewById(R.id.shuffle);
        repeat = (ImageButton) findViewById(R.id.repeat);
        currentPosition = (TextView) findViewById(R.id.currentPosition);
        totalDuration = (TextView) findViewById(R.id.totalDuration);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        title_mini = (TextView) findViewById(R.id.name_mini);
        artist_mini = (TextView) findViewById(R.id.artist_mini);
        album_art_mini = (ImageView) findViewById(R.id.album_art_mini);
        play_pause_mini = (ImageView) findViewById(R.id.play_pause_mini);
        miniPlayer = (CardView) findViewById(R.id.song_list_card);
        mainPlayer = (ConstraintLayout) findViewById(R.id.player);
        equalizerView = (EqualizerView) findViewById(R.id.equalizer_view);

        play_pause.setOnClickListener(togglePlayBtn);
        play_pause_mini.setOnClickListener(togglePlayBtn);

        if (!sharedPreferenceSingleton.getSavedBoolean(this, "mainScreenSequence")) {
            new TapTargetSequence(this).targets(
                    TapTarget.forToolbarNavigationIcon(mToolbar, getString(R.string.app_walk1))
                            .dimColor(android.R.color.black)
                            .outerCircleColor(R.color.colorAccentDark)
                            .targetCircleColor(R.color.colorWhite)
                            .transparentTarget(true)
                            .textTypeface(Typeface.SANS_SERIF)
                            .textColor(android.R.color.white)
                            .targetRadius(20)
                            .cancelable(true)
                            .id(1),
                    TapTarget.forView(album_art_mini, getString(R.string.app_walk2))
                            .dimColor(android.R.color.black)
                            .outerCircleColor(R.color.colorAccentDark)
                            .targetCircleColor(R.color.colorWhite)
                            .textTypeface(Typeface.SANS_SERIF)
                            .transparentTarget(true)
                            .textColor(R.color.colorWhite)
                            .targetRadius(30)
                            .id(2)
            ).listener(new TapTargetSequence.Listener() {
                @Override
                public void onSequenceFinish() {

                }

                @Override
                public void onSequenceStep(TapTarget tapTarget, boolean b) {
                    if (tapTarget.id() == 2)
                        showMainPlayer();
                }

                @Override
                public void onSequenceCanceled(TapTarget tapTarget) {
                }
            }).start();
            sharedPreferenceSingleton.saveAs(this, "mainScreenSequence", true);
        }
    }

    private void toolbarsInitiallize() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.inflateMenu(R.menu.main_menu);
        mToolbar.setNavigationIcon(R.drawable.ic_settings);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, AppSettingActivity.class), 1);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.favourites) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    ScrollingFragment scrollingFragment = new ScrollingFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("Action", "Favourites");
                    scrollingFragment.setArguments(bundle);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.drawerLayout, scrollingFragment);
                    fragmentTransaction.commit();
                } else if (item.getItemId() == R.id.action_search) {
                    doCircularReveal(searchBar);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>
                            (MainActivity.this, android.R.layout.simple_dropdown_item_1line, titles);
                    autoComplete.setAdapter(adapter);
                    autoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String selection = (String)parent.getItemAtPosition(position);
                            playFromSearch(selection);
                            doExitReveal(searchBar);
                        }
                    });
                }
                return true;
            }
        });
        toolbarPlayer = (Toolbar) findViewById(R.id.toolbar_player);
        toolbarPlayer.inflateMenu(R.menu.player_menu);
        toolbarPlayer.setNavigationIcon(R.drawable.ic_back);

        toolbarPlayer.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Song song = songList.get(musicService.getCurrentIndex());
                if (item.getItemId() == R.id.playlist) {
                    Intent i = new Intent(MainActivity.this, SelectPlaylistActivity.class);
                    startActivityForResult(i, 1);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else if (item.getItemId() == R.id.youtube) {
                    if (musicPlaying) {
                        musicService.togglePlay();
                    }
                    Intent intent = new Intent(Intent.ACTION_SEARCH);
                    intent.setPackage("com.google.android.youtube");
                    intent.putExtra("query", song.getName());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    Toast.makeText(MainActivity.this, getString(R.string.opening_youtube), Toast.LENGTH_SHORT).show();
                } else if (item.getItemId() == R.id.share) {
                    Uri uri = new FilePathFromId().pathFromID(MainActivity.this, song.getID());
                    if (uri != null) {
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("audio/*");
                        share.putExtra(Intent.EXTRA_STREAM, uri);
                        startActivity(Intent.createChooser(share, "Share Sound File"));
                    }

                }
                return true;
            }
        });
        toolbarPlayer.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideMainPlayer();
            }
        });
        toolbarContext = (Toolbar) findViewById(R.id.toolbar_context);
        toolbarContext.inflateMenu(R.menu.context_menu);
        toolbar_context_title = (MainTextView) findViewById(R.id.toolbar_context_title);
        toolbarContext.setNavigationIcon(R.drawable.ic_back);
        toolbarContext.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.playlist) {
                    Intent i = new Intent(MainActivity.this, SelectPlaylistActivity.class);
                    startActivityForResult(i, 2);
                } else if (item.getItemId() == R.id.delete) {
                    openDeleteDialog();
                }
                return true;
            }
        });
        toolbarContext.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeContentSelection();
            }
        });
    }

    private void removeContentSelection() {
        toolbarContext.setVisibility(View.GONE);
        mToolbar.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.fade_in));
        mToolbar.setVisibility(View.VISIBLE);
        miniPlayer.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.fade_in));
        miniPlayer.setVisibility(View.VISIBLE);
        selectedID = new ArrayList<>();
        songRefreshListener.OnContextBackPressed();

    }

    public void startTheService() {
        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            startService(playIntent);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            miniPlayer.setAlpha(0.f);
            miniPlayer.animate()
                    .alpha(1.f)
                    .setDuration(100)
                    .start();
            miniPlayer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {

        sharedPreferenceSingleton.saveAs(this, "Loader", false);
        sharedPreferenceSingleton.saveAs(this, "Shuffle", false);
        sharedPreferenceSingleton.saveAs(this, "Repeat", false);
        sharedPreferenceSingleton.saveAs(this, "Last_Song", musicService.getCurrentIndex());
        unregisterReceiver(stopReceiver);
        unbindService(musicConnection);
        stopService(playIntent);
        if (sharedPreferenceSingleton.getSavedBoolean(MainActivity.this, "Pro_controls"))
            mSensorManager.unregisterListener(proximityDetector);

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        if (sharedPreferenceSingleton.getSavedBoolean(this, "Pro_Controls")) {
            mSensorManager.registerListener(proximityDetector, mProximity, 2 * 1000 * 1000);
        }
        if (mainPlayer.getVisibility() == View.VISIBLE) {
            miniPlayer.setVisibility(View.VISIBLE);
            mViewPager.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.VISIBLE);
            hideMainPlayer();
        }
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if(searchBar.getVisibility()==View.VISIBLE){
            doExitReveal(searchBar);
        }else {
            if (mainPlayer.getVisibility() == View.VISIBLE) {
                hideMainPlayer();
            } else {
                if (musicPlaying) {
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0)
                        getSupportFragmentManager().popBackStackImmediate();
                    else {
                        if (selectedID.size() > 0) {
                            removeContentSelection();
                        } else
                            moveTaskToBack(true);
                    }
                } else {
                    if (selectedID.size() > 0) {
                        removeContentSelection();
                    } else
                        super.onBackPressed();
                }
            }
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    public ArrayList<Song> getSongs() {
        return songList;
    }

    public void setSongs(ArrayList<Song> songList) {
        this.songList = songList;
    }

    public void setCompleteSongList(ArrayList<Song> arrayList) {
        completeList = arrayList;
        setSongs(completeList);
        titles=getTitles();
    }

    void showMainPlayer() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStackImmediate();
        miniPlayer.setVisibility(View.GONE);
        mainPlayer.setVisibility(View.VISIBLE);
        toolbarPlayer.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.GONE);
        mViewPager.setVisibility(View.GONE);
        miniPlayer.setVisibility(View.GONE);
        mToolbar.setVisibility(View.GONE);
        mainPlayer.startAnimation(new CustomAnimation().slide_up(MainActivity.this));
        if (!sharedPreferenceSingleton.getSavedBoolean(MainActivity.this, "playerSequence")) {
            new TapTargetSequence(this).target(
                    TapTarget.forToolbarMenuItem(toolbarPlayer, R.id.youtube, getString(R.string.app_walk3))
                            .dimColor(android.R.color.black)
                            .outerCircleColor(R.color.colorAccentDark)
                            .targetCircleColor(R.color.whitePrimary)
                            .transparentTarget(true)
                            .textColor(android.R.color.white)
                            .targetRadius(25)
                            .id(1)).start();
            sharedPreferenceSingleton.saveAs(MainActivity.this, "playerSequence", true);
        }

    }

    void hideMainPlayer() {
        mainPlayer.startAnimation(new CustomAnimation().slide_down(MainActivity.this));
        mainPlayer.setVisibility(View.GONE);
        miniPlayer.setVisibility(View.VISIBLE);
        mViewPager.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.VISIBLE);
        toolbarPlayer.setVisibility(View.GONE);
        mToolbar.setVisibility(View.VISIBLE);
    }

    public MusicService getMusicService() {
        return musicService;
    }

    public void playFromSearch(String s){
        for (Song song : completeList) {
            if (song != null && song.getName().equalsIgnoreCase(s)) {
                musicService.setSongs(completeList);
                setSongs(completeList);
                new PlaySongExec(this, songList.indexOf(song)).startPlaying();

            }
        }
    }

    private void doCircularReveal(View view) {
        tabLayout.setVisibility(View.GONE);
        autoComplete.requestFocus();
        mToolbar.setVisibility(View.GONE);
        miniPlayer.setVisibility(View.GONE);
        mViewPager.setVisibility(View.GONE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            appBarLayout.setElevation(10.0f);
            searchBar.setVisibility(View.VISIBLE);
            int centerX = view.getWidth();
            int centerY = view.getHeight()/2;
            int startRadius = 0;
            int endRadius =view.getWidth();
            Animator anim = null;
            anim = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius, endRadius);
            anim.setDuration(220);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                }
            });
            anim.start();
        } else {
            searchBar.setVisibility(View.VISIBLE);
        }
        InputMethodManager keyboard = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.showSoftInput(autoComplete, 0);
    }

    void doExitReveal(View view) {
        InputMethodManager imm = (InputMethodManager)
        getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(
                searchBar.getWindowToken(), 0);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            appBarLayout.setElevation(0.0f);
            int centerX = view.getWidth();
            int centerY = view.getHeight()/2;
            int startRadius = view.getWidth();
            Animator anim =
                    ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius, 0);
            anim.setDuration(220);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    searchBar.setVisibility(View.GONE);
                    tabLayout.setVisibility(View.VISIBLE);
                    mToolbar.setVisibility(View.VISIBLE);
                    miniPlayer.setVisibility(View.VISIBLE);
                    mViewPager.setVisibility(View.VISIBLE);
                }
            });
            anim.start();
        } else {
            searchBar.setVisibility(View.GONE);
            tabLayout.setVisibility(View.VISIBLE);
            mToolbar.setVisibility(View.VISIBLE);
            miniPlayer.setVisibility(View.VISIBLE);
            mViewPager.setVisibility(View.VISIBLE);
        }
        autoComplete.setText("");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {

        } else if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            String str = data.getExtras().getString("selected_playlist");
            if (!str.equalsIgnoreCase("")) {
                long id = songList.get(musicService.getCurrentIndex()).getID();
                new MyApplication(MainActivity.this).getWritableDatabase().addSongToPlaylists(id, str);
                Toast.makeText(MainActivity.this, getString(R.string.song_added_to_playlist), Toast.LENGTH_SHORT).show();
                playlistRefreshListener.OnPlaylistRefresh();
            }
        } else if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            String str = data.getStringExtra("selected_playlist");
            toolbarContext.setVisibility(View.GONE);
            mToolbar.setVisibility(View.VISIBLE);
            miniPlayer.setVisibility(View.VISIBLE);
            long array[] = new long[selectedID.size()];
            int c = 0;
            for (long id : selectedID) {
                array[c] = id;
                c++;
            }
            new MyApplication(MainActivity.this).getWritableDatabase().addMultipleSongToMultiplePlaylist(str, array);
            playlistRefreshListener.OnPlaylistRefresh();

            toolbarContext.setVisibility(View.GONE);
            mToolbar.setVisibility(View.VISIBLE);
            miniPlayer.setVisibility(View.VISIBLE);
            songRefreshListener.OnContextBackPressed();

        }
        if (mainPlayer.getVisibility() == View.VISIBLE) {
            hideMainPlayer();
        }
    }

    private String[] getTitles() {
        ArrayList<Song> list = completeList;
        String[] array = {""};
        if (list != null) {
            array = new String[list.size()];
            for (int i = 0; i < array.length; i++) {
                array[i] = list.get(i).getName();
            }
        }
        return array;
    }

    public void setSongRefreshListener(SongRefreshListener refreshListener) {
        this.songRefreshListener = refreshListener;
    }

    public void setPlaylistRefreshListener(PlaylistRefreshListener refreshListener) {
        this.playlistRefreshListener = refreshListener;
    }

    @Override
    public void onTrackLongPress(int c, long songId, boolean songAdded) {
        toolbar_context_title.setText(c + " Selected");
        if (songAdded) {
            selectedID.add(songId);
        } else {
            selectedID.remove(songId);
        }
        if (c == 0) {
            toolbarContext.setVisibility(View.GONE);
            mToolbar.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.fade_in));
            mToolbar.setVisibility(View.VISIBLE);
            miniPlayer.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.fade_in));
            miniPlayer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFirstTrackLongPress() {
        toolbarContext.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.fade_in));
        toolbarContext.setVisibility(View.VISIBLE);
        mToolbar.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.fade_out));
        mToolbar.setVisibility(View.GONE);
        miniPlayer.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.fade_out));
        miniPlayer.setVisibility(View.GONE);
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        String tabTitles[] = new String[]{getResources().getString(R.string.TAB1), getResources().getString(R.string.TAB4), getResources().getString(R.string.TAB2), getResources().getString(R.string.TAB3)};

        //String tabTitles[] = new String[]{getResources().getString(R.string.TAB4), getResources().getString(R.string.TAB2), getResources().getString(R.string.TAB3)};
        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return PlaylistFragment.newInstance();
                case 1:
                    return SongsFragment.newInstance();
                case 2:
                    return AlbumFragment.newInstance();
                case 3:
                    return ArtistFragment.newInstance();

            }
            return PlaylistFragment.newInstance();
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        public View getTabView(int position) {
            View tab = LayoutInflater.from(MainActivity.this).inflate(R.layout.custom_tab, null);
            TextView tv = (TextView) tab.findViewById(R.id.custom_text);
            tv.setText(tabTitles[position]);
            return tab;
        }
    }

    private final BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equalsIgnoreCase("Stop")) {
                finish();
            }
        }
    };


    public void checkPermission() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[0])) {
                    Snackbar.make(findViewById(android.R.id.content),
                            "Permission required if you want to delete songs",
                            Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ActivityCompat.requestPermissions(MainActivity.this,
                                            permissionsRequired,
                                            REQUEST_PERMISSION);
                                }
                            }).show();
                } else {
                    ActivityCompat.requestPermissions(this, permissionsRequired, REQUEST_PERMISSION);
                }
            }
        }
    }

    private void openDeleteDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.delete_confirm_dialog);
        dialog.show();
        Button done = (Button) dialog.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (long id : selectedID) {
                    if (musicService.currSongID == id) {
                        changeToNextSong();
                    }
                    try {
                        Uri uri = new FilePathFromId().pathFromID(MainActivity.this, id);
                        File file = new File(uri.getPath());
                        if (file.exists()) {
                            file.delete();
                            int rows = getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.Audio.Media._ID + "=" + id, null);
                        }
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                selectedID.clear();
                toolbarContext.setVisibility(View.GONE);
                mToolbar.setVisibility(View.VISIBLE);
                miniPlayer.setVisibility(View.VISIBLE);
                songRefreshListener.OnSongDelete();
                dialog.dismiss();

            }
        });
        Button cancel = (Button) dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedID.clear();
                toolbarContext.setVisibility(View.GONE);
                mToolbar.setVisibility(View.VISIBLE);
                miniPlayer.setVisibility(View.VISIBLE);
                songRefreshListener.OnContextBackPressed();
                dialog.dismiss();
            }
        });

    }

    public void switchContent(int id, Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(id, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }
}

