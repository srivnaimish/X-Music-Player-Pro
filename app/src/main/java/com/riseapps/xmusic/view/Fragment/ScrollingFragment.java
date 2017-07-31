package com.riseapps.xmusic.view.Fragment;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
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
import com.riseapps.xmusic.executor.Interfaces.ScrollingClick;
import com.riseapps.xmusic.executor.MyApplication;
import com.riseapps.xmusic.executor.PlaySongExec;
import com.riseapps.xmusic.executor.RecycleViewAdapters.NestedFragmentAdapter;
import com.riseapps.xmusic.model.Pojo.Song;
import com.riseapps.xmusic.view.Activity.MainActivity;
import com.riseapps.xmusic.widgets.MainTextView;
import com.riseapps.xmusic.widgets.MainTextViewSub;

import java.util.ArrayList;

public class ScrollingFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int MUSIC_LOADER_ID = 4;

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

    String multipleIDs = "";


    private OnFragmentInteractionListener mListener;
    private String filter=" AND (";

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
        imageView = (ImageView) rootView.findViewById(R.id.imageView1);
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
                    .placeholder(R.drawable.dummy)
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
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        playAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playFromThisFragment(0, false);
            }
        });

        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playFromThisFragment(0, true);
            }
        });
        if (Action.equalsIgnoreCase("Favourites")) {
            ArrayList<Long> IDs = new MyApplication(getActivity()).getWritableDatabase().readFavourites();
            initiallizeMultipleIDs(IDs);
        } else if (Action.equalsIgnoreCase("Playlists")) {
            ArrayList<Long> IDs = new MyApplication(getActivity()).getWritableDatabase().readSongsFromPlaylist(Name);
            initiallizeMultipleIDs(IDs);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (Action.equalsIgnoreCase("Favourites") || Action.equalsIgnoreCase("Playlists")) {
            if (!multipleIDs.equalsIgnoreCase("")) {
                startLoader();
            } else {
                empty.setVisibility(View.VISIBLE);
            }
        } else if (Action.equalsIgnoreCase("Recent_Playlists")) {
            String recentJSON = sharedPreferenceSingelton.getSavedString(getContext(), "Recent");
            if (recentJSON != null) {
                ArrayList<Long> ids = new Gson().fromJson(recentJSON, new TypeToken<ArrayList<Long>>() {
                }.getType());
                initiallizeMultipleIDs(ids);
                startLoader();
            } else {
                empty.setVisibility(View.VISIBLE);
            }
        } else {
            startLoader();
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
        } else if (Action.equalsIgnoreCase("Favourites") || Action.equalsIgnoreCase("Playlists") ||
                Action.equalsIgnoreCase("Recent_Playlists")) {
            selection = multipleIDs;
        }

        initiallizeMultipleNames();
        if(filter!=null){
            selection+=filter;
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
        nestedFragmentAdapter = new NestedFragmentAdapter(getContext(), songMainArrayList, recyclerView, Action);
        recyclerView.setAdapter(nestedFragmentAdapter);
        nestedFragmentAdapter.setScrollingClick(new ScrollingClick() {
            @Override
            public void onClick(int position) {
                playFromThisFragment(position, false);
            }

            @Override
            public void onDeleteClick(int position) {
                new MyApplication(getContext()).getWritableDatabase().deleteSongFromPlaylist(Name,songMainArrayList.get(position).getID());
                nestedFragmentAdapter.delete(position);
            }
        });
        getLoaderManager().destroyLoader(MUSIC_LOADER_ID);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        songMainArrayList = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    void playFromThisFragment(int position, boolean shuffle) {
        if (songMainArrayList.size() != 0) {
            if ((((MainActivity) getActivity()).getSongs() != songMainArrayList)) {
                Toast.makeText(getContext(), getContext().getString(R.string.now_playing) + " " + name.getText().toString(), Toast.LENGTH_SHORT).show();
                ((MainActivity) getActivity()).setSongs(songMainArrayList);
                ((MainActivity) getActivity()).getMusicService().setSongs(songMainArrayList);
            }
            playSongExec = new PlaySongExec(getContext(), position);
            playSongExec.startPlaying();
            new SharedPreferenceSingelton().saveAs(getActivity(), "Shuffle", shuffle);
        } else
            Snackbar.make(imageView, getString(R.string.empty_state_message), Snackbar.LENGTH_SHORT).show();
    }

    void startLoader() {
        if (sharedPreferenceSingelton.getSavedBoolean(getContext(), "Loader"))
            getActivity().getSupportLoaderManager().restartLoader(MUSIC_LOADER_ID, null, this);
        else {
            getActivity().getSupportLoaderManager().initLoader(MUSIC_LOADER_ID, null, this);
            sharedPreferenceSingelton.saveAs(getContext(), "Loader", true);
        }
    }

    void initiallizeMultipleIDs(ArrayList<Long> IDs) {
        for (int i = 0; i < IDs.size(); i++) {
            if (i < IDs.size() - 1)
                multipleIDs += MediaStore.Audio.Media._ID + "=" + IDs.get(i) + " OR ";
            else
                multipleIDs += MediaStore.Audio.Media._ID + "=" + IDs.get(i);
        }
    }

    void initiallizeMultipleNames() {
        String names=sharedPreferenceSingelton.getSavedString(getContext(),"SkipFolders");
        if(names!=null) {
            String[] folders = names.split(",");
            for (int i = 0; i < folders.length; i++) {
                if (i < folders.length - 1)
                    filter += MediaStore.Audio.Media.DATA + " NOT LIKE '%/" + folders[i] + "/%'" + " AND ";
                else
                    filter += MediaStore.Audio.Media.DATA + " NOT LIKE '%/" + folders[i] + "/%')";
            }
        }
        else {
            filter=null;
        }
    }

}
