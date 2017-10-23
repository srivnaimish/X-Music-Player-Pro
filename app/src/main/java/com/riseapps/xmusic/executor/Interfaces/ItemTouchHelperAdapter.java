package com.riseapps.xmusic.executor.Interfaces;

/**
 * Created by naimish on 22/10/17.
 */

public interface ItemTouchHelperAdapter {

    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}
