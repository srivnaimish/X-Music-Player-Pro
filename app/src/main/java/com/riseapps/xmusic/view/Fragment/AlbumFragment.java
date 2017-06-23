package com.riseapps.xmusic.view.Fragment;

import android.content.ContentUris;
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
import com.riseapps.xmusic.executor.Interfaces.AlbumRefreshListener;
import com.riseapps.xmusic.executor.Interfaces.ClickListener;
import com.riseapps.xmusic.executor.RecycleTouchListener;
import com.riseapps.xmusic.executor.RecycleViewAdapters.AlbumsAdapter;
import com.riseapps.xmusic.model.Pojo.Album;
import com.riseapps.xmusic.model.Pojo.Artist;
import com.riseapps.xmusic.utils.GridItemDecoration;
import com.riseapps.xmusic.view.Activity.MainActivity;

import java.util.ArrayList;

public class AlbumFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    RecyclerView recyclerView;
    ArrayList<Album> albumList = new ArrayList<>();
    AlbumsAdapter albumAdapter;
    private static final int ALBUM_LOADER = 3;
    private SharedPreferenceSingelton sharedPreferenceSingelton;
    final Uri sArtworkUri = Uri
            .parse("content://media/external/audio/albumart");


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
                bundle.putLong("ID", albumList.get(position).getId());
                bundle.putString("Name", albumList.get(position).getName());
                bundle.putString("Imagepath", albumList.get(position).getImagepath());
                bundle.putString("Action", "Albums");
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

        getActivity().getSupportLoaderManager().initLoader(ALBUM_LOADER, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri musicUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        return new CursorLoader(getContext(), musicUri, null, null, null, MediaStore.Audio.Media.ALBUM + " COLLATE NOCASE ASC");

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            do {
                long album_id = data.getLong(data.getColumnIndex(MediaStore.Audio.Albums._ID));
                String album = data.getString(data.getColumnIndex(MediaStore.Audio.Albums.ALBUM));

                Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
                if (album.length() > 40)
                    album = album.substring(0, 32) + "...";
                albumList.add(new Album(album_id, album, uri.toString()));
            }
            while (data.moveToNext());
            data.close();
        }
        albumAdapter = new AlbumsAdapter(getActivity(), albumList, recyclerView);
        recyclerView.setAdapter(albumAdapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}