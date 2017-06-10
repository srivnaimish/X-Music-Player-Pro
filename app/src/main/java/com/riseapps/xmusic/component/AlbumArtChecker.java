package com.riseapps.xmusic.component;

import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.io.FileNotFoundException;

/**
 * Created by naimish on 14/3/17.
 */

public class AlbumArtChecker {
    public boolean hasAlbumArt(Context context,String imagePath){
        Uri uri = Uri.parse(imagePath);
        ParcelFileDescriptor pfd = null;
        try {
            pfd = context.getContentResolver().openFileDescriptor(uri, "r");
        } catch (FileNotFoundException | NullPointerException e) {
        }
        if (pfd != null) {
            return true;
        }
        else
            return false;
    }
}
