package com.riseapps.xmusic.executor.RecycleViewAdapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
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
import com.bumptech.glide.request.target.Target;
import com.riseapps.xmusic.R;
import com.riseapps.xmusic.component.CustomAnimation;
import com.riseapps.xmusic.component.SharedPreferenceSingelton;
import com.riseapps.xmusic.executor.Interfaces.AdapterToActivityListener;
import com.riseapps.xmusic.executor.Interfaces.MainListPlayingListener;
import com.riseapps.xmusic.executor.MyApplication;
import com.riseapps.xmusic.executor.PlaySongExec;
import com.riseapps.xmusic.model.Pojo.Song;

import java.util.ArrayList;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter {

    private List<Song> songsList;
    private Context c;
    public int count = 0;
    private RecyclerView.ViewHolder holder;
    private MainListPlayingListener mainListPlayingListener;
    private AdapterToActivityListener adapterToActivityListener;
    private SharedPreferenceSingelton sharedPreferenceSingelton;
    private int colorSelected = Color.LTGRAY;
    private int colorNormal = Color.WHITE;

    public SongAdapter(Context context, List<Song> songs, RecyclerView recyclerView) {
        songsList = songs;
        adapterToActivityListener = (AdapterToActivityListener) context;
        sharedPreferenceSingelton = new SharedPreferenceSingelton();
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        c = context;
        if (sharedPreferenceSingelton.getSavedBoolean(c, "Theme")) {
            colorSelected = Color.rgb(58, 58, 71);
            colorNormal = Color.rgb(22, 22, 25);
        }

    }

    public SongAdapter getInstance() {
        return SongAdapter.this;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_list_row, parent, false);
        return new SongViewHolder(view, c);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof SongViewHolder) {
            final Song song = songsList.get(position);
            this.holder = holder;

            String name = song.getName();
            String artist = song.getArtist();
            String imagepath = song.getImagepath();

            ((SongViewHolder) holder).name.setText(name);

            ((SongViewHolder) holder).artist.setText(artist);

            if (!imagepath.equalsIgnoreCase("no_image"))
                Glide.with(c).load(imagepath)
                        .dontAnimate()
                        .centerCrop()
                        .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .into(((SongViewHolder) holder).iv);
            else
                ((SongViewHolder) holder).iv.setImageResource(R.drawable.empty);
            if (song.getFavourite())
                ((SongViewHolder) holder).like.setImageResource(R.drawable.ic_liked_toolbar);
            else
                ((SongViewHolder) holder).like.setImageResource(R.drawable.ic_like);

            ((SongViewHolder) holder).songListCard.setCardBackgroundColor(song.isSelected() ? colorSelected : colorNormal);

            ((SongViewHolder) holder).song = song;
        }
    }

    public void setMainListPlayingListener(MainListPlayingListener mainListPlayingListener) {
        this.mainListPlayingListener = mainListPlayingListener;
    }


    @Override
    public int getItemCount() {
        return songsList.size();
    }

    class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView iv;
        TextView name, artist;
        ImageButton like;
        CardView songListCard;
        private Context ctx;
        Song song;

        SongViewHolder(View v, Context context) {
            super(v);
            this.ctx = context;
            iv = (ImageView) v.findViewById(R.id.album_art);
            name = (TextView) v.findViewById(R.id.name);
            artist = (TextView) v.findViewById(R.id.artist_mini);
            like = (ImageButton) v.findViewById(R.id.like);
            songListCard = (CardView) v.findViewById(R.id.song_list_card);
            like.setOnClickListener(this);
            songListCard.setOnClickListener(this);
            songListCard.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == like.getId()) {
                like.startAnimation(new CustomAnimation().likeAnimation(ctx));
                if (song.getFavourite()) {
                    new MyApplication(ctx).getWritableDatabase().updateFavourites(song.getID(), 0);
                    like.setImageResource(R.drawable.ic_like);
                    song.setFavourite(false);
                } else {
                    new MyApplication(ctx).getWritableDatabase().updateFavourites(song.getID(), 1);
                    like.setImageResource(R.drawable.ic_liked_toolbar);
                    song.setFavourite(true);
                }
            } else if (v.getId() == songListCard.getId()) {
                if (count > 0 && count <= songsList.size()) {
                    if (song.isSelected()) {
                        song.setSelected(false);
                        count--;
                        adapterToActivityListener.onTrackLongPress(count,song.getID(),false);
                    } else {
                        song.setSelected(true);
                        count++;
                        adapterToActivityListener.onTrackLongPress(count,song.getID(),true);
                    }
                    songListCard.setCardBackgroundColor(song.isSelected() ? colorSelected : colorNormal);
                } else {
                    mainListPlayingListener.onPlayingFromTrackList();
                    new PlaySongExec(ctx, getAdapterPosition()).startPlaying();
                    new SharedPreferenceSingelton().saveAs(ctx, "Shuffle", false);
                }
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (count == 0) {
                adapterToActivityListener.onFirstTrackLongPress();
            }

            if (song.isSelected()) {
                song.setSelected(false);
                count--;
                adapterToActivityListener.onTrackLongPress(count,song.getID(),false);
            } else {
                song.setSelected(true);
                count++;
                adapterToActivityListener.onTrackLongPress(count,song.getID(),true);
            }
            songListCard.setCardBackgroundColor(song.isSelected() ? colorSelected : colorNormal);
            return true;
        }

    }
}
