package com.riseapps.xmusic.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Created by kanishk on 17/02/17.
 */

public class DipToPx {

    private DipToPx() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation");
    }

    public static int dipToPx(Context c, float dipValue) {
        DisplayMetrics metrics = c.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }
}
