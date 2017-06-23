package com.riseapps.xmusic.model.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.riseapps.xmusic.component.SharedPreferenceSingelton;
import com.riseapps.xmusic.model.Pojo.Album;
import com.riseapps.xmusic.model.Pojo.Artist;
import com.riseapps.xmusic.model.Pojo.Playlist;
import com.riseapps.xmusic.model.Pojo.Song;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class DBPlayer {

    private PlayerHelper playerHelper;
    private SQLiteDatabase mDatabase;
    private Context c;
    public static final String strSeparator = ",";
    SharedPreferenceSingelton sharedPreferenceSingelton;

    public DBPlayer(Context context) {
        c = context;
        playerHelper = new PlayerHelper(context);
        mDatabase = playerHelper.getWritableDatabase();
        sharedPreferenceSingelton=new SharedPreferenceSingelton();
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

    public boolean isFavourite(long id){
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

    public Cursor readFavourites() {
        String[] columns = {
                PlayerHelper.COLUMN_ID
        };
        return mDatabase.query(PlayerHelper.SONG_TABLE_NAME, columns, null, null, null, null, null);
    }

    public void addSongToPlaylists(long id, String playlistNames) {
        String S[] = convertStringToArray(playlistNames);
        String playlistJson=sharedPreferenceSingelton.getSavedString(c,"PlaylistNames");
        ArrayList<Playlist> playlists=new Gson().fromJson(playlistJson, new TypeToken<ArrayList<Playlist>>() {
        }.getType());

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
            Playlist playlist=new Playlist(value);
            if(!playlists.contains(playlist)){
                playlists.add(playlist);
            }
        }
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Playlist>>() {
        }.getType();
        sharedPreferenceSingelton.saveAs(c,"PlaylistNames",gson.toJson(playlists, type));
        mDatabase.close();
    }

    public void addMultipleSongToSinglePlaylist(String playlistName, long id[]) {
        String playlistJson=sharedPreferenceSingelton.getSavedString(c,"PlaylistNames");
        ArrayList<Playlist> playlists=new Gson().fromJson(playlistJson, new TypeToken<ArrayList<Playlist>>() {
        }.getType());
        Playlist playlist=new Playlist(playlistName);
        if(!playlists.contains(playlist)){
            playlists.add(playlist);
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Playlist>>() {
            }.getType();
            sharedPreferenceSingelton.saveAs(c,"PlaylistNames",gson.toJson(playlists, type));
        }

        for (long anId : id) {
            String sql = "INSERT INTO " + PlayerHelper.PLAYLIST_TRACKS_TABLE_NAME + " (" + PlayerHelper.PLAYLIST_NAME + "," + PlayerHelper.PLAYLIST_SONG_ID + ") VALUES(?,?);";
            SQLiteStatement statement = mDatabase.compileStatement(sql);
            mDatabase.beginTransaction();
            statement.clearBindings();
            statement.bindString(1, playlistName);
            statement.bindLong(2, anId);
            statement.execute();
            mDatabase.setTransactionSuccessful();
            mDatabase.endTransaction();
        }
        mDatabase.close();
    }

    public void deletePlaylist(String name) {
        String whereClause = PlayerHelper.PLAYLIST_NAME + "=?";
        String whereArgs[] = {name};
        mDatabase.delete(PlayerHelper.PLAYLIST_TRACKS_TABLE_NAME, whereClause, whereArgs);
        mDatabase.close();
        Log.d("Playlist", "Deleted");
    }

    private class PlayerHelper extends SQLiteOpenHelper {
        private static final String DB_NAME = "playerDB";
        private static final int DB_VERSION = 7;
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