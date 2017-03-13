package com.riseapps.xmusic.view;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.riseapps.xmusic.R;
import com.riseapps.xmusic.executor.ClickListener;
import com.riseapps.xmusic.executor.RecycleTouchListener;
import com.riseapps.xmusic.executor.SongAdapter;
import com.riseapps.xmusic.model.MusicService;
import com.riseapps.xmusic.model.Song;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by naimish on 11/3/17.
 */

public class SongsFragment extends Fragment {

    String path="content://media/external/audio/media/";
    RecyclerView recyclerView;
    ArrayList<Song> songList;
    SongAdapter songsAdapter;
    Bundle bundle=new Bundle();
    View view;
    Gson gson = new Gson();
    Async async;

    public static SongsFragment newInstance() {
        return new SongsFragment();
    }

    public SongsFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_songs, container, false);
        async=new Async();
        songList = ((MainActivity) getActivity()).getSongs();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.songs);

        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        if (songList.size() != 0) {

        }
        else {
            async.execute();

        }


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
        view=rootView;
        return rootView;
    }

    public void getSongList() {
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
            int song_duration = musicCursor.getColumnIndex
                    (MediaStore.Audio.AudioColumns.DURATION);
            int albumColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM_ID);
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                long albumId = musicCursor.getLong(albumColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                if (thisTitle.length() > 25)
                    thisTitle = thisTitle.substring(0, 25) + "...";
                String thisArtist = musicCursor.getString(artistColumn);
                if (thisArtist.length() > 25)
                    thisArtist = thisArtist.substring(0, 25) + "...";
                long thisduration = musicCursor.getLong(song_duration);
                Uri uri = Uri.parse("content://media/external/audio/media/" + thisId + "/albumart");
                ParcelFileDescriptor pfd = null;
                try {
                    pfd = getActivity().getContentResolver().openFileDescriptor(uri, "r");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                if (pfd != null) {
                    songList.add(new Song(thisId, thisduration, thisTitle, thisArtist, uri));
                }
                else {
                    songList.add(new Song(thisId, thisduration, thisTitle, thisArtist, null));
                }

            }
            while (musicCursor.moveToNext());
            musicCursor.close();
        }


    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            View v=getView();
            if (v != null){

                //songList=gson.fromJson(bundle.getString("list"), new TypeToken<ArrayList<Song>>(){}.getType());
               Toast.makeText(view.getContext(),"visible", Toast.LENGTH_SHORT).show();
            }

        }
        else {
            View v=getView();
            if (v != null){

                Type type = new TypeToken<ArrayList<Song>>() {}.getType();
                String json = gson.toJson(songList, type);
                bundle.putString("list",json);
            }
        }
    }


    public class Async extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            getSongList();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ((MainActivity) getActivity()).setSongs(songList);
            songsAdapter = new SongAdapter(getActivity(), songList, recyclerView);
            recyclerView.setAdapter(songsAdapter);
            ((MainActivity) getActivity()).setRecyclerView(recyclerView);
            Toast.makeText(getContext(), songList.size()+"", Toast.LENGTH_SHORT).show();
            super.onPostExecute(aVoid);
        }
    }



    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
