package com.riseapps.xmusic.executor.RecycleViewAdapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.riseapps.xmusic.R;
import com.riseapps.xmusic.model.Pojo.Playlist;
import com.riseapps.xmusic.view.Activity.ScrollingActivity;

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
            String count=playlist.getcount()+"songs";
            ((PlaylistViewHolder)holder).name.setText(name);
            ((PlaylistViewHolder)holder).count.setText(count);

            ((PlaylistViewHolder) holder).playlist = playlist;

        }
    }


    @Override
    public int getItemCount() {
        return playlistsList.size();
    }

}

class PlaylistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    ImageView imageView;
    TextView name,count;
    Context ctx;

    Playlist playlist;

    PlaylistViewHolder(View v, Context context) {
        super(v);
        this.ctx = context;

        imageView= (ImageView) v.findViewById(R.id.imageView);
        name= (TextView) v.findViewById(R.id.name);
        count= (TextView) v.findViewById(R.id.count);

        imageView.setOnClickListener(this);
        name.setOnClickListener(this);
        count.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.name ||view.getId() == R.id.count||view.getId() == R.id.imageView) {
            Intent intent=new Intent(ctx, ScrollingActivity.class);
            intent.setAction("Open Playlist");
            intent.putExtra("position",getAdapterPosition());
            ctx.startActivity(intent);
        }
    }
}

