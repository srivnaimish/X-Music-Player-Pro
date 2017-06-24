package com.riseapps.xmusic.model.Pojo;

/**
 * Created by naimish on 15/3/17.
 */

public class PlaylistSelect {
    private String name;
    boolean selected;

    public PlaylistSelect(){}

    public PlaylistSelect(String name,boolean selected) {
        this.name = name;
        this.selected=selected;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }
}
