package com.riseapps.xmusic.view.Activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.riseapps.xmusic.R;
import com.riseapps.xmusic.component.AlbumArtChecker;
import com.riseapps.xmusic.executor.MyApplication;
import com.riseapps.xmusic.model.Pojo.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Walkthrough extends AppCompatActivity {
    FloatingActionButton fab;
    final int textLimit = 26;
    Async async;
    private static final int REQUEST_PERMISSION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walkthrough);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        async=new Async();
        checkPermission();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            startActivity(new Intent(Walkthrough.this,MainActivity.class));
                finish();
            }
        });
    }

    public void getSongList() {
        ContentResolver musicResolver = getContentResolver();
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
           int x=0;
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                if (thisTitle.length() > textLimit)
                    thisTitle = thisTitle.substring(0, textLimit) + "...";
                String thisArtist = musicCursor.getString(artistColumn);
                if (thisArtist.length() > textLimit)
                    thisArtist = thisArtist.substring(0, textLimit) + "...";
                long thisduration = musicCursor.getLong(song_duration);

                String imagepath = "content://media/external/audio/media/" + thisId + "/albumart";
                if (new AlbumArtChecker().hasAlbumArt(this, imagepath)) {
                    new MyApplication(this).getWritableDatabase().insertSong(thisId, thisTitle, thisArtist, thisduration, imagepath, "none", false);
                } else {
                    new MyApplication(this).getWritableDatabase().insertSong(thisId, thisTitle, thisArtist, thisduration, "no_image", "none", false);
                }
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

    public void checkPermission()
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>=android.os.Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_CALENDAR)) {
                    Snackbar.make(fab, R.string.permission_rationale,
                            Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ActivityCompat.requestPermissions(Walkthrough.this,
                                            new String[]{Manifest.permission.CAMERA},
                                            REQUEST_PERMISSION);
                                }
                            })
                            .show();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
                }
            } else {
                async.execute();
            }
        } else {
            async.execute();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    async.execute();
                } else {
                    Snackbar.make(fab, R.string.permission_rationale,
                            Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ActivityCompat.requestPermissions(Walkthrough.this,
                                            new String[]{Manifest.permission.CAMERA},
                                            REQUEST_PERMISSION);
                                }
                            }).show();
                }
                break;
        }
    }
}
