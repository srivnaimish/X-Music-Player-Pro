package com.riseapps.xmusic.view.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    ProgressBar progressBar;
    NestedScrollView nestedScrollView;
    // views
    String Name,Imagepath=null,Action;
    ImageView imageView;
    private ImageView circleAlbumArt;
    MainTextViewSub name;
    RecyclerView recyclerView;
    private Button playAllButton, shuffleButton;

    ArrayList<Song> songAllArrayList=new ArrayList<>();
    ArrayList<Song> songMainArrayList;
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
        nestedScrollView = (NestedScrollView) rootView.findViewById(R.id.nestedScrollView);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        imageView = (ImageView) rootView.findViewById(R.id.imageView);
        fab= (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        recyclerView= (RecyclerView) rootView.findViewById(R.id.recyclerView);
        circleAlbumArt = (ImageView) rootView.findViewById(R.id.album_art);
        name = (MainTextViewSub) rootView.findViewById(R.id.type_name);
        playAllButton = (Button) rootView.findViewById(R.id.play_all_button);
        shuffleButton = (Button) rootView.findViewById(R.id.shuffle_button);

        if(Name!=null) {
            name.setText(Name);
        }

        else {
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
            if (Build.VERSION.SDK_INT >21) {
                Glide.with(getContext()).load(R.drawable.ic_music_player)
                        .crossFade()
                        .placeholder(R.drawable.ic_music_player)
                        .into(circleAlbumArt);
                Glide.with(getContext()).load("https://cdn.pixabay.com/photo/2016/09/08/21/09/piano-1655558_960_720.jpg")
                        .crossFade()
                        .placeholder(R.drawable.ic_music_player)
                        .into(imageView);
            }
            else {
                circleAlbumArt.setImageResource(R.drawable.ic_play);
                Glide.with(getContext()).load("https://cdn.pixabay.com/photo/2016/09/08/21/09/piano-1655558_960_720.jpg")
                        .crossFade()
                        .placeholder(R.drawable.ic_splash)
                        .into(imageView);
            }

        }

        if(Action.equalsIgnoreCase("Album")){
            songAllArrayList=new MyApplication(getActivity()).getWritableDatabase().readAlbumSongs(Name);
        }
        else if (Action.equalsIgnoreCase("Artists"))
            songAllArrayList=new MyApplication(getActivity()).getWritableDatabase().readArtistSongs(Name);
        else if(Action.equalsIgnoreCase("Playlists")){
            songAllArrayList=new MyApplication(getActivity()).getWritableDatabase().readSongsFromPlaylist(Name);
        }
        else
            songAllArrayList=new MyApplication(getActivity()).getWritableDatabase().readFavouriteSongs();

        int spanCount = 1; // 2 columns
        int spacing = 20; // 50px

        recyclerView.addItemDecoration(new GridItemDecoration(spanCount, spacing, true));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        if(new CheckConnectivity().isNetworkAvailable(getActivity())&&songAllArrayList.size()!=0) {
            songAllArrayList.add(1, null);
            if(songAllArrayList.size()>10)
                songAllArrayList.add(9,null);
        }
        if (songAllArrayList.size() > 20) {
            songMainArrayList = new ArrayList<>(songAllArrayList.subList(0,20));

        } else {
            songMainArrayList = songAllArrayList;
        }
        nestedFragmentAdapter = new NestedFragmentAdapter(getActivity(), songMainArrayList, recyclerView);

        nestedScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                View view = (View) nestedScrollView.getChildAt(nestedScrollView.getChildCount() - 1);
                int diff = (view.getBottom() - (nestedScrollView.getHeight() + nestedScrollView
                        .getScrollY()));

                if (diff == 0) {
                    progressBar.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (songMainArrayList.size() < songAllArrayList.size()) {
                                int x = 0, y = 0;
                                if ((songAllArrayList.size() - songMainArrayList.size()) >= 20) {
                                    x = songMainArrayList.size();
                                    y = x + 20;
                                } else {
                                    x = songMainArrayList.size();
                                    y = x + songAllArrayList.size() - songMainArrayList.size();
                                }

                                for (int i = x; i < y; i++) {
                                    songMainArrayList.add(songAllArrayList.get(i));
                                    nestedFragmentAdapter.notifyDataSetChanged();

                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    }, 1500);


                }
            }
        });
        recyclerView.setAdapter(nestedFragmentAdapter);

        recyclerView.addOnItemTouchListener(new RecycleTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if((((MainActivity) getActivity()).getSongs()!=songAllArrayList)) {
                    Toast.makeText(getContext(), getContext().getString(R.string.now_playing)+" "+name.getText().toString(), Toast.LENGTH_SHORT).show();
                    ((MainActivity) getActivity()).setSongs(songAllArrayList);
                    ((MainActivity) getActivity()).getMusicService().setSongs(songAllArrayList);
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
                if(songAllArrayList.size()!=0) {
                    if ((((MainActivity) getActivity()).getSongs() != songAllArrayList)) {
                        Toast.makeText(getContext(), getContext().getString(R.string.playing_all)+" " + name.getText().toString(), Toast.LENGTH_SHORT).show();
                        ((MainActivity) getActivity()).setSongs(songAllArrayList);
                        ((MainActivity) getActivity()).getMusicService().setSongs(songAllArrayList);
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
                if ((((MainActivity) getActivity()).getSongs() != songAllArrayList)) {
                    Toast.makeText(getContext(), getContext().getString(R.string.playing_all)+" " + name.getText().toString(), Toast.LENGTH_SHORT).show();
                    ((MainActivity) getActivity()).setSongs(songAllArrayList);
                    ((MainActivity) getActivity()).getMusicService().setSongs(songAllArrayList);
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
