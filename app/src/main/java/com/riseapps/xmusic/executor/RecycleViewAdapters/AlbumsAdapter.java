package com.riseapps.xmusic.executor.RecycleViewAdapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.riseapps.xmusic.R;
import com.riseapps.xmusic.executor.GridAdViewHolder;
import com.riseapps.xmusic.model.Pojo.Album;

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
        RecyclerView.ViewHolder vh = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 1:
                View v = inflater.inflate(R.layout.grid_row, parent, false);
                vh = new AlbumViewHolder(v, c);
                break;
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof AlbumViewHolder) {
            Album album = albumList.get(position);
            Log.d("viewtype", "" + holder.getItemViewType());
            switch (holder.getItemViewType()) {
                case 1:
                    String name=album.getName();
                    String imagepath=album.getImagepath();
                    Log.d("imagepath", " " + imagepath);
                    if (!imagepath.equalsIgnoreCase("NoImage") && !name.equals("Ad")) {
                        if (!imagepath.equalsIgnoreCase("no_image")) {
                            Glide.with(c).load(Uri.parse(imagepath))
                                    .centerCrop()
                                    .into(((AlbumViewHolder) holder).imageView);
                        }
                        else {
                            Glide.with(c).load("")
                                    .placeholder(R.drawable.empty)
                                    .into(((AlbumViewHolder) holder).imageView);
                        }
                        ((AlbumViewHolder)holder).name.setText(name);
                        ((AlbumViewHolder) holder).album = album;
                    }
                    else {
                        ((AlbumViewHolder)holder).name.setText(name);
                        Glide.with(c).load("")
                                .placeholder(R.drawable.empty)
                                .into(((AlbumViewHolder) holder).imageView);
                    }
                    break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

}

class AlbumViewHolder extends RecyclerView.ViewHolder{

    ImageView imageView;
    TextView name;
    Context ctx;
    Album album;

    AlbumViewHolder(View v, Context context) {
        super(v);
        this.ctx = context;
        imageView= (ImageView) v.findViewById(R.id.imageView);
        name= (TextView) v.findViewById(R.id.name);
    }
}



