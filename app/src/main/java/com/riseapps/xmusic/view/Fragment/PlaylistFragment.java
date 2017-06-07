package com.riseapps.xmusic.view.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.riseapps.xmusic.R;
import com.riseapps.xmusic.executor.Interfaces.ClickListener;
import com.riseapps.xmusic.executor.Interfaces.PlaylistRefreshListener;
import com.riseapps.xmusic.executor.MyApplication;
import com.riseapps.xmusic.executor.RecycleTouchListener;
import com.riseapps.xmusic.executor.RecycleViewAdapters.PlaylistAdapter;
import com.riseapps.xmusic.model.Pojo.Playlist;
import com.riseapps.xmusic.utils.GridItemDecoration;
import com.riseapps.xmusic.view.Activity.MainActivity;

import java.util.ArrayList;


public class PlaylistFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    NestedScrollView nestedScrollView;
    RecyclerView recyclerView;
    ArrayList<Playlist> playLists=new ArrayList<>();
    PlaylistAdapter playlistAdapter;
    private OnFragmentInteractionListener mListener;
    //  private LinearLayout createPlaylist;
    private Dialog dialog;

    public PlaylistFragment() {
        // Required empty public constructor
    }


    public static PlaylistFragment newInstance() {
        return new PlaylistFragment();
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
        // Inflate the layout for this fragment_songs
        final View v=inflater.inflate(R.layout.fragment_playlist, container, false);
        nestedScrollView= (NestedScrollView) v.findViewById(R.id.nestedScrollView);
        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.BOTTOM_TOP,
                new int[]{Color.parseColor("#EEEEEE"), Color.parseColor("#FFFFFF")});
        nestedScrollView.setBackground(gd);

        recyclerView = (RecyclerView) v.findViewById(R.id.playlists);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager grid = new GridLayoutManager(v.getContext(), 1);
        recyclerView.setLayoutManager(grid);

        int spanCount = 1;
        int spacing = 4;

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                new MyApplication(getActivity()).getWritableDatabase().deletePlaylist(playLists.get(viewHolder.getAdapterPosition()).getName());
                playlistAdapter.delete(viewHolder.getAdapterPosition());
                Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        recyclerView.addItemDecoration(new GridItemDecoration(spanCount, spacing, true));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.addOnItemTouchListener(new RecycleTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                CardView cardView= (CardView) view.findViewById(R.id.playlist_list_card);
                ImageButton delete= (ImageButton) view.findViewById(R.id.delete);

                View.OnClickListener onClickListener=new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(v.getId()==R.id.delete){
                            new MyApplication(getContext()).getWritableDatabase().deletePlaylist(playLists.get(position).getName());
                            playlistAdapter.delete(position);
                            Toast.makeText(getContext(), "Playlist Deleted", Toast.LENGTH_SHORT).show();
                        }
                        else if(v.getId()==R.id.playlist_list_card){
                            FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                            ScrollingFragment scrollingFragment=new ScrollingFragment();
                            Bundle bundle=new Bundle();
                            bundle.putString("Name",playLists.get(position).getName());
                            //   bundle.putString("Imagepath",playLists.get(position).getImagepath());
                            bundle.putString("Action","Playlists");
                            scrollingFragment.setArguments(bundle);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.replace(R.id.drawerLayout,scrollingFragment);
                            fragmentTransaction.commit();
                        }

                    }
                };
                cardView.setOnClickListener(onClickListener);
                delete.setOnClickListener(onClickListener);


            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        refreshPlaylists();

        ((MainActivity) getActivity()).setPlaylistRefreshListener(new PlaylistRefreshListener() {
            @Override
            public void OnPlaylistRefresh() {
                refreshPlaylists();
            }
        });
        return v;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            View v = getView();
            if (v != null) {
                playlistAdapter.notifyDataSetChanged();
            }

        } else {
            View v = getView();
            if (v != null) {

            }
        }
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void refreshPlaylists(){
        playLists=new MyApplication(getActivity()).getWritableDatabase().readPlaylists();
        playlistAdapter = new PlaylistAdapter(getActivity(), playLists, recyclerView);
        recyclerView.setAdapter(playlistAdapter);
    }


}
