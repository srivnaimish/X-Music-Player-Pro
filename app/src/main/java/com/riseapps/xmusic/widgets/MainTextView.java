package com.riseapps.xmusic.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.widget.TextView;

import com.riseapps.xmusic.R;
import com.riseapps.xmusic.component.SharedPreferenceSingelton;

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
        SharedPreferenceSingelton sharedPreferenceSingelton = new SharedPreferenceSingelton();
        Typeface font = ResourcesCompat.getFont(context, R.font.meerainimal);
        if (sharedPreferenceSingelton.getSavedInt(context, "Themes") == 9) {
            font = ResourcesCompat.getFont(context, R.font.minions);
        } else if (sharedPreferenceSingelton.getSavedInt(context, "Themes") == 8) {
            font = ResourcesCompat.getFont(context, R.font.harry_potter);
        } else if (sharedPreferenceSingelton.getSavedInt(context, "Themes") == 10) {
            font = ResourcesCompat.getFont(context, R.font.iron_man);
        } else if (sharedPreferenceSingelton.getSavedInt(context, "Themes") == 11) {
            font = ResourcesCompat.getFont(context, R.font.deadpool);
        } else if (sharedPreferenceSingelton.getSavedInt(context, "Themes") == 12) {
            font = ResourcesCompat.getFont(context, R.font.inception);
        }
        setTypeface(font);
    }
}
