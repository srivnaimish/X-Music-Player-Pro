package com.riseapps.xmusic.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.EditText;

import com.riseapps.xmusic.utils.FontCache;

/**
 * Created by naimish on 3/4/17.
 */

public class MainEditText extends EditText {
    public MainEditText(Context context) {
        super(context);
        applyFont(context);
    }
    public MainEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyFont(context);
    }

    public MainEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MainEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        applyFont(context);
    }

    private void applyFont(Context context) {
        //Typeface font = FontCache.getTypeface("fonts/Mada/Mada-Regular.ttf", context);
        //Typeface font = FontCache.getTypeface("fonts/Montserrat/Montserrat-Medium.ttf", context);
        //Typeface font = FontCache.getTypeface("fonts/Work_Sans/WorkSans-Regular.ttf", context);
        Typeface font = FontCache.getTypeface("fonts/Meera_Inimai/MeeraInimai-Regular.ttf", context);
        setTypeface(font);
    }
}
