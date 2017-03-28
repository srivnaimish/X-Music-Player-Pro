package com.riseapps.xmusic.executor.RecycleViewAdapters;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.riseapps.xmusic.R;
import com.riseapps.xmusic.component.CustomAnimation;
import com.riseapps.xmusic.executor.MyApplication;
import com.riseapps.xmusic.executor.PlaySongExec;
import com.riseapps.xmusic.executor.Interfaces.SongLikedListener;
import com.riseapps.xmusic.model.Pojo.Song;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SongAdapter extends RecyclerView.Adapter {

    private List<Song> songsList;
    Context c;
    private SparseBooleanArray mSelectedItemsIds;
    private int position;
    private RecyclerView.ViewHolder holder;

    public SongAdapter(Context context, List<Song> songs, RecyclerView recyclerView) {
        songsList = songs;
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();
            c = context;

        }
        mSelectedItemsIds = new SparseBooleanArray();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.song_list_row, parent, false);
        return new SongViewHolder(v, c);

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof SongViewHolder) {
            final Song song = songsList.get(position);
            this.position = holder.getAdapterPosition();
            this.holder = holder;

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
                Glide.with(c).load(Uri.parse(imagepath))
                        .crossFade()
                        .into(((SongViewHolder) holder).iv);
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

            ((SongViewHolder) holder).like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Toast.makeText(getContext(), "Added to Favourites", Toast.LENGTH_SHORT).show();
                    ((SongViewHolder) holder).like.startAnimation(new CustomAnimation().likeAnimation(c));
                    if(song.getFavourite()){
                        new MyApplication(c).getWritableDatabase().updateFavourites(song.getID(),0);
                        ((SongViewHolder) holder).like.setImageResource(R.drawable.ic_like);
                        song.setFavourite(false);
                    }
                    else {
                        new MyApplication(c).getWritableDatabase().updateFavourites(song.getID(),1);
                        ((SongViewHolder) holder).like.setImageResource(R.drawable.ic_liked);
                        song.setFavourite(true);
                    }
                }
            });

            ((SongViewHolder) holder).itemView
                    .setBackgroundColor(mSelectedItemsIds.get(position) ? 0x9934B5E4
                            : Color.TRANSPARENT);

            ((SongViewHolder) holder).song = song;

        }
    }

    /***
     * Methods required for do selections, remove selections, etc.
     */

    //Toggle selection methods
    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }


    //Remove selected selections
    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    //Put or delete selected position into SparseBooleanArray
    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);

        notifyDataSetChanged();

    }

    //Get total selected count
    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }

    SongLikedListener l = new SongLikedListener() {
        @Override
        public void onSongLiked(boolean status) {
            Toast.makeText(c, " "  + status, Toast.LENGTH_SHORT).show();
        }
    };

    public void onSongLiked(boolean status) {
        final Song song = songsList.get(position);
        if (status) {
            Toast.makeText(c, "hello 1", Toast.LENGTH_SHORT).show();
            if(song.getFavourite()){
                //new MyApplication(c).getWritableDatabase().updateFavourites(song.getID(),0);
                ((SongViewHolder) holder).like.setImageResource(R.drawable.ic_like);
                //song.setFavourite(false);
            }
            else {
                //new MyApplication(c).getWritableDatabase().updateFavourites(song.getID(),1);
                ((SongViewHolder) holder).like.setImageResource(R.drawable.ic_liked);
                //song.setFavourite(true);
            }
        }
        else
            Toast.makeText(c, "hello 2", Toast.LENGTH_SHORT).show();
    }
}

class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    ImageView iv;
    TextView name,artist,duration;
    ImageButton like;
    private PlaySongExec playSongExec;
    private Context ctx;
    Song song;

    SongViewHolder(View v, Context context) {
        super(v);
        this.ctx = context;
        iv= (ImageView) v.findViewById(R.id.album_art);
        name= (TextView) v.findViewById(R.id.name);
        artist= (TextView) v.findViewById(R.id.artist_mini);
        duration= (TextView) v.findViewById(R.id.duration);
        like= (ImageButton) v.findViewById(R.id.like);
        name.setOnClickListener(this);
        iv.setOnClickListener(this);
        artist.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.name || view.getId() == R.id.album_art|| view.getId() == R.id.artist) {
            playSongExec = new PlaySongExec(ctx, getAdapterPosition());
            playSongExec.startPlaying();
        }
    }
}
