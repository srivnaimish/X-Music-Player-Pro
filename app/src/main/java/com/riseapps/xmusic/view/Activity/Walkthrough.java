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
import com.riseapps.xmusic.component.SharedPreferenceSingelton;
import com.riseapps.xmusic.executor.MyApplication;
import com.riseapps.xmusic.executor.UpdateSongs;
import com.riseapps.xmusic.model.Pojo.Album;
import com.riseapps.xmusic.model.Pojo.Artist;
import com.riseapps.xmusic.model.Pojo.Playlist;
import com.riseapps.xmusic.model.Pojo.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Walkthrough extends AppCompatActivity {
    FloatingActionButton fab;
    final int textLimit = 26;
    private static final int REQUEST_PERMISSION = 0;
    UpdateSongs updateSongs;

    ArrayList<Song> songList=new ArrayList<>();
    ArrayList<Album> albumList=new ArrayList<>();
    ArrayList<Artist> artistList=new ArrayList<>();
   // ArrayList<Playlist> playLists=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walkthrough);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        updateSongs = new UpdateSongs(this);
        checkPermission();

        fab = (FloatingActionButton) findViewById(R.id.fab);

    }


    public void checkPermission() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
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
               new Async().execute();
            }
        } else {
            new Async().execute();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //updateSongs.fetchSongs();
                    new Async().execute();
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

    private class Async extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            new UpdateSongs(Walkthrough.this).getSongList();
            new MyApplication(Walkthrough.this).getWritableDatabase().insertPlaylist("All Songs,");
            songList = new MyApplication(Walkthrough.this).getWritableDatabase().readSongs();
            artistList = new MyApplication(Walkthrough.this).getWritableDatabase().readArtists();
            albumList = new MyApplication(Walkthrough.this).getWritableDatabase().readAlbums();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            new SharedPreferenceSingelton().saveAs(Walkthrough.this,"opened_before",true);
            Intent intent=new Intent(Walkthrough.this, MainActivity.class);
            intent.putParcelableArrayListExtra("songList",songList);
            intent.putParcelableArrayListExtra("albumList",albumList);
            intent.putParcelableArrayListExtra("artistList",artistList);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
            super.onPostExecute(aVoid);
        }
    }
}
