package com.riseapps.xmusic.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.TextView;

import com.riseapps.xmusic.component.SharedPreferenceSingelton;
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
        SharedPreferenceSingelton sharedPreferenceSingelton=new SharedPreferenceSingelton();
        Typeface font=FontCache.getTypeface("fonts/Meera_Inimai/MeeraInimai-Regular.ttf", context);
        if(sharedPreferenceSingelton.getSavedInt(context,"Themes")==9){
            font = FontCache.getTypeface("fonts/minions.ttf", context);
        }else if(sharedPreferenceSingelton.getSavedInt(context,"Themes")==8){
            font = FontCache.getTypeface("fonts/harry_potter.ttf", context);
        }else if(sharedPreferenceSingelton.getSavedInt(context,"Themes")==10){
            font = FontCache.getTypeface("fonts/iron_man.ttf", context);
        }else if(sharedPreferenceSingelton.getSavedInt(context,"Themes")==11){
            font = FontCache.getTypeface("fonts/deadpool.ttf", context);
        }
        setTypeface(font);
    }
}
