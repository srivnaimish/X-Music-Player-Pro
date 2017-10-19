package com.riseapps.xmusic.executor.Interfaces;

import com.riseapps.xmusic.model.Pojo.Song;

/**
 * Created by naimish on 21/6/17.
 */

public interface AdapterToActivityListener {
    public void onTrackLongPress(int c, long songId, boolean songAdded, Song song);

    public void onFirstTrackLongPress();
}
