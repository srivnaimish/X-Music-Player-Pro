package com.riseapps.xmusic.model.xplayertags;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by kanishk on 17/02/17.
 */

public class TagClass {

    private String code;
    private String name;
    private String color;

    public TagClass() {

    }

    public TagClass(String sinif, String name) {
        this.code = sinif;
        this.name = name;
        this.color = getRandomColor();

    }

    public String getRandomColor() {
        ArrayList<String> colors = new ArrayList<>();
        colors.add("#ffffff");

        return colors.get(new Random().nextInt(colors.size()));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSinif() {
        return code;
    }

    public void setSinif(String code) {
        this.code = code;
    }
}
