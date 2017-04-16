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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.riseapps.xmusic.R;
import com.riseapps.xmusic.executor.Interfaces.AlbumRefreshListener;
import com.riseapps.xmusic.executor.Interfaces.ClickListener;
import com.riseapps.xmusic.executor.Interfaces.SongRefreshListener;
import com.riseapps.xmusic.executor.RecycleTouchListener;
import com.riseapps.xmusic.executor.RecycleViewAdapters.AlbumsAdapter;
import com.riseapps.xmusic.model.Pojo.Album;
import com.riseapps.xmusic.model.Pojo.Artist;
import com.riseapps.xmusic.model.Pojo.Song;
import com.riseapps.xmusic.utils.GridItemDecoration;
import com.riseapps.xmusic.view.Activity.MainActivity;

import java.util.ArrayList;

public class AlbumFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<Album> albumLists=new ArrayList<>();
    AlbumsAdapter albumAdapter;

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

        View v=inflater.inflate(R.layout.fragment_album, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.albums);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager grid = new GridLayoutManager(v.getContext(), 2);

        recyclerView.setLayoutManager(grid);

        int spanCount = 2;
        int spacing = 18;

        recyclerView.addItemDecoration(new GridItemDecoration(spanCount, spacing, true));

        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.addOnItemTouchListener(new RecycleTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                ScrollingFragment scrollingFragment=new ScrollingFragment();
                Bundle bundle=new Bundle();
                bundle.putString("Name",albumLists.get(position).getName());
                bundle.putString("Imagepath",albumLists.get(position).getImagepath());
                bundle.putString("Action","Album");
                scrollingFragment.setArguments(bundle);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.drawerLayout,scrollingFragment);
                fragmentTransaction.commit();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        String songJson=getActivity().getIntent().getStringExtra("albumList");

        albumLists=new Gson().fromJson(songJson, new TypeToken<ArrayList<Album>>() {}.getType());

        albumAdapter = new AlbumsAdapter(getActivity(), albumLists, recyclerView);

        recyclerView.setAdapter(albumAdapter);

        ((MainActivity) getActivity()).setAlbumRefreshListener(new AlbumRefreshListener() {

            @Override
            public void OnAlbumRefresh(ArrayList<Album> arrayList) {
                albumLists=arrayList;
                albumAdapter = new AlbumsAdapter(getActivity(), albumLists, recyclerView);
                recyclerView.setAdapter(albumAdapter);
            }

        });



        return v;
    }

}