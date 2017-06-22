package com.riseapps.xmusic.view.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.riseapps.xmusic.R;
import com.riseapps.xmusic.component.SharedPreferenceSingelton;
import com.riseapps.xmusic.executor.Interfaces.MainListPlayingListener;
import com.riseapps.xmusic.executor.Interfaces.SongRefreshListener;
import com.riseapps.xmusic.executor.MyApplication;
import com.riseapps.xmusic.executor.RecycleViewAdapters.SongAdapter;
import com.riseapps.xmusic.model.Pojo.Song;
import com.riseapps.xmusic.utils.GridItemDecoration;
import com.riseapps.xmusic.view.Activity.MainActivity;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by naimish on 11/3/17.
 */

public class SongsFragment extends Fragment {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    RecyclerView recyclerView;
    LinearLayout background;
    ArrayList<Song> songMainList = new ArrayList<>();
    ArrayList<Song> songAllList = new ArrayList<>();
    SongAdapter songsAdapter;

    private LinearLayoutManager layoutManager;
    SharedPreferenceSingelton sharedPreferenceSingelton;

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

        background = (LinearLayout) rootView.findViewById(R.id.background);
        sharedPreferenceSingelton = new SharedPreferenceSingelton();

        songAllList = ((MainActivity) getActivity()).getCompleteSongList();
        if (songAllList.size() > 50) {
            songMainList = new ArrayList<>(songAllList.subList(0, 30));
        } else {
            songMainList = songAllList;
        }
        recyclerView = (RecyclerView) rootView.findViewById(R.id.songs);
        int spanCount = 1; // 2 columns
        int spacing = 5; // 50px
        recyclerView.addItemDecoration(new GridItemDecoration(spanCount, spacing, true));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        songsAdapter = new SongAdapter(getActivity(), songMainList, recyclerView);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1))
                    onScrolledToBottom();
            }
        });

        recyclerView.setAdapter(songsAdapter);


        ((MainActivity) getActivity()).setSongRefreshListener(new SongRefreshListener() {
            @Override
            public void OnSongRefresh(ArrayList<Song> arrayList) {
                songAllList = arrayList;
                if (songAllList.size() > 30) {
                    songMainList = new ArrayList<>(songAllList.subList(0, 30));
                } else {
                    songMainList = songAllList;
                }
                ((MainActivity) getActivity()).setSongs(songAllList);
                songsAdapter = new SongAdapter(getActivity(), songMainList, recyclerView);
                recyclerView.setAdapter(songsAdapter);

            }

            @Override
            public void OnContextBackPressed() {
                for(Song song:songMainList){
                    if(song.isSelected())
                    song.setSelected(false);
                }
                songsAdapter.count=0;
                songsAdapter.notifyDataSetChanged();
            }
        });

        songsAdapter.setMainListPlayingListener(new MainListPlayingListener() {
            @Override
            public void onPlayingFromTrackList() {
                if (((MainActivity) getActivity()).getSongs().size() != songAllList.size()) {
                    ((MainActivity) getActivity()).setSongs(songAllList);
                    ((MainActivity) getActivity()).getMusicService().setSongs(songAllList);
                }
            }
        });



        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    private void onScrolledToBottom() {
        if (songMainList.size() < songAllList.size()) {
            int x, y;
            if ((songAllList.size() - songMainList.size()) >= 30) {
                x = songMainList.size();
                y = x + 30;
            } else {
                x = songMainList.size();
                y = x + songAllList.size() - songMainList.size();
            }
            for (int i = x; i < y; i++) {
                songMainList.add(songAllList.get(i));
            }
            recyclerView.post(new Runnable() {
                public void run() {
                    songsAdapter.notifyDataSetChanged();
                }
            });
        }
    }

}
