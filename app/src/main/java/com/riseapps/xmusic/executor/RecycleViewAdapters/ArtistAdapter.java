package com.riseapps.xmusic.executor.RecycleViewAdapters;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.riseapps.xmusic.R;
import com.riseapps.xmusic.executor.Interfaces.FragmentTransitionListener;
import com.riseapps.xmusic.model.Pojo.Artist;
import com.riseapps.xmusic.view.activity.MainActivity;
import com.riseapps.xmusic.view.Fragment.ScrollingFragment;

public class ArtistAdapter extends RecyclerView.Adapter {

    private Cursor dataCursor;
    private ScrollingFragment scrollingFragment;
    private FragmentTransitionListener fragmentTransitionListener;
    Context c;

    public ArtistAdapter(Context context, RecyclerView recyclerView, Cursor cursor) {

        dataCursor = cursor;
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                .getLayoutManager();
        c = context;
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
        dataCursor.moveToPosition(position);
        String artist = dataCursor.getString(dataCursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST));
        long id = dataCursor.getLong(dataCursor.getColumnIndex(MediaStore.Audio.Artists._ID));

        ((ArtistViewHolder) holder).name.setText(artist);
        ((ArtistViewHolder) holder).artist = new Artist(artist, id);

    }

    public void setFragmentTransitionListener(FragmentTransitionListener fragmentTransitionListener) {
        this.fragmentTransitionListener = fragmentTransitionListener;
    }

    public Cursor swapCursor(Cursor cursor) {
        if (dataCursor == cursor) {
            return null;
        }
        Cursor oldCursor = dataCursor;
        this.dataCursor = cursor;
        if (cursor != null) {
            this.notifyDataSetChanged();
        }
        return oldCursor;
    }


    @Override
    public int getItemCount() {
        return (dataCursor == null) ? 0 : dataCursor.getCount();
    }

    class ArtistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        TextView name;
        Context ctx;
        CardView cardView;

        Artist artist;

        ArtistViewHolder(View v, Context context) {
            super(v);
            this.ctx = context;

            name = (TextView) v.findViewById(R.id.name);
            imageView = (ImageView) v.findViewById(R.id.artist_art_card);
            cardView = (CardView) v.findViewById(R.id.artist_list_card);
            cardView.setOnClickListener(this);
        }

        private void fragmentJump(Artist artist) {
            scrollingFragment = new ScrollingFragment();
            Bundle bundle = new Bundle();
            bundle.putLong("ID", artist.getId());
            bundle.putString("Name", artist.getName());
            bundle.putString("Action", "Artists");
            scrollingFragment.setArguments(bundle);
            fragmentTransitionListener.onFragmentTransition(scrollingFragment);
            switchContent(R.id.drawerLayout, scrollingFragment);
        }

        public void switchContent(int id, Fragment fragment) {
            if (c == null)
                return;
            if (c instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) c;
                mainActivity.switchContent(id, fragment);
            }

        }


        @Override
        public void onClick(View v) {
            fragmentJump(artist);
        }
    }

}




