package com.riseapps.xmusic.utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by kanishk on 04/05/17.
 */

public class RandomAlbumArt {

    private ArrayList<String> albumArt = new ArrayList<>();
    private Random randomGenerator;

    public RandomAlbumArt() {
        albumArt.add("https://cdn.pixabay.com/photo/2016/09/08/21/09/piano-1655558_960_720.jpg");
        albumArt.add("https://cdn.pixabay.com/photo/2015/06/16/16/48/guitar-811343_960_720.jpg");
        albumArt.add("https://cdn.pixabay.com/photo/2014/09/14/20/24/guitar-445387_960_720.jpg");
        albumArt.add("https://cdn.pixabay.com/photo/2016/12/01/07/30/music-1874621_960_720.jpg");
        albumArt.add("https://cdn.pixabay.com/photo/2016/04/19/05/07/turntable-1337986_960_720.jpg");
        albumArt.add("https://cdn.pixabay.com/photo/2014/05/21/15/18/musician-349790_960_720.jpg");
        albumArt.add("https://cdn.pixabay.com/photo/2015/01/20/12/51/mobile-605422_960_720.jpg");
        albumArt.add("https://cdn.pixabay.com/photo/2017/01/19/01/58/drum-1991366_960_720.jpg");
        albumArt.add("https://cdn.pixabay.com/photo/2017/03/16/00/10/dj-2147859_960_720.jpg");
        albumArt.add("https://cdn.pixabay.com/photo/2017/03/16/18/17/music-2149880_960_720.jpg");
        albumArt.add("https://cdn.pixabay.com/photo/2016/11/22/18/56/audience-1850022_960_720.jpg");

    }

    public String getArt() {
        randomGenerator = new Random();
        int randomNumber = randomGenerator.nextInt(albumArt.size());
        Log.d("random", String.valueOf(randomNumber));
        if (randomNumber > 0) {
            return albumArt.get(randomNumber);
        }
        return albumArt.get(3);
    }
}
