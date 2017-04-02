package com.riseapps.xmusic.view.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.riseapps.xmusic.R;
import com.riseapps.xmusic.executor.ActionModeCallback;
import com.riseapps.xmusic.executor.Interfaces.SongRefreshListener;
import com.riseapps.xmusic.executor.RecycleViewAdapters.SongAdapter;
import com.riseapps.xmusic.utils.RecyclerClickListener;
import com.riseapps.xmusic.model.Pojo.Song;
import com.riseapps.xmusic.utils.GridItemDecoration;
import com.riseapps.xmusic.utils.RecyclerTouchListener;
import com.riseapps.xmusic.view.Activity.MainActivity;

import java.util.ArrayList;

/**
 * Created by naimish on 11/3/17.
 */

public class SongsFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<Song> songList=new ArrayList<>();
    SongAdapter songsAdapter;
    private ActionMode actionMode;
    private ActionModeCallback callback;
    private OnShowContextMenuListener mListener;

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
      //  async = new Async();

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

        String songJson=getActivity().getIntent().getStringExtra("songList");
        songList=new Gson().fromJson(songJson, new TypeToken<ArrayList<Song>>() {}.getType());
        ((MainActivity) getActivity()).setSongs(songList);
        songsAdapter = new SongAdapter(getActivity(), songList, recyclerView);
        recyclerView.setAdapter(songsAdapter);

        songsAdapter.setContextMenuListener(new SongAdapter.OnShowContextMenuListener() {
            @Override
            public void onShowFirst(int count) {
                Toast.makeText(getActivity(), "show " + count, Toast.LENGTH_SHORT).show();
                mListener.onShowToolbar(count);
            }

            @Override
            public void onShow(int count) {
                mListener.onShowCount(count);
            }

            @Override
            public void onHide() {
                Toast.makeText(getActivity(), "hide", Toast.LENGTH_SHORT).show();
                mListener.onHideToolbar();
            }
        });

        implemetRecyclerViewListener();

        ((MainActivity) getActivity()).setSongRefreshListener(new SongRefreshListener() {
            @Override
            public void OnSongRefresh(ArrayList<Song> arrayList) {
                songList=arrayList;
                ((MainActivity) getActivity()).setSongs(songList);
                songsAdapter = new SongAdapter(getActivity(), songList, recyclerView);
                recyclerView.setAdapter(songsAdapter);
                songsAdapter.setContextMenuListener(new SongAdapter.OnShowContextMenuListener() {
                    @Override
                    public void onShowFirst(int count) {
                        Toast.makeText(getActivity(), "show", Toast.LENGTH_SHORT).show();
                        mListener.onShowToolbar(count);
                    }

                    @Override
                    public void onShow(int count) {
                        mListener.onShowCount(count);
                    }

                    @Override
                    public void onHide() {
                        Toast.makeText(getActivity(), "hide", Toast.LENGTH_SHORT).show();
                        mListener.onHideToolbar();
                    }
                });
            }

            @Override
            public void onSongRefresh() {
                ((MainActivity) getActivity()).setSongs(songList);
                songsAdapter = new SongAdapter(getActivity(), songList, recyclerView);
                songsAdapter.removeAllSelection();
                recyclerView.setAdapter(songsAdapter);
                songsAdapter.setContextMenuListener(new SongAdapter.OnShowContextMenuListener() {
                    @Override
                    public void onShowFirst(int count) {
                        Toast.makeText(getActivity(), "show", Toast.LENGTH_SHORT).show();
                        mListener.onShowToolbar(count);
                    }

                    @Override
                    public void onShow(int count) {
                        mListener.onShowCount(count);
                    }

                    @Override
                    public void onHide() {
                        Toast.makeText(getActivity(), "hide", Toast.LENGTH_SHORT).show();
                        mListener.onHideToolbar();
                    }
                });
            }
        });
      //  async.execute();

        return rootView;
    }


    private void implemetRecyclerViewListener() {
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new RecyclerClickListener() {
            @Override
            public void onClick(View view, int position) {
                if(view.getId()==R.id.name||view.getId()==R.id.artist_mini||view.getId()==R.id.album_art_card) {
                    if ((((MainActivity) getActivity()).getSongs() != songList)) {
                        Toast.makeText(getContext(), "Now Playing All Songs", Toast.LENGTH_SHORT).show();
                        ((MainActivity) getActivity()).setSongs(songList);
                        ((MainActivity) getActivity()).getMusicService().setSongs(songList);
                    }
                }
                if (actionMode != null)
                    onListItemSelect(position);
            }

            @Override
            public void onLongClick(View view, int position) {
                //Select item on long click
                //onListItemSelect(position);
            }
        }));
    }

    //List item select method
    private void onListItemSelect(int position) {
        songsAdapter.toggleSelection(position);//Toggle the selection

        boolean hasCheckedItems = songsAdapter.getSelectedCount() > 0;//Check if any items are already selected or not

        if (hasCheckedItems && actionMode == null) {
            // there are some selected items, start the actionMode
            callback = new ActionModeCallback(getActivity(), songsAdapter);
            actionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(callback);
            callback.setListener(new ActionModeCallback.OnSetNullListener() {
                @Override
                public void onNullifySuccess() {
                    setNullToActionMode();
                }
            });

        }

        else if (!hasCheckedItems && actionMode != null)
            // there no selected items, finish the actionMode
            actionMode.finish();

        if (actionMode != null)
            //set action mode title on item selection
            actionMode.setTitle(String.valueOf(songsAdapter
                    .getSelectedCount()) + " selected");

    }

    public void setNullToActionMode() {
        if (actionMode != null) {
            actionMode = null;
            Toast.makeText(getActivity(), "setting null", Toast.LENGTH_SHORT).show();
        }
    }

    public interface OnShowContextMenuListener {
        void onShowToolbar(int count);
        void onShowCount(int count);
        void onHideToolbar();
    }

    public void setOnShowContextMenuListener(OnShowContextMenuListener listener) {
        mListener = listener;
    }
}
