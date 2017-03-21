package com.riseapps.xmusic.model.Pojo;

/**
 * Created by naimish on 16/3/17.
 */

public class Artist {
    private String name;
    private String imagepath;

    public Artist(){}

    public Artist(String name,String imagepath) {
        this.name = name;
        this.imagepath=imagepath;
    }
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
}
