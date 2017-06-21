package com.riseapps.xmusic.executor.Interfaces;

/**
 * Created by naimish on 21/6/17.
 */

public interface AdapterToActivityListener {
    public void onTrackLongPress(int c,long songId,boolean songAdded);
    public void onFirstTrackLongPress();
}
