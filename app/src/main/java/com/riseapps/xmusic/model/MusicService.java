package com.riseapps.xmusic.model;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.riseapps.xmusic.component.SharedPreferenceSingelton;
import com.riseapps.xmusic.executor.GenerateNotification;
import com.riseapps.xmusic.executor.RecentQueue;
import com.riseapps.xmusic.model.Pojo.Song;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {

    public SharedPreferenceSingelton sharedPreferenceSingelton;

    public MediaPlayer player;
    public Equalizer equalizer;
    public HeadsetPlugReceiver headsetPlugReceiver;
    public ArrayList<Song> songs;
    public ArrayList<Integer> shufflePlayed = new ArrayList<>();
    AudioManager audioManager;
    private int songPos;

    private final IBinder musicBind = new MusicBinder();

    private OnSongChangedListener onSongChangedListener;

    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;

    public static final int STOPPED = 0;
    public static final int PAUSED = 1;
    public static final int PLAYING = 2;
    private int playerState = STOPPED;
    public SeekBar mSeekBar;
    public TextView mCurrentPosition;
    public TextView mTotalDuration;
    private int mInterval = 1000;
    private static final int NOTIFICATION_ID = 1;
    ArrayList<Long> ids = new ArrayList<>();
    public long currSongID;

    public void onCreate() {

        super.onCreate();
        sharedPreferenceSingelton = new SharedPreferenceSingelton();
        songPos = 0;
        player = new MediaPlayer();
        initMusicPlayer();
        headsetPlugReceiver = new HeadsetPlugReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.HEADSET_PLUG");
        registerReceiver(headsetPlugReceiver, intentFilter);
        intentFilter = new IntentFilter();
        intentFilter.addAction("play");
        intentFilter.addAction("next");
        intentFilter.addAction("previous");
        registerReceiver(myReceiver, intentFilter);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    if(playerState==PLAYING){
                        togglePlay();
                    }

                } else if(state == TelephonyManager.CALL_STATE_IDLE) {

                } else if(state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    //A call is dialing, active or on hold
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };
        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if(telephonyManager != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        int newPos = 0;
        if (sharedPreferenceSingelton.getSavedBoolean(this, "Repeat"))
            newPos = songPos;
        else
            newPos = songPos + 1;
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
        // onSongChangedListener.onPlayerStatusChanged(playerState = STOPPED);
        mSeekBar = null;
        player.stop();
        player.reset();
        player.release();
        stopSelf();
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(NOTIFICATION_ID);
        if (mSeekBar != null)
            mSeekBar = null;
        onSongChangedListener.onPlayerStatusChanged(playerState = STOPPED);
        unregisterReceiver(myReceiver);
        if(telephonyManager != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        if (headsetPlugReceiver != null) {
            unregisterReceiver(headsetPlugReceiver);
            headsetPlugReceiver = null;
        }
        player.release();
        audioManager.abandonAudioFocus(this);

        super.onDestroy();
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            if (player.isPlaying()) {
                togglePlay();
            }
        }
    }

    public void updateDetails(String title,String artist,String album) {
        songs.get(songPos).setName(title);
        songs.get(songPos).setArtist(artist);
        songs.get(songPos).setAlbum(album);
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    public MusicService getInstance() {
        return MusicService.this;
    }

    public void initMusicPlayer() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);

    }

    public boolean requestAudioFocus() {
        int result = audioManager.requestAudioFocus(this,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return true;
        }
        return false;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
        shufflePlayed.clear();
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSong(int songIndex) {
        if (sharedPreferenceSingelton.getSavedBoolean(this, "Shuffle")) {
            if (shufflePlayed.size() == songs.size()) {
                shufflePlayed.clear();
            }
            while (true) {
                int random = new Random().nextInt(songs.size());
                if (!shufflePlayed.contains(random)) {
                    songIndex = random;
                    shufflePlayed.add(random);
                    break;
                }
            }
        }
        if (songs.size() <= songIndex || songIndex < 0) // if the list is empty... just return
            return;
        if (songs.get(songIndex) == null) {
            songIndex++;
            if (songIndex == songs.size())
                songIndex = 0;
        }

        songPos = songIndex;
        equalizer = new Equalizer(0, player.getAudioSessionId());
        if (sharedPreferenceSingelton.getSavedBoolean(this, "Equalizer_Switch")) {
            equalizer.setEnabled(true);
            int presetNumber = sharedPreferenceSingelton.getSavedInt(this, "Preset");
            if (presetNumber != 0)
                equalizer.usePreset((short) (presetNumber - 1));
            else {
                final short lowerEqualizerBandLevel = equalizer.getBandLevelRange()[0];
                String savedSeekBarProgress = sharedPreferenceSingelton.getSavedString(this, "SeekBarPositions");
                if (savedSeekBarProgress != null) {
                    String progress[] = savedSeekBarProgress.split(" ");
                    for (short i = 0; i < 5; i++) {
                        equalizer.setBandLevel(i, (short) (Integer.parseInt(progress[i]) + lowerEqualizerBandLevel));
                    }
                }

            }

        }

        playerState = STOPPED;
        onSongChangedListener.onSongChanged(songs.get(songPos));
    }

    public void togglePlay() {
        switch (playerState) {
            case STOPPED:
                if (requestAudioFocus()) {
                    playSong();
                    new GenerateNotification(1).getNotification(this, getInstance());
                }
                break;
            case PAUSED:
                if (requestAudioFocus()) {
                    player.start();
                    onSongChangedListener.onPlayerStatusChanged(playerState = PLAYING);
                    mProgressRunner.run();
                    new GenerateNotification(1).getNotification(this, getInstance());
                }
                break;
            case PLAYING:
                player.pause();
                onSongChangedListener.onPlayerStatusChanged(playerState = PAUSED);
                mSeekBar.removeCallbacks(mProgressRunner);
                new GenerateNotification(0).getNotification(this, getInstance());
                break;
        }
    }

    private void playSong() {

        if (songs.size() <= songPos)
            return;
        player.reset();

        Song playSong = songs.get(songPos);
        currSongID = playSong.getID();
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSongID);
        try {
            player.setDataSource(getApplicationContext(), trackUri);
            player.prepareAsync();
            mProgressRunner.run();
            onSongChangedListener.onPlayerStatusChanged(playerState = PLAYING);
            String s = sharedPreferenceSingelton.getSavedString(this, "Recent");
            if (s == null) {
                ids = new RecentQueue().pushPop(ids, currSongID);
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<Long>>() {
                }.getType();
                String recentJSON = gson.toJson(ids, type);
                sharedPreferenceSingelton.saveAs(this, "Recent", recentJSON);
            } else {
                ids = new Gson().fromJson(s, new TypeToken<ArrayList<Long>>() {
                }.getType());
                ids = new RecentQueue().pushPop(ids, currSongID);
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<Long>>() {
                }.getType();
                String recentJSON = gson.toJson(ids, type);
                sharedPreferenceSingelton.saveAs(this, "Recent", recentJSON);
            }

        } catch (Exception e) {
            songs.remove(getCurrentIndex());
        }


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

    public class HeadsetPlugReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                return;
            }
            boolean disconnectHeadphones = (intent.getIntExtra("state", 0) == 0);
            if (player.isPlaying() && disconnectHeadphones) {
                togglePlay();
            }
        }
    }


    private final BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case "play":
                    togglePlay();
                    break;
                case "next": {
                    int current = getCurrentIndex();
                    int next = current + 1;
                    if (next == songs.size())// If current was the last song, then play the first song in the list
                        next = 0;
                    setSong(next);
                    togglePlay();
                    break;
                }
                case "previous": {
                    int current = getCurrentIndex();
                    int previous = current - 1;
                    if (previous < 0)            // If current was 0, then play the last song in the list
                        previous = songs.size() - 1;
                    setSong(previous);
                    togglePlay();
                    break;
                }
            }
        }
    };


}