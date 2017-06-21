package com.riseapps.xmusic.model.Pojo;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by naimish on 4/1/17.
 */

public class Song implements Parcelable{
    private long ID, duration;
    private String Name, Artist,Imagepath;
    private boolean favourite,isSelected=false;

    public Song(){

    }

    public Song(long id, long duration, String name, String artist, String imagepath,boolean favourite) {
        Name = name;
        ID = id;
        Artist = artist;
        Imagepath = imagepath;
        this.duration = duration;
        this.favourite = favourite;
    }

    public Song(Song song){
        if(song==null)
            Log.d("Null","yes");

    }

    protected Song(Parcel in) {
        ID = in.readLong();
        duration = in.readLong();
        Name = in.readString();
        Artist = in.readString();
        favourite = in.readByte() != 0;
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

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


    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(ID);
        parcel.writeLong(duration);
        parcel.writeString(Name);
        parcel.writeString(Artist);
        parcel.writeString(Imagepath);
        parcel.writeByte((byte) (favourite ? 1 : 0));
    }
}
