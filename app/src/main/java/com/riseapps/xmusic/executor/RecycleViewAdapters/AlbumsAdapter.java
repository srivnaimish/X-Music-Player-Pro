package com.riseapps.xmusic.executor.RecycleViewAdapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.riseapps.xmusic.R;
import com.riseapps.xmusic.model.Pojo.Album;
import com.riseapps.xmusic.utils.RandomAlbumArt;

import java.util.List;

public class AlbumsAdapter extends RecyclerView.Adapter {

    private List<Album> albumList;
    Context c;
    private RandomAlbumArt randomAlbumArt;

    public AlbumsAdapter(Context context, List<Album> albumList, RecyclerView recyclerView) {
        this.albumList = albumList;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();
            c = context;
        }

        randomAlbumArt = new RandomAlbumArt();

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View v = inflater.inflate(R.layout.grid_row, parent, false);
        vh = new AlbumViewHolder(v, c);


        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        Album album = albumList.get(position);

        String name = album.getName().trim();
        String imagepath = album.getImagepath();
        //Log.d("imagepath", " " + imagepath);
        if (!imagepath.equalsIgnoreCase("NoImage") && !name.equals("Ad")) {
            if (!imagepath.equalsIgnoreCase("no_image")) {
                Glide.with(c).load(imagepath)
                        .centerCrop()
                        .into(((AlbumViewHolder) holder).imageView);
            } else {
                Glide.with(c).load(randomAlbumArt.getArt())
                        .centerCrop()
                        .placeholder(R.drawable.ic_equaliser)
                        .into(((AlbumViewHolder) holder).imageView);
            }
            ((AlbumViewHolder) holder).name.setText(name);
            ((AlbumViewHolder) holder).album = album;
        } else {
            ((AlbumViewHolder) holder).name.setText(name);
            Glide.with(c).load(randomAlbumArt.getArt())
                    .placeholder(R.drawable.ic_equaliser)
                    .centerCrop()
                    .into(((AlbumViewHolder) holder).imageView);
        }


    }


    @Override
    public int getItemCount() {
        return albumList.size();
    }


}

class AlbumViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

    ImageView imageView, optionMenuClose;
    TextView name;
    Context ctx;
    Album album;
    RelativeLayout optionsMenu;
    private Animator anim;
    private static boolean isOpen;

    AlbumViewHolder(final View view, Context context) {
        super(view);
        this.ctx = context;
        imageView = (ImageView) view.findViewById(R.id.imageView);
        name = (TextView) view.findViewById(R.id.name);
    }

    @Override
    public boolean onLongClick(View v) {
        Toast.makeText(ctx, " " + getLayoutPosition(), Toast.LENGTH_SHORT).show();
        doCircularReveal(v, optionsMenu);
        return true;
    }

    private void doCircularReveal(final View v, RelativeLayout menu) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            int x = menu.getLeft() + menu.getWidth() / 2;
            int y = 0;
            int startRadius = 0;
            int hypotenuse = (int) Math.hypot(menu.getWidth(), menu.getHeight());
            anim = ViewAnimationUtils.createCircularReveal(menu, x, y, startRadius, hypotenuse);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.setDuration(400);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    v.setVisibility(View.GONE);
                    isOpen = true;
                }
            });
            menu.setVisibility(View.VISIBLE);
            anim.start();
        } else {
            menu.setVisibility(View.VISIBLE);
        }
    }
}



