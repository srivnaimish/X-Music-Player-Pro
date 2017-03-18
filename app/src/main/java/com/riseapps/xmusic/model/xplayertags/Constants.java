package com.riseapps.xmusic.model.xplayertags;

import android.graphics.Color;

/**
 * Created by kanishk on 17/02/17.
 */

public class Constants {

    public static final float DEFAULT_LINE_MARGIN = 10;
    public static final float DEFAULT_TAG_MARGIN = 10;
    public static final float DEFAULT_TAG_TEXT_PADDING_LEFT = 8;
    public static final float DEFAULT_TAG_TEXT_PADDING_TOP = 5;
    public static final float DEFAULT_TAG_TEXT_PADDING_RIGHT = 8;
    public static final float DEFAULT_TAG_TEXT_PADDING_BOTTOM = 5;

    public static final float LAYOUT_WIDTH_OFFSET = 1;

    //----------------- separator Tag Item-----------------//
    public static final float DEFAULT_TAG_TEXT_SIZE = 16f;
    public static final float DEFAULT_TAG_DELETE_INDICATOR_SIZE = 14f;
    public static final float DEFAULT_TAG_LAYOUT_BORDER_SIZE = 1f;
    public static final float DEFAULT_TAG_RADIUS = 2;
    public static final int DEFAULT_TAG_LAYOUT_COLOR = Color.parseColor("#ffffff");
    public static final int DEFAULT_TAG_LAYOUT_COLOR_PRESS = Color.parseColor("#F44336");
    public static final int DEFAULT_TAG_LAYOUT_COLOR_LONGPRESS = Color.parseColor("#3F51B5");
    public static final int DEFAULT_TAG_TEXT_COLOR = Color.parseColor("#000000");
    public static final int DEFAULT_TAG_TEXT_COLOR_PRESS = Color.parseColor("#ffffff");
    public static final int DEFAULT_TAG_DELETE_INDICATOR_COLOR = Color.parseColor("#ffffff");
    public static final int DEFAULT_TAG_LAYOUT_BORDER_COLOR = Color.parseColor("#BDBDBD");
    public static final String DEFAULT_TAG_DELETE_ICON = "×";
    public static final boolean DEFAULT_TAG_IS_DELETABLE = false;


    private Constants() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation");
    }

}
