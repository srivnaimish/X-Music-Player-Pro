package com.riseapps.xmusic.executor.RecycleViewAdapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.riseapps.xmusic.R;
import com.riseapps.xmusic.model.Pojo.Song;

import java.util.ArrayList;

public class NestedFragmentAdapter extends RecyclerView.Adapter {

    private ArrayList<Song> songsList;
    Context c;


    public NestedFragmentAdapter(Context context, ArrayList<Song> songs, RecyclerView recyclerView) {
        songsList = songs;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();
            c = context;


        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.song_name_rows, parent, false);
        vh = new NestedSongViewHolder(v, c);

        return vh;


    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof NestedSongViewHolder) {
            Song song = (Song) songsList.get(position);

            String name = song.getName();
            String artist = song.getArtist();

            ((NestedSongViewHolder) holder).name.setText(name);

            ((NestedSongViewHolder) holder).artist.setText(artist);

            ((NestedSongViewHolder) holder).song = song;

        }
    }


    @Override
    public int getItemCount() {
        return songsList.size();
    }

    class NestedSongViewHolder extends RecyclerView.ViewHolder {

        TextView name, artist;
        private Context ctx;
        Song song;

        NestedSongViewHolder(View v, Context context) {
            super(v);
            this.ctx = context;

            name = (TextView) v.findViewById(R.id.name);
            artist = (TextView) v.findViewById(R.id.artist);
        }

    }
}

