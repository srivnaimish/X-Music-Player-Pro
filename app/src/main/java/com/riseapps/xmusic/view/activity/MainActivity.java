package com.riseapps.xmusic.view.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
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
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gelitenight.waveview.library.WaveView;
import com.riseapps.xmusic.R;
import com.riseapps.xmusic.component.AppConstants;
import com.riseapps.xmusic.component.CustomAnimation;
import com.riseapps.xmusic.component.SharedPreferenceSingelton;
import com.riseapps.xmusic.component.ThemeSelector;
import com.riseapps.xmusic.executor.FilePathFromId;
import com.riseapps.xmusic.executor.Interfaces.AdapterToActivityListener;
import com.riseapps.xmusic.executor.Interfaces.PlaylistRefreshListener;
import com.riseapps.xmusic.executor.Interfaces.SongRefreshListener;
import com.riseapps.xmusic.executor.MyApplication;
import com.riseapps.xmusic.executor.PlaySongExec;
import com.riseapps.xmusic.model.MusicService;
import com.riseapps.xmusic.model.Pojo.Song;
import com.riseapps.xmusic.utils.OnSwipeTouchListener;
import com.riseapps.xmusic.utils.WaveHelper;
import com.riseapps.xmusic.utils.ZoomOutPageTransformer;
import com.riseapps.xmusic.view.Fragment.AlbumFragment;
import com.riseapps.xmusic.view.Fragment.ArtistFragment;
import com.riseapps.xmusic.view.Fragment.FolderFragment;
import com.riseapps.xmusic.view.Fragment.GenreFragment;
import com.riseapps.xmusic.view.Fragment.PlaylistFragment;
import com.riseapps.xmusic.view.Fragment.ScrollingFragment;
import com.riseapps.xmusic.view.Fragment.SongsFragment;
import com.riseapps.xmusic.widgets.EqualizerView;
import com.riseapps.xmusic.widgets.MainTextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import me.toptas.fancyshowcase.DismissListener;
import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.FocusShape;

public class MainActivity extends AppCompatActivity implements ScrollingFragment.OnFragmentInteractionListener, PlaylistFragment.OnFragmentInteractionListener, AdapterToActivityListener {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    Song currentSong;
    AppBarLayout appBarLayout;
    public EqualizerView equalizerView;
    public boolean musicPlaying, isMusicShuffled = false;
    public ImageButton prev, next, repeat, shuffle;
    FloatingActionButton play_pause, shuffle_play;
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
    private ArrayList<Song> selectedSongs = new ArrayList<>();

    private Dialog dialog;
    private SharedPreferenceSingelton sharedPreferenceSingleton;
    public static WaveView waveView;

    public ArrayList<Song> completeList=new ArrayList<>();
    Toolbar searchBar;
    AutoCompleteTextView autoComplete;

    private View.OnClickListener togglePlayBtn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.shuffle_songs) {
                sharedPreferenceSingleton.saveAs(MainActivity.this, "Shuffle", true);
                shuffle.setColorFilter(Color.argb(255, 236, 100, 75));
                changeToNextSong();
            } else {
                musicService.togglePlay();
            }
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

                    currentSong = song;

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
                            play_pause.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.play_pause));
                            play_pause.setImageResource(R.drawable.pause);
                            play_pause_mini.setImageResource(R.drawable.pause);
                            musicPlaying = true;
                            mWaveHelper.start();
                            equalizerView.animateBars();
                            equalizerView.setVisibility(View.VISIBLE);
                            break;
                        case MusicService.PAUSED:
                            play_pause.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.play_pause));
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
    private ItemTouchHelper mItemTouchHelper;
    private FancyShowCaseView fancyShowCaseView1, fancyShowCaseView2;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferenceSingleton = new SharedPreferenceSingelton();

        new ThemeSelector().setAppTheme(this);
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
                    int a = sharedPreferenceSingleton.getSavedInt(MainActivity.this, "Themes");
                    if (!(a == 1 || a == 2 || a == 4 || a == 5 || a == 7))
                        shuffle.setColorFilter(Color.argb(255, 0, 0, 0));
                    else
                        shuffle.setColorFilter(Color.argb(255, 255, 255, 255));
                } else {
                    sharedPreferenceSingleton.saveAs(MainActivity.this, "Shuffle", true);
                    shuffle.setColorFilter(Color.argb(255, 236, 100, 75));
                }
            }
        });

        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedPreferenceSingleton.getSavedBoolean(MainActivity.this, "Repeat")) {
                    sharedPreferenceSingleton.saveAs(MainActivity.this, "Repeat", false);
                    int a = sharedPreferenceSingleton.getSavedInt(MainActivity.this, "Themes");
                    Toast.makeText(MainActivity.this, getString(R.string.song_repeat_off_toast), Toast.LENGTH_SHORT).show();
                    if (!(a == 1 || a == 2 || a == 4 || a == 5 || a == 7))
                        repeat.setColorFilter(Color.argb(255, 0, 0, 0));
                    else
                        repeat.setColorFilter(Color.argb(255, 255, 255, 255));
                    //DrawableCompat.setTint(repeat.getDrawable(), ContextCompat.getColor(MainActivity.this, R.color.colorBlack));
                } else {
                    sharedPreferenceSingleton.saveAs(MainActivity.this, "Repeat", true);
                    Toast.makeText(MainActivity.this, getString(R.string.song_repeat_on_toast), Toast.LENGTH_SHORT).show();
                    repeat.setColorFilter(Color.argb(255, 236, 100, 75));
                    // DrawableCompat.setTint(repeat.getDrawable(), ContextCompat.getColor(MainActivity.this, R.color.colorAccent));
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
                    Toast.makeText(MainActivity.this, R.string.song_removed_from_favourites, Toast.LENGTH_SHORT).show();
                } else {
                    new MyApplication(MainActivity.this).getWritableDatabase().insertFavourite(song.getID());
                    liked.setVisibility(View.VISIBLE);
                    liked.startAnimation(new CustomAnimation().likeAnimation(MainActivity.this));
                    liked.setVisibility(View.INVISIBLE);
                    Toast.makeText(MainActivity.this, R.string.song_added_to_favourites, Toast.LENGTH_SHORT).show();
                    song.setFavourite(true);
                }
            }

            public void onSwipeDown() {
                hideMainPlayer();
            }

        });

        mainPlayer.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeDown() {
                hideMainPlayer();
            }
        });
        waveView = (WaveView) findViewById(R.id.wave);
        int mBorderWidth = 5;
        waveView.setBorder(mBorderWidth, mBorderColor);

        // set wave view
        mWaveHelper = new WaveHelper(waveView);
        waveView.setShapeType(WaveView.ShapeType.CIRCLE);
        waveView.setWaveColor(
                Color.parseColor("#D32F2F"),
                Color.parseColor("#F44336"));
        if (!sharedPreferenceSingleton.getSavedBoolean(this, "Waves")) {
            mWaveHelper.start();
        } else {
            waveView.setVisibility(View.GONE);
        }

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

        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("Stop");
        registerReceiver(stopReceiver, intentFilter);

        searchBar = (Toolbar) findViewById(R.id.search_bar);
        ImageButton searchClear = (ImageButton) findViewById(R.id.search_bar_clear);
        autoComplete = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        searchClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (autoComplete.getText().toString().length() > 0) {
                    autoComplete.setText("");
                } else {
                    doExitReveal(searchBar);
                }

            }
        });

        autoComplete.setThreshold(2);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(5);
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
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
        shuffle_play = (FloatingActionButton) findViewById(R.id.shuffle_songs);
        shuffle_play.setOnClickListener(togglePlayBtn);
        play_pause.setOnClickListener(togglePlayBtn);
        play_pause_mini.setOnClickListener(togglePlayBtn);

        if (!sharedPreferenceSingleton.getSavedBoolean(MainActivity.this, "sequence1")) {

            View centerview = findViewById(R.id.center_view);
            fancyShowCaseView1 = new FancyShowCaseView.Builder(this)
                    .focusOn(centerview)
                    .title(getString(R.string.showcase1))
                    .closeOnTouch(true)
                    .focusShape(FocusShape.ROUNDED_RECTANGLE)
                    .titleGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL)
                    .titleSize((int) getResources().getDimension(R.dimen.size8dp), 1)
                    .backgroundColor(Color.parseColor("#F221242B"))
                    .build();
            fancyShowCaseView2 = new FancyShowCaseView.Builder(this)
                    .focusOn(miniPlayer)
                    .title(getString(R.string.showcase2))
                    .closeOnTouch(true)
                    .focusShape(FocusShape.ROUNDED_RECTANGLE)
                    .dismissListener(new DismissListener() {
                        @Override
                        public void onDismiss(String id) {
                            showMainPlayer();
                        }

                        @Override
                        public void onSkipped(String id) {

                        }
                    })
                    .titleGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL)
                    .titleSize((int) getResources().getDimension(R.dimen.size8dp), 1)
                    .backgroundColor(Color.parseColor("#F221242B"))
                    .build();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new FancyShowCaseQueue()
                            .add(fancyShowCaseView1)
                            .add(fancyShowCaseView2)
                            .show();
                    sharedPreferenceSingleton.saveAs(MainActivity.this, "sequence1", true);

                }
            }, 1000);
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
                if (item.getItemId() == R.id.action_sort) {
                    dialog = new Dialog(MainActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.sorting_dialog);
                    try {
                        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                    } catch (Exception e) {
                    }
                    Button name=dialog.findViewById(R.id.sort_name);
                    Button duration=dialog.findViewById(R.id.sort_duration);
                    Button date=dialog.findViewById(R.id.sort_added);

                    switch (sharedPreferenceSingleton.getSavedInt(MainActivity.this,"Sort_by")){
                        case 0:
                            name.setTextColor(ContextCompat.getColor(MainActivity.this,R.color.colorAccent));
                            break;
                        case 1:
                            duration.setTextColor(ContextCompat.getColor(MainActivity.this,R.color.colorAccent));
                            break;
                        case 2:
                            date.setTextColor(ContextCompat.getColor(MainActivity.this,R.color.colorAccent));
                            break;
                    }
                    dialog.show();
                    name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sharedPreferenceSingleton.saveAs(MainActivity.this,"Sort_by",0);
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this, "Restart App to view Changes", Toast.LENGTH_SHORT).show();
                        }
                    });
                    duration.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sharedPreferenceSingleton.saveAs(MainActivity.this,"Sort_by",1);
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this, "Restart App to view Changes", Toast.LENGTH_SHORT).show();

                        }
                    });
                    date.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sharedPreferenceSingleton.saveAs(MainActivity.this,"Sort_by",2);
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this, "Restart App to view Changes", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else if (item.getItemId() == R.id.favourites) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    ScrollingFragment scrollingFragment = new ScrollingFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("Action", "Favourites");
                    scrollingFragment.setArguments(bundle);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.drawerLayout, scrollingFragment, "ScrollingFragment");
                    fragmentTransaction.commit();
                } else if (item.getItemId() == R.id.action_search) {
                    doCircularReveal(searchBar);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>
                            (MainActivity.this, android.R.layout.simple_dropdown_item_1line, titles);
                    autoComplete.setAdapter(adapter);
                    autoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String selection = (String) parent.getItemAtPosition(position);
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
        // toolbarPlayer.setNavigationIcon(R.drawable.ic_back);

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
                    try {
                        startActivity(intent);
                        Toast.makeText(MainActivity.this, getString(R.string.opening_youtube), Toast.LENGTH_SHORT).show();
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(MainActivity.this, getString(R.string.not_found_youtube), Toast.LENGTH_SHORT).show();
                    }

                } else if (item.getItemId() == R.id.share) {
                    Uri uri = new FilePathFromId().pathFromID(MainActivity.this, song.getID());
                    if (uri != null) {
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("audio");
                        share.putExtra(Intent.EXTRA_STREAM, uri);
                        startActivity(Intent.createChooser(share, "Share Sound File"));
                    }

                } else if (item.getItemId() == R.id.equalizer) {
                    openEqualizerDialog();
                } else if (item.getItemId() == R.id.edit) {
                    openUpdateDialog();
                }
                return true;
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
                } else if (item.getItemId() == R.id.play_now) {
                    setSongs(selectedSongs);
                    musicService.setSongs(selectedSongs);
                    removeContentSelection();
                    new PlaySongExec(MainActivity.this, 0).startPlaying();
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

    private void openUpdateDialog() {
        try {
            dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.edit_dialog);
            dialog.show();
            final EditText editTextTitle = dialog.findViewById(R.id.dialogTitle);
            final EditText editTextArtist = dialog.findViewById(R.id.dialogArtist);
            final EditText editTextAlbum = dialog.findViewById(R.id.dialogAlbum);

            editTextTitle.setText(currentSong.getName());
            editTextArtist.setText(currentSong.getArtist());
            editTextAlbum.setText(currentSong.getAlbum());
            dialog.findViewById(R.id.create).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String newName = editTextTitle.getText().toString();
                    String newArtist = editTextArtist.getText().toString();
                    String newAlbum = editTextAlbum.getText().toString();
                    if (newName.length() > 0 && newArtist.length() > 0 && newAlbum.length() > 0) {
                        updateName(newName, newArtist, newAlbum);
                        Toast.makeText(MainActivity.this, R.string.successfully_updated, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
            });
            dialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateName(String newTitle, String newArtist, String newAlbum) {
        ContentValues values = new ContentValues(3);
        values.put(MediaStore.Audio.Media.TITLE, newTitle);
        values.put(MediaStore.Audio.Media.ARTIST, newArtist);
        values.put(MediaStore.Audio.Media.ALBUM, newAlbum);
        getContentResolver().update(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                values, MediaStore.Audio.Media._ID + "=" + currentSong.getID(), null);
        title.setText(newTitle);
        title_mini.setText(newTitle);
        artist.setText(newArtist);
        artist_mini.setText(newArtist);
        musicService.updateDetails(newTitle, newArtist, newAlbum);
    }

    private void removeContentSelection() {
        toolbarContext.setVisibility(View.GONE);
        mToolbar.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.fade_in));
        mToolbar.setVisibility(View.VISIBLE);
        miniPlayer.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.fade_in));
        miniPlayer.setVisibility(View.VISIBLE);
        shuffle_play.show();
        selectedID = new ArrayList<>();
        selectedSongs = new ArrayList<>();
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
            shuffle_play.show();
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

        super.onDestroy();
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (searchBar.getVisibility() == View.VISIBLE) {
            doExitReveal(searchBar);
        } else {
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
                    } else {

                        super.onBackPressed();
                    }
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

    public void setCompleteSongList(ArrayList<Song> presetList) {
        completeList = presetList;
        setSongs(completeList);
        titles = getTitles();
    }

    void showMainPlayer() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("ScrollingFragment");
            try {
                fragment.getView().setVisibility(View.GONE);
            } catch (NullPointerException e) {

            }
        }
        shuffle_play.hide();
        mainPlayer.setVisibility(View.VISIBLE);
        toolbarPlayer.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.GONE);
        mViewPager.setVisibility(View.GONE);
        miniPlayer.setVisibility(View.GONE);
        mToolbar.setVisibility(View.GONE);
        mainPlayer.startAnimation(new CustomAnimation().slide_up(MainActivity.this));
        if (!sharedPreferenceSingleton.getSavedBoolean(MainActivity.this, "sequence2")) {
            fancyShowCaseView1 = new FancyShowCaseView.Builder(this)
                    .focusOn(toolbarPlayer)
                    .title(getString(R.string.showcase3))
                    .closeOnTouch(true)
                    .focusShape(FocusShape.ROUNDED_RECTANGLE)
                    .titleGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL)
                    .titleSize((int) getResources().getDimension(R.dimen.size8dp), 1)
                    .backgroundColor(Color.parseColor("#F221242B"))
                    .build();
            fancyShowCaseView1.show();
            sharedPreferenceSingleton.saveAs(MainActivity.this, "sequence2", true);
        }

    }

    void hideMainPlayer() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("ScrollingFragment");
            try {
                if (fragment.getView() != null)
                    fragment.getView().setVisibility(View.VISIBLE);
            } catch (NullPointerException e) {
            }

        } else {
            mainPlayer.startAnimation(new CustomAnimation().slide_down(MainActivity.this));
        }
        mainPlayer.setVisibility(View.GONE);
        miniPlayer.setVisibility(View.VISIBLE);
        mViewPager.setVisibility(View.VISIBLE);
        shuffle_play.show();
        tabLayout.setVisibility(View.VISIBLE);
        toolbarPlayer.setVisibility(View.GONE);
        mToolbar.setVisibility(View.VISIBLE);
    }

    public MusicService getMusicService() {
        return musicService;
    }

    public void playFromSearch(String s) {
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
        shuffle_play.hide();
        mViewPager.setVisibility(View.GONE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            appBarLayout.setElevation(10.0f);
            searchBar.setVisibility(View.VISIBLE);
            int centerX = view.getWidth() - 220;
            int centerY = view.getHeight() / 2;
            int startRadius = 0;
            int endRadius = view.getWidth();
            Animator anim = null;
            anim = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius, endRadius);
            anim.setDuration(250);
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
            int centerX = view.getWidth() - 220;
            int centerY = view.getHeight() / 2;
            int startRadius = view.getWidth();
            Animator anim =
                    ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius, 0);
            anim.setDuration(250);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    searchBar.setVisibility(View.GONE);
                    tabLayout.setVisibility(View.VISIBLE);
                    mToolbar.setVisibility(View.VISIBLE);
                    miniPlayer.setVisibility(View.VISIBLE);
                    shuffle_play.show();
                    mViewPager.setVisibility(View.VISIBLE);
                }
            });
            anim.start();
        } else {
            searchBar.setVisibility(View.GONE);
            tabLayout.setVisibility(View.VISIBLE);
            mToolbar.setVisibility(View.VISIBLE);
            miniPlayer.setVisibility(View.VISIBLE);
            shuffle_play.show();
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
            shuffle_play.show();
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
    public void onTrackLongPress(int c, long songId, boolean songAdded, Song song) {
        toolbar_context_title.setText(c + "");
        if (songAdded) {
            selectedID.add(songId);
            selectedSongs.add(song);
        } else {
            selectedID.remove(songId);
            selectedSongs.remove(song);
        }
        if (c == 0) {
            toolbarContext.setVisibility(View.GONE);
            mToolbar.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.fade_in));
            mToolbar.setVisibility(View.VISIBLE);
            miniPlayer.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.fade_in));
            miniPlayer.setVisibility(View.VISIBLE);
            shuffle_play.show();
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
        shuffle_play.hide();
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        String tabTitles[] = new String[]{getResources().getString(R.string.TAB4), getResources().getString(R.string.TAB1), getResources().getString(R.string.TAB2), getResources().getString(R.string.TAB3), "Genres", getString(R.string.folder)};

        //String tabTitles[] = new String[]{getResources().getString(R.string.TAB4), getResources().getString(R.string.TAB2), getResources().getString(R.string.TAB3)};
        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return SongsFragment.newInstance();
                case 1:
                    return PlaylistFragment.newInstance();
                case 2:
                    return AlbumFragment.newInstance();
                case 3:
                    return ArtistFragment.newInstance();
                case 4:
                    return GenreFragment.newInstance();
                case 5:
                    return FolderFragment.newInstance();

            }
            return SongsFragment.newInstance();
        }

        @Override
        public int getCount() {
            return 6;
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
        try {
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        } catch (NullPointerException e) {
            Log.d("NullPointer", "yes");
        }
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
                selectedSongs.clear();
                toolbarContext.setVisibility(View.GONE);
                mToolbar.setVisibility(View.VISIBLE);
                miniPlayer.setVisibility(View.VISIBLE);
                shuffle_play.show();
                songRefreshListener.OnSongDelete();
                dialog.dismiss();

            }
        });
        Button cancel = (Button) dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedID.clear();
                selectedSongs.clear();
                toolbarContext.setVisibility(View.GONE);
                mToolbar.setVisibility(View.VISIBLE);
                miniPlayer.setVisibility(View.VISIBLE);
                shuffle_play.show();
                songRefreshListener.OnContextBackPressed();
                dialog.dismiss();
            }
        });

    }

    public void openEqualizerDialog() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.equalizer_dialog);
        try {
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        } catch (NullPointerException e) {
            Log.d("NullPointer", "yes");
        }
        dialog.show();

        final SeekBar seekBars[] = new SeekBar[5];
        TextView levels[] = new TextView[5];
        final Spinner spinner = (Spinner) dialog.findViewById(R.id.spinner);
        Switch toggleSwitch = (Switch) dialog.findViewById(R.id.switch1);
        int supportedPresets = musicService.equalizer.getNumberOfPresets();
        final short lowerEqualizerBandLevel = musicService.equalizer.getBandLevelRange()[0];
        final short upperEqualizerBandLevel = musicService.equalizer.getBandLevelRange()[1];
        ArrayList<String> presetList = new ArrayList<>();
        int savedPreset = sharedPreferenceSingleton.getSavedInt(MainActivity.this, "Preset");
        String savedSeekBarProgress = sharedPreferenceSingleton.getSavedString(MainActivity.this, "SeekBarPositions");
        final boolean switchOn = sharedPreferenceSingleton.getSavedBoolean(MainActivity.this, "Equalizer_Switch");

        for (short i = 0; i < 5; i++) {
            final short equalizerBandIndex = i;
            seekBars[i] = (SeekBar) dialog.findViewById(AppConstants.seekBars[i]);
            seekBars[i].setMax(upperEqualizerBandLevel - lowerEqualizerBandLevel);
            levels[i] = (TextView) dialog.findViewById(AppConstants.levels[i]);
            levels[i].setText((musicService.equalizer.getCenterFreq(equalizerBandIndex) / 1000) + "Hz");
            seekBars[i].setProgress((upperEqualizerBandLevel - lowerEqualizerBandLevel) / 2);
            seekBars[i].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    musicService.equalizer.setBandLevel(equalizerBandIndex, (short) (progress + lowerEqualizerBandLevel));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }

        presetList.add("Custom");
        for (short i = 0; i < supportedPresets; i++) {
            presetList.add(musicService.equalizer.getPresetName(i));
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, presetList);
        spinner.setAdapter(arrayAdapter);

        if (savedPreset != 0) {
            for (int i = 0; i < 5; i++) {
                seekBars[i].setEnabled(false);
            }
            spinner.setSelection(savedPreset);
        } else {
            if (savedSeekBarProgress != null) {
                String progress[] = savedSeekBarProgress.split(" ");

                for (short i = 0; i < 5; i++) {
                    seekBars[i].setProgress(Integer.parseInt(progress[i]));
                }
            }
        }
        if (switchOn) {
            toggleSwitch.setChecked(true);
        }


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    musicService.equalizer.usePreset((short) (position - 1));
                    final short lowerEqualizerBandLevel = musicService.equalizer.getBandLevelRange()[0];
                    for (short i = 0; i < 5; i++) {
                        seekBars[i].setProgress(musicService.equalizer.getBandLevel(i) - lowerEqualizerBandLevel);
                        seekBars[i].setEnabled(false);
                    }
                } else {
                    for (short i = 0; i < 5; i++) {
                        seekBars[i].setEnabled(true);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        toggleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!musicService.equalizer.getEnabled()) {
                        musicService.equalizer.setEnabled(true);
                        sharedPreferenceSingleton.saveAs(MainActivity.this, "Equalizer_Switch", true);
                    }
                } else {
                    musicService.equalizer.setEnabled(false);
                    sharedPreferenceSingleton.saveAs(MainActivity.this, "Equalizer_Switch", false);
                }
            }
        });

        Button done = (Button) dialog.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String seekBarPositions = "";
                if (spinner.getSelectedItemPosition() == 0) {      //if spinner is set to custom
                    for (int i = 0; i < 5; i++) {
                        seekBarPositions += seekBars[i].getProgress() + " ";
                    }
                    sharedPreferenceSingleton.saveAs(MainActivity.this, "SeekBarPositions", seekBarPositions);     //save all seekbar progress
                }
                sharedPreferenceSingleton.saveAs(MainActivity.this, "Preset", spinner.getSelectedItemPosition());   //save spinner position

                dialog.dismiss();
            }
        });
    }

    public void switchContent(int id, Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(id, fragment, "ScrollingFragment");
        ft.addToBackStack(null);
        ft.commit();
    }
}

