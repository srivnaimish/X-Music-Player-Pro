package com.riseapps.xmusic.view.Fragment;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.riseapps.xmusic.R;
import com.riseapps.xmusic.component.SharedPreferenceSingelton;
import com.riseapps.xmusic.executor.Interfaces.ClickListener;
import com.riseapps.xmusic.executor.Interfaces.FragmentTransitionListener;
import com.riseapps.xmusic.executor.RecycleTouchListener;
import com.riseapps.xmusic.executor.RecycleViewAdapters.ArtistAdapter;
import com.riseapps.xmusic.model.Pojo.Artist;
import com.riseapps.xmusic.utils.GridItemDecoration;

import java.util.ArrayList;

public class ArtistFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    RecyclerView recyclerView;
    ArtistAdapter artistAdapter;
    private static final int ARTIST_LOADER = 2;
    private SharedPreferenceSingelton sharedPreferenceSingelton;

    public ArtistFragment() {
        // Required empty public constructor
    }


    public static ArtistFragment newInstance() {
        return new ArtistFragment();
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
        View v = inflater.inflate(R.layout.fragment_artist, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.artists);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        sharedPreferenceSingelton = new SharedPreferenceSingelton();

        artistAdapter = new ArtistAdapter(getActivity(), recyclerView,null);
        recyclerView.setAdapter(artistAdapter);

        artistAdapter.setFragmentTransitionListener(new FragmentTransitionListener() {
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
        getActivity().getSupportLoaderManager().initLoader(ARTIST_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri musicUri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        return new CursorLoader(getContext(), musicUri, null, null, null, MediaStore.Audio.Media.ARTIST + " COLLATE NOCASE ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        artistAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        artistAdapter.swapCursor(null);
    }
}
