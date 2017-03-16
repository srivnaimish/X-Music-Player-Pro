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
import com.riseapps.xmusic.model.Pojo.Album;
import com.riseapps.xmusic.view.Activity.ScrollingActivity;

import java.util.List;

public class AlbumsAdapter extends RecyclerView.Adapter {

    private List<Album> albumList;
    Context c;

    public AlbumsAdapter(Context context, List<Album> albumList, RecyclerView recyclerView) {
        this.albumList = albumList;

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
        return new AlbumViewHolder(v,c);

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof AlbumViewHolder) {
            final Album album = (Album) albumList.get(position);
            String name=album.getName();
            String count=album.getcount()+"songs";
            ((AlbumViewHolder)holder).name.setText(name);
            ((AlbumViewHolder)holder).count.setText(count);
            ((AlbumViewHolder) holder).album = album;

        }
    }


    @Override
    public int getItemCount() {
        return albumList.size();
    }

}

class AlbumViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    ImageView imageView;
    ImageButton imageButton;
    TextView name,count;
    Context ctx;
    Album album;

    AlbumViewHolder(View v, Context context) {
        super(v);
        this.ctx = context;

        imageView= (ImageView) v.findViewById(R.id.imageView);
        imageButton= (ImageButton) v.findViewById(R.id.play);
        name= (TextView) v.findViewById(R.id.name);
        count= (TextView) v.findViewById(R.id.count);

        imageView.setOnClickListener(this);
        imageButton.setOnClickListener(this);
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



