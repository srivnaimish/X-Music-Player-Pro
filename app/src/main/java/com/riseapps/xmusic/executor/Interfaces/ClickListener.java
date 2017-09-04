package com.riseapps.xmusic.executor.Interfaces;

import android.view.View;

/**
 * Created by naimish on 22/3/17.
 */

public interface ClickListener {
    void onClick(View view, int position);

    void onLongClick(View view, int position);
}