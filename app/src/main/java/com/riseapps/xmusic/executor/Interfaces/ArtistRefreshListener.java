package com.riseapps.xmusic.executor.Interfaces;

import com.riseapps.xmusic.model.Pojo.Artist;
import com.riseapps.xmusic.model.Pojo.Song;

import java.util.ArrayList;

/**
 * Created by naimish on 29/3/17.
 */

public interface ArtistRefreshListener {
    void OnArtistRefresh(ArrayList<Artist> arrayList);
}
