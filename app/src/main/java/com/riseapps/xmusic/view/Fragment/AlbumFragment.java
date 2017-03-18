package com.riseapps.xmusic.view.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.riseapps.xmusic.R;
import com.riseapps.xmusic.executor.RecycleViewAdapters.AlbumsAdapter;
import com.riseapps.xmusic.model.Pojo.Album;
import com.riseapps.xmusic.utils.GridItemDecoration;

import java.util.ArrayList;

public class AlbumFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    RecyclerView recyclerView;
    ArrayList<Album> albumLists=new ArrayList<>();
    AlbumsAdapter albumAdapter;
    private OnFragmentInteractionListener mListener;

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
        // Inflate the layout for this fragment_songs
        View v=inflater.inflate(R.layout.fragment_album, container, false);

        albumLists.add(new Album("auto album 1",5));
        albumLists.add(new Album("auto album 1",5));
        albumLists.add(new Album("auto album 1",5));
        albumLists.add(new Album("auto album 1",5));
        albumLists.add(new Album("auto album 1",5));
        albumLists.add(new Album("auto album 1",5));
        recyclerView = (RecyclerView) v.findViewById(R.id.albums);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager grid = new GridLayoutManager(v.getContext(), 2);
        recyclerView.setLayoutManager(grid);

        int spanCount = 2;
        int spacing = 20;

        recyclerView.addItemDecoration(new GridItemDecoration(spanCount, spacing, true));
        recyclerView.setNestedScrollingEnabled(false);

        albumAdapter = new AlbumsAdapter(getActivity(), albumLists, recyclerView);
        recyclerView.setAdapter(albumAdapter);
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}