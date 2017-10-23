package com.riseapps.xmusic.executor.RecycleViewAdapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.riseapps.xmusic.R;
import com.riseapps.xmusic.executor.Interfaces.ItemTouchHelperAdapter;
import com.riseapps.xmusic.executor.Interfaces.OnStartDragListener;
import com.riseapps.xmusic.executor.PlaySongExec;
import com.riseapps.xmusic.model.Pojo.Song;

import java.util.ArrayList;
import java.util.Collections;

public class QueueAdapter extends RecyclerView.Adapter implements ItemTouchHelperAdapter {

    private ArrayList<Song> songsList;
    private OnStartDragListener mDragStartListener;
    Context c;
    private boolean dragging;

    public QueueAdapter(Context context, ArrayList<Song> songs, RecyclerView recyclerView) {
        songsList = songs;
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                .getLayoutManager();
        mDragStartListener = (OnStartDragListener) context;
        c = context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.queue_row, parent, false);
        vh = new QueueViewHolder(v, c);

        return vh;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof QueueViewHolder) {
            Song song = songsList.get(position);

            String name = song.getName();

            ((QueueViewHolder) holder).name.setText(name);

            ((QueueViewHolder) holder).song = song;

            ((QueueViewHolder) holder).reorder.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        mDragStartListener.onStartDrag(holder);
                    }
                    return false;
                }
            });
        }
    }

    public void delete(int position) {
        songsList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < songsList.size() && toPosition < songsList.size()) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(songsList, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(songsList, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
        }
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
    }

    private class QueueViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name;
        private Context ctx;
        Song song;
        ImageButton delete,reorder;
        CardView cardView;

        QueueViewHolder(View v, Context context) {
            super(v);
            this.ctx = context;
            name = v.findViewById(R.id.name);
            delete = v.findViewById(R.id.delete);
            reorder=v.findViewById(R.id.reorder);
            cardView = v.findViewById(R.id.song_list_card);
            cardView.setOnClickListener(this);
            delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == delete.getId()) {
                delete(getAdapterPosition());
            } else if(v.getId()==cardView.getId()){
                new PlaySongExec(ctx, getAdapterPosition()).startPlaying();
            }
        }
    }
}

