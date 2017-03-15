package com.riseapps.xmusic.view;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.riseapps.xmusic.R;
import com.riseapps.xmusic.component.AlbumArtChecker;
import com.riseapps.xmusic.executor.SongAdapter;
import com.riseapps.xmusic.model.Song;
import com.riseapps.xmusic.utils.GridItemDecoration;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by naimish on 11/3/17.
 */

public class SongsFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<Song> songList;
    SongAdapter songsAdapter;
    View view;
    Gson gson = new Gson();
    Async async;

    ImageButton like;
    Type type=new TypeToken<ArrayList<Song>>() {}.getType();

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
        int spanCount = 1; // 2 columns
        int spacing = 20; // 50px
        boolean includeEdge = true;
        recyclerView.addItemDecoration(new GridItemDecoration(spanCount, spacing, includeEdge));
        recyclerView.setHasFixedSize(true);

        recyclerView.setNestedScrollingEnabled(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        async.execute();

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
                if (thisTitle.length() > 32)
                    thisTitle = thisTitle.substring(0, 32) + "...";
                String thisArtist = musicCursor.getString(artistColumn);
                if (thisArtist.length() > 32)
                    thisArtist = thisArtist.substring(0, 32) + "...";
                long thisduration = musicCursor.getLong(song_duration);

                String imagepath="content://media/external/audio/media/" + thisId + "/albumart";
                if(new AlbumArtChecker().hasAlbumArt(getContext(),imagepath)){
                    songList.add(new Song(thisId, thisduration, thisTitle, thisArtist, imagepath,false));
                }
                else {
                    songList.add(new Song(thisId, thisduration, thisTitle, thisArtist, "no_image",false));
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


              // Toast.makeText(view.getContext(),"visible", Toast.LENGTH_SHORT).show();
            }

        }
        else {
            View v=getView();
            if (v != null){

            }
        }
    }


    private class Async extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
           // String recieved=new SharedPreferenceSingelton().getSavedString(getContext(),"songList");

         /*   if (recieved!=null) {
                songList=gson.fromJson(recieved,type);
                Log.d(getClass().getSimpleName(),songList.size()+"");
            }
            else {*/
                getSongList();
                Collections.sort(songList, new Comparator<Song>() {
                    @Override
                    public int compare(Song song, Song t1) {
                        return song.getName().compareTo(t1.getName());
                    }
                });
            /*    String json = gson.toJson(songList, type);
                new SharedPreferenceSingelton().saveAs(getContext(),"songList",json);
            }*/
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ((MainActivity) getActivity()).setSongs(songList);
            songsAdapter = new SongAdapter(getActivity(), songList, recyclerView);
            recyclerView.setAdapter(songsAdapter);
           // ((MainActivity) getActivity()).setRecyclerView(recyclerView);
            super.onPostExecute(aVoid);
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
