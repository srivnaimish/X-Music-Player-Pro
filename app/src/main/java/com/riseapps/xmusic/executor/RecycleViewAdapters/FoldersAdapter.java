package com.riseapps.xmusic.executor.RecycleViewAdapters;

import android.content.Context;
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
import com.riseapps.xmusic.model.Pojo.PlaylistSelect;

import java.util.ArrayList;
import java.util.List;

public class FoldersAdapter extends RecyclerView.Adapter {

    private List<PlaylistSelect> folders;
    private Context c;
    private String unselected;

    public FoldersAdapter(Context context, ArrayList<PlaylistSelect> folders, RecyclerView recyclerView) {
        this.folders = folders;
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        c = context;
        unselected=new SharedPreferenceSingelton().getSavedString(c,"SkipFolders");

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.folders_row, parent, false);
        return new FolderViewHolder(view, c);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        PlaylistSelect folder = folders.get(position);
        String name = folder.getName();
        ((FolderViewHolder) holder).name.setText(name);
        ((FolderViewHolder) holder).folder = folder;
        if(unselected!=null && unselected.contains(name)) {
            ((FolderViewHolder) holder).add.setChecked(false);
            folder.setSelected(false);
        }

    }


    @Override
    public int getItemCount() {
        return folders.size();
    }

    private class FolderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name;
        CheckBox add;
        CardView folderCard;
        private Context ctx;
        PlaylistSelect folder;

        FolderViewHolder(View v, Context context) {
            super(v);
            this.ctx = context;
            name = (TextView) v.findViewById(R.id.name);
            add = (CheckBox) v.findViewById(R.id.allowed);
            folderCard = (CardView) v.findViewById(R.id.folders_list_card);
            add.setOnClickListener(this);
            folderCard.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == folderCard.getId()) {
                if (add.isChecked()) {
                    add.setChecked(false);
                    folder.setSelected(false);
                }
                else {
                    add.setChecked(true);
                    folder.setSelected(true);
                }

            }else if(v.getId() == add.getId()){
                if (folder.isSelected()) {
                    folder.setSelected(false);
                }
                else {
                    folder.setSelected(true);
                }
            }
        }

    }
}
