package com.riseapps.xmusic.widgets;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.RemoteViews;
import com.riseapps.xmusic.R;
import com.riseapps.xmusic.component.AppConstants;
import com.riseapps.xmusic.model.MusicService;
import com.riseapps.xmusic.view.activity.MainActivity;

import java.util.concurrent.ExecutionException;

/**
 * Created by nv95 on 08.07.16.
 */

public class StandardWidget extends BaseWidget {

    RemoteViews remoteView;

    @Override
    int getLayoutRes() {
        return R.layout.widget_standard;
    }

    @Override
    void onViewsUpdate(Context context, final RemoteViews remoteViews, ComponentName serviceName, Bundle extras) {
        this.remoteView=remoteViews;
        remoteView.setOnClickPendingIntent(R.id.image_next, PendingIntent.getService(
                context,
                REQUEST_NEXT,
                new Intent(context, MusicService.class)
                        .setAction(AppConstants.ACTION_NEXT)
                        .setComponent(serviceName),
                0
        ));
        remoteView.setOnClickPendingIntent(R.id.image_prev, PendingIntent.getService(
                context,
                REQUEST_PREV,
                new Intent(context, MusicService.class)
                        .setAction(AppConstants.ACTION_PREVIOUS)
                        .setComponent(serviceName),
                0
        ));
        remoteView.setOnClickPendingIntent(R.id.image_playpause, PendingIntent.getService(
                context,
                REQUEST_PLAYPAUSE,
                new Intent(context, MusicService.class)
                        .setAction(AppConstants.ACTION_PLAY)
                        .setComponent(serviceName),
                0
        ));

        if (extras != null) {
            String t = extras.getString("track");

            if (t != null) {
                remoteView.setTextViewText(R.id.textView_title, t);
            }
            t = extras.getString("artist");
            ;
            if (t != null) {
                String album = extras.getString("album");
                ;
                if (!TextUtils.isEmpty(album)) {
                    t += " - " + album;
                }
                remoteView.setTextViewText(R.id.textView_subtitle, t);
            }
            remoteView.setImageViewResource(R.id.image_playpause,
                    extras.getBoolean("playing") ? R.drawable.ic_notification_pause : R.drawable.ic_notification_play);

        }
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 5, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.background, resultPendingIntent);
    }
}
