package com.riseapps.xmusic.model;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.riseapps.xmusic.R;
import com.riseapps.xmusic.executor.GenerateNotification;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;



public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    public MediaPlayer player;

    private ArrayList<Song> songs;

    private int songPos;

    private final IBinder musicBind = new MusicBinder();

    private OnSongChangedListener onSongChangedListener;

    GenerateNotification generateNotification;

    private static final int NOTIFICATION_ID = 1;

    public static final int STOPPED = 0;
    public static final int PAUSED = 1;
    public static final int PLAYING = 2;
    private int playerState = STOPPED;
    public SeekBar mSeekBar;
    public TextView mCurrentPosition;
    public TextView mTotalDuration;
    private int mInterval = 1000;

    public void onCreate() {

        super.onCreate();
        songPos = 0;
        player = new MediaPlayer();
        initMusicPlayer();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        generateNotification = new GenerateNotification();
        startForeground(NOTIFICATION_ID,generateNotification.getNotification(this));
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        int newPos = songPos + 1;
        if (newPos == songs.size())
            newPos = 0;
        setSong(newPos);
        togglePlay();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        // Start playback
        mp.start();
        int duration = mp.getDuration();
        mSeekBar.setMax(duration);
        mSeekBar.postDelayed(mProgressRunner, mInterval);

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        mSeekBar=null;
        onSongChangedListener.onPlayerStatusChanged(playerState = STOPPED);
        player.stop();
        player.reset();
        player.release();
        stopSelf();
        super.onTaskRemoved(rootIntent);
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    public void initMusicPlayer() {

        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    public void setSong(int songIndex) {
        if (songs.size() <= songIndex || songIndex < 0) // if the list is empty... just return
            return;
        songPos = songIndex;
        playerState = STOPPED;
        onSongChangedListener.onSongChanged(songs.get(songPos));
    }

    public void togglePlay() {
        switch (playerState) {
            case STOPPED:
                playSong();
                break;
            case PAUSED:
                player.start();
                onSongChangedListener.onPlayerStatusChanged(playerState = PLAYING);
                //  Toast.makeText(this, "Resume song", Toast.LENGTH_SHORT).show();
                mProgressRunner.run();
                break;
            case PLAYING:
                player.pause();
                onSongChangedListener.onPlayerStatusChanged(playerState = PAUSED);
                mSeekBar.removeCallbacks(mProgressRunner);
                //   Toast.makeText(this, "Pause song", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void playSong() {
        if (songs.size() <= songPos)
            return;
        player.reset();

        Song playSong = songs.get(songPos);
        long currSongID = playSong.getID();
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSongID);
        try {
            player.setDataSource(getApplicationContext(), trackUri);
        } catch (Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }

        player.prepareAsync();
        mProgressRunner.run();
        onSongChangedListener.onPlayerStatusChanged(playerState = PLAYING);
    }

    public interface OnSongChangedListener {
        void onSongChanged(Song song);

        void onPlayerStatusChanged(int status);
    }

    public void setOnSongChangedListener(OnSongChangedListener listener) {   // Sets a callback to execute when we switch songs.. ie: update UI
        onSongChangedListener = listener;
    }

    public void setUIControls(SeekBar seekBar, TextView currentPosition, TextView totalDuration) {
        mSeekBar = seekBar;
        mCurrentPosition = currentPosition;
        mTotalDuration = totalDuration;
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    // Change current position of the song playback
                    player.seekTo(progress);
                }

                // Update our textView to display the correct number of second in format 0:00
                mCurrentPosition.setText(String.format(Locale.getDefault(), "%d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(progress),
                        TimeUnit.MILLISECONDS.toSeconds(progress) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(progress))
                ));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    public int getCurrentIndex() {
        return songPos;
    }

    private Runnable mProgressRunner = new Runnable() {
        @Override
        public void run() {
            if (mSeekBar != null) {
                mSeekBar.setProgress(player.getCurrentPosition());

                if (player.isPlaying()) {
                    mSeekBar.postDelayed(mProgressRunner, mInterval);
                }
            }
        }
    };

}