package com.riseapps.xmusic.model.Pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by naimish on 16/3/17.
 */

public class Album implements Parcelable{
    private String name;
    private String imagepath;


    private int viewType;

    public Album() {
    }

    public Album(String name,String imagepath) {
        this.name = name;
        this.imagepath=imagepath;
    }

    protected Album(Parcel in) {
        name = in.readString();
        imagepath = in.readString();
    }

    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }

    public String getImagepath() {
        return imagepath;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(imagepath);
    }
}
