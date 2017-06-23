package com.riseapps.xmusic.view.Fragment;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionInflater;
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
import com.riseapps.xmusic.widgets.MainTextView;
import com.riseapps.xmusic.widgets.MainTextViewSub;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ScrollingFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    NestedScrollView nestedScrollView;
    // views
    SharedPreferenceSingelton sharedPreferenceSingelton;
    String Name, Imagepath = null, Action;
    ImageView imageView;
    TextView name, empty;
    long id;
    RecyclerView recyclerView;
    private Button playAllButton, shuffleButton;

    ArrayList<Song> songMainArrayList;
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
            id = getArguments().getLong("ID");
            Name = getArguments().getString("Name");
            Imagepath = getArguments().getString("Imagepath");
            Action = getArguments().getString("Action");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_scrolling, container, false);
        imageView = (ImageView) rootView.findViewById(R.id.imageView);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        ImageView circleAlbumArt = (ImageView) rootView.findViewById(R.id.album_art);
        name = (MainTextViewSub) rootView.findViewById(R.id.type_name);
        empty = (MainTextView) rootView.findViewById(R.id.empty);
        playAllButton = (Button) rootView.findViewById(R.id.play_all_button);
        shuffleButton = (Button) rootView.findViewById(R.id.shuffle_button);
        songMainArrayList = new ArrayList<>();
        sharedPreferenceSingelton = new SharedPreferenceSingelton();
        if (Name != null) {
            name.setText(Name);
        } else {
            name.setText(getString(R.string.action_favourites));
        }

        if (Imagepath == null) {
            circleAlbumArt.setImageResource(R.drawable.play);
            Glide.with(getContext()).load("https://cdn.pixabay.com/photo/2016/09/08/21/09/piano-1655558_960_720.jpg")
                    .crossFade()
                    .placeholder(R.drawable.ic_splash)
                    .into(imageView);
        } else {
            Glide.with(getContext()).load(Uri.parse(Imagepath))
                    .crossFade()
                    .placeholder(R.drawable.dummy)
                    .into(imageView);

            Glide.with(getContext()).load(Uri.parse(Imagepath))
                    .dontAnimate()
                    .placeholder(ContextCompat.getDrawable(getActivity(), R.drawable.play))
                    .into(circleAlbumArt);
        }


        int spanCount = 1; // 2 columns
        int spacing = 20; // 50px

        recyclerView.addItemDecoration(new GridItemDecoration(spanCount, spacing, true));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        nestedFragmentAdapter = new NestedFragmentAdapter(getActivity(), songMainArrayList, recyclerView);


        recyclerView.setAdapter(nestedFragmentAdapter);

        recyclerView.addOnItemTouchListener(new RecycleTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                playFromThisFragment();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        playAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (songMainArrayList.size() != 0) {
                    if ((((MainActivity) getActivity()).getSongs() != songMainArrayList)) {
                        Toast.makeText(getContext(), getContext().getString(R.string.playing_all) + " " + name.getText().toString(), Toast.LENGTH_SHORT).show();
                        ((MainActivity) getActivity()).setSongs(songMainArrayList);
                        ((MainActivity) getActivity()).getMusicService().setSongs(songMainArrayList);
                    }
                    playSongExec = new PlaySongExec(getContext(), 0);
                    playSongExec.startPlaying();
                    new SharedPreferenceSingelton().saveAs(getActivity(), "Shuffle", true);
                } else
                    Snackbar.make(nestedScrollView, getString(R.string.empty_state_message), Snackbar.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (Action.equalsIgnoreCase("Favourites")) {
            //TODO FETCH FROM DB
            if (songMainArrayList.size() == 0) {
                empty.setVisibility(View.VISIBLE);
            }
        } else {
            if (sharedPreferenceSingelton.getSavedBoolean(getContext(), "Loader"))
                getActivity().getSupportLoaderManager().restartLoader(4, null, this);
            else {
                getActivity().getSupportLoaderManager().initLoader(4, null, this);
                sharedPreferenceSingelton.saveAs(getContext(), "Loader", true);
            }
        }
    }

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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = "";
        if (Action.equalsIgnoreCase("Albums")) {
            selection = MediaStore.Audio.Media.ALBUM_ID + "=" + this.id;
        } else if (Action.equalsIgnoreCase("Artists")) {
            selection = MediaStore.Audio.Media.ARTIST_ID + "=" + this.id;
        }
        return new CursorLoader(getContext(), musicUri, null, selection, null, MediaStore.Audio.Media.TITLE + " COLLATE NOCASE ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int textLimit = 26;
        if (data != null && data.moveToFirst()) {
            do {
                long id = data.getLong(data.getColumnIndex(MediaStore.Audio.Media._ID));
                long duration = data.getLong(data.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION));
                String title = data.getString(data.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String artist = data.getString(data.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String imagepath = "content://media/external/audio/media/" + id + "/albumart";
                if (title.length() > 27)
                    title = title.substring(0, textLimit) + "...";
                if (artist.length() > 35)
                    artist = artist.substring(0, 30) + "...";
                songMainArrayList.add(new Song(id, duration, title, artist, imagepath, false));
            }
            while (data.moveToNext());
            data.close();
        }
        nestedFragmentAdapter = new NestedFragmentAdapter(getContext(), songMainArrayList, recyclerView);
        recyclerView.setAdapter(nestedFragmentAdapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        songMainArrayList = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    void playFromThisFragment(){
        if (songMainArrayList.size() != 0) {
            if ((((MainActivity) getActivity()).getSongs() != songMainArrayList)) {
                Toast.makeText(getContext(), getContext().getString(R.string.now_playing) + " " + name.getText().toString(), Toast.LENGTH_SHORT).show();
                ((MainActivity) getActivity()).setSongs(songMainArrayList);
                ((MainActivity) getActivity()).getMusicService().setSongs(songMainArrayList);
            }
            playSongExec = new PlaySongExec(getContext(), 0);
            playSongExec.startPlaying();
        } else
            Snackbar.make(nestedScrollView, getString(R.string.empty_state_message), Snackbar.LENGTH_SHORT).show();
    }

}
