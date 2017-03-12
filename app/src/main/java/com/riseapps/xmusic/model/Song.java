package com.riseapps.xmusic.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by naimish on 4/1/17.
 */

public class Song implements Parcelable {
    private long ID, duration;
    private String Name, Artist;
    private Uri Imagepath;

    public Song(long id, long duration, String name, String artist, Uri imagepath) {
        Name = name;
        ID = id;
        Artist = artist;
        Imagepath = imagepath;
        this.duration = duration;
    }

    protected Song(Parcel in) {
        ID = in.readLong();
        duration = in.readLong();
        Name = in.readString();
        Artist = in.readString();
        Imagepath = in.readParcelable(Uri.class.getClassLoader());
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

    public long getDuration() {
        return duration;
    }

    public String getName() {
        return Name;
    }

    public String getArtist() {
        return Artist;
    }

    public Uri getImagepath() {
        return Imagepath;
    }

    public long getID() {
        return ID;
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
        parcel.writeParcelable(Imagepath, i);
    }
}
