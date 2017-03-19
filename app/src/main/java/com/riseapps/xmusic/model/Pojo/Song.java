package com.riseapps.xmusic.model.Pojo;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by naimish on 4/1/17.
 */

public class Song{
    private long ID, duration;
    private String Name, Artist,Imagepath,playlist;
    private boolean favourite;

    public Song(){

    }

    public Song(long id, long duration, String name, String artist, String imagepath,String playlist,boolean favourite) {
        Name = name;
        ID = id;
        Artist = artist;
        Imagepath = imagepath;
        this.duration = duration;
        this.favourite = favourite;
        this.playlist=playlist;
    }

    public long getID() {
        return ID;
    }

    public String getName() {
        return Name;
    }

    public String getArtist() {
        return Artist;
    }

    public long getDuration() {
        return duration;
    }

    public String getImagepath() {
        return Imagepath;
    }

    public String getPlaylist() {
        return playlist;
    }

    public boolean getFavourite(){ return favourite; }

    public void setID(long ID) {
        this.ID = ID;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setArtist(String artist) {
        Artist = artist;
    }

    public void setImagepath(String imagepath) {
        Imagepath = imagepath;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setPlaylist(String playlist) {
        this.playlist = playlist;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }


}
