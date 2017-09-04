package com.riseapps.xmusic.executor;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * Created by ayush on 22/6/17.
 */

public class FilePathFromId {
    public Uri pathFromID(Context c, long id) {
        Uri mediaContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{MediaStore.Audio.Media.DATA};
        String selection = MediaStore.Audio.Media._ID + "=?";
        String[] selectionArgs = new String[]{"" + id}; //This is the id you are looking for
        Cursor mediaCursor = c.getContentResolver().query(mediaContentUri, projection, selection, selectionArgs, null);
        if (mediaCursor != null && mediaCursor.getCount() >= 0) {
            mediaCursor.moveToPosition(0);
            String path = mediaCursor.getString(mediaCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            Uri uri = Uri.parse("file:///" + path);
            return uri;
        }
        return null;
    }
}
