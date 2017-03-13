package com.riseapps.xmusic.executor;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.riseapps.xmusic.R;
import com.riseapps.xmusic.model.Song;

/**
 * Created by naimish on 10/3/17.
 */

public class SongViewHolder extends RecyclerView.ViewHolder {
    ImageView iv;
    TextView name,artist,duration;

    Song song;

    SongViewHolder(View v) {
        super(v);
        iv= (ImageView) v.findViewById(R.id.album_art);
        name= (TextView) v.findViewById(R.id.name);
        artist= (TextView) v.findViewById(R.id.artist_mini);
        duration= (TextView) v.findViewById(R.id.duration);
    }

}