package com.riseapps.xmusic.view.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.riseapps.xmusic.R;
import com.riseapps.xmusic.executor.ClickListener;
import com.riseapps.xmusic.executor.MyApplication;
import com.riseapps.xmusic.executor.PlaySongExec;
import com.riseapps.xmusic.executor.RecycleTouchListener;
import com.riseapps.xmusic.executor.RecycleViewAdapters.NestedFragmentAdapter;
import com.riseapps.xmusic.executor.RecycleViewAdapters.SongAdapter;
import com.riseapps.xmusic.model.Pojo.Song;
import com.riseapps.xmusic.utils.GridItemDecoration;
import com.riseapps.xmusic.view.Activity.MainActivity;

import java.util.ArrayList;

public class ScrollingFragment extends Fragment {

    String Name,Imagepath=null,Action;
    ImageView imageView;
    TextView title;
    RecyclerView recyclerView;
    ArrayList<Song> songArrayList;
    NestedFragmentAdapter nestedFragmentAdapter;
    private PlaySongExec playSongExec;


    private OnFragmentInteractionListener mListener;

    public ScrollingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Name=getArguments().getString("Name");
            Imagepath=getArguments().getString("Imagepath");
            Action=getArguments().getString("Action");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_scrolling, container, false);
        title= (TextView) rootView.findViewById(R.id.textView);
        imageView = (ImageView) rootView.findViewById(R.id.imageView);
        recyclerView= (RecyclerView) rootView.findViewById(R.id.recyclerView);

        title.setText(Name);
        if (Imagepath!=null&&!Imagepath.equalsIgnoreCase("no_image")) {
            Glide.with(getContext()).load(Uri.parse(Imagepath))
                    .crossFade()
                    .into(imageView);
        }
        else {
            imageView.setImageResource(R.drawable.empty);
        }

        if(Action.equalsIgnoreCase("Album")){
            songArrayList=new MyApplication(getActivity()).getWritableDatabase().readAlbumSongs(Name);
        }
        else if (Action.equalsIgnoreCase("Artists"))
            songArrayList=new MyApplication(getActivity()).getWritableDatabase().readArtistSongs(Name);
        else {
            songArrayList=new MyApplication(getActivity()).getWritableDatabase().readSongsFromPlaylist(Name);
        }

        int spanCount = 1; // 2 columns
        int spacing = 22; // 50px
        boolean includeEdge = true;

        recyclerView.addItemDecoration(new GridItemDecoration(spanCount, spacing, includeEdge));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        nestedFragmentAdapter = new NestedFragmentAdapter(getActivity(), songArrayList, recyclerView);
        recyclerView.setAdapter(nestedFragmentAdapter);

        recyclerView.addOnItemTouchListener(new RecycleTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
              //  Toast.makeText(getContext(), ""+position, Toast.LENGTH_SHORT).show();
                if((((MainActivity) getActivity()).getSongs()!=songArrayList)) {
                    Toast.makeText(getContext(), "Now Playing from "+Name, Toast.LENGTH_SHORT).show();
                    ((MainActivity) getActivity()).setSongs(songArrayList);
                    ((MainActivity) getActivity()).getMusicService().setSongs(songArrayList);
                }
                playSongExec = new PlaySongExec(getContext(), position);
                playSongExec.startPlaying();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
       // ((MainActivity) getActivity()).setSongs(songArrayList);
        //((MainActivity) getActivity()).getMusicService().setSongs(songArrayList);

        return rootView;
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
