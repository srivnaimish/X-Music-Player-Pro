package com.riseapps.xmusic.executor.RecycleViewAdapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.riseapps.xmusic.R;
import com.riseapps.xmusic.executor.Interfaces.ScrollingClick;
import com.riseapps.xmusic.model.Pojo.Song;

import java.util.ArrayList;

public class NestedFragmentAdapter extends RecyclerView.Adapter {

    private ArrayList<Song> songsList;
    Context c;
    String Action;
    ScrollingClick scrollingClick;


    public NestedFragmentAdapter(Context context, ArrayList<Song> songs, RecyclerView recyclerView,String Action) {
        songsList = songs;
        this.Action=Action;
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                .getLayoutManager();
        c = context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.song_name_rows, parent, false);
        vh = new NestedSongViewHolder(v, c, Action);

        return vh;
    }

    public void setScrollingClick(ScrollingClick scrollingClick) {
        this.scrollingClick = scrollingClick;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof NestedSongViewHolder) {
            Song song = (Song) songsList.get(position);

            String name = song.getName();
            String artist = song.getArtist();

            ((NestedSongViewHolder) holder).name.setText(name);

            ((NestedSongViewHolder) holder).artist.setText(artist);

            ((NestedSongViewHolder) holder).song = song;

        }
    }

    public void delete(int position) {
        songsList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }

    private class NestedSongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView name, artist;
        private Context ctx;
        Song song;
        String Action;
        ImageButton delete;
        CardView cardView;

        NestedSongViewHolder(View v, Context context,String Action) {
            super(v);
            this.ctx = context;
            this.Action=Action;
            name = (TextView) v.findViewById(R.id.name);
            artist = (TextView) v.findViewById(R.id.artist);
            delete= (ImageButton) v.findViewById(R.id.delete_playlist);
            if(Action.equalsIgnoreCase("Playlists")){
                delete.setVisibility(View.VISIBLE);
            }
            cardView= (CardView) v.findViewById(R.id.song_list_card);
            cardView.setOnClickListener(this);
            delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId()==delete.getId()){
                scrollingClick.onDeleteClick(getAdapterPosition());
            }
            else {
                scrollingClick.onClick(getAdapterPosition());
            }
        }
    }
}

