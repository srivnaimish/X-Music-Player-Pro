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

import com.riseapps.xmusic.model.Pojo.Album;
import com.riseapps.xmusic.model.Pojo.Artist;
import com.riseapps.xmusic.model.Pojo.Playlist;
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

    /*For Songs-
     1)Insertion              -insertSong(...)
     2)Read                   -readSongs()
     3)Update Favourites      -updateFavourites(SongId,FavouriteStatus)
     4)Read Favourite  Songs  -readFavouriteSongs()
     5)Delete All Songs       -Deletes all songs in Song table just in case*/

    public void insertSong(long id, String Name, String Artist, long duration, String Imagepath,String playlist, String albumName,boolean favourite) {
        String sql = "INSERT INTO " + PlayerHelper.SONG_TABLE_NAME + " (" + PlayerHelper.COLUMN_ID + "," + PlayerHelper.COLUMN_NAME + "," +
                PlayerHelper.COLUMN_ARTIST + "," + PlayerHelper.COLUMN_DURATION + "," + PlayerHelper.COLUMN_IMAGEPATH + "," + PlayerHelper.COLUMN_PLAYLIST +
                "," + PlayerHelper.COLUMN_ALBUM + "," + PlayerHelper.COLUMN_FAVOURITE + ") VALUES(?,?,?,?,?,?,?,?);";
        SQLiteStatement statement = mDatabase.compileStatement(sql);
        mDatabase.beginTransaction();
        statement.clearBindings();
        statement.bindLong(1, id);
        statement.bindString(2, Name);
        statement.bindString(3, Artist);
        statement.bindLong(4, duration);
        statement.bindString(5, Imagepath);
        statement.bindString(6, playlist+",");
        statement.bindString(7, albumName);
        statement.bindLong(8, 0);
        statement.execute();
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
        mDatabase.close();

    }

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
        Cursor cursor = mDatabase.query(PlayerHelper.SONG_TABLE_NAME, columns, null, null, null, null, PlayerHelper.COLUMN_NAME);
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
        return songlist;
    }

    public void updateFavourites(long id,int favourite){

        ContentValues contentValues = new ContentValues();
        contentValues.put(PlayerHelper.COLUMN_FAVOURITE, favourite);
        String[] args = {"" + id};
        mDatabase.update(PlayerHelper.SONG_TABLE_NAME, contentValues, PlayerHelper.COLUMN_ID + " =? ", args);
        mDatabase.close();
    }

    public ArrayList<Song> readFavouriteSongs(){
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
        String whereClause="FAVOURITE=?";
        String whereArgs[]={""+1};
        Cursor cursor = mDatabase.query(PlayerHelper.SONG_TABLE_NAME, columns, whereClause, whereArgs, null, null, PlayerHelper.COLUMN_NAME);
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
        return songlist;
    }

    public void deleteAllSongs() {
        mDatabase.delete(PlayerHelper.SONG_TABLE_NAME, null, null);
        Log.d("Songs", "Deleted");
        mDatabase.close();
    }

    /*For Playlists
     1)Insert A NewPlaylist            -insertNewPlaylist(PlaylistName)
     2)Delete A Playlist               -deletePlaylist(PlaylistName)
     3)Read list of playlists          -readPlaylists()
     4)Read songs from a playlist      -readSongsFromPlaylists(playlistName)
     5)Add single Song to playlist(s)  -addSongToPlaylist(Song id,String playlistNames ending with comma)
     6)Add multiple Song to Sing Playlist  */


    public void insertNewPlaylist(String PlaylistName) {
        String sql = "INSERT INTO " + PlayerHelper.PLAYLIST_TABLE_NAME + " (" + PlayerHelper.PLAYLIST_COLUMN_NAME + "," + PlayerHelper.COLUMN_NAME +  ") VALUES(?,?);";
        SQLiteStatement statement = mDatabase.compileStatement(sql);
        mDatabase.beginTransaction();
        statement.clearBindings();
        statement.bindString(1, PlaylistName+",");
        statement.execute();
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
        mDatabase.close();

    }

    public void deletePlaylist(String PlaylistName){

        String whereClause="Name=?";
        String whereArgs[]={PlaylistName};
        mDatabase.delete(PlayerHelper.PLAYLIST_TABLE_NAME, whereClause, whereArgs); //Delete this playlist from playlist table

        String[] columns = { PlayerHelper.COLUMN_ID ,PlayerHelper.COLUMN_PLAYLIST };
        Cursor cursor = mDatabase.query(PlayerHelper.SONG_TABLE_NAME, columns, null, null, null, null, PlayerHelper.COLUMN_NAME);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String list=cursor.getString(cursor.getColumnIndex(PlayerHelper.COLUMN_PLAYLIST));
                if(list.contains(PlaylistName+",")){
                    String newPlaylistsForThisSong = list.replace(PlaylistName+",","");
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(PlayerHelper.COLUMN_PLAYLIST, newPlaylistsForThisSong);
                    String[] args = {"" + cursor.getLong(cursor.getColumnIndex(PlayerHelper.COLUMN_ID))};
                    mDatabase.update(PlayerHelper.SONG_TABLE_NAME, contentValues, PlayerHelper.COLUMN_ID + " =? ", args);
                            //remove occurence of this playlist in songs playlist column
                }
            }
            while (cursor.moveToNext());
            cursor.close();
        }

        Log.d(PlaylistName, "Deleted");
        mDatabase.close();
    }

    public ArrayList<Playlist> readPlaylists() {
        ArrayList<Playlist> playlists = new ArrayList<>();
        String[] columns = {
                PlayerHelper.PLAYLIST_COLUMN_NAME
        };
        Cursor cursor = mDatabase.query(PlayerHelper.PLAYLIST_TABLE_NAME, columns, null, null, null, null, PlayerHelper.PLAYLIST_COLUMN_NAME);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Playlist playlist=new Playlist();
                String s=cursor.getString(cursor.getColumnIndex(PlayerHelper.PLAYLIST_COLUMN_NAME));
                playlist.setName(s.substring(0,s.length()-1));
                playlists.add(playlist);
            }
            while (cursor.moveToNext());
            cursor.close();
        }
        mDatabase.close();
        return playlists;
    }

    public ArrayList<Song> readSongsFromPlaylist(String playlist) {
        ArrayList<Song> playlists = new ArrayList<>();
        String[] columns = {
                PlayerHelper.COLUMN_ID,
                PlayerHelper.COLUMN_NAME,
                PlayerHelper.COLUMN_ARTIST,
                PlayerHelper.COLUMN_DURATION,
                PlayerHelper.COLUMN_IMAGEPATH,
                PlayerHelper.COLUMN_PLAYLIST,
                PlayerHelper.COLUMN_FAVOURITE
        };
        Cursor cursor = mDatabase.query(PlayerHelper.SONG_TABLE_NAME, columns, null, null, null, null, PlayerHelper.COLUMN_NAME);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String list=cursor.getString(cursor.getColumnIndex(PlayerHelper.COLUMN_PLAYLIST));
                if(list.contains(playlist+",")){
                    Song song = new Song();
                    song.setID(cursor.getLong(cursor.getColumnIndex(PlayerHelper.COLUMN_ID)));
                    song.setName(cursor.getString(cursor.getColumnIndex(PlayerHelper.COLUMN_NAME)));
                    song.setArtist(cursor.getString(cursor.getColumnIndex(PlayerHelper.COLUMN_ARTIST)));
                    song.setDuration(cursor.getLong(cursor.getColumnIndex(PlayerHelper.COLUMN_DURATION)));
                    song.setImagepath(cursor.getString(cursor.getColumnIndex(PlayerHelper.COLUMN_IMAGEPATH)));
                    song.setPlaylist(list);
                    int f = cursor.getInt(cursor.getColumnIndex(PlayerHelper.COLUMN_FAVOURITE));
                    if (f == 1)
                        song.setFavourite(true);
                    else
                        song.setFavourite(false);
                    playlists.add(song);
                }
                /**/
            }
            while (cursor.moveToNext());
            cursor.close();
        }
        mDatabase.close();
        Log.d("Songs in this playlist",""+playlists.size());
        return playlists;
    }

    public void addSongToPlaylists(long id,String playlistNames){

      // Cursor cursor = mDatabase.rawQuery("UPDATE "+ PlayerHelper.SONG_TABLE_NAME +" "+ PlayerHelper.COLUMN_PLAYLIST +"=" +PlayerHelper.COLUMN_PLAYLIST + playlistNames +"where ID="+id,null);
       /* String currentlist="";
        if (cursor.moveToFirst())
        {
            do
            {
                currentlist = cursor.getString(cursor.getColumnIndex(PlayerHelper.COLUMN_PLAYLIST));
            }while (cursor.moveToNext());
        }
        cursor.close();*/
        mDatabase.close();
    }

    public void addMultipleSongToSinglePlaylist(String playlistName,long id[]){

/*
        String[] columns = { PlayerHelper.COLUMN_ID ,PlayerHelper.COLUMN_PLAYLIST };
        Cursor cursor = mDatabase.query(PlayerHelper.SONG_TABLE_NAME, columns, null, null, null, null, PlayerHelper.COLUMN_NAME);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String list=cursor.getString(cursor.getColumnIndex(PlayerHelper.COLUMN_PLAYLIST));
                if(!list.contains(playlistName+",")){
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(PlayerHelper.COLUMN_PLAYLIST,list+playlistName+",");
                    String[] args = {"" + cursor.getLong(cursor.getColumnIndex(PlayerHelper.COLUMN_ID))};
                    mDatabase.update(PlayerHelper.SONG_TABLE_NAME, contentValues, PlayerHelper.COLUMN_ID + " =? ", args);
                }
            }
            while (cursor.moveToNext());
            cursor.close();
        }

        mDatabase.close();*/
    }

    /*For Albums
     1)Read list of Albums
     2)Read Songs within An Album*/
    public ArrayList<Album> readAlbums(){
        ArrayList<Album> albumlist = new ArrayList<>();
        String[] columns = {
                PlayerHelper.COLUMN_IMAGEPATH,
                PlayerHelper.COLUMN_ALBUM
        };
        int count=0;
        Cursor cursor=mDatabase.query(true,PlayerHelper.SONG_TABLE_NAME,columns,null,null,PlayerHelper.COLUMN_ALBUM,null,PlayerHelper.COLUMN_ALBUM,null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Album album=new Album();
                album.setName(cursor.getString(cursor.getColumnIndex(PlayerHelper.COLUMN_ALBUM)));
                album.setImagepath(cursor.getString(cursor.getColumnIndex(PlayerHelper.COLUMN_IMAGEPATH)));
                album.setViewType(1);
                count++;
                albumlist.add(album);
                Log.d("album list", " " + album.getViewType() );
            }
            while (cursor.moveToNext());
            cursor.close();
        }
        Log.d("Albums",count+"");
        mDatabase.close();

        return albumlist;
    }

    public ArrayList<Song> readAlbumSongs(String album) {
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
        String whereClause="ALBUM=?";
        String whereArgs[]={album};
        Cursor cursor = mDatabase.query(PlayerHelper.SONG_TABLE_NAME, columns, whereClause, whereArgs, null, null, PlayerHelper.COLUMN_NAME);
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
        return songlist;
    }


    /*For Artists
     1)Read list of Artists
     2)Read Songs of an Artist*/


    public ArrayList<Artist> readArtists(){
        ArrayList<Artist> artistlist = new ArrayList<>();
        String[] columns = {
                PlayerHelper.COLUMN_IMAGEPATH,
                PlayerHelper.COLUMN_ARTIST
        };
        int count=0;
        Cursor cursor=mDatabase.query(true,PlayerHelper.SONG_TABLE_NAME,columns,null,null,PlayerHelper.COLUMN_ARTIST,null,PlayerHelper.COLUMN_ARTIST,null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Artist artist=new Artist();
                artist.setName(cursor.getString(cursor.getColumnIndex(PlayerHelper.COLUMN_ARTIST)));
                artist.setImagepath(cursor.getString(cursor.getColumnIndex(PlayerHelper.COLUMN_IMAGEPATH)));
                count++;
                artistlist.add(artist);
            }
            while (cursor.moveToNext());
            cursor.close();
        }
        Log.d("Artists",count+"");
        mDatabase.close();

        return artistlist;
    }

    public ArrayList<Song> readArtistSongs(String artist) {
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
        String whereClause="ARTIST=?";
        String whereArgs[]={artist};
        Cursor cursor = mDatabase.query(PlayerHelper.SONG_TABLE_NAME, columns, whereClause, whereArgs, null, null, PlayerHelper.COLUMN_NAME);
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
        return songlist;
    }



    private class PlayerHelper extends SQLiteOpenHelper {
        private static final String DB_NAME = "playerDB";
        private static final int DB_VERSION = 2;
        private Context mContext;

        static final String SONG_TABLE_NAME = "SONGS_LIST";     //Attributes of Songs
        static final String COLUMN_ID = "ID";
        static final String COLUMN_NAME = "NAME";
        static final String COLUMN_ARTIST = "ARTIST";
        static final String COLUMN_DURATION = "DURATION";
        static final String COLUMN_IMAGEPATH = "IMAGEPATH";
        static final String COLUMN_PLAYLIST = "PLAYLIST";
        static final String COLUMN_ALBUM = "ALBUM";
        static final String COLUMN_FAVOURITE = "FAVOURITE";
        static final String COLUMN_VIEWTYPE = "VIEWTYPE";

        static final String PLAYLIST_TABLE_NAME = "PLAYLISTS_LIST";     //Attributes of Playlists
        static final String PLAYLIST_COLUMN_NAME = "NAME";

        static final String CREATE_TABLE_SONG_LIST = "CREATE TABLE " + SONG_TABLE_NAME + "(" +
                COLUMN_ID + " INTEGER," +
                COLUMN_NAME + " VARCHAR(40)," +
                COLUMN_ARTIST + " VARCHAR(40)," +
                COLUMN_DURATION + " INTEGER," +
                COLUMN_IMAGEPATH + " VARCHAR(40)," +
                COLUMN_PLAYLIST + " VARCHAR," +
                COLUMN_ALBUM + " VARCHAR(40)," +
                COLUMN_FAVOURITE + " INTEGER" +
                ");";

        static final String CREATE_TABLE_PLAYLISTS = "CREATE TABLE " + PLAYLIST_TABLE_NAME + "(" +
                PLAYLIST_COLUMN_NAME + " VARCHAR(40)" +
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