package com.riseapps.xmusic.view.Activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
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
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.claudiodegio.msv.OnSearchViewListener;
import com.claudiodegio.msv.SuggestionMaterialSearchView;
import com.gelitenight.waveview.library.WaveView;
import com.riseapps.xmusic.R;
import com.riseapps.xmusic.component.CustomAnimation;
import com.riseapps.xmusic.executor.Interfaces.AlbumRefreshListener;
import com.riseapps.xmusic.executor.Interfaces.ArtistRefreshListener;
import com.riseapps.xmusic.executor.Interfaces.ContextMenuListener;
import com.riseapps.xmusic.executor.Interfaces.SongRefreshListener;
import com.riseapps.xmusic.executor.MyApplication;
import com.riseapps.xmusic.executor.PlaySongExec;
import com.riseapps.xmusic.executor.RecycleViewAdapters.SongAdapter;
import com.riseapps.xmusic.executor.ShakeDetector;
import com.riseapps.xmusic.executor.Interfaces.SongLikedListener;
import com.riseapps.xmusic.executor.UpdateSongs;
import com.riseapps.xmusic.model.MusicService;
import com.riseapps.xmusic.model.Pojo.Album;
import com.riseapps.xmusic.model.Pojo.Artist;
import com.riseapps.xmusic.model.Pojo.Song;
import com.riseapps.xmusic.utils.WaveHelper;
import com.riseapps.xmusic.view.Fragment.AlbumFragment;
import com.riseapps.xmusic.view.Fragment.ArtistFragment;
import com.riseapps.xmusic.view.Fragment.PlaylistFragment;
import com.riseapps.xmusic.view.Fragment.ScrollingFragment;
import com.riseapps.xmusic.view.Fragment.SongsFragment;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends BaseMatSearchViewActivity implements ScrollingFragment.OnFragmentInteractionListener, PlaylistFragment.OnFragmentInteractionListener, OnSearchViewListener {

    private ArrayList<Song> songList = new ArrayList<>();
    private MusicService musicService;
    private Intent playIntent;
    public boolean musicPlaying, isMusicShuffled = false;
    //Mini & Main player layouts
    private CardView miniPlayer;
    private ConstraintLayout mainPlayer;
    //MiniPlayer items
    TextView title_mini, artist_mini;
    ImageButton play_pause_mini;
    ImageView album_art_mini;
    //MainPlayer items
    TextView title, artist, currentPosition, totalDuration;
    public ImageButton play_pause, prev, next, repeat, shuffle;
    ImageView album_art;
    private SeekBar seekBar;
    private SongLikedListener mListener;
    private final Context ctx = MainActivity.this;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar, toolbarPlayer, toolbarContext;
    ProgressBar progressBar;

    private WaveHelper mWaveHelper;

    private int mBorderColor = Color.parseColor("#000000");
    private int mBorderWidth = 5;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    private SongRefreshListener songRefreshListener;
    private ArtistRefreshListener artistRefreshListener;
    private AlbumRefreshListener albumRefreshListener;

    private SongsFragment songFragment;
    private ContextMenuListener clearAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_main);
        initiallize();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
                musicService.togglePlay();

            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = musicService.getCurrentIndex();
                int previous = current - 1;
                if (previous < 0)            // If current was 0, then play the last song in the list
                    previous = songList.size() - 1;
                musicService.setSong(previous);
                musicService.togglePlay();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = musicService.getCurrentIndex();
                int next = current + 1;
                if (next == songList.size())// If current was the last song, then play the first song in the list
                    next = 0;
                musicService.setSong(next);
                musicService.togglePlay();
            }
        });
        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isMusicShuffled) {
                    musicService.sortSongs();
                    isMusicShuffled = false;
                    DrawableCompat.setTint(shuffle.getDrawable(), ContextCompat.getColor(MainActivity.this, R.color.colorBlack));
                } else {
                    musicService.shuffleSongs();
                    isMusicShuffled = true;
                    DrawableCompat.setTint(shuffle.getDrawable(), ContextCompat.getColor(MainActivity.this, R.color.colorAccent));
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

        final WaveView waveView = (WaveView) findViewById(R.id.wave);
        waveView.setBorder(mBorderWidth, mBorderColor);

        // set wave view
        mWaveHelper = new WaveHelper(waveView);
        waveView.setShapeType(WaveView.ShapeType.CIRCLE);
        waveView.setWaveColor(
                Color.parseColor("#D32F2F"),
                Color.parseColor("#F44336"));
        mWaveHelper.start();
    }

    private View.OnClickListener togglePlayBtn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            musicService.togglePlay();
        }
    };

    private void initiallize() {
        progressBar= (ProgressBar) findViewById(R.id.progress);
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
                } else if (item.getItemId() == R.id.settings) {
                    startActivityForResult(new Intent(MainActivity.this, SettingsActivity.class), 1);
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
                if (item.getItemId() == R.id.favouritesPlayer) {
                    View v = findViewById(R.id.favouritesPlayer);
                    v.startAnimation(new CustomAnimation().likeAnimation(MainActivity.this));
                    Song song = songList.get(musicService.getCurrentIndex());
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
                    startActivityForResult(i, 1);
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
        toolbarContext.setNavigationIcon(R.drawable.ic_back);
        toolbarContext.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.favouritesPlayer) {
                    Toast.makeText(MainActivity.this, "Action 1", Toast.LENGTH_SHORT).show();
                } else if (item.getItemId() == R.id.playlist) {
                    Toast.makeText(MainActivity.this, "Action 2", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
        toolbarContext.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolbarContext.setVisibility(View.GONE);
                mToolbar.setVisibility(View.VISIBLE);
                clearAll.onClearAll();
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

    }

    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(final ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();
            musicService.setSongs(songList);
            musicService.setUIControls(seekBar, currentPosition, totalDuration);
            Log.d("Songs", "Connected to service");
            musicService.setOnSongChangedListener(new MusicService.OnSongChangedListener() {
                @Override
                public void onSongChanged(Song song) {
                    if (!song.getImagepath().equalsIgnoreCase("no_image")) {
                        Glide.with(MainActivity.this).load(Uri.parse(song.getImagepath())).into(album_art);
                        Glide.with(MainActivity.this).load(Uri.parse(song.getImagepath())).into(album_art_mini);
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
                            break;
                        case MusicService.PAUSED:
                            play_pause.setImageResource(R.drawable.ic_play);
                            play_pause_mini.setImageResource(R.drawable.ic_play);
                            musicPlaying = false;
                            mWaveHelper.cancel();
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

    @Override
    protected void onStart() {
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            startService(playIntent);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);

            miniPlayer.setVisibility(View.VISIBLE);
            miniPlayer.setAlpha(0.f);
            miniPlayer.animate()
                    .alpha(1.f)
                    .setDuration(1000)
                    .start();
            mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onDestroy() {
        unbindService(musicConnection);
        stopService(playIntent);
        //new MyApplication(this).getWritableDatabase().deleteAllSongs();
        //new UpdateSongs(this).fetchSongs();
        mSensorManager.unregisterListener(mShakeDetector);
        super.onDestroy();
    }

    @Override
    protected void onResume() {

        if (mainPlayer.getVisibility() == View.VISIBLE) {
            miniPlayer.setVisibility(View.VISIBLE);
            mViewPager.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.VISIBLE);
            mainPlayer.setVisibility(View.GONE);
        }
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (mainPlayer.getVisibility() == View.VISIBLE) {
            hideMainPlayer();
        } else {
            if (musicPlaying) {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0)
                    getSupportFragmentManager().popBackStackImmediate();
                else
                    moveTaskToBack(true);
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Toast.makeText(this, "helo", Toast.LENGTH_SHORT).show();
    }

    public ArrayList<Song> getSongs() {
        return songList;
    }

    public void setSongs(ArrayList<Song> songList) {
        this.songList = songList;

    }

    void showMainPlayer() {
        //   mainPlayer.startAnimation(new CustomAnimation().slideShow(MainActivity.this));
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
        for (Song song : songList) {
            if (song.getName().equalsIgnoreCase(s)) {
                new PlaySongExec(this, songList.indexOf(song)).startPlaying();
                // Toast.makeText(this, ""+songList.indexOf(song), Toast.LENGTH_SHORT).show();
            }
        }
        //startActivity(new Intent(this, ScrollingActivity.class));
        return false;
    }

    @Override
    public void onQueryTextChange(String s) {

    }

    public void setListener(SongLikedListener mListener) {
        this.mListener = mListener;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        String tabTitles[] = new String[]{"PLAYLIST", "ALBUM", "ARTISTS", "TRACKS"};

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
                        public void onShowToolbar() {
                            Toast.makeText(MainActivity.this, "hello in activity", Toast.LENGTH_SHORT).show();
                            toolbarContext.setVisibility(View.VISIBLE);
                            mToolbar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onHideToolbar() {
                            toolbarContext.setVisibility(View.GONE);
                            mToolbar.setVisibility(View.VISIBLE);
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

        } else if (resultCode == Activity.RESULT_OK) {
            String str = data.getStringExtra("selected_playlist");
            if (!str.equalsIgnoreCase("")) {
                long id = songList.get(musicService.getCurrentIndex()).getID();
                new MyApplication(MainActivity.this).getWritableDatabase().addSongToPlaylists(id, str);
                Toast.makeText(MainActivity.this, "Added to Playlist", Toast.LENGTH_SHORT).show();
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

    public void setSongRefreshListener(SongRefreshListener refreshListener){
        this.songRefreshListener=refreshListener;
    }

    public void setArtistRefreshListener(ArtistRefreshListener refreshListener){
        this.artistRefreshListener=refreshListener;
    }

    public void setAlbumRefreshListener(AlbumRefreshListener refreshListener){
        this.albumRefreshListener=refreshListener;
    }

    private class RefreshAsync extends AsyncTask<Void, Void, Void> {

        ArrayList<Song> songs = new ArrayList<>();
        ArrayList<Artist> artists = new ArrayList<>();
        ArrayList<Album> albums = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            unbindService(musicConnection);
            stopService(playIntent);
            mViewPager.setAlpha(0.8f);
           progressBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String adString = getResources().getString(R.string.adStringPlaceholder);
            new UpdateSongs(MainActivity.this).refreshList();
            songs = new MyApplication(MainActivity.this).getWritableDatabase().readSongs();
            artists = new MyApplication(MainActivity.this).getWritableDatabase().readArtists();
            albums = new MyApplication(MainActivity.this).getWritableDatabase().readAlbums();

            // Place 3 ads for album fragment
            Album albumAdOne = new Album();
            albumAdOne.setName(adString);
            albumAdOne.setImagepath("NoImage");
            albumAdOne.setViewType(2);
            albums.add(3, albumAdOne);

            Album albumAdTwo = new Album();
            albumAdTwo.setName(adString);
            albumAdTwo.setImagepath("NoImage");
            albumAdTwo.setViewType(2);
            albums.add(8, albumAdTwo);

            Album albumAdThree = new Album();
            albumAdThree.setName(adString);
            albumAdThree.setImagepath("NoImage");
            albumAdThree.setViewType(2);
            albums.add(13, albumAdThree);

            // Place 3 ads for artist fragment
            Artist artistAdOne = new Artist();
            artistAdOne.setName(adString);
            artistAdOne.setImagepath("NoImage");
            artistAdOne.setViewType(2);
            artists.add(4, artistAdOne);

            Artist artistAdTwo = new Artist();
            artistAdTwo.setName(adString);
            artistAdTwo.setImagepath("NoImage");
            artistAdTwo.setViewType(2);
            artists.add(11, artistAdTwo);

            Artist artistAdThree = new Artist();
            artistAdThree.setName(adString);
            artistAdThree.setImagepath("NoImage");
            artistAdThree.setViewType(2);
            artists.add(17, artistAdThree);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            songRefreshListener.OnSongRefresh(songs);
            albumRefreshListener.OnAlbumRefresh(albums);
            artistRefreshListener.OnArtistRefresh(artists);
            playIntent = new Intent(MainActivity.this, MusicService.class);
            startService(playIntent);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);

            mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
            progressBar.setVisibility(View.GONE);
            mViewPager.setAlpha(1.0f);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            super.onPostExecute(aVoid);
        }
    }


}

