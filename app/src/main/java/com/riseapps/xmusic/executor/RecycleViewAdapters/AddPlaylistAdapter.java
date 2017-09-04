package com.riseapps.xmusic.executor.RecycleViewAdapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.riseapps.xmusic.R;
import com.riseapps.xmusic.model.Pojo.PlaylistSelect;

import java.util.ArrayList;
import java.util.List;

public class AddPlaylistAdapter extends RecyclerView.Adapter {

    private List<PlaylistSelect> playlists;
    private Context c;
    public int count = 0;

    public AddPlaylistAdapter(Context context, ArrayList<PlaylistSelect> playlists, RecyclerView recyclerView) {
        this.playlists = playlists;
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        c = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_to_playlist_row, parent, false);
        return new PlaylistViewHolder(view, c);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        PlaylistSelect playlist = playlists.get(position);
        String name = playlist.getName();
        ((PlaylistViewHolder) holder).name.setText(name);
        ((PlaylistViewHolder) holder).playlist = playlist;
    }

    public void delete(int position) {
        playlists.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    private class PlaylistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name;
        CheckBox add;
        CardView playlistCard;
        private Context ctx;
        PlaylistSelect playlist;

        PlaylistViewHolder(View v, Context context) {
            super(v);
            this.ctx = context;
            name = (TextView) v.findViewById(R.id.name);
            add = (CheckBox) v.findViewById(R.id.add);
            playlistCard = (CardView) v.findViewById(R.id.playlist_list_card);
            add.setOnClickListener(this);
            playlistCard.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == playlistCard.getId()) {
                if (add.isChecked()) {
                    add.setChecked(false);
                    playlist.setSelected(false);
                } else {
                    add.setChecked(true);
                    playlist.setSelected(true);
                }

            }
        }

    }
}
