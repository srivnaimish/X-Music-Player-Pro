package com.riseapps.xmusic.component.TagToken.customviews;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.riseapps.xmusic.R;
import com.riseapps.xmusic.model.xplayertags.TagClass;

/**
 * Created by kanishk on 21/02/17.
 */

public class TagCompletionView extends TokenCompleteTextView<TagClass> {

    public TagCompletionView(Context context) {
        super(context);
    }

    public TagCompletionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TagCompletionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected View getViewForObject(TagClass tagClass) {
        LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        TokenTextView token = (TokenTextView) l.inflate(R.layout.contact_token, (ViewGroup) getParent(), false);
        token.setText(tagClass.getName());
        return token;
    }

    @Override
    protected TagClass defaultObject(String completionText) {
        //Stupid simple example of guessing if we have an email or not
        int index = completionText.indexOf('@');
        if (index == -1) {
            return new TagClass(completionText, completionText.replace(" ", " "));
        } else {
            return new TagClass(completionText.substring(0, index), completionText);
        }
    }


}
