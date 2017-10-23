package com.riseapps.xmusic.view.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.riseapps.xmusic.R;
import com.riseapps.xmusic.component.ThemeSelector;
import com.riseapps.xmusic.executor.Interfaces.ClickListener;
import com.riseapps.xmusic.executor.MyApplication;
import com.riseapps.xmusic.utils.RecycleTouchListener;
import com.riseapps.xmusic.executor.RecycleViewAdapters.AddPlaylistAdapter;
import com.riseapps.xmusic.model.Pojo.PlaylistSelect;

import java.util.ArrayList;


public class SelectPlaylistActivity extends AppCompatActivity {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    String selection = "";
    LinearLayout empty_state;
    RecyclerView recyclerView;
    ArrayList<PlaylistSelect> playLists = new ArrayList<>();
    AddPlaylistAdapter addPlaylistAdapter;
    CardView cardView, dialog;
    TextView hint;
    //  private Dialog dialog;
    // utils

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        new ThemeSelector().setAppTheme(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_select_playlist);

        empty_state = (LinearLayout) findViewById(R.id.linearLayout4);
        dialog = (CardView) findViewById(R.id.playlist_new);
        hint = (TextView) findViewById(R.id.hint);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SelectPlaylistActivity.this, getString(R.string.did_not_select), Toast.LENGTH_SHORT).show();
                Intent i = new Intent();
                setResult(RESULT_CANCELED, i);
                finish();
            }
        });
        toolbar.inflateMenu(R.menu.playlist_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_done) {
                    for (int i = 0; i < playLists.size(); i++) {
                        PlaylistSelect playlistSelect = playLists.get(i);
                        if (playlistSelect.isSelected()) {
                            if (i < playLists.size() - 1)
                                selection += playlistSelect.getName() + ",";
                            else selection += playlistSelect.getName();
                        }
                    }

                    if (selection.equalsIgnoreCase("")) {
                        /*Intent i = new Intent();
                        setResult(RESULT_CANCELED, i);*/
                        Toast.makeText(SelectPlaylistActivity.this, getString(R.string.did_not_select), Toast.LENGTH_SHORT).show();
                        //  finish();
                    } else {
                        Intent i = new Intent();
                        i.putExtra("selected_playlist", selection);
                        setResult(RESULT_OK, i);
                        finish();
                    }
                }
                return true;
            }
        });

        Button create = (Button) findViewById(R.id.create);
        Button cancel = (Button) findViewById(R.id.cancel);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = (EditText) dialog.findViewById(R.id.dialogEditText);
                String s = editText.getText().toString();
                if (!s.equalsIgnoreCase("")) {
                    if (playLists.contains(new PlaylistSelect(s, false))) {
                        Snackbar.make(cardView, "Playlist already created", Snackbar.LENGTH_SHORT).show();
                    } else {
                        if (empty_state.getVisibility() == View.VISIBLE) {
                            empty_state.setVisibility(View.GONE);
                        }
                        playLists.add(new PlaylistSelect(s, false));
                        addPlaylistAdapter.notifyItemInserted(playLists.size());
                        doExitReveal(dialog);
                        editText.setText("");
                    }
                } else {
                    Snackbar.make(cardView, "Please give playlist a Name", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doExitReveal(dialog);
            }
        });

        cardView = (CardView) findViewById(R.id.add_playlist);
        recyclerView = (RecyclerView) findViewById(R.id.playlists);

        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCircularReveal(dialog);
            }
        });

        playLists = new MyApplication(this).getWritableDatabase().readPlaylistsSelect();
        if (playLists.size() == 0) {
            empty_state.setVisibility(View.VISIBLE);
        }
        addPlaylistAdapter = new AddPlaylistAdapter(this, playLists, recyclerView);
        recyclerView.setAdapter(addPlaylistAdapter);


        recyclerView.addOnItemTouchListener(new RecycleTouchListener(SelectPlaylistActivity.this, recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                CheckBox add = (CheckBox) view.findViewById(R.id.add);

                add.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked)
                            playLists.get(position).setSelected(true);
                        else
                            playLists.get(position).setSelected(false);
                    }
                });
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (dialog.getVisibility() == View.VISIBLE)
            doExitReveal(dialog);
        else
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void doCircularReveal(View view) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            dialog.setVisibility(View.VISIBLE);
            int centerX = 0;
            int centerY = view.getHeight();
            int startRadius = 0;
            int endRadius = Math.max(view.getWidth(), view.getHeight());
            Animator anim = null;
            anim = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius, endRadius);
            anim.setDuration(300);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                }
            });
            anim.start();
        } else {
            dialog.setVisibility(View.VISIBLE);
        }
        cardView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        hint.setVisibility(View.GONE);
        if (empty_state.getVisibility() == View.VISIBLE)
            empty_state.setVisibility(View.GONE);
    }

    void doExitReveal(View view) {
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(
                dialog.getWindowToken(), 0);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            int centerX = view.getWidth();
            int centerY = view.getHeight();
            int initialRadius = view.getWidth();
            Animator anim =
                    ViewAnimationUtils.createCircularReveal(view, centerX, centerY, initialRadius, 0);
            anim.setDuration(200);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    dialog.setVisibility(View.INVISIBLE);
                }
            });
            anim.start();
        } else {
            dialog.setVisibility(View.GONE);
        }
        cardView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        hint.setVisibility(View.VISIBLE);
        if (playLists.size() == 0)
            empty_state.setVisibility(View.VISIBLE);
    }

}
