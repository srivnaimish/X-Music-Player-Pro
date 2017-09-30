package com.riseapps.xmusic.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.widget.EditText;

import com.riseapps.xmusic.R;

/**
 * Created by naimish on 3/4/17.
 */

public class MainEditText extends EditText {
    public MainEditText(Context context) {
        super(context);
        applyFont(context);
    }

    public MainEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyFont(context);
    }

    public MainEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MainEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        applyFont(context);
    }

    private void applyFont(Context context) {
        Typeface font = ResourcesCompat.getFont(context, R.font.meerainimal);
        setTypeface(font);
    }
}
