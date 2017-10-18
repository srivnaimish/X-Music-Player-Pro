package com.riseapps.xmusic.view.Fragment;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.ImageView;

import com.riseapps.xmusic.R;
import com.riseapps.xmusic.component.SharedPreferenceSingelton;
import com.riseapps.xmusic.executor.Interfaces.MainListPlayingListener;
import com.riseapps.xmusic.executor.Interfaces.SongRefreshListener;
import com.riseapps.xmusic.executor.RecycleViewAdapters.SongAdapter;
import com.riseapps.xmusic.model.Pojo.Song;
import com.riseapps.xmusic.utils.GridItemDecoration;
import com.riseapps.xmusic.view.activity.MainActivity;

import java.util.ArrayList;

/**
 * Created by naimish on 11/3/17.
 */

public class SongsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    RecyclerView recyclerView;
    ArrayList<Song> songsList = new ArrayList<>();
    SongAdapter songsAdapter;
    private static final int SONG_LOADER = 1;
    ImageView imageView;
    private String selection = "";

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

        imageView = (ImageView) rootView.findViewById(R.id.empty_state);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.songs);
        int spanCount = 1; // 2 columns
        int spacing = 5; // 50px
        recyclerView.addItemDecoration(new GridItemDecoration(spanCount, spacing, true));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        songsAdapter = new SongAdapter(getActivity(), recyclerView, null);
        recyclerView.setAdapter(songsAdapter);

        ((MainActivity) getActivity()).setSongRefreshListener(new SongRefreshListener() {

            @Override
            public void OnContextBackPressed() {
                for (Song song : songsList) {
                    if (song.isSelected())
                        song.setSelected(false);
                }
                songsAdapter.count = 0;
                songsAdapter.notifyDataSetChanged();
            }

            @Override
            public void OnSongDelete() {
                for (int i = songsList.size() - 1; i >= 0; i--) {
                    Song song = songsList.get(i);
                    if (song.isSelected()) {
                        songsAdapter.delete(i);
                    }
                }
                songsAdapter.count = 0;
            }
        });


        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getSupportLoaderManager().initLoader(SONG_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        initiallizeMultipleNames();
        if (selection != null)
            return new CursorLoader(getContext(), musicUri, null, selection, null, MediaStore.Audio.Media.TITLE + " COLLATE NOCASE ASC");
        else
            return new CursorLoader(getContext(), musicUri, null, null, null, MediaStore.Audio.Media.TITLE + " COLLATE NOCASE ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                songsAdapter.swapCursor(data);
                songsList = songsAdapter.songsList;
                setPlayingFromThisFragment();
                ((MainActivity) getActivity()).setCompleteSongList(songsList);
                ((MainActivity) getActivity()).startTheService();
                imageView.setVisibility(View.GONE);
            }
        },300);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        songsAdapter.swapCursor(null);
    }

    void setPlayingFromThisFragment() {
        songsAdapter.setMainListPlayingListener(new MainListPlayingListener() {
            @Override
            public void onPlayingFromTrackList() {
                if (((MainActivity) getActivity()).getSongs() != songsList) {
                    ((MainActivity) getActivity()).setSongs(songsList);
                    ((MainActivity) getActivity()).getMusicService().setSongs(songsList);
                }
            }
        });
    }

    void initiallizeMultipleNames() {
        String names = new SharedPreferenceSingelton().getSavedString(getContext(), "SkipFolders");
        if (names != null) {
            String[] folders = names.split(",");
            for (int i = 0; i < folders.length; i++) {
                if (i < folders.length - 1)
                    selection += MediaStore.Audio.Media.DATA + " NOT LIKE '%/" + folders[i] + "/%'" + " AND ";
                else
                    selection += MediaStore.Audio.Media.DATA + " NOT LIKE '%/" + folders[i] + "/%'";
            }
        } else {
            selection = null;
        }
    }

}
