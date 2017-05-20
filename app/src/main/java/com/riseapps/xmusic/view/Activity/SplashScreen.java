package com.riseapps.xmusic.view.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.riseapps.xmusic.R;
import com.riseapps.xmusic.component.SharedPreferenceSingelton;
import com.riseapps.xmusic.executor.MyApplication;
import com.riseapps.xmusic.executor.UpdateSongs;
import com.riseapps.xmusic.model.Pojo.Album;
import com.riseapps.xmusic.model.Pojo.Artist;
import com.riseapps.xmusic.model.Pojo.Playlist;
import com.riseapps.xmusic.model.Pojo.Song;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SplashScreen extends AppCompatActivity {

    ArrayList<Song> songList = new ArrayList<>();
    ArrayList<Album> albumList = new ArrayList<>();
    ArrayList<Artist> artistList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_splash_screen);
        if (new SharedPreferenceSingelton().getSavedBoolean(SplashScreen.this, "opened_before")) {
            new Async().execute();
        } else {
            startActivity(new Intent(SplashScreen.this, Walkthrough.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }


    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private class Async extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            songList = new MyApplication(SplashScreen.this).getWritableDatabase().readSongs();
            artistList = new MyApplication(SplashScreen.this).getWritableDatabase().readArtists();
            albumList = new MyApplication(SplashScreen.this).getWritableDatabase().readAlbums();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Song>>() {
            }.getType();
            String songJson = gson.toJson(songList, type);
            if(songList.size()>6) {
                String songSubJson = gson.toJson(songList.subList(0, 10), type);
                intent.putExtra("songSubList", songSubJson);
            }
            type = new TypeToken<ArrayList<Album>>() {
            }.getType();
            String albumJson = gson.toJson(albumList, type);

            type = new TypeToken<ArrayList<Artist>>() {
            }.getType();
            String artistJson = gson.toJson(artistList, type);

            intent.putExtra("songList", songJson);
            intent.putExtra("albumList", albumJson);
            intent.putExtra("artistList", artistJson);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
            super.onPostExecute(aVoid);
        }
    }

}