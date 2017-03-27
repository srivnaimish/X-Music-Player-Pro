package com.riseapps.xmusic.model.Pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by naimish on 16/3/17.
 */

public class Artist implements Parcelable {
    private String name;
    private String imagepath;

    private int viewType;
    public Artist(){}

    public Artist(String name,String imagepath) {
        this.name = name;
        this.imagepath=imagepath;
    }

    protected Artist(Parcel in) {
        name = in.readString();
        imagepath = in.readString();
        viewType=in.readInt();
    }

    public static final Creator<Artist> CREATOR = new Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel in) {
            return new Artist(in);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
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
        parcel.writeInt(viewType);
    }
}
