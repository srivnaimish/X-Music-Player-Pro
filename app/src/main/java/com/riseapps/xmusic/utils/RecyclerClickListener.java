package com.riseapps.xmusic.utils;

import android.view.View;

/**
 * Created by kanishk on 25/03/17.
 */

public interface RecyclerClickListener {
    void onClick(View view, int position);

    void onLongClick(View view, int position);
}
