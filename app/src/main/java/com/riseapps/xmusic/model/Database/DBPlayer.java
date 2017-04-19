package com.riseapps.xmusic.model.Database;

import android.content.ContentValues;
import android.content.Context;
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

    public void insertSong(long id, String Name, String Artist, long duration, String Imagepath, String albumName) {
        String sql = "INSERT INTO " + PlayerHelper.SONG_TABLE_NAME + " (" + PlayerHelper.COLUMN_ID + "," + PlayerHelper.COLUMN_NAME + "," +
                PlayerHelper.COLUMN_ARTIST + "," + PlayerHelper.COLUMN_DURATION + "," + PlayerHelper.COLUMN_IMAGEPATH + "," +
                PlayerHelper.COLUMN_ALBUM + "," + PlayerHelper.COLUMN_FAVOURITE + ") VALUES(?,?,?,?,?,?,?);";
        SQLiteStatement statement = mDatabase.compileStatement(sql);
        mDatabase.beginTransaction();
        statement.clearBindings();
        statement.bindLong(1, id);
        statement.bindString(2, Name);
        statement.bindString(3, Artist);
        statement.bindLong(4, duration);
        statement.bindString(5, Imagepath);
        statement.bindString(6, albumName);
        statement.bindLong(7, 0);
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

    public void updateFavourites(long id, int favourite) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(PlayerHelper.COLUMN_FAVOURITE, favourite);
        String[] args = {"" + id};
        mDatabase.update(PlayerHelper.SONG_TABLE_NAME, contentValues, PlayerHelper.COLUMN_ID + " =? ", args);
        mDatabase.close();
    }

    public ArrayList<Song> readFavouriteSongs() {
        ArrayList<Song> songlist = new ArrayList<>();
        String[] columns = {
                PlayerHelper.COLUMN_ID,
                PlayerHelper.COLUMN_NAME,
                PlayerHelper.COLUMN_ARTIST,
                PlayerHelper.COLUMN_DURATION,
                PlayerHelper.COLUMN_IMAGEPATH,
                PlayerHelper.COLUMN_FAVOURITE
        };
        String whereClause = "FAVOURITE=?";
        String whereArgs[] = {"" + 1};
        Cursor cursor = mDatabase.query(PlayerHelper.SONG_TABLE_NAME, columns, whereClause, whereArgs, null, null, PlayerHelper.COLUMN_NAME);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Song song = new Song();
                song.setID(cursor.getLong(cursor.getColumnIndex(PlayerHelper.COLUMN_ID)));
                song.setName(cursor.getString(cursor.getColumnIndex(PlayerHelper.COLUMN_NAME)));
                song.setArtist(cursor.getString(cursor.getColumnIndex(PlayerHelper.COLUMN_ARTIST)));
                song.setDuration(cursor.getLong(cursor.getColumnIndex(PlayerHelper.COLUMN_DURATION)));
                song.setImagepath(cursor.getString(cursor.getColumnIndex(PlayerHelper.COLUMN_IMAGEPATH)));
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

    public boolean isSongPresent(long id) {
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

    public void deleteSong(long id) {
        String whereClause = PlayerHelper.COLUMN_ID + "=?";
        String whereArgs[] = {"" + id};
        mDatabase.delete(PlayerHelper.SONG_TABLE_NAME, whereClause, whereArgs);

        String whereC = PlayerHelper.PLAYLIST_COLUMN_SONG + "=?";
        String whereA[] = {"" + id};
        mDatabase.delete(PlayerHelper.PLAYLIST_TABLE_NAME, whereC, whereA);

        mDatabase.close();
        Log.d("Song", "Deleted");
    }


    public void insertNewPlaylist(String PlaylistName, long id) {
        String sql = "INSERT INTO " + PlayerHelper.PLAYLIST_TABLE_NAME + " (" + PlayerHelper.PLAYLIST_COLUMN_NAME + "," + PlayerHelper.PLAYLIST_COLUMN_SONG + ") VALUES(?,?);";
        SQLiteStatement statement = mDatabase.compileStatement(sql);
        mDatabase.beginTransaction();
        statement.clearBindings();
        statement.bindString(1, PlaylistName);
        statement.bindLong(2, id);
        statement.execute();
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
        mDatabase.close();
    }

    public ArrayList<Playlist> readPlaylists() {
        ArrayList<Playlist> playlists = new ArrayList<>();
        String[] columns = {
                PlayerHelper.PLAYLIST_COLUMN_NAME
        };
        Cursor cursor = mDatabase.query(true, PlayerHelper.PLAYLIST_TABLE_NAME, columns, null, null, PlayerHelper.PLAYLIST_COLUMN_NAME, null, PlayerHelper.PLAYLIST_COLUMN_NAME, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Playlist playlist = new Playlist();
                playlist.setName(cursor.getString(cursor.getColumnIndex(PlayerHelper.PLAYLIST_COLUMN_NAME)));
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
        String args[] = {playlist};
        Cursor cursor = mDatabase.rawQuery("select * from SONGS_LIST where ID in (select SONG from PLAYLISTS_LIST where NAME = ?)  ORDER BY NAME", args);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Song song = new Song();
                song.setID(cursor.getLong(cursor.getColumnIndex(PlayerHelper.COLUMN_ID)));
                song.setName(cursor.getString(cursor.getColumnIndex(PlayerHelper.COLUMN_NAME)));
                song.setArtist(cursor.getString(cursor.getColumnIndex(PlayerHelper.COLUMN_ARTIST)));
                song.setDuration(cursor.getLong(cursor.getColumnIndex(PlayerHelper.COLUMN_DURATION)));
                song.setImagepath(cursor.getString(cursor.getColumnIndex(PlayerHelper.COLUMN_IMAGEPATH)));
                int f = cursor.getInt(cursor.getColumnIndex(PlayerHelper.COLUMN_FAVOURITE));
                if (f == 1)
                    song.setFavourite(true);
                else
                    song.setFavourite(false);

                if (playlists.size() == 0 ||playlists.size() ==11) {
                    playlists.add(song);
                    playlists.add(null);
                } else
                    playlists.add(song);

            }
            while (cursor.moveToNext());
            cursor.close();
        }
        mDatabase.close();
        return playlists;
    }


    public void addSongToPlaylists(long id, String playlistNames) {
        String S[] = convertStringToArray(playlistNames);
        for (int i = 0; i < S.length; i++) {
            String sql = "INSERT INTO " + PlayerHelper.PLAYLIST_TABLE_NAME + " (" + PlayerHelper.PLAYLIST_COLUMN_NAME + "," + PlayerHelper.PLAYLIST_COLUMN_SONG + ") VALUES(?,?);";
            SQLiteStatement statement = mDatabase.compileStatement(sql);
            mDatabase.beginTransaction();
            statement.clearBindings();
            statement.bindString(1, S[i]);
            statement.bindLong(2, id);
            statement.execute();
            mDatabase.setTransactionSuccessful();
            mDatabase.endTransaction();
        }
        mDatabase.close();

    }

    public void addMultipleSongToSinglePlaylist(String playlistName, long id[]) {
        for (long anId : id) {
            Log.d("TAG ARRAY LONG", String.valueOf(anId));
            String sql = "INSERT INTO " + PlayerHelper.PLAYLIST_TABLE_NAME + " (" + PlayerHelper.PLAYLIST_COLUMN_NAME + "," + PlayerHelper.PLAYLIST_COLUMN_SONG + ") VALUES(?,?);";
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
        String whereClause = PlayerHelper.PLAYLIST_COLUMN_NAME + "=?";
        String whereArgs[] = {name};
        mDatabase.delete(PlayerHelper.PLAYLIST_TABLE_NAME, whereClause, whereArgs);
        mDatabase.close();
        Log.d("Playlist", "Deleted");
    }

    /*For Albums
     1)Read list of Albums
     2)Read Songs within An Album*/
    public ArrayList<Album> readAlbums() {
        ArrayList<Album> albumlist = new ArrayList<>();
        String[] columns = {
                PlayerHelper.COLUMN_IMAGEPATH,
                PlayerHelper.COLUMN_ALBUM
        };
        int count = 0;
        Cursor cursor = mDatabase.query(true, PlayerHelper.SONG_TABLE_NAME, columns, null, null, PlayerHelper.COLUMN_ALBUM, null, PlayerHelper.COLUMN_ALBUM, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {

                Album album = new Album();
                album.setName(cursor.getString(cursor.getColumnIndex(PlayerHelper.COLUMN_ALBUM)));
                album.setImagepath(cursor.getString(cursor.getColumnIndex(PlayerHelper.COLUMN_IMAGEPATH)));
                count++;
                    /*if(albumlist.size()==3||albumlist.size()==12) {
                        albumlist.add(album);
                        albumlist.add(null);
                    }
                    else*/
                albumlist.add(album);

            }
            while (cursor.moveToNext());
            cursor.close();
        }
        Log.d("Albums", count + "");
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
                PlayerHelper.COLUMN_FAVOURITE
        };
        String whereClause = "ALBUM=?";
        String whereArgs[] = {album};
        Cursor cursor = mDatabase.query(PlayerHelper.SONG_TABLE_NAME, columns, whereClause, whereArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Song song = new Song();
                song.setID(cursor.getLong(cursor.getColumnIndex(PlayerHelper.COLUMN_ID)));
                song.setName(cursor.getString(cursor.getColumnIndex(PlayerHelper.COLUMN_NAME)));
                song.setArtist(cursor.getString(cursor.getColumnIndex(PlayerHelper.COLUMN_ARTIST)));
                song.setDuration(cursor.getLong(cursor.getColumnIndex(PlayerHelper.COLUMN_DURATION)));
                song.setImagepath(cursor.getString(cursor.getColumnIndex(PlayerHelper.COLUMN_IMAGEPATH)));
                int f = cursor.getInt(cursor.getColumnIndex(PlayerHelper.COLUMN_FAVOURITE));
                if (f == 1)
                    song.setFavourite(true);
                else
                    song.setFavourite(false);

                if (songlist.size() == 0 || songlist.size() == 7) {
                    songlist.add(song);
                    songlist.add(null);
                } else
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


    public ArrayList<Artist> readArtists() {
        ArrayList<Artist> artistlist = new ArrayList<>();
        String[] columns = {
                PlayerHelper.COLUMN_IMAGEPATH,
                PlayerHelper.COLUMN_ARTIST
        };
        int count = 0;
        Cursor cursor = mDatabase.query(true, PlayerHelper.SONG_TABLE_NAME, columns, null, null, PlayerHelper.COLUMN_ARTIST, null, PlayerHelper.COLUMN_ARTIST, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Artist artist = new Artist();
                artist.setName(cursor.getString(cursor.getColumnIndex(PlayerHelper.COLUMN_ARTIST)));
                artist.setImagepath(cursor.getString(cursor.getColumnIndex(PlayerHelper.COLUMN_IMAGEPATH)));
                count++;
                /*if(artistlist.size()==5||artistlist.size()==23) {
                    artistlist.add(artist);
                    artistlist.add(null);
                }
                else*/
                artistlist.add(artist);
            }
            while (cursor.moveToNext());
            cursor.close();
        }
        Log.d("Artists", count + "");
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
                PlayerHelper.COLUMN_FAVOURITE
        };
        String whereClause = "ARTIST=?";
        String whereArgs[] = {artist};
        Cursor cursor = mDatabase.query(PlayerHelper.SONG_TABLE_NAME, columns, whereClause, whereArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Song song = new Song();
                song.setID(cursor.getLong(cursor.getColumnIndex(PlayerHelper.COLUMN_ID)));
                song.setName(cursor.getString(cursor.getColumnIndex(PlayerHelper.COLUMN_NAME)));
                song.setArtist(cursor.getString(cursor.getColumnIndex(PlayerHelper.COLUMN_ARTIST)));
                song.setDuration(cursor.getLong(cursor.getColumnIndex(PlayerHelper.COLUMN_DURATION)));
                song.setImagepath(cursor.getString(cursor.getColumnIndex(PlayerHelper.COLUMN_IMAGEPATH)));
                int f = cursor.getInt(cursor.getColumnIndex(PlayerHelper.COLUMN_FAVOURITE));
                if (f == 1)
                    song.setFavourite(true);
                else
                    song.setFavourite(false);
                if (songlist.size() == 0 || songlist.size() == 7) {
                    songlist.add(song);
                    songlist.add(null);
                } else
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
        private static final int DB_VERSION = 4;
        private Context mContext;

        static final String SONG_TABLE_NAME = "SONGS_LIST";     //Attributes of Songs
        static final String COLUMN_ID = "ID";
        static final String COLUMN_NAME = "NAME";
        static final String COLUMN_ARTIST = "ARTIST";
        static final String COLUMN_DURATION = "DURATION";
        static final String COLUMN_IMAGEPATH = "IMAGEPATH";
        static final String COLUMN_ALBUM = "ALBUM";
        static final String COLUMN_FAVOURITE = "FAVOURITE";
        static final String COLUMN_VIEWTYPE = "VIEWTYPE";

        static final String PLAYLIST_TABLE_NAME = "PLAYLISTS_LIST";     //Attributes of Playlists
        static final String PLAYLIST_COLUMN_NAME = "NAME";
        static final String PLAYLIST_COLUMN_SONG = "SONG";

        static final String CREATE_TABLE_SONG_LIST = "CREATE TABLE " + SONG_TABLE_NAME + "(" +
                COLUMN_ID + " INTEGER," +
                COLUMN_NAME + " VARCHAR(40)," +
                COLUMN_ARTIST + " VARCHAR(40)," +
                COLUMN_DURATION + " INTEGER," +
                COLUMN_IMAGEPATH + " VARCHAR(40)," +
                COLUMN_ALBUM + " VARCHAR(40)," +
                COLUMN_FAVOURITE + " INTEGER" +
                ");";

        static final String CREATE_TABLE_PLAYLISTS = "CREATE TABLE " + PLAYLIST_TABLE_NAME + "(" +
                PLAYLIST_COLUMN_NAME + " VARCHAR(40)," +
                PLAYLIST_COLUMN_SONG + " INTEGER" +
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

    public static String[] convertStringToArray(String str) {
        String[] arr = str.split(strSeparator);
        return arr;
    }
}