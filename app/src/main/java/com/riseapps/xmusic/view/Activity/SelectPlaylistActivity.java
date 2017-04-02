package com.riseapps.xmusic.view.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.riseapps.xmusic.R;
import com.riseapps.xmusic.component.TagToken.customviews.TokenCompleteTextView;
import com.riseapps.xmusic.executor.MyApplication;
import com.riseapps.xmusic.model.Pojo.Playlist;
import com.riseapps.xmusic.model.Pojo.Tag;
import com.riseapps.xmusic.model.xplayertags.TagClass;
import com.riseapps.xmusic.model.xplayertags.TagViewData;
import com.riseapps.xmusic.utils.TagSelector;
import com.riseapps.xmusic.widgets.MainTextView;
import com.riseapps.xmusic.widgets.TagView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class SelectPlaylistActivity extends AppCompatActivity implements TokenCompleteTextView.TokenListener<TagClass> {

    private final String TAG = getClass().getSimpleName();

    private Boolean isLongPressed = false;
    private String coreSkill;
    private View coreSkillView;
    private Tag coreSkillTag;
    private int coreSkillPosition;
    private TextView coreSkillTextView;
    private String str = "";

    Tag tag;
    ArrayList<Tag> tags = new ArrayList<>();
    TagView tagGroup;

    private HashMap<String, Integer> selectedPlaylist = new HashMap<>();
    private HashMap<String, Integer> hashMap = new HashMap<>();
    private HashMap<String, Integer> skillFactoryHashMap = new HashMap<>();

    // utils
    private TagSelector tagSelector = new TagSelector(SelectPlaylistActivity.this);

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_playlist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                * Add logic to add all the song to all the selected playlists. dataset - otherSKills hashmap.
                 * This hashmap contains all the selected items.*/
                Intent i = new Intent();
                setResult(RESULT_CANCELED, i);
                Toast.makeText(SelectPlaylistActivity.this, "Oops, you didn't select any playlist for your song!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        toolbar.setElevation(0);
        toolbar.inflateMenu(R.menu.select_playlist_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_done) {
                    if (selectedPlaylist.size() < 1) {
                        Toast.makeText(SelectPlaylistActivity.this, "You must select at least 1 playlist", Toast.LENGTH_SHORT).show();
                    }
                    else {
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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
        /*tagGroup.setOnTagLongClickListener(new TagView.OnTagLongClickListener() {
            @Override
            public void onTagLongClick(View view, TextView tagView, Tag tag, int position) {

                if (!isLongPressed && coreSkill == null) {
                    tagView.setTextColor(getResources().getColor(R.color.colorWhite));
                    view.setBackground(tagSelector.getLongSelector(tag));
                    isLongPressed = true;
                    coreSkill = tag.text;
                    coreSkillView = view;
                    coreSkillTextView = tagView;
                    coreSkillTag = tag;
                    coreSkillPosition = position;
                } else {
                    if ((tag.text).equals(coreSkill)) {
                        // reset same tag
                        tagView.setTextColor(getResources().getColor(R.color.colorAccent));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            view.setBackground(tagSelector.getNormalSelector(tag));
                        }
                        isLongPressed = false;
                        coreSkill = null;
                    }
                    else {
                        // set this tag
                        tagView.setTextColor(getResources().getColor(R.color.colorWhite));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            view.setBackground(tagSelector.getLongSelector(tag));
                        }
                        // reset previous tag
                        coreSkillTextView.setTextColor(getResources().getColor(R.color.colorAccent));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            coreSkillView.setBackground(tagSelector.getNormalSelector(tag));
                        }
                        isLongPressed = true;
                        coreSkill = tag.text;
                        coreSkillView = view;
                        coreSkillTextView = tagView;
                        coreSkillTag = tag;
                        coreSkillPosition = position;
                    }
                }
            }
        });*/

        tagGroup.setOnTagClickListener(new TagView.OnTagClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onTagClick(View view, TextView tagView, Tag tag, int position) {

                if (selectedPlaylist.get(tag.text) == null) {
                    view.setBackground(tagSelector.getSelector(tag));
                    tagView.setTextColor(getResources().getColor(R.color.colorWhite));
                    selectedPlaylist.put(tag.text, 1); // tag.text - playlist name
                } else {
                    // reset same tag
                    tagView.setTextColor(getResources().getColor(R.color.colorBlack));
                    tag.layoutColor = Color.parseColor("#FFFFFF");
                    tag.tagTextColor = Color.parseColor("#000000");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        view.setBackground(tagSelector.getNormalSelector(tag));
                    }
                    selectedPlaylist.remove(tag.text);
                }
            }
        });

        /*tagGroup.setOnTagDeleteListener(new TagView.OnTagDeleteListener() {
            @Override
            public void onTagDeleted(final TagView view, final Tag tag, final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SelectPlaylistActivity.this);
                builder.setMessage("\"" + tag.text + "\" will be delete. Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        view.remove(position);
                        Toast.makeText(SelectPlaylistActivity.this, "\"" + tag.text + "\" deleted", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("No", null);
                builder.show();
            }
        });*/
    }

    private void prepareTags() throws Exception {
        ArrayList<Playlist> playlists=new MyApplication(SelectPlaylistActivity.this).getWritableDatabase().readPlaylists();
        for (int i=0;i<playlists.size();i++) {
            skillFactoryHashMap.put(playlists.get(i).getName(),0);
        }
        /*JSONArray jsonArray;
        JSONObject temp;
        try {
            jsonArray = new JSONArray(TagViewData.COUNTRIES);
            for (int i = 0; i < jsonArray.length(); i++) {
                temp = jsonArray.getJSONObject(i);
                skillFactoryHashMap.put(temp.getString("name"), 0);
            }
        } catch (Exception e) {
            Log.d("error ", e.toString());
        }*/
    }

    private void setTags() {
        Log.d("hashmap",  " " + skillFactoryHashMap.toString());
        //skillFactoryHashMap.putAll(hashMap);
        Iterator it = skillFactoryHashMap.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            String tagName = pair.getKey().toString();
            int status = Integer.parseInt(pair.getValue().toString());
            Log.d(TAG, "int value " + status);
            tag = new Tag(tagName);
            tag.radius = 8f;
            if (hashMap.containsKey(tagName) && status == 2) {
                tag.layoutColor = Color.parseColor(String.valueOf(R.color.colorAccent));
                tag.tagTextColor = Color.parseColor("#ffffff");
                selectedPlaylist.put(tagName, 2);
            }
            else if (hashMap.containsKey(tagName) && status == 0) {
                continue;
            }
            else if (hashMap.containsKey(tagName) && status == 1) {
                tag.layoutColor = Color.parseColor("#3F51B5");
                tag.tagTextColor = Color.parseColor("#ffffff");
                selectedPlaylist.put(tagName, 1);
            }
            else if (!hashMap.containsKey(tagName) && status == 0) {
                tag.layoutColor = Color.parseColor("#ffffff");
            }
            else {
                tag.layoutColor = Color.parseColor("#ffffff");
            }
            tags.add(tag);
        }
        tagGroup.addTags(tags);
    }

    private void convertHashmapToString() {
        Iterator it = selectedPlaylist.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            str = str + pair.getKey() + ",";
            it.remove();
        }
        Log.d(TAG, "" + str);
    }

    private void openDialog(){
        LayoutInflater inflater = LayoutInflater.from(SelectPlaylistActivity.this);
        View subView = inflater.inflate(R.layout.dialog_layout, null);
        final EditText subEditText = (EditText)subView.findViewById(R.id.dialogEditText);
        final MainTextView textInfo = (MainTextView) subEditText.findViewById(R.id.dialog_placeholder);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("AlertDialog");
        builder.setMessage("AlertDialog Message");
        builder.setView(subView);
        AlertDialog alertDialog = builder.create();

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                skillFactoryHashMap.put(subEditText.getText().toString(), 0);
                tagGroup = (TagView) findViewById(R.id.tag_group);
                tags = new ArrayList<>();
                setTags();
                Toast.makeText(SelectPlaylistActivity.this, "Created", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(SelectPlaylistActivity.this, "Cancel", Toast.LENGTH_LONG).show();
            }
        });

        builder.show();
    }
}
