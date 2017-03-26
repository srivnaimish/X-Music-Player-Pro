package com.riseapps.xmusic.view.Fragment;

import android.net.Uri;
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
import com.riseapps.xmusic.R;
import com.riseapps.xmusic.executor.ActionModeCallback;
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
    View view;
    Gson gson = new Gson();
   // Async async;
    final int textLimit = 26;
    //Type type=new TypeToken<ArrayList<Song>>() {}.getType();
    private ActionMode actionMode;
    private ActionModeCallback callback;

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
        songList=getActivity().getIntent().getParcelableArrayListExtra("songList");
        ((MainActivity) getActivity()).setSongs(songList);
        songsAdapter = new SongAdapter(getActivity(), songList, recyclerView);
        recyclerView.setAdapter(songsAdapter);

        implemetRecyclerViewListener();

        /*recyclerView.addOnItemTouchListener(new RecycleTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //  Toast.makeText(getContext(), ""+position, Toast.LENGTH_SHORT).show();
                if((((MainActivity) getActivity()).getSongs()!=songList)) {
                    Toast.makeText(getContext(), "Now Playing All Songs", Toast.LENGTH_SHORT).show();
                    ((MainActivity) getActivity()).setSongs(songList);
                    ((MainActivity) getActivity()).getMusicService().setSongs(songList);
                }

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));*/
      //  async.execute();

        view = rootView;
        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            View v = getView();
            if (v != null) {
                // Toast.makeText(view.getContext(),"visible", Toast.LENGTH_SHORT).show();
            }

        } else {
            View v = getView();
            if (v != null) {

            }
        }
    }

    private void implemetRecyclerViewListener() {
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new RecyclerClickListener() {
            @Override
            public void onClick(View view, int position) {
                //If ActionMode not null select item
                if (actionMode != null)
                    onListItemSelect(position);
            }

            @Override
            public void onLongClick(View view, int position) {
                //Select item on long click
                onListItemSelect(position);
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

   /* private class Async extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            songList = new MyApplication(getActivity()).getWritableDatabase().readSongs();
            ((MainActivity) getActivity()).setSongs(songList);
            songsAdapter = new SongAdapter(getActivity(), songList, recyclerView);
            Log.d("Songs","Loaded in async "+songList.size());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            recyclerView.setAdapter(songsAdapter);
            // ((MainActivity) getActivity()).setRecyclerView(recyclerView);
            super.onPostExecute(aVoid);
        }
    }
*/
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
