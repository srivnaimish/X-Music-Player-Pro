package com.riseapps.xmusic.model.Pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by naimish on 15/3/17.
 */

public class Playlist{
    private String name;

    public Playlist(){}

    public Playlist(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
