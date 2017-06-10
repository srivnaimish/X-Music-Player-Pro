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
import android.support.v7.widget.LinearLayoutManager;
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

public class ArtistFragment extends Fragment {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    RecyclerView recyclerView;
    ArrayList<Artist> artistAllList = new ArrayList<>();
    ArrayList<Artist> artistMainList = new ArrayList<>();
    ArtistAdapter artistAdapter;
    LinearLayout background;

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
        background = (LinearLayout) v.findViewById(R.id.background);
        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.BOTTOM_TOP,
                new int[]{Color.parseColor("#EEEEEE"), Color.parseColor("#FFFFFF")});
        background.setBackground(gd);
        String artistJson = getActivity().getIntent().getStringExtra("artistList");
        getActivity().getIntent().removeExtra("artistList");
        artistAllList = new Gson().fromJson(artistJson, new TypeToken<ArrayList<Artist>>() {
        }.getType());

        if (artistAllList.size() > 50) {
            artistMainList = new ArrayList<>(artistAllList.subList(0, 50));

        } else {
            artistMainList = artistAllList;
        }

        recyclerView = (RecyclerView) v.findViewById(R.id.artists);
        int spanCount = 1;
        int spacing = 4;
        recyclerView.addItemDecoration(new GridItemDecoration(spanCount, spacing, true));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        GridLayoutManager grid = new GridLayoutManager(v.getContext(), 1);
        recyclerView.setLayoutManager(grid);
        artistAdapter = new ArtistAdapter(getActivity(), artistMainList, recyclerView);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1))
                    onScrolledToBottom();
            }
        });

        recyclerView.setAdapter(artistAdapter);

        ((MainActivity) getActivity()).setArtistRefreshListener(new ArtistRefreshListener() {

            @Override
            public void OnArtistRefresh(ArrayList<Artist> arrayList) {
                artistAllList = arrayList;
                if (artistAllList.size() > 20) {
                    artistMainList = new ArrayList<>(artistAllList.subList(0, 20));

                } else {
                    artistMainList = artistAllList;
                }
                artistAdapter = new ArtistAdapter(getActivity(), artistMainList, recyclerView);
                recyclerView.setAdapter(artistAdapter);
            }
        });

        recyclerView.addOnItemTouchListener(new RecycleTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                ScrollingFragment scrollingFragment = new ScrollingFragment();
                Bundle bundle = new Bundle();
                bundle.putString("Name", artistAllList.get(position).getName());
                bundle.putString("Imagepath", artistAllList.get(position).getImagepath());
                bundle.putString("Action", "Artists");
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
        if (artistMainList.size() < artistAllList.size()) {
            int x, y;
            if ((artistAllList.size() - artistMainList.size()) >= 20) {
                x = artistMainList.size();
                y = x + 20;
            } else {
                x = artistMainList.size();
                y = x + artistAllList.size() - artistMainList.size();
            }

            for (int i = x; i < y; i++) {
                artistMainList.add(artistAllList.get(i));
            }
            artistAdapter.notifyDataSetChanged();
        }

    }
}
