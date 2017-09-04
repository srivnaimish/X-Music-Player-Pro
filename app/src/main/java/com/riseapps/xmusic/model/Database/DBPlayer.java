package com.riseapps.xmusic.model.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.riseapps.xmusic.component.SharedPreferenceSingelton;
import com.riseapps.xmusic.model.Pojo.Playlist;
import com.riseapps.xmusic.model.Pojo.PlaylistSelect;

import java.util.ArrayList;


public class DBPlayer {

    private PlayerHelper playerHelper;
    private SQLiteDatabase mDatabase;
    private Context c;
    private static final String strSeparator = ",";
    private SharedPreferenceSingelton sharedPreferenceSingelton;

    public DBPlayer(Context context) {
        c = context;
        playerHelper = new PlayerHelper(context);
        mDatabase = playerHelper.getWritableDatabase();
        sharedPreferenceSingelton = new SharedPreferenceSingelton();
    }

    public void insertFavourite(long id) {
        String sql = "INSERT INTO " + PlayerHelper.SONG_TABLE_NAME + " (" + PlayerHelper.COLUMN_ID + ") VALUES(?);";
        SQLiteStatement statement = mDatabase.compileStatement(sql);
        mDatabase.beginTransaction();
        statement.clearBindings();
        statement.bindLong(1, id);
        statement.execute();
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
        mDatabase.close();
    }

    public void deleteFavourite(long id) {
        String whereClause = PlayerHelper.COLUMN_ID + "=?";
        String whereArgs[] = {"" + id};
        mDatabase.delete(PlayerHelper.SONG_TABLE_NAME, whereClause, whereArgs);
        mDatabase.close();
    }

    public boolean isFavourite(long id) {
        boolean b = false;
        String[] columns = {
                PlayerHelper.COLUMN_ID
        };
        String args[] = {"" + id};
        Cursor cursor = null;
        try {
            cursor = mDatabase.query(PlayerHelper.SONG_TABLE_NAME, columns, PlayerHelper.COLUMN_ID + "=?", args, null, null, null);
            if (cursor.getCount() > 0) {
                cursor.close();
                b = true;
            }
        } catch (SQLiteException e) {
        } finally {
            mDatabase.close();
        }
        return b;
    }

    public ArrayList<Long> readFavourites() {
        ArrayList<Long> songlist = new ArrayList<>();
        String[] columns = {
                PlayerHelper.COLUMN_ID
        };
        Cursor cursor = mDatabase.query(PlayerHelper.SONG_TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                songlist.add(cursor.getLong(cursor.getColumnIndex(PlayerHelper.COLUMN_ID)));
            }
            while (cursor.moveToNext());
            cursor.close();
        }
        mDatabase.close();
        return songlist;
    }


    public void addSongToPlaylists(long id, String playlistNames) {
        String S[] = convertStringToArray(playlistNames);

        for (String value : S) {
            String sql = "INSERT INTO " + PlayerHelper.PLAYLIST_TRACKS_TABLE_NAME + " (" + PlayerHelper.PLAYLIST_NAME + "," + PlayerHelper.PLAYLIST_SONG_ID + ") VALUES(?,?);";
            SQLiteStatement statement = mDatabase.compileStatement(sql);
            mDatabase.beginTransaction();
            statement.clearBindings();
            statement.bindString(1, value);
            statement.bindLong(2, id);
            statement.execute();
            mDatabase.setTransactionSuccessful();
            mDatabase.endTransaction();

        }

        mDatabase.close();
    }

    public void addMultipleSongToMultiplePlaylist(String playlistNames, long id[]) {
        String S[] = convertStringToArray(playlistNames);
        for (String value : S) {
            for (long anId : id) {
                String sql = "INSERT INTO " + PlayerHelper.PLAYLIST_TRACKS_TABLE_NAME + " (" + PlayerHelper.PLAYLIST_NAME + "," + PlayerHelper.PLAYLIST_SONG_ID + ") VALUES(?,?);";
                SQLiteStatement statement = mDatabase.compileStatement(sql);
                mDatabase.beginTransaction();
                statement.clearBindings();
                statement.bindString(1, value);
                statement.bindLong(2, anId);
                statement.execute();
                mDatabase.setTransactionSuccessful();
                mDatabase.endTransaction();
            }
        }
        mDatabase.close();
    }

    public ArrayList<Playlist> readPlaylists() {
        ArrayList<Playlist> playlists = new ArrayList<>();
        String[] columns = {
                PlayerHelper.PLAYLIST_NAME
        };
        Cursor cursor = mDatabase.query(true, PlayerHelper.PLAYLIST_TRACKS_TABLE_NAME, columns, null, null, PlayerHelper.PLAYLIST_NAME, null, PlayerHelper.PLAYLIST_NAME, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Playlist playlist = new Playlist();
                playlist.setName(cursor.getString(cursor.getColumnIndex(PlayerHelper.PLAYLIST_NAME)));
                playlists.add(playlist);
            }
            while (cursor.moveToNext());
            cursor.close();
        }
        mDatabase.close();
        return playlists;
    }

    public ArrayList<PlaylistSelect> readPlaylistsSelect() {
        ArrayList<PlaylistSelect> playlists = new ArrayList<>();
        String[] columns = {
                PlayerHelper.PLAYLIST_NAME
        };
        Cursor cursor = mDatabase.query(true, PlayerHelper.PLAYLIST_TRACKS_TABLE_NAME, columns, null, null, PlayerHelper.PLAYLIST_NAME, null, PlayerHelper.PLAYLIST_NAME, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                PlaylistSelect playlist = new PlaylistSelect();
                playlist.setName(cursor.getString(cursor.getColumnIndex(PlayerHelper.PLAYLIST_NAME)));
                playlist.setSelected(false);
                playlists.add(playlist);
            }
            while (cursor.moveToNext());
            cursor.close();
        }
        mDatabase.close();
        return playlists;
    }

    public ArrayList<Long> readSongsFromPlaylist(String playlist) {
        ArrayList<Long> songlist = new ArrayList<>();
        String[] columns = {
                PlayerHelper.PLAYLIST_SONG_ID
        };
        String selection = "PLAYLIST_NAME=?";
        String args[] = {"" + playlist};
        Cursor cursor = mDatabase.query(PlayerHelper.PLAYLIST_TRACKS_TABLE_NAME, columns, selection, args, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                songlist.add(cursor.getLong(cursor.getColumnIndex(PlayerHelper.PLAYLIST_SONG_ID)));
            }
            while (cursor.moveToNext());
            cursor.close();
        }
        mDatabase.close();
        return songlist;
    }

    public void deletePlaylist(String name) {
        String whereClause = PlayerHelper.PLAYLIST_NAME + "=?";
        String whereArgs[] = {name};
        mDatabase.delete(PlayerHelper.PLAYLIST_TRACKS_TABLE_NAME, whereClause, whereArgs);
        mDatabase.close();
        Log.d("Playlist", "Deleted");
    }

    public void deleteSongFromPlaylist(String playlist, long id) {
        String whereClause = PlayerHelper.PLAYLIST_NAME + "=? AND " + PlayerHelper.PLAYLIST_SONG_ID + "=?";
        String whereArgs[] = {playlist, "" + id};
        mDatabase.delete(PlayerHelper.PLAYLIST_TRACKS_TABLE_NAME, whereClause, whereArgs);
        mDatabase.close();
    }

    private class PlayerHelper extends SQLiteOpenHelper {
        private static final String DB_NAME = "playerDB";
        private static final int DB_VERSION = 8;
        private Context mContext;

        static final String SONG_TABLE_NAME = "FAVOURITE_LIST";     //Attributes of Favourite tracks
        static final String COLUMN_ID = "ID";

        static final String PLAYLIST_TRACKS_TABLE_NAME = "PLAYLISTS_TRACKS";     //Attributes of playlists tracks
        static final String PLAYLIST_NAME = "PLAYLIST_NAME";
        static final String PLAYLIST_SONG_ID = "SONG_ID";

        static final String CREATE_TABLE_SONG_LIST = "CREATE TABLE " + SONG_TABLE_NAME + "(" +
                COLUMN_ID + " INTEGER" +
                ");";

        static final String CREATE_TABLE_PLAYLIST_TRACKS = "CREATE TABLE " + PLAYLIST_TRACKS_TABLE_NAME + "(" +
                PLAYLIST_NAME + " VARCHAR(40)," +
                PLAYLIST_SONG_ID + " INTEGER" +
                ");";

        PlayerHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
            mContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CREATE_TABLE_SONG_LIST);
                db.execSQL(CREATE_TABLE_PLAYLIST_TRACKS);
            } catch (SQLiteException exception) {
                Log.d("error", exception.getMessage());
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {
            try {
                db.execSQL("DROP TABLE IF EXISTS " + SONG_TABLE_NAME + ";");
                db.execSQL("DROP TABLE IF EXISTS " + PLAYLIST_TRACKS_TABLE_NAME + ";");
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

    public static String[] convertStringToArray(String str) {
        String[] arr = str.split(strSeparator);
        return arr;
    }
}