package com.riseapps.xmusic.model;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.media.session.MediaSessionManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.riseapps.xmusic.R;
import com.riseapps.xmusic.component.AppConstants;
import com.riseapps.xmusic.component.SharedPreferenceSingelton;
import com.riseapps.xmusic.executor.RecentQueue;
import com.riseapps.xmusic.model.Pojo.Song;
import com.riseapps.xmusic.view.activity.MainActivity;

import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.riseapps.xmusic.component.AppConstants.ACTION_NEXT;
import static com.riseapps.xmusic.component.AppConstants.ACTION_PAUSE;
import static com.riseapps.xmusic.component.AppConstants.ACTION_PLAY;
import static com.riseapps.xmusic.component.AppConstants.ACTION_PREVIOUS;
import static com.riseapps.xmusic.component.AppConstants.ACTION_STOP;
import static com.riseapps.xmusic.component.AppConstants.ANDROID_CHANNEL_ID;
import static com.riseapps.xmusic.component.AppConstants.ANDROID_CHANNEL_NAME;

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {

    public SharedPreferenceSingelton sharedPreferenceSingelton;
    private MediaSessionCompat mediaSession;
    private MediaControllerCompat.TransportControls transportControls;
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

    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleIncomingActions(intent);
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
        if(telephonyManager != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        if (headsetPlugReceiver != null) {
            unregisterReceiver(headsetPlugReceiver);
            headsetPlugReceiver = null;
        }
        player.release();
        mediaSession.release();
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
        if (playerState==STOPPED) {
            try {
                initMediaSession();
            } catch (RemoteException e) {
                e.printStackTrace();
                stopSelf();
            }
        }
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
                    buildNotification(playerState);
                }
                break;
            case PAUSED:
                if (requestAudioFocus()) {
                    player.start();
                    onSongChangedListener.onPlayerStatusChanged(playerState = PLAYING);
                    mProgressRunner.run();
                    buildNotification(playerState);
                }
                break;
            case PLAYING:
                player.pause();
                onSongChangedListener.onPlayerStatusChanged(playerState = PAUSED);
                mSeekBar.removeCallbacks(mProgressRunner);
                buildNotification(playerState);
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
            updateMetaData();
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

    private void initMediaSession() throws RemoteException {
        //if (mediaSessionManager != null) return; //mediaSessionManager exists

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            MediaSessionManager mediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        }
        // Create a new MediaSession
        mediaSession = new MediaSessionCompat(getApplicationContext(), "XMusicPro");
        //Get MediaSessions transport controls
        transportControls = mediaSession.getController().getTransportControls();
        //set MediaSession -> ready to receive media commands
        mediaSession.setActive(true);
        //indicate that the MediaSession handles transport control commands
        // through its MediaSessionCompat.Callback.
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS|MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Attach Callback to receive MediaSession updates
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            // Implement callbacks
            @Override
            public void onPlay() {
                super.onPlay();
                togglePlay();
                buildNotification(playerState);
            }

            @Override
            public void onPause() {
                super.onPause();
                togglePlay();
                buildNotification(playerState);
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                int current = getCurrentIndex();
                int next = current + 1;
                if (next == songs.size())// If current was the last song, then play the first song in the list
                    next = 0;
                setSong(next);
                togglePlay();
                updateMetaData();
                buildNotification(playerState);
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                int current = getCurrentIndex();
                int previous = current - 1;
                if (previous < 0)            // If current was 0, then play the last song in the list
                    previous = songs.size() - 1;
                setSong(previous);
                togglePlay();
                updateMetaData();
                buildNotification(playerState);
            }

            @Override
            public void onStop() {
                super.onStop();
                removeNotification();
                //Stop the service
                stopSelf();
            }

            @Override
            public void onSeekTo(long position) {
                super.onSeekTo(position);
            }
        });
    }

    private void updateMetaData() {
        Bitmap albumArt = BitmapFactory.decodeResource(getResources(),
                R.drawable.placeholder);
        MediaMetadataCompat.Builder metadataBuilder = new MediaMetadataCompat.Builder();
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, songs.get(songPos).getName());
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, songs.get(songPos).getArtist());
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, songs.get(songPos).getAlbum());
        metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART,albumArt);

        mediaSession.setMetadata(metadataBuilder.build());

        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder();
        stateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_PAUSE );
        stateBuilder.setState(playerState,PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1.0f);
        mediaSession.setPlaybackState(stateBuilder.build());
    }

    private void buildNotification(int playerState) {

        int notificationAction = R.drawable.ic_notification_pause;//needs to be initialized
        PendingIntent play_pauseAction = null;
        boolean ongoing=false;
        if (playerState==PLAYING) {
            notificationAction = R.drawable.ic_notification_pause;
            ongoing=true;
            //create the pause action
            play_pauseAction = playbackAction(1);
        } else if (playerState==PAUSED||playerState==STOPPED) {
            notificationAction = R.drawable.ic_notification_play;
            ongoing=false;
            //create the play action
            play_pauseAction = playbackAction(0);
        }
        Bitmap albumArt=null;
        try {
            albumArt = AppConstants.decodeUri(getApplication()
                    , songs.get(songPos).getImagepath(),200);
        } catch (FileNotFoundException e) {
            albumArt = BitmapFactory.decodeResource(getResources(),R.drawable.placeholder);
            e.printStackTrace();
        }
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 2, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        if(Build.VERSION.SDK_INT<26) {
            NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                    .setShowWhen(false)
                    .setStyle(new NotificationCompat.MediaStyle()
                            .setMediaSession(mediaSession.getSessionToken())
                            .setShowActionsInCompactView(0, 1, 2))
                    .setColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                    .setOngoing(ongoing)
                    .setLargeIcon(albumArt)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentText(songs.get(songPos).getArtist())
                    .setContentTitle(songs.get(songPos).getName())
                    .setContentInfo(songs.get(songPos).getAlbum())
                    .setContentIntent(resultPendingIntent)
                    .addAction(R.drawable.ic_notification_prev, "previous", playbackAction(3))
                    .addAction(notificationAction, "pause", play_pauseAction)
                    .addAction(R.drawable.ic_notification_next, "next", playbackAction(2))
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notificationBuilder.build());
        }else{
            android.support.v4.app.NotificationCompat.Builder mBuilder=new android.support.v4.app.NotificationCompat.Builder(getApplicationContext(),ANDROID_CHANNEL_ID);
            mBuilder.setChannelId(ANDROID_CHANNEL_ID);
            mBuilder.setShowWhen(false)
                    .setStyle(new NotificationCompat.MediaStyle()
                            .setMediaSession(mediaSession.getSessionToken())
                            .setShowActionsInCompactView(0, 1, 2))
                    .setColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                    .setOngoing(ongoing)
                    .setLargeIcon(albumArt)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentText(songs.get(songPos).getArtist())
                    .setContentTitle(songs.get(songPos).getName())
                    .setContentInfo(songs.get(songPos).getAlbum())
                    .setContentIntent(resultPendingIntent)
                    .addAction(R.drawable.ic_notification_prev, "previous", playbackAction(3))
                    .addAction(notificationAction, "pause", play_pauseAction)
                    .addAction(R.drawable.ic_notification_next, "next", playbackAction(2));

            NotificationManager mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel androidChannel = new NotificationChannel(ANDROID_CHANNEL_ID,
                        ANDROID_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                androidChannel.enableLights(true);
                androidChannel.setLightColor(Color.RED);
                androidChannel.setSound(null,null);
                androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                mManager.createNotificationChannel(androidChannel);
            mManager.notify(NOTIFICATION_ID, mBuilder.build());
        }
    }

    private PendingIntent playbackAction(int actionNumber) {
        Intent playbackAction = new Intent(this, MusicService.class);
        switch (actionNumber) {
            case 0:
                // Play
                playbackAction.setAction(ACTION_PLAY);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 1:
                // Pause
                playbackAction.setAction(ACTION_PAUSE);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 2:
                // Next track
                playbackAction.setAction(ACTION_NEXT);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 3:
                // Previous track
                playbackAction.setAction(ACTION_PREVIOUS);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            default:
                break;
        }
        return null;
    }

    private void handleIncomingActions(Intent playbackAction) {
        if (playbackAction == null || playbackAction.getAction() == null) return;

        String actionString = playbackAction.getAction();
        if (actionString.equalsIgnoreCase(ACTION_PLAY)) {
            transportControls.play();
        } else if (actionString.equalsIgnoreCase(ACTION_PAUSE)) {
            transportControls.pause();
        } else if (actionString.equalsIgnoreCase(ACTION_NEXT)) {
            transportControls.skipToNext();
        } else if (actionString.equalsIgnoreCase(ACTION_PREVIOUS)) {
            transportControls.skipToPrevious();
        } else if (actionString.equalsIgnoreCase(ACTION_STOP)) {
            transportControls.stop();
        }
    }

    private void removeNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

}