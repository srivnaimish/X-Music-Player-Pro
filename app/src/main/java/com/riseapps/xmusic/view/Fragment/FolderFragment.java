package com.riseapps.xmusic.view.Fragment;


import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.riseapps.xmusic.R;
import com.riseapps.xmusic.component.AppConstants;
import com.riseapps.xmusic.component.SharedPreferenceSingelton;
import com.riseapps.xmusic.executor.Interfaces.FragmentTransitionListener;
import com.riseapps.xmusic.executor.RecycleViewAdapters.FoldersFragmentAdapter;
import com.riseapps.xmusic.model.Pojo.PlaylistSelect;

import java.util.ArrayList;

public class FolderFragment extends Fragment {

    ArrayList<PlaylistSelect> folders;
    RecyclerView recyclerView;
    FoldersFragmentAdapter foldersAdapter;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public FolderFragment() {
        // Required empty public constructor
    }

    public static FolderFragment newInstance() {
        return new FolderFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=  inflater.inflate(R.layout.fragment_folder, container, false);
        recyclerView = v.findViewById(R.id.folders);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initiallizeFolderNames();
                foldersAdapter = new FoldersFragmentAdapter(getContext(), folders, recyclerView);
                recyclerView.setAdapter(foldersAdapter);
                foldersAdapter.setFragmentTransitionListener(new FragmentTransitionListener() {
                    @Override
                    public void onFragmentTransition(ScrollingFragment scrollingFragment) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            setExitTransition(TransitionInflater.from(
                                    getActivity()).inflateTransition(android.R.transition.fade));
                            scrollingFragment.setEnterTransition(TransitionInflater.from(
                                    getActivity()).inflateTransition(android.R.transition.fade));
                        }
                    }
                });
            }
        },1200);


        return v;
    }

    void initiallizeFolderNames() {
        folders= AppConstants.getFolderNames(getContext());

    }

}
