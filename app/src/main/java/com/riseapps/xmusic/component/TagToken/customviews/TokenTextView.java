package com.riseapps.xmusic.component.TagToken.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.riseapps.xmusic.R;


/**
 * Created by kanishk on 21/02/17.
 */

public class TokenTextView extends TextView {

    public TokenTextView(Context context) {
        super(context);
    }

    public TokenTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        setCompoundDrawablesWithIntrinsicBounds(0, 0, selected ? R.drawable.ic_close_grey600_24dp : 0, 0);
        if (selected) {
            setTextColor(getResources().getColor(R.color.colorBlack));
            //mListener.onSelected(getText().toString());
            //Toast.makeText(getContext(), " " + getText(), Toast.LENGTH_SHORT).show();
            //mListener.onSelected(getText().toString());
        }
        else {
            setTextColor(getResources().getColor(R.color.colorWhite));
        }
    }
}