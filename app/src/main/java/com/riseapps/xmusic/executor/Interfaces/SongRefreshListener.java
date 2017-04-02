package com.riseapps.xmusic.executor.Interfaces;

import com.riseapps.xmusic.model.Pojo.Album;
import com.riseapps.xmusic.model.Pojo.Artist;
import com.riseapps.xmusic.model.Pojo.Song;

import java.util.ArrayList;

/**
 * Created by naimish on 29/3/17.
 */

public interface SongRefreshListener {
    void OnSongRefresh(ArrayList<Song> arrayList);
    void onSongRefresh();
}
