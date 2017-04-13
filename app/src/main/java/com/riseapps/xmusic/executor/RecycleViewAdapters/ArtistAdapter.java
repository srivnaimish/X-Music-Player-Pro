package com.riseapps.xmusic.executor.RecycleViewAdapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.riseapps.xmusic.R;
import com.riseapps.xmusic.model.Pojo.Artist;
import com.riseapps.xmusic.widgets.MainTextViewSub;

import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter {

    private static final int AD_TYPE = 0;
    private static final int NORMAL_TYPE = 1;
    private List<Artist> artistList;
    Context c;

    public ArtistAdapter(Context context, List<Artist> artistList, RecyclerView recyclerView) {
        this.artistList = artistList;

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
                View v = inflater.inflate(R.layout.artist_name_row, parent, false);
                vh = new ArtistViewHolder(v, c);
                break;
        }
        return vh;

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

            Artist artist = artistList.get(position);
            switch (holder.getItemViewType()){
                case NORMAL_TYPE:
                    String name=artist.getName().trim();
                    String imagepath=artist.getImagepath();
                    if (!imagepath.equalsIgnoreCase("NoImage") && !name.equals("Ad")) {
                        ((ArtistViewHolder)holder).name.setText(name);

                        ((ArtistViewHolder) holder).artist = artist;
                    }
                    else {
                        ((ArtistViewHolder)holder).name.setText(name);
                    }
                 break;

                case AD_TYPE:
                    break;
            }


    }


    @Override
    public int getItemCount() {
        return artistList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(artistList.get(position)==null)
            return AD_TYPE;

        return NORMAL_TYPE;
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
class ArtistViewHolder extends RecyclerView.ViewHolder {

    ImageView imageView;
    TextView name;
    Context ctx;
    MainTextViewSub artistTextView;

    Artist artist;

    ArtistViewHolder(View v, Context context) {
        super(v);
        this.ctx = context;

        name= (TextView) v.findViewById(R.id.name);
    }

}



