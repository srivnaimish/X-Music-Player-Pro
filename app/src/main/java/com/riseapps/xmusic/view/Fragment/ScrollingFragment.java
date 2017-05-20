package com.riseapps.xmusic.view.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.riseapps.xmusic.R;
import com.riseapps.xmusic.component.SharedPreferenceSingelton;
import com.riseapps.xmusic.executor.CheckConnectivity;
import com.riseapps.xmusic.executor.Interfaces.ClickListener;
import com.riseapps.xmusic.executor.MyApplication;
import com.riseapps.xmusic.executor.PlaySongExec;
import com.riseapps.xmusic.executor.RecycleTouchListener;
import com.riseapps.xmusic.executor.RecycleViewAdapters.NestedFragmentAdapter;
import com.riseapps.xmusic.model.Pojo.Song;
import com.riseapps.xmusic.utils.GridItemDecoration;
import com.riseapps.xmusic.view.Activity.MainActivity;
import com.riseapps.xmusic.widgets.MainTextViewSub;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ScrollingFragment extends Fragment {

    // views
    String Name,Imagepath=null,Action;
    ImageView imageView;
    private ImageView circleAlbumArt;
    TextView title;
    MainTextViewSub name;
    RecyclerView recyclerView;
    private Button playAllButton, shuffleButton;

    ArrayList<Song> songArrayList;
    NestedFragmentAdapter nestedFragmentAdapter;
    private PlaySongExec playSongExec;

    FloatingActionButton fab;


    private OnFragmentInteractionListener mListener;
    private ArrayList<Song> songAllList;

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
        circleAlbumArt = (ImageView) rootView.findViewById(R.id.album_art);
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
            //imageView.setImageResource(R.drawable.empty);
            Glide.with(getContext()).load("https://cdn.pixabay.com/photo/2016/04/19/05/07/turntable-1337986_960_720.jpg")
                    .crossFade()
                    .placeholder(R.drawable.ic_music_player)
                    .into(imageView);
            Glide.with(getContext()).load("https://cdn.pixabay.com/photo/2016/04/19/05/07/turntable-1337986_960_720.jpg")
                    .crossFade()
                    .placeholder(R.drawable.ic_music_player)
                    .into(circleAlbumArt);
        }

        if(Action.equalsIgnoreCase("Album")){
            songArrayList=new MyApplication(getActivity()).getWritableDatabase().readAlbumSongs(Name);
        }
        else if (Action.equalsIgnoreCase("Artists"))
            songArrayList=new MyApplication(getActivity()).getWritableDatabase().readArtistSongs(Name);
        else if(Action.equalsIgnoreCase("Playlists")){
            songArrayList=new MyApplication(getActivity()).getWritableDatabase().readSongsFromPlaylist(Name);
            /*try {
                songArrayList = getDummyData();
                ((MainActivity) getActivity()).setSongs(songArrayList);
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
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
        if(new CheckConnectivity().isNetworkAvailable(getActivity())) {
            songArrayList.add(1, null);
            if(songArrayList.size()>10)
                songArrayList.add(9,null);
        }
        nestedFragmentAdapter = new NestedFragmentAdapter(getActivity(), songArrayList, recyclerView);
        recyclerView.setAdapter(nestedFragmentAdapter);

        recyclerView.addOnItemTouchListener(new RecycleTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if((((MainActivity) getActivity()).getSongs()!=songArrayList)) {
                    Toast.makeText(getContext(), getContext().getString(R.string.now_playing)+" "+title.getText().toString(), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getContext(), getContext().getString(R.string.playing_all)+" " + title.getText().toString(), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getContext(), getContext().getString(R.string.playing_all)+" " + title.getText().toString(), Toast.LENGTH_SHORT).show();
                    ((MainActivity) getActivity()).setSongs(songArrayList);
                    ((MainActivity) getActivity()).getMusicService().setSongs(songArrayList);
                }
                playSongExec = new PlaySongExec(getContext(), 0);
                playSongExec.startPlaying();
                new SharedPreferenceSingelton().saveAs(getActivity(), "Shuffle", true);
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

    public ArrayList<Song> getDummyData() throws JSONException {
        String dummyData = loadJSONFromAsset();
        songAllList=new Gson().fromJson(dummyData, new TypeToken<ArrayList<Song>>() {}.getType());
        return songAllList;
    }

    public String loadJSONFromAsset() {
        String json;
        try {
            InputStream is = getActivity().getAssets().open("dummy.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
