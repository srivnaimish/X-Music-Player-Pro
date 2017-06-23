package com.riseapps.xmusic.view.Fragment;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.riseapps.xmusic.R;
import com.riseapps.xmusic.component.SharedPreferenceSingelton;
import com.riseapps.xmusic.executor.Interfaces.ArtistRefreshListener;
import com.riseapps.xmusic.executor.Interfaces.ClickListener;
import com.riseapps.xmusic.executor.Interfaces.SongRefreshListener;
import com.riseapps.xmusic.executor.RecycleTouchListener;
import com.riseapps.xmusic.executor.RecycleViewAdapters.ArtistAdapter;
import com.riseapps.xmusic.model.Pojo.Album;
import com.riseapps.xmusic.model.Pojo.Artist;
import com.riseapps.xmusic.model.Pojo.Song;
import com.riseapps.xmusic.utils.GridItemDecoration;
import com.riseapps.xmusic.view.Activity.MainActivity;

import java.util.ArrayList;

public class ArtistFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    RecyclerView recyclerView;
    ArrayList<Artist> artistList = new ArrayList<>();
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
        int spanCount = 1;
        int spacing = 5;
        recyclerView.addItemDecoration(new GridItemDecoration(spanCount, spacing, true));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        sharedPreferenceSingelton = new SharedPreferenceSingelton();
        recyclerView.addOnItemTouchListener(new RecycleTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                ScrollingFragment scrollingFragment = new ScrollingFragment();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    setExitTransition(TransitionInflater.from(
                            getActivity()).inflateTransition(android.R.transition.fade));
                    scrollingFragment.setEnterTransition(TransitionInflater.from(
                            getActivity()).inflateTransition(android.R.transition.fade));
                }
                Bundle bundle = new Bundle();
                bundle.putLong("ID", artistList.get(position).getId());
                bundle.putString("Name", artistList.get(position).getName());
                bundle.putString("Action", "Artists");
                scrollingFragment.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.drawerLayout, scrollingFragment)
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


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
        if (data != null && data.moveToFirst()) {
            do {
                String artist = data.getString(data.getColumnIndex(MediaStore.Audio.Artists.ARTIST));
                long id = data.getLong(data.getColumnIndex(MediaStore.Audio.Artists._ID));
                if (artist.length() > 40)
                    artist = artist.substring(0, 32) + "...";
                artistList.add(new Artist(artist, id));
            }
            while (data.moveToNext());

            data.close();
        }
        artistAdapter = new ArtistAdapter(getActivity(), artistList, recyclerView);
        recyclerView.setAdapter(artistAdapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
