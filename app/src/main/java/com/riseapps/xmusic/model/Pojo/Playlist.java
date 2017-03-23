package com.riseapps.xmusic.model.Pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by naimish on 15/3/17.
 */

public class Playlist implements Parcelable{
    private String name;

    public Playlist(){}

    public Playlist(String name) {
        this.name = name;
    }

    protected Playlist(Parcel in) {
        name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Playlist> CREATOR = new Creator<Playlist>() {
        @Override
        public Playlist createFromParcel(Parcel in) {
            return new Playlist(in);
        }

        @Override
        public Playlist[] newArray(int size) {
            return new Playlist[size];
        }
    };

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
