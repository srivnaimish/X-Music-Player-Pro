package com.riseapps.xmusic.component;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.CardView;

import com.riseapps.xmusic.R;
import com.riseapps.xmusic.model.Pojo.PlaylistSelect;

import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by naimish on 3/7/17.
 */

public class AppConstants {
    public static final String ITEM_SKU[] = {"", "", "theme_3", "theme_4", "theme_5", "theme_6", "theme_7", "theme_8","all_themes"};

    public static int[] seekBars = {R.id.seekBar0, R.id.seekBar1, R.id.seekBar2, R.id.seekBar3, R.id.seekBar4};
    public static int[] levels = {R.id.level0, R.id.level1, R.id.level2, R.id.level3, R.id.level4};

    public static final int buttonId[] = {R.id.card1, R.id.card2, R.id.card3, R.id.card4, R.id.card5, R.id.card6, R.id.card7, R.id.card8};

    public static final int checkImages[] = {R.id.tick1, R.id.tick2, R.id.tick3, R.id.tick4, R.id.tick5, R.id.tick6, R.id.tick7, R.id.tick8};

    public static final String THEME_URL = "https://xmusicplayerpro.github.io/musicplayer/themes.html";

    public static boolean themesPurchased = false;

    public static int[] encrypted = {88, 87, 90, 96, 96, 74, 84, 80, 81, 69, 66, 49, 61,
            53, 58, 101, 112, 55, 133, 92, 82, 83, 108, 102, 84,
            95, 92, 97, 104, 81, 141, 95, 94, 107, 96, 98, 86, 57,
            88, 97, 104, 81, 80, 95, 61, 100, 111, 111, 80, 50, 130,
            70, 100, 65, 92, 79, 134, 58, 100, 77, 91, 106, 93, 77, 78,
            58, 68, 138, 64, 137, 122, 78, 68, 57, 105, 113, 104, 87, 129,
            52, 53, 105, 48, 53, 86, 137, 82, 76, 106, 71, 87, 140, 134, 87,
            130, 117, 132, 59, 99, 50, 62, 65, 51, 113, 139, 96, 81, 101, 84,
            89, 95, 114, 120, 78, 56, 136, 103, 64, 96, 81, 103, 137, 48, 101,
            61, 88, 140, 137, 133, 110, 123, 107, 87, 138, 59, 106, 125, 71, 87,
            52, 75, 49, 58, 52, 71, 49, 58, 58, 111, 56, 100, 57, 55, 75, 62, 108,
            134, 140, 84, 52, 57, 85, 71, 89, 61, 73, 83, 85, 108, 135, 94, 67, 61,
            83, 128, 79, 50, 109, 66, 119, 94, 134, 66, 67, 91, 108, 60, 77, 120, 48,
            99, 117, 140, 55, 97, 90, 49, 76, 62, 113, 60, 100, 90, 50, 93, 111, 80, 80,
            49, 66, 140, 75, 128, 97, 92, 56, 96, 91, 105, 104, 78, 113, 66, 109, 60, 80,
            48, 70, 65, 58, 81, 75, 83, 143, 51, 139, 82, 73, 109, 80, 77, 76, 85, 64, 70,
            74, 56, 73, 55, 83, 59, 77, 83, 111, 128, 76, 103, 82, 87, 117, 92, 108, 100, 67,
            88, 80, 80, 87, 126, 53, 89, 134, 64, 115, 70, 50, 122, 111, 105, 67, 71, 80, 82, 79,
            62, 108, 95, 58, 126, 110, 65, 110, 83, 83, 102, 137, 97, 102, 124, 82, 99, 55, 89, 79,
            92, 73, 60, 102, 94, 55, 134, 143, 66, 105, 97, 110, 130, 76, 93, 58, 100, 73, 61, 89, 134,
            85, 87, 52, 70, 72, 77, 108, 71, 118, 109, 80, 135, 79, 50, 80, 76, 109, 89, 102, 56, 110, 74,
            109, 102, 95, 73, 111, 75, 108, 122, 80, 89, 141, 65, 50, 97, 49, 65, 87, 63, 79, 61, 100, 95, 106,
            93, 77, 98, 48, 107, 91, 92, 78, 98, 70, 87, 106, 68, 71, 55, 111, 90, 102, 104, 81, 84, 92};

    public static String decrypt(int[] input) {
        String output = "";
        String key = "encrypt";
        for (int i = 0; i < input.length; i++) {
            output += (char) ((input[i] - 48) ^ (int) key.charAt(i % (key.length() - 1)));
        }
        return output;
    }

    public static final String ACTION_PLAY = "com.riseapps.xplayer.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.riseapps.xplayer.ACTION_PAUSE";
    public static final String ACTION_PREVIOUS = "com.riseapps.xplayer.ACTION_PREVIOUS";
    public static final String ACTION_NEXT = "com.riseapps.xplayer.ACTION_NEXT";
    public static final String ACTION_STOP = "com.riseapps.xplayer.ACTION_STOP";

    public static Bitmap decodeUri(Context c, Uri uri, final int requiredSize)
            throws FileNotFoundException, SecurityException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;


        BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o);

        int width_tmp = o.outWidth
                , height_tmp = o.outHeight;
        int scale = 1;

        while(true) {
            if(width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o2);
    }

    public static Uri getAlbumArtUri(long albumid){
        Uri sArtworkUri = Uri
                .parse("content://media/external/audio/albumart");

        return ContentUris.withAppendedId(sArtworkUri,albumid);
    }

    public static final String ANDROID_CHANNEL_ID = "Music";
    public static final String ANDROID_CHANNEL_NAME = "Music Playing Notification";

    public static ArrayList<PlaylistSelect> getFolderNames(Context context) {
        ArrayList<PlaylistSelect> folders = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                int lastSlash = path.lastIndexOf('/');
                int secondLastSlash = 0;
                for (int i = lastSlash - 1; i >= 0; i--) {
                    if (path.charAt(i) == '/') {
                        secondLastSlash = i;
                        break;
                    }
                }
                String folder = path.substring(secondLastSlash + 1, lastSlash);
                if (!names.contains(folder))
                    names.add(folder);
            }
            while (cursor.moveToNext());
        }
        for (String s : names) {
            folders.add(new PlaylistSelect(s, true));
        }

        return folders;
    }


}
