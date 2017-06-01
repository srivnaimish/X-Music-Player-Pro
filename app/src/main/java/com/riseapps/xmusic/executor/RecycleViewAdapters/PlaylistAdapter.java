package com.riseapps.xmusic.executor.RecycleViewAdapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.riseapps.xmusic.R;
import com.riseapps.xmusic.executor.MyApplication;
import com.riseapps.xmusic.model.Pojo.Playlist;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter {

    private List<Playlist> playlistsList;
    Context c;
    View v;

    public PlaylistAdapter(Context context, List<Playlist> playlists, RecyclerView recyclerView) {
        this.playlistsList = playlists;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();
            c = context;


        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.playlist_name_row, parent, false);
        return new PlaylistViewHolder(v,c);

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof PlaylistViewHolder) {
            Playlist playlist = (Playlist) playlistsList.get(position);
            String name=playlist.getName().trim();
            ((PlaylistViewHolder)holder).name.setText(name);

            ((PlaylistViewHolder) holder).playlist = playlist;

        }
    }

    public void delete(int position) {
        playlistsList.remove(position);
        notifyItemRemoved(position);
    }
    @Override
    public int getItemCount() {
        return playlistsList.size();
    }

    private class PlaylistViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView name;
        ImageButton delete;
        Context ctx;

        Playlist playlist;

        PlaylistViewHolder(View v, Context context) {
            super(v);
            this.ctx = context;
            imageView= (ImageView) v.findViewById(R.id.imageView);
            name= (TextView) v.findViewById(R.id.name);
            delete= (ImageButton) v.findViewById(R.id.delete);
        }

    }
}



