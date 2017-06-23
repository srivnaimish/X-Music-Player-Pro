package com.riseapps.xmusic.executor.RecycleViewAdapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.riseapps.xmusic.R;
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

        View v = inflater.inflate(R.layout.grid_row, parent, false);
        vh = new AlbumViewHolder(v, c);


        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        Album album = albumList.get(position);

        String name = album.getName().trim();
        String imagepath = album.getImagepath();

        if(Build.VERSION.SDK_INT>=21){
            ((AlbumViewHolder) holder).imageView.setTransitionName(name);
        }
            Glide.with(c).load(imagepath)
                    .dontAnimate()
                    .centerCrop()
                    .placeholder(R.drawable.dummy)
                    .error(R.drawable.dummy)
                    .into(((AlbumViewHolder) holder).imageView);


        ((AlbumViewHolder) holder).name.setText(name);
        ((AlbumViewHolder) holder).album = album;
    }


    @Override
    public int getItemCount() {
        return albumList.size();
    }

    class AlbumViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView name;
        Context ctx;
        Album album;

        AlbumViewHolder(final View view, Context context) {
            super(view);
            this.ctx = context;
            imageView = (ImageView) view.findViewById(R.id.imageView);
            name = (TextView) view.findViewById(R.id.name);
        }
    }
}





