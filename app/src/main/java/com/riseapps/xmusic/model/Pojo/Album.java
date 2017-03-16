package com.riseapps.xmusic.model.Pojo;

/**
 * Created by naimish on 16/3/17.
 */

public class Album {
    private String name;
    private int count;

    public Album(String name, int count) {
        this.name = name;
        this.count=count;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getcount() {
        return count;
    }
}
