package com.riseapps.xmusic.executor.RecycleViewAdapters;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
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
import com.riseapps.xmusic.widgets.MainTextViewSub;

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
        RecyclerView.ViewHolder vh = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View v = inflater.inflate(R.layout.artist_name_row, parent, false);
        vh = new ArtistViewHolder(v, c);

        return vh;

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ArtistViewHolder) {
            Artist artist = artistList.get(position);
            String name = artist.getName().trim();
            String imagepath = artist.getImagepath();
            if(Build.VERSION.SDK_INT>=21){
                ((ArtistViewHolder) holder).imageView.setTransitionName(name);
            }
            if (!imagepath.equalsIgnoreCase("NoImage") && !name.equals("Ad")) {
                ((ArtistViewHolder) holder).name.setText(name);

                ((ArtistViewHolder) holder).artist = artist;
            } else {
                ((ArtistViewHolder) holder).name.setText(name);
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
    MainTextViewSub artistTextView;

    Artist artist;

    ArtistViewHolder(View v, Context context) {
        super(v);
        this.ctx = context;

        name = (TextView) v.findViewById(R.id.name);
        imageView= (ImageView) v.findViewById(R.id.artist_art_card);
    }

}



