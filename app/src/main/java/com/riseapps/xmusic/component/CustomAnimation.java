package com.riseapps.xmusic.component;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.riseapps.xmusic.R;

/**
 * Created by naimish on 15/3/17.
 */

public class CustomAnimation {

    View V;


    public Animation likeAnimation(Context context){
        return AnimationUtils.loadAnimation(context, R.anim.like);
    }

    public Animation big_likeAnimation(Context context){
        return AnimationUtils.loadAnimation(context, R.anim.big_like);
    }

    public Animation slide_up(Context context){
        return AnimationUtils.loadAnimation(context, R.anim.slide_up);
    }

    public Animation slide_down(Context context){
        return AnimationUtils.loadAnimation(context, R.anim.slide_down);
    }

}
