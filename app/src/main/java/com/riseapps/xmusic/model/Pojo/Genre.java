package com.riseapps.xmusic.model.Pojo;

/**
 * Created by naimish on 16/3/17.
 */

public class Genre {
    private String name;
    private int id;

    public Genre() {
    }

    public Genre(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
