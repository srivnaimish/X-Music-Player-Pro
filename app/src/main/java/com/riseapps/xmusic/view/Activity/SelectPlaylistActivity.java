package com.riseapps.xmusic.view.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.riseapps.xmusic.R;
import com.riseapps.xmusic.component.SharedPreferenceSingelton;
import com.riseapps.xmusic.component.TagToken.customviews.TokenCompleteTextView;
import com.riseapps.xmusic.executor.MyApplication;
import com.riseapps.xmusic.model.Pojo.Playlist;
import com.riseapps.xmusic.model.Pojo.Tag;
import com.riseapps.xmusic.model.xplayertags.TagClass;
import com.riseapps.xmusic.utils.TagSelector;
import com.riseapps.xmusic.widgets.TagView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class SelectPlaylistActivity extends AppCompatActivity implements TokenCompleteTextView.TokenListener<TagClass> {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private final String TAG = getClass().getSimpleName();
    TextView hint;
    FloatingActionButton fab;

    // private AdView mAdView;
    private String str = "";

    Tag tag;
    ArrayList<Tag> tags = new ArrayList<>();
    TagView tagGroup;

    private HashMap<String, Integer> selectedPlaylist = new HashMap<>();
    private HashMap<String, Integer> hashMap = new HashMap<>();
    private HashMap<String, Integer> skillFactoryHashMap = new HashMap<>();
    SharedPreferenceSingelton sharedPreferenceSingelton;
    // utils
    private TagSelector tagSelector = new TagSelector(SelectPlaylistActivity.this);
    private Dialog dialog;
    private String selectionType;
    private String singlePlaylist;
    private View singlePlaylistView;
    private TextView singlePlaylistTextView;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferenceSingelton = new SharedPreferenceSingelton();
        if (sharedPreferenceSingelton.getSavedBoolean(SelectPlaylistActivity.this, "Theme")) {
            setTheme(R.style.AppTheme_Dark);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_playlist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        hint = (TextView) findViewById(R.id.hint);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                * Add logic to add all the song to all the selected playlists. dataset - otherSKills hashmap.
                 * This hashmap contains all the selected items.*/
                Intent i = new Intent();
                setResult(RESULT_CANCELED, i);
                Toast.makeText(SelectPlaylistActivity.this, getString(R.string.did_not_select), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        toolbar.inflateMenu(R.menu.select_playlist_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_done) {
                    if (selectedPlaylist.size() < 1) {
                        Toast.makeText(SelectPlaylistActivity.this, getString(R.string.select_atleast_one), Toast.LENGTH_SHORT).show();
                    } else {
                        selectedPlaylist.remove("A");
                        convertHashmapToString();
                        Intent i = new Intent();
                        i.putExtra("selected_playlist", str);
                        setResult(RESULT_OK, i);
                        finish();
                    }
                }
                return true;
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });

        try {
            setupTagView();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = getIntent();
        if (intent != null)
            selectionType = intent.getStringExtra("selection_type");

    }

    @Override
    public void onTokenAdded(TagClass token) {

    }

    @Override
    public void onTokenRemoved(TagClass token) {

    }

    // setup tags and listeners
    private void setupTagView() throws Exception {
        tagGroup = (TagView) findViewById(R.id.tag_group);
        prepareTags();
        setTags();
        selectedPlaylist.put("A", 0);

        tagGroup.setOnTagClickListener(new TagView.OnTagClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onTagClick(View view, TextView tagView, Tag tag, int position) {
                if (selectionType.equals("multiple_playlist")) {
                    if (selectedPlaylist.get(tag.text) == null) {
                        view.setBackground(tagSelector.getSelector(tag));
                        tagView.setTextColor(getResources().getColor(R.color.colorWhite));
                        selectedPlaylist.put(tag.text, 1); // tag.text - playlist name
                    } else {
                        // reset same tag
                        tagView.setTextColor(getResources().getColor(R.color.colorBlack));
                        tag.layoutColor = Color.parseColor("#FFFFFF");
                        tag.tagTextColor = Color.parseColor("#000000");
                        view.setBackground(tagSelector.getNormalSelector(tag));
                        selectedPlaylist.remove(tag.text);
                    }
                } else if (selectionType.equals("single_playlist")) {
                    if (selectedPlaylist.get(tag.text) == null) {
                        Log.d(TAG, "IF ADD TAG AND SINGLE PLAYLIST " + singlePlaylist);
                        // CHECK IF ANYTHING ALREADY PRESENT IN HASHMAP
                        if (selectedPlaylist.size() > 1) {
                            // reset previous tag
                            selectedPlaylist.remove(singlePlaylist);
                            singlePlaylistTextView.setTextColor(getResources().getColor(R.color.colorBlack));
                            singlePlaylistView.setBackground(tagSelector.getNormalSelector(tag));
                            singlePlaylist = tag.text;
                            Log.d(TAG, "IF RESET PREVIOUS TAG AND SINGLE PLAYLIST " + singlePlaylist);
                            // ADD TAG
                            view.setBackground(tagSelector.getSelector(tag));
                            tagView.setTextColor(getResources().getColor(R.color.colorWhite));
                            selectedPlaylist.put(tag.text, 1); // tag.text - playlist name
                            singlePlaylistView = view;
                            singlePlaylistTextView = tagView;
                        } else {
                            singlePlaylist = tag.text;
                            Log.d(TAG, "IF ADD NEW TAG AND SINGLE PLAYLIST " + singlePlaylist);
                            // ADD TAG
                            view.setBackground(tagSelector.getSelector(tag));
                            tagView.setTextColor(getResources().getColor(R.color.colorWhite));
                            selectedPlaylist.put(tag.text, 1); // tag.text - playlist name
                            singlePlaylistView = view;
                            singlePlaylistTextView = tagView;
                        }
                    } else if (selectedPlaylist.get(tag.text) != null) {
                        Log.d(TAG, "IF RESET TAG AND SINGLE PLAYLIST " + singlePlaylist);
                        // RESET TAG
                        selectedPlaylist.remove(tag.text);
                        tagView.setTextColor(getResources().getColor(R.color.colorBlack));
                        tag.layoutColor = Color.parseColor("#FFFFFF");
                        tag.tagTextColor = Color.parseColor("#000000");
                        view.setBackground(tagSelector.getNormalSelector(tag));
                        //singlePlaylist = "";
                        singlePlaylistTextView.setTextColor(getResources().getColor(R.color.colorBlack));
                        singlePlaylistView.setBackground(tagSelector.getNormalSelector(tag));
                    }
                }
            }
        });


    }

    private void prepareTags() throws Exception {
        ArrayList<Playlist> playlists = new MyApplication(SelectPlaylistActivity.this).getWritableDatabase().readPlaylists();
        for (int i = 0; i < playlists.size(); i++) {
            skillFactoryHashMap.put(playlists.get(i).getName(), 0);
        }

    }

    private void setTags() {
        Log.d("hashmap", " " + skillFactoryHashMap.toString());
        //skillFactoryHashMap.putAll(hashMap);
        Iterator it = skillFactoryHashMap.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry) it.next();
            String tagName = pair.getKey().toString();
            int status = Integer.parseInt(pair.getValue().toString());
            Log.d(TAG, "int value " + status);
            tag = new Tag(tagName);
            tag.radius = 8f;
            if (hashMap.containsKey(tagName) && status == 2) {
                tag.layoutColor = Color.parseColor(String.valueOf(R.color.colorAccent));
                tag.tagTextColor = Color.parseColor("#ffffff");
                selectedPlaylist.put(tagName, 2);
            } else if (hashMap.containsKey(tagName) && status == 0) {
                continue;
            } else if (hashMap.containsKey(tagName) && status == 1) {
                tag.layoutColor = Color.parseColor("#3F51B5");
                tag.tagTextColor = Color.parseColor("#ffffff");
                selectedPlaylist.put(tagName, 1);
            } else if (!hashMap.containsKey(tagName) && status == 0) {
                tag.layoutColor = Color.parseColor("#ffffff");
            } else {
                tag.layoutColor = Color.parseColor("#ffffff");
            }
            tags.add(tag);
        }
        tagGroup.addTags(tags);
    }

    private void convertHashmapToString() {
        Iterator it = selectedPlaylist.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry) it.next();
            str = str + pair.getKey() + ",";
            it.remove();
        }
        Log.d(TAG, "" + str);
    }

    private void openDialog() {
        dialog = new Dialog(SelectPlaylistActivity.this);
        dialog.setContentView(R.layout.playlist_create_dialog);
        dialog.show();
        Button create = (Button) dialog.findViewById(R.id.create);
        Button cancel = (Button) dialog.findViewById(R.id.cancel);
        final EditText editText = (EditText) dialog.findViewById(R.id.dialogEditText);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editText.getText().toString().equalsIgnoreCase("")) {
                    skillFactoryHashMap.put(editText.getText().toString(), 0);
                    tagGroup = (TagView) findViewById(R.id.tag_group);
                    tags = new ArrayList<>();
                    setTags();
                    dialog.dismiss();
                    Toast.makeText(SelectPlaylistActivity.this, getString(R.string.created), Toast.LENGTH_SHORT).show();
                    if (hint.getVisibility() == View.GONE)
                        hint.setVisibility(View.VISIBLE);
                } else {
                    Snackbar.make(fab, "Please give playlist a Name", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
}
