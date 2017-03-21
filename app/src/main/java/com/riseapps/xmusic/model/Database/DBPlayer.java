package com.riseapps.xmusic.model.Database;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.riseapps.xmusic.model.Pojo.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class DBPlayer {

    private PlayerHelper playerHelper;
    private SQLiteDatabase mDatabase;
    private Context c;
    public static final String strSeparator = ",";

    public DBPlayer(Context context) {
        c = context;
        playerHelper = new PlayerHelper(context);
        mDatabase = playerHelper.getWritableDatabase();
    }
    /*Insert Song*/
    public void insertSong(long id, String Name, String Artist, long duration, String Imagepath,String playlist, boolean favourite) {
        String sql = "INSERT INTO " + PlayerHelper.SONG_TABLE_NAME + " (" + PlayerHelper.COLUMN_ID + "," + PlayerHelper.COLUMN_NAME + "," +
                PlayerHelper.COLUMN_ARTIST + "," + PlayerHelper.COLUMN_DURATION + "," + PlayerHelper.COLUMN_IMAGEPATH + "," + PlayerHelper.COLUMN_PLAYLIST +
                "," + PlayerHelper.COLUMN_FAVOURITE + ") VALUES(?,?,?,?,?,?,?);";
        SQLiteStatement statement = mDatabase.compileStatement(sql);
        mDatabase.beginTransaction();
        statement.clearBindings();
        statement.bindLong(1, id);
        statement.bindString(2, Name);
        statement.bindString(3, Artist);
        statement.bindLong(4, duration);
        statement.bindString(5, Imagepath);
        statement.bindString(6, playlist);
        if (favourite)
            statement.bindLong(7, 1);
        else
            statement.bindLong(7, 0);
        statement.execute();
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
        mDatabase.close();

    }
    /*Read Songs*/
    public ArrayList<Song> readSongs() {
        ArrayList<Song> songlist = new ArrayList<>();
        String[] columns = {
                PlayerHelper.COLUMN_ID,
                PlayerHelper.COLUMN_NAME,
                PlayerHelper.COLUMN_ARTIST,
                PlayerHelper.COLUMN_DURATION,
                PlayerHelper.COLUMN_IMAGEPATH,
                PlayerHelper.COLUMN_PLAYLIST,
                PlayerHelper.COLUMN_FAVOURITE
        };
        Cursor cursor = mDatabase.query(PlayerHelper.SONG_TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Song song = new Song();
                song.setID(cursor.getLong(cursor.getColumnIndex(PlayerHelper.COLUMN_ID)));
                song.setName(cursor.getString(cursor.getColumnIndex(PlayerHelper.COLUMN_NAME)));
                song.setArtist(cursor.getString(cursor.getColumnIndex(PlayerHelper.COLUMN_ARTIST)));
                song.setDuration(cursor.getLong(cursor.getColumnIndex(PlayerHelper.COLUMN_DURATION)));
                song.setImagepath(cursor.getString(cursor.getColumnIndex(PlayerHelper.COLUMN_IMAGEPATH)));
                song.setPlaylist(cursor.getString(cursor.getColumnIndex(PlayerHelper.COLUMN_PLAYLIST)));
                int f = cursor.getInt(cursor.getColumnIndex(PlayerHelper.COLUMN_FAVOURITE));
                if (f == 1)
                    song.setFavourite(true);
                else
                    song.setFavourite(false);
                songlist.add(song);
            }
            while (cursor.moveToNext());
            cursor.close();
        }

        mDatabase.close();
        Collections.sort(songlist, new Comparator<Song>() {
            @Override
            public int compare(Song song, Song t1) {
                return song.getName().compareTo(t1.getName());
            }
        });
        return songlist;
    }
    /*Delete All Songs*/
    public void deleteAllSongs() {
        mDatabase.delete(PlayerHelper.SONG_TABLE_NAME, null, null);
        Log.d("Songs", "Deleted");
        mDatabase.close();
    }


    private class PlayerHelper extends SQLiteOpenHelper {
        private static final String DB_NAME = "playerDB";
        private static final int DB_VERSION = 1;
        private Context mContext;

        static final String SONG_TABLE_NAME = "SONGS_LIST";     //Attributes of Songs
        static final String COLUMN_ID = "ID";
        static final String COLUMN_NAME = "NAME";
        static final String COLUMN_ARTIST = "ARTIST";
        static final String COLUMN_DURATION = "DURATION";
        static final String COLUMN_IMAGEPATH = "IMAGEPATH";
        static final String COLUMN_PLAYLIST = "PLAYLIST";
        static final String COLUMN_FAVOURITE = "FAVOURITE";

        static final String PLAYLIST_TABLE_NAME = "PLAYLISTS_LIST";     //Attributes of Playlists
        static final String PLAYLIST_COLUMN_NAME = "NAME";
        static final String PLAYLIST_COLUMN_COUNT = "COUNT";
        static final String PLAYLIST_COLUMN_SONGS = "SONGS";

        static final String CREATE_TABLE_SONG_LIST = "CREATE TABLE " + SONG_TABLE_NAME + "(" +
                COLUMN_ID + " INTEGER," +
                COLUMN_NAME + " VARCHAR(40)," +
                COLUMN_ARTIST + " VARCHAR(40)," +
                COLUMN_DURATION + " INTEGER," +
                COLUMN_IMAGEPATH + " VARCHAR(40)," +
                COLUMN_PLAYLIST + " VARCHAR(40)," +
                COLUMN_FAVOURITE + " INTEGER" +
                ");";

        static final String CREATE_TABLE_PLAYLISTS = "CREATE TABLE " + PLAYLIST_TABLE_NAME + "(" +
                PLAYLIST_COLUMN_NAME + " VARCHAR(40)," +
                PLAYLIST_COLUMN_COUNT + " INTEGER," +
                PLAYLIST_COLUMN_SONGS + " VARCHAR(150)" +
                ");";

        PlayerHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
            mContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CREATE_TABLE_SONG_LIST);
                db.execSQL(CREATE_TABLE_PLAYLISTS);
            } catch (SQLiteException exception) {
                Log.d("error", exception.getMessage());
            }

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {
            try {
                db.execSQL("DROP TABLE IF EXISTS " + SONG_TABLE_NAME + ";");
                db.execSQL("DROP TABLE IF EXISTS " + PLAYLIST_TABLE_NAME + ";");
                onCreate(db);
            } catch (SQLiteException e) {

            }
        }

        @Override
        protected void finalize() throws Throwable {
            this.close();
            super.finalize();
        }
    }

    public static String convertArrayToString(String[] array) {
        String str = "";
        for (int i = 0; i < array.length; i++) {
            str = str + array[i];
            if (i < array.length - 1) {
                str = str + strSeparator;
            }
        }
        return str;
    }

    public static String[] convertStringToArray(String str) {
        String[] arr = str.split(strSeparator);
        return arr;
    }
}