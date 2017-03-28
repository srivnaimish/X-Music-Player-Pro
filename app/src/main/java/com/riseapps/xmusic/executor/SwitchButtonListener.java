package com.riseapps.xmusic.executor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by naimish on 28/3/17.
 */

public class SwitchButtonListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case "play":
                GenerateNotification.musicService.togglePlay();
                break;
            case "next": {
                int current = GenerateNotification.musicService.getCurrentIndex();
                int next = current + 1;
                if (next == GenerateNotification.musicService.songs.size())// If current was the last song, then play the first song in the list
                    next = 0;
                GenerateNotification.musicService.setSong(next);
                GenerateNotification.musicService.togglePlay();
                break;
            }
            case "previous": {
                int current = GenerateNotification.musicService.getCurrentIndex();
                int previous = current - 1;
                if (previous < 0)            // If current was 0, then play the last song in the list
                    previous = GenerateNotification.musicService.songs.size() - 1;
                GenerateNotification.musicService.setSong(previous);
                GenerateNotification.musicService.togglePlay();
                break;
            }
        }
    }
}
