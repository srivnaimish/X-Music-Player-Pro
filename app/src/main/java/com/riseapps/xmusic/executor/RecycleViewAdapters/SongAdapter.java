package com.riseapps.xmusic.executor.RecycleViewAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.riseapps.xmusic.component.TagToken.customviews.CountSpan;
import com.riseapps.xmusic.executor.MyApplication;
import com.riseapps.xmusic.executor.PlaySongExec;
import com.riseapps.xmusic.executor.Interfaces.SongLikedListener;
import com.riseapps.xmusic.model.Pojo.LongSelectedSong;
import com.riseapps.xmusic.model.Pojo.Song;
import com.riseapps.xmusic.utils.DipToPx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SongAdapter extends RecyclerView.Adapter {

    private List<Song> songsList;
    private Context c, mainActivityContext;
    private SparseBooleanArray mSelectedItemsIds;
    private int position;
    private RecyclerView.ViewHolder holder;
    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, Boolean> longPressedSongs = new HashMap<>();
    private ArrayList<LongSelectedSong> longSelectedSongs = new ArrayList<>();
    private OnShowContextMenuListener mListener;
    private static boolean startLongPress;
    private int count = 0;
    private PlaySongExec playSongExec;

    public SongAdapter(Context context) {
        mainActivityContext = context;
    }

    public SongAdapter(Context context, List<Song> songs, RecyclerView recyclerView) {
        songsList = songs;
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();
            c = context;

        }
        mSelectedItemsIds = new SparseBooleanArray();
    }

    public SongAdapter getInstance() {
        return SongAdapter.this;
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

            ((SongViewHolder) holder).setLongPressListener(new SongViewHolder.OnLongPressListener() {
                @Override
                public void onLongPressed(int i) {
                    startLongPress = true;
                    count++;
                    if (!longPressedSongs.isEmpty()) {
                        if (longPressedSongs.get(i) != null) {
                            if (!longPressedSongs.get(i)) {
                                longSelectedSongs.add(new LongSelectedSong(((SongViewHolder) holder).songListCard, true));
                                longPressedSongs.put(i, true);
                                mListener.onShowFirst(longPressedSongs.size(), longPressedSongs);
                                ((SongViewHolder) holder).songListCard.setBackgroundColor(c.getResources().getColor(R.color.colorLongSelection));
                            } else {
                                longPressedSongs.put(i, false);
                                ((SongViewHolder) holder).songListCard.setBackgroundColor(c.getResources().getColor(R.color.colorWhite));
                                mListener.onShowFirst(longPressedSongs.size(), longPressedSongs);
                            }
                        } else {
                            longSelectedSongs.add(new LongSelectedSong(((SongViewHolder) holder).songListCard, true));
                            longPressedSongs.put(i, true);
                            ((SongViewHolder) holder).songListCard.setBackgroundColor(c.getResources().getColor(R.color.colorLongSelection));
                            mListener.onShowFirst(longPressedSongs.size(), longPressedSongs);
                        }
                    } else {
                        longSelectedSongs.add(new LongSelectedSong(((SongViewHolder) holder).songListCard, true));
                        longPressedSongs.put(i, true);
                        ((SongViewHolder) holder).songListCard.setBackgroundColor(c.getResources().getColor(R.color.colorLongSelection));
                        mListener.onShowFirst(longPressedSongs.size(), longPressedSongs);
                    }
                }

                @Override
                public void onPressed(int i) {
                    if (startLongPress) {
                        if (longPressedSongs.get(i) != null) {
                            if (longPressedSongs.get(i) != null && !longPressedSongs.get(i)) {
                                count++;
                                longSelectedSongs.add(new LongSelectedSong(((SongViewHolder) holder).songListCard, true));
                                longPressedSongs.put(i, true);
                                mListener.onShow(count, longPressedSongs);
                                ((SongViewHolder) holder).songListCard.setBackgroundColor(c.getResources().getColor(R.color.colorLongSelection));
                            } else {
                                longPressedSongs.remove(i);
                                count--;
                                mListener.onShow(count, longPressedSongs);
                                ((SongViewHolder) holder).songListCard.setBackgroundColor(c.getResources().getColor(R.color.colorWhite));
                            }
                        } else {
                            count++;
                            longSelectedSongs.add(new LongSelectedSong(((SongViewHolder) holder).songListCard, true));
                            longPressedSongs.put(i, true);
                            mListener.onShow(count, longPressedSongs);
                            ((SongViewHolder) holder).songListCard.setBackgroundColor(c.getResources().getColor(R.color.colorLongSelection));
                        }
                    } else {
                        playSongExec = new PlaySongExec(c, holder.getAdapterPosition());
                        playSongExec.startPlaying();
                    }
                }

                @Override
                public void onClearSelection(int i) {
                    longPressedSongs.remove(i);
                    count--;
                    mListener.onShow(count, longPressedSongs);
                    ((SongViewHolder) holder).songListCard.setBackgroundColor(c.getResources().getColor(R.color.colorWhite));
                }

                @Override
                public void onFinalClearSelection(int i) {
                    mListener.onHide();
                    startLongPress = false;
                    count = 0;
                }
            });

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
                Glide.with(c).load(imagepath)
                        .crossFade()
                        .centerCrop()
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

    public void removeAllSelection() {
        Log.d("Hello" , "Helo" );
        notifyDataSetChanged();
        longPressedSongs = new HashMap<>();
        longSelectedSongs.clear();
        startLongPress = false;
        count = 0;
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


    public interface OnShowContextMenuListener {
        void onShowFirst(int count, HashMap<Integer, Boolean> list);
        void onShow(int count, HashMap<Integer, Boolean> list);
        void onHide();
    }

    public void setContextMenuListener(OnShowContextMenuListener listener) {
        mListener = listener;
    }

    static class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView iv;
        TextView name,artist,duration;
        ImageButton like;
        CardView songListCard;
        private PlaySongExec playSongExec;
        private Context ctx;
        Song song;
        private static boolean longSelectionStart = false;
        @SuppressLint("UseSparseArrays")
        private static HashMap<Integer, Boolean> selectionList = new HashMap<>();

        private OnLongPressListener mListener;

        SongAdapter adapter = new SongAdapter(ctx);

        SongViewHolder(View v, Context context) {
            super(v);
            this.ctx = context;
            iv= (ImageView) v.findViewById(R.id.album_art);
            name= (TextView) v.findViewById(R.id.name);
            artist= (TextView) v.findViewById(R.id.artist_mini);
            duration= (TextView) v.findViewById(R.id.duration);
            like= (ImageButton) v.findViewById(R.id.like);
            songListCard = (CardView) v.findViewById(R.id.song_list_card);

            // set listeners
            name.setOnClickListener(this);
            iv.setOnClickListener(this);
            artist.setOnClickListener(this);
            songListCard.setOnClickListener(this);

            // set longClick listeners
            songListCard.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onPressed(getLayoutPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            mListener.onLongPressed(getLayoutPosition());
            return true;
        }

        interface OnLongPressListener {
            void onLongPressed(int i);
            void onPressed(int i);
            void onClearSelection(int i);
            void onFinalClearSelection(int i);
        }

        void setLongPressListener(OnLongPressListener listener) {
            mListener = listener;
        }
    }
}
