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
import com.riseapps.xmusic.model.Pojo.Artist;

import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter {

    private List<Artist> artistList;
    Context c;

    public ArtistAdapter(Context context, List<Artist> artistList, RecyclerView recyclerView) {
        this.artistList = artistList;

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
        return new ArtistViewHolder(v,c);

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof ArtistViewHolder) {
            Artist artist = (Artist) artistList.get(position);
            String name=artist.getName();
            String imagepath=artist.getImagepath();
            if (!imagepath.equalsIgnoreCase("NoImage") && !name.equals("Ad")) {
                ((ArtistViewHolder)holder).name.setText(name);

                if (!imagepath.equalsIgnoreCase("no_image")) {
                    Glide.with(c).load(imagepath)
                            .centerCrop()
                            .into(((ArtistViewHolder) holder).imageView);
                }
                else {
                    Glide.with(c).load("")
                            .placeholder(R.drawable.empty)
                            .into(((ArtistViewHolder) holder).imageView);
                    //((ArtistViewHolder) holder).imageView.setImageResource(R.drawable.empty);
                }
                ((ArtistViewHolder) holder).artist = artist;
            }
            else {
                ((ArtistViewHolder)holder).name.setText(name);
                Glide.with(c).load("")
                        .placeholder(R.drawable.empty)
                        .into(((ArtistViewHolder) holder).imageView);
            }
        }
    }


    @Override
    public int getItemCount() {
        return artistList.size();
    }

}
class ArtistViewHolder extends RecyclerView.ViewHolder {

    ImageView imageView;
    TextView name;
    Context ctx;

    Artist artist;

    ArtistViewHolder(View v, Context context) {
        super(v);
        this.ctx = context;

        imageView= (ImageView) v.findViewById(R.id.imageView);
        name= (TextView) v.findViewById(R.id.name);

    }

}



