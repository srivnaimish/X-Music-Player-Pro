package com.riseapps.xmusic.executor;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;
import com.riseapps.xmusic.R;
import com.riseapps.xmusic.model.MusicService;
import com.riseapps.xmusic.model.Pojo.Song;
import com.riseapps.xmusic.view.Activity.MainActivity;

/**
 * Created by naimish on 12/3/17.
 */

public class GenerateNotification {
    MusicService musicService;
    private int status;
    private static final int NOTIFICATION_ID = 1;
    private Context context;

    public GenerateNotification(int status) {
        this.status = status;
    }

    public void getNotification(Context context, MusicService musicService) {
        this.context = context;
        //GenerateNotification.musicService =musicService;
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 2, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setPriority(Notification.PRIORITY_HIGH);
        mBuilder.setGroupSummary(true);
        mBuilder.setAutoCancel(false);
        mBuilder.setTicker("Playing Music");

        if (status == 0)
            mBuilder.setOngoing(false);
        else
            mBuilder.setOngoing(true);

        mBuilder.setSmallIcon(R.drawable.ic_notification);
        mBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);


        Intent playIntent = new Intent("play");
        Intent nextIntent = new Intent("next");
        Intent prevIntent = new Intent("previous");

        PendingIntent pendingPlayIntent = PendingIntent.getBroadcast(context, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingNextIntent = PendingIntent.getBroadcast(context, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingPrevIntent = PendingIntent.getBroadcast(context, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Song song = musicService.getSongs().get(musicService.getCurrentIndex());

        RemoteViews contentView = contentView(song, pendingPlayIntent, pendingNextIntent, pendingPrevIntent);
        mBuilder.setCustomContentView(contentView);

        RemoteViews expandedView = expandView(song, pendingPlayIntent, pendingNextIntent, pendingPrevIntent);

        mBuilder.setCustomBigContentView(expandedView);

        Notification notification = mBuilder.build();
        Glide.with(context.getApplicationContext()) // safer!
                .load(Uri.parse(song.getImagepath()))
                .asBitmap()
                .into(new NotificationTarget(
                        context,
                        contentView,
                        R.id.imageView1,
                        notification,
                        NOTIFICATION_ID));
        Glide.with(context.getApplicationContext()) // safer!
                .load(Uri.parse(song.getImagepath()))
                .asBitmap()
                .into(new NotificationTarget(
                        context,
                        expandedView,
                        R.id.imageView1,
                        notification,
                        NOTIFICATION_ID));

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String id = "Music";
            CharSequence name = "Music Playing notification";
            String description = "Shows the current track playing in notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            mNotificationManager.createNotificationChannel(mChannel);
            mBuilder.setChannelId(id);
        }


        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        //  return notification;
    }

    private RemoteViews contentView(Song song, PendingIntent pendingPlayIntent, PendingIntent pendingNextIntent, PendingIntent pendingPrevIntent) {
        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification_content_layout);
        if (status == 0)
            contentView.setImageViewResource(R.id.play_pause, R.drawable.ic_notification_play);
        else
            contentView.setImageViewResource(R.id.play_pause, R.drawable.ic_notification_pause);

        contentView.setTextViewText(R.id.track, song.getName());
        contentView.setImageViewResource(R.id.prev, R.drawable.ic_notification_left);
        contentView.setImageViewResource(R.id.next, R.drawable.ic_notification_right);
        if (song.getImagepath().equalsIgnoreCase("no_image"))
            contentView.setImageViewResource(R.id.imageView1, R.drawable.empty);

        contentView.setOnClickPendingIntent(R.id.play_pause, pendingPlayIntent);
        contentView.setOnClickPendingIntent(R.id.next, pendingNextIntent);
        contentView.setOnClickPendingIntent(R.id.prev, pendingPrevIntent);
        return contentView;
    }

    private RemoteViews expandView(Song song, PendingIntent pendingPlayIntent, PendingIntent pendingNextIntent, PendingIntent pendingPrevIntent) {
        RemoteViews expandedView = new RemoteViews(context.getPackageName(), R.layout.notification_expand);
        expandedView.setTextViewText(R.id.track, song.getName());
        expandedView.setTextViewText(R.id.artist, song.getArtist());
        if (status == 0)
            expandedView.setImageViewResource(R.id.play_pause, R.drawable.ic_notification_play);
        else
            expandedView.setImageViewResource(R.id.play_pause, R.drawable.ic_notification_pause);

        expandedView.setImageViewResource(R.id.prev, R.drawable.ic_notification_left);
        expandedView.setImageViewResource(R.id.next, R.drawable.ic_notification_right);

        if (song.getImagepath().equalsIgnoreCase("no_image"))
            expandedView.setImageViewResource(R.id.imageView1, R.drawable.empty);

        expandedView.setOnClickPendingIntent(R.id.play_pause, pendingPlayIntent);
        expandedView.setOnClickPendingIntent(R.id.next, pendingNextIntent);
        expandedView.setOnClickPendingIntent(R.id.prev, pendingPrevIntent);
        return expandedView;
    }

}

