package com.riseapps.xmusic.component;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.riseapps.xmusic.R;

/**
 * Created by naimish on 15/3/17.
 */

public class CustomAnimation {

    public Animation slideShow(Context context){
        return AnimationUtils.loadAnimation(context, R.anim.main_player_show);
    }

    public Animation slideHide(Context context){
        return AnimationUtils.loadAnimation(context, R.anim.main_player_hide);
    }

    public Animation likeAnimation(Context context){
        return AnimationUtils.loadAnimation(context, R.anim.like);
    }
}
