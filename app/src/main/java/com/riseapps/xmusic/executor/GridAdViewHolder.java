package com.riseapps.xmusic.executor;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.riseapps.xmusic.R;

/**
 * Created by kanishk on 27/03/17.
 */

public class GridAdViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public GridAdViewHolder(View itemView) {
        super(itemView);
        CardView adCardView = (CardView) itemView.findViewById(R.id.grid_card_ad);
    }

    @Override
    public void onClick(View v) {

    }
}