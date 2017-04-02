package com.riseapps.xmusic.executor.RecycleViewAdapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.riseapps.xmusic.R;
import com.riseapps.xmusic.executor.MyApplication;
import com.riseapps.xmusic.model.Pojo.Playlist;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter {

    private List<Playlist> playlistsList;
    Context c;

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
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.grid_row, parent, false);
        return new PlaylistViewHolder(v,c);

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof PlaylistViewHolder) {
            final Playlist playlist = (Playlist) playlistsList.get(position);
            String name=playlist.getName();
            ((PlaylistViewHolder)holder).name.setText(name);

            String imagepath=new MyApplication(c).getWritableDatabase().readFirstSongInPlaylist(name);
            if (!imagepath.equalsIgnoreCase("no_image")) {
                Glide.with(c).load(imagepath)
                        .crossFade()
                        .centerCrop()
                       // .placeholder(R.drawable.empty)
                        .into(((PlaylistViewHolder) holder).imageView);
            }
            else {
                Glide.with(c).load("")
                        .crossFade()
                        //.placeholder(R.drawable.empty)
                        .into(((PlaylistViewHolder) holder).imageView);
            }
            ((PlaylistViewHolder) holder).playlist = playlist;

        }
    }


    @Override
    public int getItemCount() {
        return playlistsList.size();
    }

}

class PlaylistViewHolder extends RecyclerView.ViewHolder{

    ImageView imageView;
    TextView name;
    Context ctx;

    Playlist playlist;

    PlaylistViewHolder(View v, Context context) {
        super(v);
        this.ctx = context;

        imageView= (ImageView) v.findViewById(R.id.imageView);
        name= (TextView) v.findViewById(R.id.name);

    }
}

