package com.riseapps.xmusic.model.Pojo;

import android.support.v7.widget.CardView;

/**
 * Created by kanishk on 02/04/17.
 */

public class LongSelectedSong {

    private CardView cardView;
    private boolean status;

    public LongSelectedSong(CardView cardView, boolean status) {
        this.cardView = cardView;
        this.status = status;
    }

    public CardView getCardView() {
        return cardView;
    }

    public void setCardView(CardView cardView) {
        this.cardView = cardView;
    }


    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
