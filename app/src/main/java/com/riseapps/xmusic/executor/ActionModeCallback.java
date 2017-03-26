package com.riseapps.xmusic.executor;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.MenuItemCompat;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.claudiodegio.msv.OnSearchViewListener;
import com.riseapps.xmusic.R;
import com.riseapps.xmusic.executor.RecycleViewAdapters.SongAdapter;
import com.riseapps.xmusic.view.Fragment.SongsFragment;

/**
 * Created by kanishk on 25/03/17.
 */

public class ActionModeCallback implements android.support.v7.view.ActionMode.Callback {

    private Context context;
    private SongAdapter songAdapter;
    private OnSetNullListener mListener;

    public ActionModeCallback(Context context,
                       SongAdapter songAdapter) {
        this.context = context;
        this.songAdapter = songAdapter;
    }

    @Override
    public boolean onCreateActionMode(android.support.v7.view.ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.main_menu, menu);//Inflate the menu over action mode
        return true;
    }

    @Override
    public boolean onPrepareActionMode(android.support.v7.view.ActionMode mode, Menu menu) {
        //Sometimes the meu will not be visible so for that we need to set their visibility manually in this method
        //So here show action menu according to SDK Levels
        if (Build.VERSION.SDK_INT < 17) {
            MenuItemCompat.setShowAsAction(menu.findItem(R.id.action_search), MenuItemCompat.SHOW_AS_ACTION_NEVER);
            MenuItemCompat.setShowAsAction(menu.findItem(R.id.favourites), MenuItemCompat.SHOW_AS_ACTION_NEVER);
        } else {
            menu.findItem(R.id.action_search).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            menu.findItem(R.id.favourites).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        return true;
    }

    @Override
    public boolean onActionItemClicked(android.support.v7.view.ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Toast.makeText(context, "Search", Toast.LENGTH_SHORT).show();
                break;
            case R.id.favourites:
                Toast.makeText(context, "fav", Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(android.support.v7.view.ActionMode mode) {
        Toast.makeText(context, "destroyed", Toast.LENGTH_SHORT).show();
        songAdapter.removeSelection();
        SongsFragment fragment = new SongsFragment().getSongsFragment();
        //fragment.setNullToActionMode();
        mListener.onNullifySuccess();
    }

    public void setListener(OnSetNullListener listener) {
        mListener = listener;
    }

    public interface OnSetNullListener {
        void onNullifySuccess();
    }
}
