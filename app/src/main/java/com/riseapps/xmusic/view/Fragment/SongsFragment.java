package com.riseapps.xmusic.view.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.riseapps.xmusic.R;
import com.riseapps.xmusic.component.SharedPreferenceSingelton;
import com.riseapps.xmusic.executor.ActionModeCallback;
import com.riseapps.xmusic.executor.Interfaces.SongRefreshListener;
import com.riseapps.xmusic.executor.PlaySongExec;
import com.riseapps.xmusic.executor.RecycleViewAdapters.SongAdapter;
import com.riseapps.xmusic.utils.RecyclerClickListener;
import com.riseapps.xmusic.model.Pojo.Song;
import com.riseapps.xmusic.utils.GridItemDecoration;
import com.riseapps.xmusic.utils.RecyclerTouchListener;
import com.riseapps.xmusic.view.Activity.MainActivity;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by naimish on 11/3/17.
 */

public class SongsFragment extends Fragment {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    ProgressBar progressBar;
    NestedScrollView nestedScrollView;
    RecyclerView recyclerView;
    ArrayList<Song> songMainList = new ArrayList<>();
    ArrayList<Song> songAllList = new ArrayList<>();
    SongAdapter songsAdapter;
    private ActionMode actionMode;
    private ActionModeCallback callback;
    private OnShowContextMenuListener mListener;
    private LinearLayoutManager layoutManager;

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
        nestedScrollView = (NestedScrollView) rootView.findViewById(R.id.nestedScrollView);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        String songJson = getActivity().getIntent().getStringExtra("songList");
        songAllList = new Gson().fromJson(songJson, new TypeToken<ArrayList<Song>>() {
        }.getType());
        ((MainActivity) getActivity()).setSongs(songAllList);

        if (songAllList.size() > 30) {
            songMainList = new ArrayList<>(songAllList.subList(0,30));
        } else {
            songMainList = songAllList;
        }
        recyclerView = (RecyclerView) rootView.findViewById(R.id.songs);
        int spanCount = 1; // 2 columns
        int spacing = 4; // 50px
        recyclerView.addItemDecoration(new GridItemDecoration(spanCount, spacing, true));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        songsAdapter = new SongAdapter(getActivity(), songMainList, recyclerView);

        nestedScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                View view = (View) nestedScrollView.getChildAt(nestedScrollView.getChildCount() - 1);
                int diff = (view.getBottom() - (nestedScrollView.getHeight() + nestedScrollView
                        .getScrollY()));

                if (diff == 0) {
                    progressBar.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (songMainList.size() < songAllList.size()) {
                                int x = 0, y = 0;
                                if ((songAllList.size() - songMainList.size()) >= 30) {
                                    x = songMainList.size();
                                    y = x + 30;
                                } else {
                                    x = songMainList.size();
                                    y = x + songAllList.size() - songMainList.size();
                                }
                                for (int i = x; i < y; i++) {
                                    songMainList.add(songAllList.get(i));
                                    songsAdapter.notifyDataSetChanged();/*
                           */
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    }, 1500);


                }
            }
        });
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

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new RecyclerClickListener() {
            @Override
            public void onClick(View view, int position) {

                    if ((((MainActivity) getActivity()).getSongs() != songMainList)||(((MainActivity) getActivity()).getSongs() != songAllList)) {
                        ((MainActivity) getActivity()).setSongs(songAllList);
                        ((MainActivity) getActivity()).getMusicService().setSongs(songAllList);
                        new PlaySongExec(getContext(), position).startPlaying();
                        new SharedPreferenceSingelton().saveAs(getContext(), "Shuffle", false);
                    }


                if (actionMode != null)
                    onListItemSelect(position);

            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));

        ((MainActivity) getActivity()).setSongRefreshListener(new SongRefreshListener() {
            @Override
            public void OnSongRefresh(ArrayList<Song> arrayList) {
                songAllList = arrayList;
                if (songAllList.size() > 30) {
                    songMainList = new ArrayList<>(songAllList.subList(0,30));
                } else {
                    songMainList = songAllList;
                }
                ((MainActivity) getActivity()).setSongs(songAllList);
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

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
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

        } else if (!hasCheckedItems && actionMode != null)
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
            //   Toast.makeText(getActivity(), "setting null", Toast.LENGTH_SHORT).show();
        }
    }

    public ArrayList<Song> getDummyData() throws JSONException {
        String dummyData = loadJSONFromAsset();
        songAllList = new Gson().fromJson(dummyData, new TypeToken<ArrayList<Song>>() {
        }.getType());
        return songAllList;
    }

    public String loadJSONFromAsset() {
        String json;
        try {
            InputStream is = getActivity().getAssets().open("dummy.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
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
