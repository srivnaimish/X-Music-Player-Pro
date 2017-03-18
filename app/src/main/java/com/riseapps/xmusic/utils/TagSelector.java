package com.riseapps.xmusic.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;

import com.riseapps.xmusic.model.Pojo.Tag;


/**
 * Created by kanishk on 17/02/17.
 */

public class TagSelector {

    private Context ctx;

    public TagSelector(Context context) {
        this.ctx = context;
    }

    public Drawable getNormalSelector(Tag tag) {
        if (tag.background != null)
            return tag.background;
        StateListDrawable states = new StateListDrawable();
        GradientDrawable gdNormal = new GradientDrawable();
        gdNormal.setColor(tag.layoutColor);

        gdNormal.setCornerRadius(tag.radius);
        if (tag.layoutBorderSize > 0) {
            gdNormal.setStroke(DipToPx.dipToPx(ctx, tag.layoutBorderSize), tag.layoutBorderColor);
        }
        states.addState(new int[]{}, gdNormal);
        return states;
    }

    public Drawable getSelector(Tag tag) {
        if (tag.background != null)
            return tag.background;
        StateListDrawable states = new StateListDrawable();
        GradientDrawable gdNormal = new GradientDrawable();
        gdNormal.setColor(tag.layoutColor);
        gdNormal.setCornerRadius(tag.radius);
        if (tag.layoutBorderSize > 0) {
            gdNormal.setStroke(DipToPx.dipToPx(ctx, tag.layoutBorderSize), tag.layoutBorderColor);
        }
        GradientDrawable gdPress = new GradientDrawable();
        gdPress.setColor(tag.layoutColorPress);
        gdPress.setCornerRadius(tag.radius);
        states.addState(new int[]{android.R.attr.state_pressed}, gdPress);
        states.addState(new int[]{}, gdPress);
        return states;
    }

    public Drawable getLongSelector(Tag tag) {
        if (tag.background != null)
            return tag.background;
        StateListDrawable states = new StateListDrawable();
        GradientDrawable gdNormal = new GradientDrawable();
        gdNormal.setColor(tag.layoutColor);
        gdNormal.setCornerRadius(tag.radius);
        if (tag.layoutBorderSize > 0) {
            gdNormal.setStroke(DipToPx.dipToPx(ctx, tag.layoutBorderSize), tag.layoutBorderColor);
        }
        GradientDrawable gdPress = new GradientDrawable();
        gdPress.setColor(tag.layoutColorLongPress);
        gdPress.setCornerRadius(tag.radius);
        states.addState(new int[]{android.R.attr.state_pressed}, gdPress);
        states.addState(new int[]{}, gdPress);
        return states;
    }
}
