package com.riseapps.xmusic.executor.RecycleViewAdapters;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
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

import com.bumptech.glide.Glide;
import com.riseapps.xmusic.R;
import com.riseapps.xmusic.executor.Interfaces.FragmentTransitionListener;
import com.riseapps.xmusic.model.Pojo.Album;
import com.riseapps.xmusic.view.Activity.MainActivity;
import com.riseapps.xmusic.view.Fragment.ScrollingFragment;

public class AlbumsAdapter extends RecyclerView.Adapter {

    Context c;
    private Cursor dataCursor;
    private final Uri sArtworkUri = Uri
            .parse("content://media/external/audio/albumart");
    private ScrollingFragment scrollingFragment;
    private FragmentTransitionListener fragmentTransitionListener;

    public AlbumsAdapter(Context context, RecyclerView recyclerView, Cursor cursor) {
        dataCursor = cursor;
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                .getLayoutManager();
        c = context;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View v = inflater.inflate(R.layout.grid_row, parent, false);
        vh = new AlbumViewHolder(v, c);
        return vh;
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
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        dataCursor.moveToPosition(position);
        long album_id = dataCursor.getLong(dataCursor.getColumnIndex(MediaStore.Audio.Albums._ID));
        String albumName = dataCursor.getString(dataCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM));
        String imagepath = ContentUris.withAppendedId(sArtworkUri, album_id).toString();
        if (albumName.length() > 40)
            albumName = albumName.substring(0, 32) + "...";

        ((AlbumViewHolder) holder).name.setText(albumName);
        Glide.with(c).load(imagepath)
                .dontAnimate()
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(((AlbumViewHolder) holder).imageView);

        ((AlbumViewHolder) holder).album = new Album(album_id, albumName, imagepath);
    }


    @Override
    public int getItemCount() {
        return (dataCursor == null) ? 0 : dataCursor.getCount();
    }

    private class AlbumViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        TextView name;
        Context ctx;
        CardView cardView;
        Album album;

        AlbumViewHolder(final View view, Context context) {
            super(view);
            this.ctx = context;
            imageView = (ImageView) view.findViewById(R.id.imageView1);
            name = (TextView) view.findViewById(R.id.name);
            cardView = (CardView) view.findViewById(R.id.grid_card);
            cardView.setOnClickListener(this);
        }

        private void fragmentJump(Album album) {
            scrollingFragment = new ScrollingFragment();
            Bundle bundle = new Bundle();
            bundle.putLong("ID", album.getId());
            bundle.putString("Name", album.getName());
            bundle.putString("Imagepath", album.getImagepath());
            bundle.putString("Action", "Albums");
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
            fragmentJump(album);
        }
    }
}





