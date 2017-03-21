package com.riseapps.xmusic.executor;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.riseapps.xmusic.component.AlbumArtChecker;

/**
 * Created by naimish on 21/3/17.
 */

public class UpdateSongs {
    private Context context;
    private final int textLimit = 26;


    public UpdateSongs( Context context) {
        this.context = context;
    }

    public void fetchSongs(){
        new Async().execute();
    }
    private void getSongList() {
        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ARTIST);
            int song_duration = musicCursor.getColumnIndex
                    (MediaStore.Audio.AudioColumns.DURATION);
            int album=musicCursor.getColumnIndex
                    (MediaStore.Audio.AudioColumns.ALBUM);
            int x=0;
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                if (thisTitle.length() > textLimit)
                    thisTitle = thisTitle.substring(0, textLimit) + "...";
                String thisArtist = musicCursor.getString(artistColumn);
                if(thisArtist.equalsIgnoreCase("<unknown>")){
                    thisArtist="Unknown";
                }
                if (thisArtist.length() > textLimit)
                    thisArtist = thisArtist.substring(0, textLimit) + "...";

                String thisAlbum=musicCursor.getString(album);
                long thisduration = musicCursor.getLong(song_duration);

                String imagepath = "content://media/external/audio/media/" + thisId + "/albumart";
                if (!new AlbumArtChecker().hasAlbumArt(context, imagepath))
                    imagepath="no_image";

                if(thisAlbum==null){
                    thisAlbum="Unknown";
                }

                new MyApplication(context).getWritableDatabase().insertSong(thisId, thisTitle, thisArtist, thisduration, imagepath, "none",thisAlbum, false);

                x++;
            }
            while (musicCursor.moveToNext());
            Log.d("Song Insert", "" + x);
            musicCursor.close();
        }
    }

    private class Async extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            getSongList();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }


}
