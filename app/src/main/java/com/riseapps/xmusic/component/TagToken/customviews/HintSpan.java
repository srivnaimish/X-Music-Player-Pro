package com.riseapps.xmusic.component.TagToken.customviews;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.text.style.TextAppearanceSpan;

/**
 * Created by kanishk on 21/02/17.
 */

@SuppressLint("ParcelCreator")
public class HintSpan extends TextAppearanceSpan {
    public HintSpan(String family, int style, int size, ColorStateList color, ColorStateList linkColor) {
        super(family, style, size, color, linkColor);
    }
}