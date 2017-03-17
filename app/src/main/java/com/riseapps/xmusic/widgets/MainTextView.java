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

public class MainTextView extends TextView {

    public MainTextView(Context context) {
        super(context);
        applyFont(context);
    }

    public MainTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyFont(context);
    }

    public MainTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MainTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        applyFont(context);
    }

    private void applyFont(Context context) {
        Typeface font = FontCache.getTypeface("fonts/Mada/Mada-Regular.ttf", context);
        //Typeface font = FontCache.getTypeface("fonts/Montserrat/Montserrat-Medium.ttf", context);
        //Typeface font = FontCache.getTypeface("fonts/Work_Sans/WorkSans-Regular.ttf", context);
        setTypeface(font);
    }
}
