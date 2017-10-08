package com.riseapps.xmusic.executor.RecycleViewAdapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.riseapps.xmusic.R;
import com.riseapps.xmusic.component.SharedPreferenceSingelton;
import com.riseapps.xmusic.executor.Interfaces.FragmentTransitionListener;
import com.riseapps.xmusic.model.Pojo.Artist;
import com.riseapps.xmusic.model.Pojo.PlaylistSelect;
import com.riseapps.xmusic.view.Activity.MainActivity;
import com.riseapps.xmusic.view.Fragment.ScrollingFragment;

import java.util.ArrayList;
import java.util.List;

public class FoldersFragmentAdapter extends RecyclerView.Adapter {

    private List<PlaylistSelect> folders;
    private Context c;
    private String unselected;
    private ScrollingFragment scrollingFragment;
    private FragmentTransitionListener fragmentTransitionListener;


    public FoldersFragmentAdapter(Context context, ArrayList<PlaylistSelect> folders, RecyclerView recyclerView) {
        this.folders = folders;
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        c = context;
        unselected = new SharedPreferenceSingelton().getSavedString(c, "SkipFolders");

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.folders_fragment_row, parent, false);
        return new FolderViewHolder(view, c);
    }

    public void setFragmentTransitionListener(FragmentTransitionListener fragmentTransitionListener) {
        this.fragmentTransitionListener = fragmentTransitionListener;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        PlaylistSelect folder = folders.get(position);
        String name = folder.getName();
        if (unselected != null && unselected.contains(name)) {
            //folders.remove(position);
        }else {
            ((FolderViewHolder) holder).name.setText(name);
            ((FolderViewHolder) holder).folder = folder;
        }

    }


    @Override
    public int getItemCount() {
        return folders.size();
    }

    private class FolderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name;
        CardView folderCard;
        private Context ctx;
        PlaylistSelect folder;

        FolderViewHolder(View v, Context context) {
            super(v);
            this.ctx = context;
            name = (TextView) v.findViewById(R.id.name);
            folderCard = (CardView) v.findViewById(R.id.folders_list_card);

            folderCard.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == folderCard.getId()) {
                fragmentJump(folder);
            }
        }

        private void fragmentJump(PlaylistSelect clickedFolder) {
            scrollingFragment = new ScrollingFragment();
            Bundle bundle = new Bundle();
            bundle.putString("Name",clickedFolder.getName());
            bundle.putString("Action", "Folder");
            scrollingFragment.setArguments(bundle);
            fragmentTransitionListener.onFragmentTransition(scrollingFragment);
            switchContent(R.id.drawerLayout, scrollingFragment);
        }

        public void switchContent(int id, Fragment fragment) {
            if (c == null)
                return;
            if (c instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) c;
                mainActivity.switchContent(id, fragment);
            }

        }

    }
}
