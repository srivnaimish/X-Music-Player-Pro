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
import java.util.HashMap;

/**
 * Created by naimish on 11/3/17.
 */

public class SongsFragment extends Fragment{

    boolean loaded=false;
    RecyclerView recyclerView;
    ArrayList<Song> songMainList=new ArrayList<>();
    ArrayList<Song> songAllList=new ArrayList<>();
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
        int spacing = 1; // 50px
        boolean includeEdge = true;
        recyclerView.addItemDecoration(new GridItemDecoration(spanCount, spacing, true));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        String songJson=getActivity().getIntent().getStringExtra("songList");
        songAllList=new Gson().fromJson(songJson, new TypeToken<ArrayList<Song>>() {}.getType());
        ((MainActivity) getActivity()).setSongs(songAllList);

        if(songAllList.size()>6){
            String songSubJson=getActivity().getIntent().getStringExtra("songSubList");
            songMainList=new Gson().fromJson(songSubJson, new TypeToken<ArrayList<Song>>() {}.getType());

        }
        else {
            songMainList=songAllList;
        }
        songsAdapter = new SongAdapter(getActivity(), songMainList, recyclerView);

        recyclerView.setAdapter(songsAdapter);

        songsAdapter.setContextMenuListener(new SongAdapter.OnShowContextMenuListener() {
            @Override
            public void onShowFirst(int count, HashMap<Integer, Boolean> list) {
                mListener.onShowToolbar(count, list);
            }

            @Override
            public void onShow(int count, HashMap<Integer, Boolean> list) {
                mListener.onShowCount(count, list);
            }

            @Override
            public void onHide() {
                mListener.onHideToolbar();
            }
        });

        implemetRecyclerViewListener();

        ((MainActivity) getActivity()).setSongRefreshListener(new SongRefreshListener() {
            @Override
            public void OnSongRefresh(ArrayList<Song> arrayList) {
                songMainList=arrayList;
                ((MainActivity) getActivity()).setSongs(songMainList);
                songsAdapter = new SongAdapter(getActivity(), songMainList, recyclerView);
                recyclerView.setAdapter(songsAdapter);
                songsAdapter.setContextMenuListener(new SongAdapter.OnShowContextMenuListener() {
                    @Override
                    public void onShowFirst(int count, HashMap<Integer, Boolean> list) {
                        //Toast.makeText(getActivity(), "show first " + list, Toast.LENGTH_SHORT).show();
                        mListener.onShowToolbar(count, list);
                    }

                    @Override
                    public void onShow(int count, HashMap<Integer, Boolean> list) {
                        //Toast.makeText(getActivity(), "show " + list, Toast.LENGTH_SHORT).show();
                        mListener.onShowCount(count, list);
                    }

                    @Override
                    public void onHide() {
                        //Toast.makeText(getActivity(), "hide", Toast.LENGTH_SHORT).show();
                        mListener.onHideToolbar();
                    }
                });
            }

            @Override
            public void onSongRefresh() {
                ((MainActivity) getActivity()).setSongs(songMainList);
                songsAdapter = new SongAdapter(getActivity(), songMainList, recyclerView);
                songsAdapter.removeAllSelection();
                recyclerView.setAdapter(songsAdapter);
                songsAdapter.setContextMenuListener(new SongAdapter.OnShowContextMenuListener() {
                    @Override
                    public void onShowFirst(int count, HashMap<Integer, Boolean> list) {
                        //Toast.makeText(getActivity(), "show first " + list, Toast.LENGTH_SHORT).show();
                        mListener.onShowToolbar(count, list);
                    }

                    @Override
                    public void onShow(int count, HashMap<Integer, Boolean> list) {
                        //Toast.makeText(getActivity(), "show " + list, Toast.LENGTH_SHORT).show();
                        mListener.onShowCount(count, list);
                    }

                    @Override
                    public void onHide() {
                        //Toast.makeText(getActivity(), "hide", Toast.LENGTH_SHORT).show();
                        mListener.onHideToolbar();
                    }
                });
            }
        });
        //  async.execute();

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser&&!loaded){
            if(songAllList.size()>6){
                for(int i=6;i<songAllList.size();i++)
                    songMainList.add(songAllList.get(i));

                songsAdapter.notifyDataSetChanged();
            }
            loaded=true;
        }
    }

    private void implemetRecyclerViewListener() {
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new RecyclerClickListener() {
            @Override
            public void onClick(View view, int position) {
                if(view.getId()==R.id.name||view.getId()==R.id.artist_mini||view.getId()==R.id.album_art_card) {
                    if ((((MainActivity) getActivity()).getSongs() != songMainList)) {
                        Toast.makeText(getContext(), "Now Playing All Songs", Toast.LENGTH_SHORT).show();
                        ((MainActivity) getActivity()).setSongs(songMainList);
                        ((MainActivity) getActivity()).getMusicService().setSongs(songMainList);
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
        void onShowToolbar(int count, HashMap<Integer, Boolean> list);
        void onShowCount(int count, HashMap<Integer, Boolean> list);
        void onHideToolbar();
    }

    public void setOnShowContextMenuListener(OnShowContextMenuListener listener) {
        mListener = listener;
    }
}
