package com.riseapps.xmusic.view.Fragment;

import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.riseapps.xmusic.R;
import com.riseapps.xmusic.component.SharedPreferenceSingelton;
import com.riseapps.xmusic.executor.Interfaces.MainListPlayingListener;
import com.riseapps.xmusic.executor.Interfaces.SongRefreshListener;
import com.riseapps.xmusic.executor.RecycleViewAdapters.SongAdapter;
import com.riseapps.xmusic.model.Pojo.Song;
import com.riseapps.xmusic.utils.GridItemDecoration;
import com.riseapps.xmusic.view.Activity.MainActivity;

import java.util.ArrayList;

/**
 * Created by naimish on 11/3/17.
 */

public class SongsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    RecyclerView recyclerView;
    ArrayList<Song> songsList = new ArrayList<>();
    SongAdapter songsAdapter;
    private static final int SONG_LOADER = 1;

    public static SongsFragment newInstance() {
        return new SongsFragment();
    }

    public SongsFragment getSongsFragment() {
        return SongsFragment.this;
    }

    public SongsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_songs, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.songs);
        int spanCount = 1; // 2 columns
        int spacing = 5; // 50px
        recyclerView.addItemDecoration(new GridItemDecoration(spanCount, spacing, true));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ((MainActivity)getActivity()).setSongRefreshListener(new SongRefreshListener() {

            @Override
            public void OnContextBackPressed() {
                for(Song song:songsList){
                    if(song.isSelected())
                        song.setSelected(false);
                }
                songsAdapter.count=0;
                songsAdapter.notifyDataSetChanged();
            }

            @Override
            public void OnSongDelete() {
                for(int i=songsList.size()-1;i>=0;i--){
                    Song song=songsList.get(i);
                    if(song.isSelected()){
                        songsAdapter.delete(i);
                    }
                }
                songsAdapter.count=0;
            }
        });


        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getSupportLoaderManager().initLoader(SONG_LOADER,null,this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        return new CursorLoader(getContext(),musicUri,null,null,null,MediaStore.Audio.Media.TITLE + " COLLATE NOCASE ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int textLimit = 27;
        int short_time=new SharedPreferenceSingelton().getSavedInt(getActivity(),"Short_music_time");
        if (data != null && data.moveToFirst()) {
            do {
                long duration = data.getLong(data.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION));
                if(duration > (short_time *1000)) {
                    long id = data.getLong(data.getColumnIndex(MediaStore.Audio.Media._ID));
                    String title = data.getString(data.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String artist = data.getString(data.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    String imagepath = "content://media/external/audio/media/" + id + "/albumart";
                    if (title.length() > textLimit)
                        title = title.substring(0, textLimit) + "...";
                    if (artist == null)
                        artist = "Unknown";
                    if (artist.length() > textLimit)
                        artist = artist.substring(0, textLimit) + "...";
                    songsList.add(new Song(id, duration, title, artist, imagepath, false));

                }
            }
            while (data.moveToNext());

            data.close();
        }
        songsAdapter = new SongAdapter(getActivity(), songsList,recyclerView);
        recyclerView.setAdapter(songsAdapter);
        setPlayingFromThisFragment();
        ((MainActivity) getActivity()).setCompleteSongList(songsList);
        ((MainActivity) getActivity()).startTheService();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    void setPlayingFromThisFragment(){
        songsAdapter.setMainListPlayingListener(new MainListPlayingListener() {
            @Override
            public void onPlayingFromTrackList() {
                if (((MainActivity) getActivity()).getSongs().size() != songsList.size()) {
                    ((MainActivity) getActivity()).setSongs(songsList);
                    ((MainActivity) getActivity()).getMusicService().setSongs(songsList);
                }
            }
        });
    }

}
