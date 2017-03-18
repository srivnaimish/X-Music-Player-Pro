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
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.riseapps.xmusic.R;
import com.riseapps.xmusic.component.TagToken.customviews.TokenCompleteTextView;
import com.riseapps.xmusic.model.Pojo.Tag;
import com.riseapps.xmusic.model.xplayertags.TagClass;
import com.riseapps.xmusic.model.xplayertags.TagViewData;
import com.riseapps.xmusic.utils.TagSelector;
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

    Tag tag;
    ArrayList<Tag> tags = new ArrayList<>();
    TagView tagGroup;

    private HashMap<String, Integer> otherSkills = new HashMap<>();
    private HashMap<String, Integer> hashMap = new HashMap<>();
    private HashMap<String, Integer> skillFactoryHashMap = new HashMap<>();

    // utils
    private TagSelector tagSelector = new TagSelector(SelectPlaylistActivity.this);

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
                Intent i = new Intent();
                setResult(RESULT_CANCELED, i);
                finish();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
        otherSkills.put("A", 0);
        tagGroup.setOnTagLongClickListener(new TagView.OnTagLongClickListener() {
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
        });

        tagGroup.setOnTagClickListener(new TagView.OnTagClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onTagClick(View view, TextView tagView, Tag tag, int position) {

                if (otherSkills.get(tag.text) == null) {
                    view.setBackground(tagSelector.getSelector(tag));
                    tagView.setTextColor(getResources().getColor(R.color.colorWhite));
                    otherSkills.put(tag.text, 2);
                } else {
                    // reset same tag
                    tagView.setTextColor(getResources().getColor(R.color.colorBlack));
                    tag.layoutColor = Color.parseColor("#FFFFFF");
                    tag.tagTextColor = Color.parseColor("#000000");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        view.setBackground(tagSelector.getNormalSelector(tag));
                    }
                    otherSkills.remove(tag.text);
                }
            }
        });

        tagGroup.setOnTagDeleteListener(new TagView.OnTagDeleteListener() {
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
        });
    }

    private void prepareTags() throws Exception {
        JSONArray jsonArray;
        JSONObject temp;
        try {
            jsonArray = new JSONArray(TagViewData.COUNTRIES);
            for (int i = 0; i < jsonArray.length(); i++) {
                temp = jsonArray.getJSONObject(i);
                skillFactoryHashMap.put(temp.getString("name"), 0);
            }
        } catch (Exception e) {
            Log.d("error ", e.toString());
        }
    }

    private void setTags() {
        skillFactoryHashMap.putAll(hashMap);
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
                otherSkills.put(tagName, 2);
            }
            else if (hashMap.containsKey(tagName) && status == 0) {
                continue;
            }
            else if (hashMap.containsKey(tagName) && status == 1) {
                tag.layoutColor = Color.parseColor("#3F51B5");
                tag.tagTextColor = Color.parseColor("#ffffff");
                otherSkills.put(tagName, 1);
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

}
