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

import java.util.List;

public class AlbumsAdapter extends RecyclerView.Adapter {

    private List<Album> albumList;
    Context c;

    private static final int AD_TYPE=0;
    private static final int NORMAL_TYPE=1;


    public AlbumsAdapter(Context context, List<Album> albumList, RecyclerView recyclerView) {
        this.albumList = albumList;

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
        switch (viewType) {

            case AD_TYPE:
                View v1 = inflater.inflate(R.layout.nativ_express_ad_container, parent, false);
                vh=new AdViewHolder(v1);
                break;
            case NORMAL_TYPE:
                View v = inflater.inflate(R.layout.grid_row, parent, false);
                vh = new AlbumViewHolder(v, c);
                break;
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

            Album album = albumList.get(position);
            switch (holder.getItemViewType()) {
                case NORMAL_TYPE:
                    String name=album.getName().trim();
                    String imagepath=album.getImagepath();
                    //Log.d("imagepath", " " + imagepath);
                    if (!imagepath.equalsIgnoreCase("NoImage") && !name.equals("Ad")) {
                        if (!imagepath.equalsIgnoreCase("no_image")) {
                            Glide.with(c).load(imagepath)
                                    .centerCrop()
                                    .into(((AlbumViewHolder) holder).imageView);
                        }
                        else {
                            Glide.with(c).load("")
                                    .placeholder(R.drawable.ic_equaliser)
                                    .into(((AlbumViewHolder) holder).imageView);
                        }
                        ((AlbumViewHolder)holder).name.setText(name);
                        ((AlbumViewHolder) holder).album = album;
                    }
                    else {
                        ((AlbumViewHolder)holder).name.setText(name);
                        Glide.with(c).load("")
                                .placeholder(R.drawable.ic_equaliser)
                                .fitCenter()
                                .into(((AlbumViewHolder) holder).imageView);
                    }
                    break;

                case AD_TYPE:
                    /*
                    NativeExpressAdView adView=adViewHolder.adView;
                    ViewGroup adCardView = (ViewGroup) adViewHolder.itemView;
                    adCardView.removeAllViews();

                    if(adView.getParent()!=null){
                        ((ViewGroup)adView.getParent()).removeView(adView);
                        Toast.makeText(c, "Removed", Toast.LENGTH_SHORT).show();
                    }
                    adCardView.addView(adView);*/

                    break;
            }

    }

    @Override
    public int getItemViewType(int position)
    {
        if(albumList.get(position)==null)
        return AD_TYPE;

        return NORMAL_TYPE;
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    private class AdViewHolder extends RecyclerView.ViewHolder {

        NativeExpressAdView adView;
        AdViewHolder(View view) {
            super(view);
            adView = (NativeExpressAdView)view.findViewById(R.id.adView);
            AdRequest request = new AdRequest.Builder()
                     . addTestDevice("1BB6AD3C4E832E63122601E2E4752AF4")
                    .build();
            adView.loadAd(request);
        }
    }

}

class AlbumViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{

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
        imageView= (ImageView) view.findViewById(R.id.imageView);
        name= (TextView) view.findViewById(R.id.name);
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
        }
        else {
            menu.setVisibility(View.VISIBLE);
        }
    }
}



