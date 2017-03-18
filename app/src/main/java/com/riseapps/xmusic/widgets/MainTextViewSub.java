package com.riseapps.xmusic.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.TextView;

import com.riseapps.xmusic.utils.FontCache;

/**
 * Created by naimish on 13/3/17.
 */

public class MainTextViewSub extends TextView {

    public MainTextViewSub(Context context) {
        super(context);
        applyFont(context);
    }

    public MainTextViewSub(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyFont(context);
    }

    public MainTextViewSub(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MainTextViewSub(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        applyFont(context);
    }

    private void applyFont(Context context) {
        //Typeface font = FontCache.getTypeface("fonts/Hind_Siliguri/HindSiliguri-Regular.ttf", context);
        Typeface font = FontCache.getTypeface("fonts/Mada/Mada-Medium.ttf", context);
        setTypeface(font);
    }
}