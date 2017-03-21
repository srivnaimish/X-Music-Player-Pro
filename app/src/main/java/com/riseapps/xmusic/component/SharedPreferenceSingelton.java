package com.riseapps.xmusic.component;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by naimish on 12/3/17.
 */

public class SharedPreferenceSingelton {

    public void saveAs(Context c, String name, String value) {
        SharedPreferences sp = c.getSharedPreferences("player", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(name, value);
        editor.apply();
    }

    public void saveAs(Context c, String name, int value) {
        SharedPreferences sp = c.getSharedPreferences("player", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(name, value);
        editor.apply();
    }

    public void saveAs(Context c, String name, long value) {
        SharedPreferences sp = c.getSharedPreferences("player", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(name, value);
        editor.apply();
    }

    public void saveAs(Context c, String name, boolean value) {
        SharedPreferences sp = c.getSharedPreferences("player", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(name, value);
        editor.apply();
    }

    public int getSavedInt(Context c, String name) {
        SharedPreferences sp = c.getSharedPreferences("player", MODE_PRIVATE);
        return sp.getInt(name, 0);
    }

    public boolean getSavedBoolean(Context c, String name) {
        SharedPreferences sp = c.getSharedPreferences("player", MODE_PRIVATE);
        return sp.getBoolean(name, false);
    }

    public long getSavedLong(Context c, String name) {
        SharedPreferences sp = c.getSharedPreferences("player", MODE_PRIVATE);
        return sp.getLong(name, 0);
    }

    public String getSavedString(Context c, String name) {
        SharedPreferences sp = c.getSharedPreferences("player", MODE_PRIVATE);
        return sp.getString(name, null);
    }

}
