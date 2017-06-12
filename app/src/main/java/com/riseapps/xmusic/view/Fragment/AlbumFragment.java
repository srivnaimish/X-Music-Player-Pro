package com.riseapps.xmusic.view.Fragment;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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

public class AlbumFragment extends Fragment {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    SharedPreferenceSingelton sharedPreferenceSingelton;
    RecyclerView recyclerView;
    ArrayList<Album> albumMainList = new ArrayList<>();
    ArrayList<Album> albumAllList = new ArrayList<>();
    AlbumsAdapter albumAdapter;
    LinearLayout background;

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
        background = (LinearLayout) v.findViewById(R.id.background);
        sharedPreferenceSingelton = new SharedPreferenceSingelton();
        if (!sharedPreferenceSingelton.getSavedBoolean(getContext(), "Theme")) {
            GradientDrawable gd;
            gd = new GradientDrawable(
                    GradientDrawable.Orientation.BOTTOM_TOP,
                    new int[]{Color.parseColor("#EEEEEE"), Color.parseColor("#FFFFFF")});
            background.setBackground(gd);
        }
        /*;*/



        String albumJson = getActivity().getIntent().getStringExtra("albumList");
        albumAllList = new Gson().fromJson(albumJson, new TypeToken<ArrayList<Album>>() {
        }.getType());

        if (albumAllList.size() > 20) {
            albumMainList = new ArrayList<>(albumAllList.subList(0, 20));

        } else {
            albumMainList = albumAllList;
        }
        recyclerView = (RecyclerView) v.findViewById(R.id.albums);
        int spanCount = 2;
        int spacing = 4;
        recyclerView.addItemDecoration(new GridItemDecoration(spanCount, spacing, true));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        GridLayoutManager grid = new GridLayoutManager(v.getContext(), 2);
        recyclerView.setLayoutManager(grid);
        albumAdapter = new AlbumsAdapter(getActivity(), albumMainList, recyclerView);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1))
                    onScrolledToBottom();
            }
        });
        recyclerView.setAdapter(albumAdapter);

        ((MainActivity) getActivity()).setAlbumRefreshListener(new AlbumRefreshListener() {

            @Override
            public void OnAlbumRefresh(ArrayList<Album> arrayList) {
                albumAllList = arrayList;
                if (albumAllList.size() > 20) {
                    albumMainList = new ArrayList<>(albumAllList.subList(0, 20));

                } else {
                    albumMainList = albumAllList;
                }
                albumAdapter = new AlbumsAdapter(getActivity(), albumMainList, recyclerView);
                recyclerView.setAdapter(albumAdapter);
            }

        });
        recyclerView.addOnItemTouchListener(new RecycleTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                ScrollingFragment scrollingFragment = new ScrollingFragment();
                Bundle bundle = new Bundle();
                bundle.putString("Name", albumAllList.get(position).getName());
                bundle.putString("Imagepath", albumAllList.get(position).getImagepath());
                bundle.putString("Action", "Album");
                scrollingFragment.setArguments(bundle);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.drawerLayout, scrollingFragment);
                fragmentTransaction.commit();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        return v;
    }

    private void onScrolledToBottom() {
        if (albumMainList.size() < albumAllList.size()) {
            int x = 0, y = 0;
            if ((albumAllList.size() - albumMainList.size()) >= 20) {
                x = albumMainList.size();
                y = x + 20;
            } else {
                x = albumMainList.size();
                y = x + albumAllList.size() - albumMainList.size();
            }
            for (int i = x; i < y; i++) {
                albumMainList.add(albumAllList.get(i));
            }
            albumAdapter.notifyDataSetChanged();
        }

    }

}