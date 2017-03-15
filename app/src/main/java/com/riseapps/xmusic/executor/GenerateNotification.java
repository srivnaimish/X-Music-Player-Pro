package com.riseapps.xmusic.executor;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.riseapps.xmusic.R;
import com.riseapps.xmusic.model.MusicService;
import com.riseapps.xmusic.model.Song;
import com.riseapps.xmusic.view.MainActivity;

/**
 * Created by naimish on 12/3/17.
 */

public class GenerateNotification {
    static MusicService musicService;
    public Notification getNotification(Context context,MusicService musicService) {
        this.musicService=musicService;
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 2,intent, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(false);
        mBuilder.setOngoing(true);
        int color = context.getResources().getColor(android.R.color.white);
        mBuilder.setSmallIcon(R.drawable.ic_play);
        mBuilder.setTicker("Music Playing");
        mBuilder.setColor(color);
        mBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);


        Intent playIntent = new Intent(context, SwitchButtonListener.class);
        playIntent.setAction("play");
        Intent nextIntent = new Intent(context, SwitchButtonListener.class);
        nextIntent.setAction("next");
        Intent prevIntent = new Intent(context, SwitchButtonListener.class);
        prevIntent.setAction("previous");

        PendingIntent pendingPlayIntent = PendingIntent.getBroadcast(context, 0,playIntent, 0);
        PendingIntent pendingNextIntent = PendingIntent.getBroadcast(context, 0,nextIntent, 0);
        PendingIntent pendingPrevIntent = PendingIntent.getBroadcast(context, 0,prevIntent, 0);
       // Song song = musicService.getSongs().get(musicService.getCurrentIndex());
        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification_content_layout);
        contentView.setTextViewText(R.id.track, "Song Name here");
        contentView.setImageViewResource(R.id.play_pause,R.drawable.ic_notification_play);
        contentView.setImageViewResource(R.id.prev,R.drawable.ic_prev);
        contentView.setImageViewResource(R.id.next,R.drawable.ic_next);
        contentView.setImageViewResource(R.id.imageView,R.drawable.empty);

        contentView.setOnClickPendingIntent(R.id.play_pause,pendingPlayIntent);
        contentView.setOnClickPendingIntent(R.id.next,pendingNextIntent);
        contentView.setOnClickPendingIntent(R.id.prev,pendingPrevIntent);
        mBuilder.setCustomContentView(contentView);

        RemoteViews expandedView = new RemoteViews(context.getPackageName(), R.layout.notification_expand);
        expandedView.setTextViewText(R.id.track, "Song Name here");
        expandedView.setTextViewText(R.id.artist, "Artist Name here");
        expandedView.setImageViewResource(R.id.play_pause,R.drawable.ic_notification_play);
        expandedView.setImageViewResource(R.id.prev,R.drawable.ic_prev);
        expandedView.setImageViewResource(R.id.next,R.drawable.ic_next);
        expandedView.setImageViewResource(R.id.imageView,R.drawable.empty);

        expandedView.setOnClickPendingIntent(R.id.play_pause,pendingPlayIntent);
        expandedView.setOnClickPendingIntent(R.id.next,pendingNextIntent);
        expandedView.setOnClickPendingIntent(R.id.prev,pendingPrevIntent);
        mBuilder.setCustomBigContentView(expandedView);

        return mBuilder.build();
    }

}
class SwitchButtonListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals("play")){
            Log.d("play","play");
            GenerateNotification.musicService.togglePlay();
        }
        else if(action.equals("next")){
            int current =  GenerateNotification.musicService.getCurrentIndex();
            int next = current + 1;
            if (next == GenerateNotification.musicService.songs.size())// If current was the last song, then play the first song in the list
                next = 0;
            GenerateNotification.musicService.setSong(next);
            GenerateNotification.musicService.togglePlay();
        }
        else if(action.equals("previous")){
            int current = GenerateNotification.musicService.getCurrentIndex();
            int previous = current - 1;
            if (previous < 0)            // If current was 0, then play the last song in the list
                previous = GenerateNotification.musicService.songs.size() - 1;
            GenerateNotification.musicService.setSong(previous);
            GenerateNotification.musicService.togglePlay();
        }
    }
}

