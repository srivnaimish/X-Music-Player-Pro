package com.riseapps.xmusic.view.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class ArtistFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match

    RecyclerView recyclerView;
    ArrayList<Artist> artistLists=new ArrayList<>();
    ArtistAdapter artistAdapter;

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
        View v=inflater.inflate(R.layout.fragment_artist, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.artists);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager grid = new GridLayoutManager(v.getContext(), 2);
        recyclerView.setLayoutManager(grid);

        int spanCount = 2;
        int spacing = 12;

        recyclerView.addItemDecoration(new GridItemDecoration(spanCount, spacing, true));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.addOnItemTouchListener(new RecycleTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                ScrollingFragment scrollingFragment=new ScrollingFragment();
                Bundle bundle=new Bundle();
                bundle.putString("Name",artistLists.get(position).getName());
                bundle.putString("Imagepath",artistLists.get(position).getImagepath());
                bundle.putString("Action","Artists");
                scrollingFragment.setArguments(bundle);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.drawerLayout,scrollingFragment);
                fragmentTransaction.commit();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        String artistJson=getActivity().getIntent().getStringExtra("artistList");
        artistLists=new Gson().fromJson(artistJson, new TypeToken<ArrayList<Artist>>() {}.getType());
        artistAdapter = new ArtistAdapter(getActivity(), artistLists, recyclerView);
        recyclerView.setAdapter(artistAdapter);

        ((MainActivity) getActivity()).setArtistRefreshListener(new ArtistRefreshListener() {

            @Override
            public void OnArtistRefresh(ArrayList<Artist> arrayList) {
                artistLists=arrayList;
                artistAdapter.notifyDataSetChanged();


            }
        });

        return v;
    }
}
