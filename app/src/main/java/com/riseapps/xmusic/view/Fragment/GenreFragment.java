package com.riseapps.xmusic.view.Fragment;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
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
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.riseapps.xmusic.R;
import com.riseapps.xmusic.component.SharedPreferenceSingelton;
import com.riseapps.xmusic.executor.Interfaces.FragmentTransitionListener;
import com.riseapps.xmusic.executor.RecycleViewAdapters.ArtistAdapter;
import com.riseapps.xmusic.executor.RecycleViewAdapters.GenreAdapter;

public class GenreFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    RecyclerView recyclerView;
    GenreAdapter genreAdapter;
    private static final int Genre_LOADER = 5;
    private SharedPreferenceSingelton sharedPreferenceSingelton;

    public GenreFragment() {
        // Required empty public constructor
    }


    public static GenreFragment newInstance() {
        return new GenreFragment();
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
        View v = inflater.inflate(R.layout.fragment_genre, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.genres);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        sharedPreferenceSingelton = new SharedPreferenceSingelton();

        genreAdapter = new GenreAdapter(getActivity(), recyclerView, null);
        recyclerView.setAdapter(genreAdapter);

        genreAdapter.setFragmentTransitionListener(new FragmentTransitionListener() {
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
        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getActivity().getSupportLoaderManager().initLoader(Genre_LOADER, null, GenreFragment.this);
                }
            },1000);
        }catch (Exception e){}

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri musicUri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI;
        return new CursorLoader(getContext(), musicUri, null, null, null, MediaStore.Audio.Genres.NAME + " COLLATE NOCASE ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        genreAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        genreAdapter.swapCursor(null);
    }
}
