package com.riseapps.xmusic.executor;

import android.view.View;

/**
 * Created by naimish on 10/3/17.
 */

public interface ClickListener {
    void onClick(View view, int position);

    void onLongClick(View view, int position);
}
