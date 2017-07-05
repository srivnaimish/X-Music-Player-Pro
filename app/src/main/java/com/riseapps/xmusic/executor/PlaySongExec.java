package com.riseapps.xmusic.executor;

import android.app.Activity;
import android.content.Context;

import com.riseapps.xmusic.component.SharedPreferenceSingelton;
import com.riseapps.xmusic.model.MusicService;
import com.riseapps.xmusic.view.Activity.MainActivity;

/**
 * Created by naimish on 14/3/17.
 */

public class PlaySongExec {

    private Context ctx;
    private Activity mActivity;
    private int mPos;

    public PlaySongExec(Context context, int position) {
        this.ctx = context;
        this.mPos = position;
    }

    public void startPlaying() {
        MusicService musicService =((MainActivity)ctx).getMusicService();
        musicService.setSong(mPos);
        musicService.togglePlay();
    }
}
