package com.riseapps.xmusic.executor;

import java.util.ArrayList;

/**
 * Created by naimish on 24/6/17.
 */

public class RecentQueue {
    public ArrayList<Long> pushPop(ArrayList<Long> list, long id) {

        if (!list.contains(id)) {
            if (list.size() > 19) {
                list.remove(0);
                list.add(id);
            } else {
                list.add(id);
            }
        }
        return list;
    }
}
