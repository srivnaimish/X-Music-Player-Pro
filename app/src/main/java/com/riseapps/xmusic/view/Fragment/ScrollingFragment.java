package com.riseapps.xmusic.view.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.riseapps.xmusic.widgets.MainTextView;
import com.riseapps.xmusic.widgets.MainTextViewSub;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ScrollingFragment extends Fragment {

    // views
    String Name,Imagepath=null,Action;
    ImageView imageView;
    private CircleImageView circleAlbumArt;
    TextView title;
    MainTextViewSub name;
    RecyclerView recyclerView;
    private Button playAllButton, shuffleButton;

    ArrayList<Song> songArrayList;
    NestedFragmentAdapter nestedFragmentAdapter;
    private PlaySongExec playSongExec;

    FloatingActionButton fab;


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
        fab= (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        recyclerView= (RecyclerView) rootView.findViewById(R.id.recyclerView);
        circleAlbumArt = (CircleImageView) rootView.findViewById(R.id.album_art);
        name = (MainTextViewSub) rootView.findViewById(R.id.type_name);
        playAllButton = (Button) rootView.findViewById(R.id.play_all_button);
        shuffleButton = (Button) rootView.findViewById(R.id.shuffle_button);

        if(Name!=null) {
            title.setText(Name);
            title.setVisibility(View.GONE);
            name.setText(Name);
        }

        else {
            title.setText("Favourites");
            title.setVisibility(View.GONE);
            name.setText("Favourites");
        }

        if (Imagepath!=null&&!Imagepath.equalsIgnoreCase("no_image")) {

            //faded image view in background
            Glide.with(getContext()).load(Uri.parse(Imagepath))
                    .crossFade()
                    .into(imageView);

            //circle image view
            Glide.with(getContext()).load(Uri.parse(Imagepath))
                    .crossFade()
                    .into(circleAlbumArt);
        }
        else {
            imageView.setImageResource(R.drawable.empty);
            circleAlbumArt.setImageResource(R.drawable.empty);
        }

        if(Action.equalsIgnoreCase("Album")){
            songArrayList=new MyApplication(getActivity()).getWritableDatabase().readAlbumSongs(Name);
        }
        else if (Action.equalsIgnoreCase("Artists"))
            songArrayList=new MyApplication(getActivity()).getWritableDatabase().readArtistSongs(Name);
        else if(Action.equalsIgnoreCase("Playlists")){
            songArrayList=new MyApplication(getActivity()).getWritableDatabase().readSongsFromPlaylist(Name);
        }
        else
            songArrayList=new MyApplication(getActivity()).getWritableDatabase().readFavouriteSongs();

        int spanCount = 1; // 2 columns
        int spacing = 20; // 50px

        recyclerView.addItemDecoration(new GridItemDecoration(spanCount, spacing, true));
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
                if((((MainActivity) getActivity()).getSongs()!=songArrayList)) {
                    Toast.makeText(getContext(), "Now Playing from "+title.getText().toString(), Toast.LENGTH_SHORT).show();
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

        playAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(songArrayList.size()!=0) {
                    if ((((MainActivity) getActivity()).getSongs() != songArrayList)) {
                        Toast.makeText(getContext(), "Playling All Songs from " + title.getText().toString(), Toast.LENGTH_SHORT).show();
                        ((MainActivity) getActivity()).setSongs(songArrayList);
                        ((MainActivity) getActivity()).getMusicService().setSongs(songArrayList);
                    }
                    playSongExec = new PlaySongExec(getContext(), 0);
                    playSongExec.startPlaying();
                }
                else
                    Snackbar.make(fab,"No Songs Here to Play",Snackbar.LENGTH_SHORT).show();
            }
        });

        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((((MainActivity) getActivity()).getSongs() != songArrayList)) {
                    Toast.makeText(getContext(), "Playling All Songs from " + title.getText().toString(), Toast.LENGTH_SHORT).show();
                    ((MainActivity) getActivity()).setSongs(songArrayList);
                    ((MainActivity) getActivity()).getMusicService().setSongs(songArrayList);
                    ((MainActivity) getActivity()).getMusicService().shuffleSongs();
                }
                playSongExec = new PlaySongExec(getContext(), 0);
                playSongExec.startPlaying();
            }
        });

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
