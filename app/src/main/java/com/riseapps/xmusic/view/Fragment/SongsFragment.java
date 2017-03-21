package com.riseapps.xmusic.view.Fragment;

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

import com.google.gson.Gson;
import com.riseapps.xmusic.R;
import com.riseapps.xmusic.component.AlbumArtChecker;
import com.riseapps.xmusic.executor.MyApplication;
import com.riseapps.xmusic.executor.RecycleViewAdapters.SongAdapter;
import com.riseapps.xmusic.model.Pojo.Song;
import com.riseapps.xmusic.utils.GridItemDecoration;
import com.riseapps.xmusic.view.Activity.MainActivity;

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
    final int textLimit = 26;
    //Type type=new TypeToken<ArrayList<Song>>() {}.getType();

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
        int spacing = 22; // 50px
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
                songList=new MyApplication(getActivity()).getWritableDatabase().readSongs();
            ((MainActivity) getActivity()).setSongs(songList);
            songsAdapter = new SongAdapter(getActivity(), songList, recyclerView);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
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
