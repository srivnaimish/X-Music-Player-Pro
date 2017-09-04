package com.riseapps.xmusic.model.Pojo;

import android.util.Log;

/**
 * Created by naimish on 4/1/17.
 */

public class Song {
    private long ID, duration;
    private String Name, Artist, Imagepath;
    private boolean favourite, isSelected = false;

    public Song() {

    }

    public Song(long id, long duration, String name, String artist, String imagepath, boolean favourite) {
        Name = name;
        ID = id;
        Artist = artist;
        Imagepath = imagepath;
        this.duration = duration;
        this.favourite = favourite;
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

    public String getImagepath() {
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

    public void setImagepath(String imagepath) {
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

}
