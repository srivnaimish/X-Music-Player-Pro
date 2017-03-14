package com.riseapps.xmusic.executor;

import android.content.Context;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.riseapps.xmusic.R;
import com.riseapps.xmusic.model.Song;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SongAdapter extends RecyclerView.Adapter {

    private List<Song> songsList;

    Context c;

    public SongAdapter(Context context, List<Song> songs, RecyclerView recyclerView) {
        songsList = songs;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();
            c = context;


        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.song_list_row, parent, false);
        return new SongViewHolder(v);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof SongViewHolder) {

            Song song = (Song) songsList.get(position);

            String name = song.getName();
            String artist = song.getArtist();
            long time = song.getDuration();
            String imagepath = song.getImagepath();

            ((SongViewHolder) holder).name.setText(name);

            ((SongViewHolder) holder).artist.setText(artist);

            ((SongViewHolder) holder).duration.setText(String.format(Locale.getDefault(), "%d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(time),
                    TimeUnit.MILLISECONDS.toSeconds(time) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time))));
            if (!imagepath.equalsIgnoreCase("no_image")) {
                ((SongViewHolder) holder).iv.setImageURI(Uri.parse(imagepath));
            }
            else {
                ((SongViewHolder) holder).iv.setImageResource(R.drawable.empty);
            }


            if(song.getFavourite()){
                ((SongViewHolder) holder).like.setImageResource(R.drawable.ic_liked);
            }
            else {
                ((SongViewHolder) holder).like.setImageResource(R.drawable.ic_like);
            }

            ((SongViewHolder) holder).song = song;

        }
    }


    @Override
    public int getItemCount() {
        return songsList.size();
    }


    //


}