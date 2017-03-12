package com.riseapps.xmusic.view;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.riseapps.xmusic.R;
import com.riseapps.xmusic.executor.ClickListener;
import com.riseapps.xmusic.executor.RecycleTouchListener;
import com.riseapps.xmusic.executor.SongAdapter;
import com.riseapps.xmusic.model.MusicService;
import com.riseapps.xmusic.model.Song;

import java.util.ArrayList;

/**
 * Created by naimish on 11/3/17.
 */

public class SongsFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<Song> songList;
    SongAdapter songsAdapter;

    private static final String ARG_SECTION_NUMBER = "section_number";

    public static SongsFragment newInstance(int sectionNumber) {
        SongsFragment fragment = new SongsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public SongsFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment, container, false);

        songList = ((MainActivity) getActivity()).getSongs();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.songs);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);



        getSongList();

        ((MainActivity) getActivity()).setSongs(songList);
        songsAdapter = new SongAdapter(getActivity(), songList, recyclerView);
        recyclerView.setAdapter(songsAdapter);

        ((MainActivity) getActivity()).setRecyclerView(recyclerView);

        recyclerView.addOnItemTouchListener(new RecycleTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                MusicService musicService =((MainActivity)getActivity()).getMusicService();
                musicService.setSong(position);
                musicService.togglePlay();

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return rootView;
    }

    public void getSongList() {
        //retrieve song info
        ContentResolver musicResolver = getActivity().getContentResolver();
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
            int song_duration=musicCursor.getColumnIndex
                    (MediaStore.Audio.AudioColumns.DURATION);
            int albumColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM_ID);
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                long albumId = musicCursor.getLong(albumColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                if(thisTitle.length()>25)
                    thisTitle=thisTitle.substring(0,25)+"...";
                String thisArtist = musicCursor.getString(artistColumn);
                if(thisArtist.length()>25)
                    thisArtist=thisArtist.substring(0,25)+"...";
                long thisduration=musicCursor.getLong(song_duration);
                final Uri ART_CONTENT_URI = Uri.parse("content://media/external/audio/albumart");
                Uri albumArtUri = ContentUris.withAppendedId(ART_CONTENT_URI, albumId);
                songList.add(new Song(thisId, thisduration,thisTitle, thisArtist, albumArtUri));
            }
            while (musicCursor.moveToNext());
            musicCursor.close();
        }

    }
}
