package com.riseapps.xmusic.view.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.claudiodegio.msv.OnSearchViewListener;
import com.claudiodegio.msv.SuggestionMaterialSearchView;
import com.gelitenight.waveview.library.WaveView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.riseapps.xmusic.R;
import com.riseapps.xmusic.component.CustomAnimation;
import com.riseapps.xmusic.component.SharedPreferenceSingelton;
import com.riseapps.xmusic.executor.Interfaces.AlbumRefreshListener;
import com.riseapps.xmusic.executor.Interfaces.ArtistRefreshListener;
import com.riseapps.xmusic.executor.Interfaces.ContextMenuListener;
import com.riseapps.xmusic.executor.Interfaces.PlaylistRefreshListener;
import com.riseapps.xmusic.executor.Interfaces.SongRefreshListener;
import com.riseapps.xmusic.executor.MyApplication;
import com.riseapps.xmusic.executor.OnSwipeTouchListener;
import com.riseapps.xmusic.executor.PlaySongExec;
import com.riseapps.xmusic.executor.Interfaces.SongLikedListener;
import com.riseapps.xmusic.executor.ProximityDetector;
import com.riseapps.xmusic.executor.UpdateSongs;
import com.riseapps.xmusic.model.MusicService;
import com.riseapps.xmusic.model.Pojo.Album;
import com.riseapps.xmusic.model.Pojo.Artist;
import com.riseapps.xmusic.model.Pojo.Song;
import com.riseapps.xmusic.widgets.EqualizerView;
import com.riseapps.xmusic.utils.WaveHelper;
import com.riseapps.xmusic.view.Fragment.AlbumFragment;
import com.riseapps.xmusic.view.Fragment.ArtistFragment;
import com.riseapps.xmusic.view.Fragment.PlaylistFragment;
import com.riseapps.xmusic.view.Fragment.ScrollingFragment;
import com.riseapps.xmusic.view.Fragment.SongsFragment;
import com.riseapps.xmusic.widgets.MainTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MainActivity extends BaseMatSearchViewActivity implements ScrollingFragment.OnFragmentInteractionListener, PlaylistFragment.OnFragmentInteractionListener, OnSearchViewListener {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public EqualizerView equalizerView;
    public boolean musicPlaying, isMusicShuffled = false;
    public ImageButton play_pause, prev, next, repeat, shuffle;
    //MiniPlayer items
    TextView title_mini, artist_mini;
    ImageButton play_pause_mini;
    ImageView album_art_mini;
    //MainPlayer items
    TextView title, artist, currentPosition, totalDuration;
    ImageView album_art;
    RelativeLayout progressView;
    private ArrayList<Song> songList = new ArrayList<>();
    private MusicService musicService;
    private Intent playIntent;
    //Mini & Main player layouts
    private CardView miniPlayer;
    private ConstraintLayout mainPlayer;
    // private final Context ctx = MainActivity.this;
    private SeekBar seekBar;
    private SongLikedListener mListener;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar, toolbarPlayer, toolbarContext;
    private WaveHelper mWaveHelper;
    private int mBorderColor = Color.parseColor("#e74c3c");
    private int mBorderWidth = 5;
    private SongRefreshListener songRefreshListener;
    private ArtistRefreshListener artistRefreshListener;
    private AlbumRefreshListener albumRefreshListener;
    private PlaylistRefreshListener playlistRefreshListener;
    private SongsFragment songFragment;
    private ContextMenuListener clearAll;
    private MainTextView toolbar_context_title;

    public static SensorManager mSensorManager;
    public static ProximityDetector proximityDetector;
    public static Sensor mProximity;

    private SharedPreferenceSingelton sharedPreferenceSingleton=new SharedPreferenceSingelton();

    private static HashMap<Integer, Boolean> multipleSongSelectionList = new HashMap<>();
    ArrayList<Song> completeList;

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
                    if (!song.getImagepath().equalsIgnoreCase("no_image")) {
                        Glide.with(MainActivity.this).load(song.getImagepath()).into(album_art);
                        Glide.with(MainActivity.this).load(song.getImagepath()).into(album_art_mini);
                    } else {
                        album_art.setImageResource(R.drawable.empty);
                        album_art_mini.setImageResource(R.drawable.empty);
                    }

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
                            play_pause.setImageResource(R.drawable.ic_pause);
                            play_pause_mini.setImageResource(R.drawable.ic_pause);
                            musicPlaying = true;
                            mWaveHelper.start();
                            equalizerView.animateBars();
                            equalizerView.setVisibility(View.VISIBLE);
                            break;
                        case MusicService.PAUSED:
                            play_pause.setImageResource(R.drawable.ic_play);
                            play_pause_mini.setImageResource(R.drawable.ic_play);
                            musicPlaying = false;
                            mWaveHelper.cancel();
                            equalizerView.setVisibility(View.GONE);
                            equalizerView.stopBars();
                            break;
                    }
                }
            });

            musicService.setSong(0);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            play_pause.setImageResource(R.drawable.ic_play);
            play_pause_mini.setImageResource(R.drawable.ic_play);
            musicPlaying = false;
            mWaveHelper.cancel();
        }
    };


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
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
                Toast.makeText(MainActivity.this, ""+sharedPreferenceSingleton.getSavedBoolean(MainActivity.this, "Shuffle"), Toast.LENGTH_SHORT).show();
                if (sharedPreferenceSingleton.getSavedBoolean(MainActivity.this, "Shuffle")) {
                    sharedPreferenceSingleton.saveAs(MainActivity.this, "Shuffle", false);
                    DrawableCompat.setTint(shuffle.getDrawable(), ContextCompat.getColor(MainActivity.this, R.color.colorBlack));
                } else {
                    sharedPreferenceSingleton.saveAs(MainActivity.this, "Shuffle", true);
                    DrawableCompat.setTint(shuffle.getDrawable(), ContextCompat.getColor(MainActivity.this, R.color.colorAccent));
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

        miniPlayer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                showMainPlayer();
                return false;
            }
        });

        album_art.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeRight() {
                changeToPreviousSong();
            }

            public void onSwipeLeft() {
                changeToNextSong();
            }

        });

        final WaveView waveView = (WaveView) findViewById(R.id.wave);
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
                if(musicPlaying)
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
        completeList=new Gson().fromJson(getIntent().getStringExtra("songList"), new TypeToken<ArrayList<Song>>() {}.getType());

        progressView = (RelativeLayout) findViewById(R.id.progress);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("Stop");
        registerReceiver(stopReceiver, intentFilter);

        mToolbar.setNavigationIcon(R.drawable.ic_settings_black_24dp);
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
                    View v = findViewById(R.id.favourites);
                    v.startAnimation(new CustomAnimation().likeAnimation(MainActivity.this));
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    ScrollingFragment scrollingFragment = new ScrollingFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("Action", "Favourites");
                    scrollingFragment.setArguments(bundle);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.drawerLayout, scrollingFragment);
                    fragmentTransaction.commit();
                } else if (item.getItemId() == R.id.refresh) {
                    new RefreshAsync().execute();
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
                if (item.getItemId() == R.id.favouritesPlayer) {
                    View v = findViewById(R.id.favouritesPlayer);
                    v.startAnimation(new CustomAnimation().likeAnimation(MainActivity.this));
                    if (song.getFavourite()) {
                        new MyApplication(MainActivity.this).getWritableDatabase().updateFavourites(song.getID(), 0);
                        song.setFavourite(false);
                        item.setIcon(R.drawable.ic_like);
                    } else {
                        new MyApplication(MainActivity.this).getWritableDatabase().updateFavourites(song.getID(), 1);
                        song.setFavourite(true);
                        item.setIcon(R.drawable.ic_liked);
                    }
                } else if (item.getItemId() == R.id.playlist) {
                    Intent i = new Intent(MainActivity.this, SelectPlaylistActivity.class);
                    i.putExtra("selection_type", "multiple_playlist");
                    startActivityForResult(i, 1);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else if (item.getItemId() == R.id.youtube) {
                    if(musicPlaying){
                        musicService.togglePlay();
                    }
                    Intent intent = new Intent(Intent.ACTION_SEARCH);
                    intent.setPackage("com.google.android.youtube");
                    intent.putExtra("query", song.getName());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    Toast.makeText(MainActivity.this, getString(R.string.opening_youtube), Toast.LENGTH_SHORT).show();
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

        toolbar_context_title = (MainTextView) findViewById(R.id.toolbar_context_title);
        toolbarContext = (Toolbar) findViewById(R.id.toolbar_context);
        toolbarContext.inflateMenu(R.menu.context_menu);
        toolbarContext.setNavigationIcon(R.drawable.ic_back);
        toolbarContext.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.favouritesPlayer) {
                   // Toast.makeText(MainActivity.this, "Action 1", Toast.LENGTH_SHORT).show();
                } else if (item.getItemId() == R.id.playlist) {
                    Intent i = new Intent(MainActivity.this, SelectPlaylistActivity.class);
                    i.putExtra("selection_type", "single_playlist");
                    startActivityForResult(i, 2);
                }
                return true;
            }
        });
        toolbarContext.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolbarContext.setVisibility(View.GONE);
                mToolbar.setVisibility(View.VISIBLE);
                miniPlayer.setVisibility(View.VISIBLE);
                songRefreshListener.onSongRefresh();
                multipleSongSelectionList.clear();
            }
        });

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            assert tab != null;
            tab.setCustomView(mSectionsPagerAdapter.getTabView(i));
        }

        title = (TextView) findViewById(R.id.name);
        artist = (TextView) findViewById(R.id.artist);

        album_art = (ImageView) findViewById(R.id.album_art);
        play_pause = (ImageButton) findViewById(R.id.play_pause);
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
        play_pause_mini = (ImageButton) findViewById(R.id.play_pause_mini);

        miniPlayer = (CardView) findViewById(R.id.song_list_card);
        mainPlayer = (ConstraintLayout) findViewById(R.id.player);

        play_pause.setOnClickListener(togglePlayBtn);
        play_pause_mini.setOnClickListener(togglePlayBtn);

        equalizerView = (EqualizerView) findViewById(R.id.equalizer_view);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            startService(playIntent);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    miniPlayer.setVisibility(View.VISIBLE);
                    miniPlayer.setAlpha(0.f);
                    miniPlayer.animate()
                            .alpha(1.f)
                            .setDuration(1000)
                            .start();
                }
            }, 1500);
        }
    }

    @Override
    protected void onDestroy() {
        sharedPreferenceSingleton.saveAs(MainActivity.this, "Shuffle",false);
        sharedPreferenceSingleton.saveAs(MainActivity.this, "Repeat",false);
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
        if (mainPlayer.getVisibility() == View.VISIBLE) {
            hideMainPlayer();
        } else {
            if (musicPlaying) {
                if (toolbarContext.isShown()) {
                    toolbarContext.setVisibility(View.GONE);
                    mToolbar.setVisibility(View.VISIBLE);
                    miniPlayer.setVisibility(View.VISIBLE);
                    songRefreshListener.onSongRefresh();
                    multipleSongSelectionList.clear();
                }
                if (getSupportFragmentManager().getBackStackEntryCount() > 0)
                    getSupportFragmentManager().popBackStackImmediate();
                else
                    moveTaskToBack(true);
            } else if (toolbarContext.isShown()) {
                toolbarContext.setVisibility(View.GONE);
                mToolbar.setVisibility(View.VISIBLE);
                miniPlayer.setVisibility(View.VISIBLE);
                songRefreshListener.onSongRefresh();
                multipleSongSelectionList.clear();
            } else {
                super.onBackPressed();
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

    void showMainPlayer() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStackImmediate();
        mainPlayer.setVisibility(View.VISIBLE);
        toolbarPlayer.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.GONE);
        mViewPager.setVisibility(View.GONE);
        miniPlayer.setVisibility(View.GONE);
        mToolbar.setVisibility(View.GONE);
    }

    void hideMainPlayer() {
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

    @Override
    public void onSearchViewShown() {

    }

    @Override
    public void onSearchViewClosed() {

    }

    @Override
    public boolean onQueryTextSubmit(String s) {

        for (Song song : completeList) {
            if (song!=null && song.getName().equalsIgnoreCase(s)) {
                musicService.setSongs(completeList);
                setSongs(completeList);
                new PlaySongExec(this, songList.indexOf(song)).startPlaying();

            }
        }
        return false;
    }

    @Override
    public void onQueryTextChange(String s) {

    }

    public void setListener(SongLikedListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initCustom() {
        //getTitles();
        //String[] arrays = getResources().getStringArray(R.array.query_suggestions);
        String[] arrays = getTitles();
        SuggestionMaterialSearchView cast = (SuggestionMaterialSearchView) mSearchView;
        cast.setSuggestion(arrays);
        mSearchView.setOnSearchViewListener(this);
        //super.initCustom();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {

        } else if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            String str = data.getStringExtra("selected_playlist");
            if (!str.equalsIgnoreCase("")) {
                long id = songList.get(musicService.getCurrentIndex()).getID();
                new MyApplication(MainActivity.this).getWritableDatabase().addSongToPlaylists(id, str);
                Toast.makeText(MainActivity.this, getString(R.string.song_added_to_playlist), Toast.LENGTH_SHORT).show();
                playlistRefreshListener.OnPlaylistRefresh();
            }
        } else if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            String str = data.getStringExtra("selected_playlist");
            if (!str.equalsIgnoreCase("")) {
                long[] array = new long[multipleSongSelectionList.size()];
                int count = 0;
                Iterator i = multipleSongSelectionList.entrySet().iterator();
                while (i.hasNext()) {
                    HashMap.Entry pair = (HashMap.Entry) i.next();
                    array[count] = songList.get(Integer.parseInt(pair.getKey().toString())).getID();
                    count++;
                }
                new MyApplication(MainActivity.this).getWritableDatabase().addMultipleSongToSinglePlaylist(str.replace(",", ""), array);
                playlistRefreshListener.OnPlaylistRefresh();
                multipleSongSelectionList.clear();
                toolbarContext.setVisibility(View.GONE);
                mToolbar.setVisibility(View.VISIBLE);
                miniPlayer.setVisibility(View.VISIBLE);
                songRefreshListener.onSongRefresh();
            }
        }
        if (mainPlayer.getVisibility() == View.VISIBLE) {
            hideMainPlayer();
        }
    }

    private String[] getTitles() {
        ArrayList<Song> list = new MyApplication(this).getWritableDatabase().readSongs();
        String[] array = new String[list.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = list.get(i).getName();
        }
        /*your logic to fetch titles of all the loaded songs and put it in string array*/
        return array;
    }

    public void setSongRefreshListener(SongRefreshListener refreshListener) {
        this.songRefreshListener = refreshListener;
    }

    public void setArtistRefreshListener(ArtistRefreshListener refreshListener) {
        this.artistRefreshListener = refreshListener;
    }

    public void setAlbumRefreshListener(AlbumRefreshListener refreshListener) {
        this.albumRefreshListener = refreshListener;
    }

    public void setPlaylistRefreshListener(PlaylistRefreshListener refreshListener) {
        this.playlistRefreshListener = refreshListener;
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        String tabTitles[] = new String[]{getResources().getString(R.string.TAB1), getResources().getString(R.string.TAB2), getResources().getString(R.string.TAB3), getResources().getString(R.string.TAB4)};

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return PlaylistFragment.newInstance();
                case 1:
                    return AlbumFragment.newInstance();
                case 2:
                    return ArtistFragment.newInstance();
                case 3:
                    songFragment = SongsFragment.newInstance();
                    songFragment.setOnShowContextMenuListener(new SongsFragment.OnShowContextMenuListener() {
                        @Override
                        public void onShowToolbar(int count, HashMap<Integer, Boolean> list) {
                            multipleSongSelectionList.putAll(list);
                            toolbarContext.setVisibility(View.VISIBLE);
                            toolbar_context_title.setText(count + " selected");
                            mToolbar.setVisibility(View.GONE);
                            miniPlayer.setVisibility(View.GONE);
                        }

                        @Override
                        public void onShowCount(int count, HashMap<Integer, Boolean> list) {
                            multipleSongSelectionList.putAll(list);
                            toolbar_context_title.setText(count + " selected");
                        }

                        @Override
                        public void onHideToolbar() {
                            multipleSongSelectionList.clear();
                            toolbarContext.setVisibility(View.GONE);
                            mToolbar.setVisibility(View.VISIBLE);
                            miniPlayer.setVisibility(View.VISIBLE);
                        }
                    });
                    return songFragment;
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

    @SuppressLint("StaticFieldLeak")
    private class RefreshAsync extends AsyncTask<Void, Void, Void> {

        ArrayList<Song> songs = new ArrayList<>();
        ArrayList<Artist> artists = new ArrayList<>();
        ArrayList<Album> albums = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            musicService.togglePlay();
            unbindService(musicConnection);
            stopService(playIntent);
            mViewPager.setAlpha(0.8f);
            progressView.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            new UpdateSongs(MainActivity.this).refreshList();
            songs = new MyApplication(MainActivity.this).getWritableDatabase().readSongs();
            artists = new MyApplication(MainActivity.this).getWritableDatabase().readArtists();
            albums = new MyApplication(MainActivity.this).getWritableDatabase().readAlbums();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    songRefreshListener.OnSongRefresh(songs);
                    albumRefreshListener.OnAlbumRefresh(albums);
                    artistRefreshListener.OnArtistRefresh(artists);
                    playlistRefreshListener.OnPlaylistRefresh();
                }
            });

            playIntent = new Intent(MainActivity.this, MusicService.class);
            startService(playIntent);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressView.setVisibility(View.GONE);
            mViewPager.setAlpha(1.0f);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            super.onPostExecute(aVoid);
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


}

