package com.riseapps.xmusic.model.Pojo;

/**
 * Created by naimish on 16/3/17.
 */

public class Artist {
    private String name;
    private long id;

    private int viewType;

    public Artist() {
    }

    public Artist(String name, long id) {
        this.name = name;
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
