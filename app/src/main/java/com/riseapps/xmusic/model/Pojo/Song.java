package com.riseapps.xmusic.model.Pojo;

import android.net.Uri;
import android.util.Log;

/**
 * Created by naimish on 4/1/17.
 */

public class Song {
    private long ID, duration;
    private String Name, Artist,Album;
    private boolean favourite, isSelected = false;
    private Uri Imagepath;
    public Song() {

    }

    public Song(long id, long duration,String album ,String name, String artist, Uri imagepath, boolean favourite) {
        Name = name;
        ID = id;
        Artist = artist;
        Imagepath = imagepath;
        this.duration = duration;
        this.favourite = favourite;
        this.Album=album;
    }

    public Song(Song song) {
        if (song == null)
            Log.d("Null", "yes");

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

    public Uri getImagepath() {
        return Imagepath;
    }


    public boolean getFavourite() {
        return favourite;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setArtist(String artist) {
        Artist = artist;
    }

    public void setImagepath(Uri imagepath) {
        Imagepath = imagepath;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }


    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getAlbum() {
        return Album;
    }

    public void setAlbum(String album) {
        Album = album;
    }
}
