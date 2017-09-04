package com.riseapps.xmusic.model.Pojo;

/**
 * Created by naimish on 16/3/17.
 */

public class Album {
    private String name;
    private String imagepath;
    private long id;

    public Album() {
    }

    public Album(long id, String name, String imagepath) {
        this.id = id;
        this.name = name;
        this.imagepath = imagepath;
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

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
