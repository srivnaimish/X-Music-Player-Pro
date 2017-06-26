package com.riseapps.xmusic.view.Fragment;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.riseapps.xmusic.R;
import com.riseapps.xmusic.component.SharedPreferenceSingelton;
import com.riseapps.xmusic.executor.Interfaces.FragmentTransitionListener;
import com.riseapps.xmusic.executor.RecycleViewAdapters.AlbumsAdapter;
import com.riseapps.xmusic.utils.GridItemDecoration;

import java.util.ArrayList;

public class AlbumFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    RecyclerView recyclerView;
    AlbumsAdapter albumAdapter;
    private static final int ALBUM_LOADER = 3;
    private SharedPreferenceSingelton sharedPreferenceSingelton;

    public AlbumFragment() {
        // Required empty public constructor
    }

    public static AlbumFragment newInstance() {
        return new AlbumFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_album, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.albums);
        int spanCount = 2;
        int spacing = 5;
        recyclerView.addItemDecoration(new GridItemDecoration(spanCount, spacing, true));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        GridLayoutManager grid = new GridLayoutManager(v.getContext(), 2);
        recyclerView.setLayoutManager(grid);
        sharedPreferenceSingelton = new SharedPreferenceSingelton();

        albumAdapter = new AlbumsAdapter(getActivity(),recyclerView,null);
        recyclerView.setAdapter(albumAdapter);

        albumAdapter.setFragmentTransitionListener(new FragmentTransitionListener() {
            @Override
            public void onFragmentTransition(ScrollingFragment scrollingFragment) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    setExitTransition(TransitionInflater.from(
                            getActivity()).inflateTransition(android.R.transition.fade));
                    scrollingFragment.setEnterTransition(TransitionInflater.from(
                            getActivity()).inflateTransition(android.R.transition.fade));
                }

            }
        });
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().getSupportLoaderManager().initLoader(ALBUM_LOADER, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri musicUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        return new CursorLoader(getContext(), musicUri, null, null, null, MediaStore.Audio.Media.ALBUM + " COLLATE NOCASE ASC");

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
      albumAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        albumAdapter.swapCursor(null);
    }
}