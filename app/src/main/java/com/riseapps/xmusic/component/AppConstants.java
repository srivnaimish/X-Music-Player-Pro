package com.riseapps.xmusic.component;

import android.widget.Toast;

import com.riseapps.xmusic.R;

/**
 * Created by naimish on 3/7/17.
 */

public class AppConstants {
    public static final String ITEM_SKU[] = {"", "", "theme_3", "theme_4", "theme_5", "theme_6", "theme_7", "theme_8"};

    public static final String THEME_URL="https://xmusicplayerpro.github.io/musicplayer/themes.html";

    public static boolean theme3 = false;
    public static boolean theme4 = false;
    public static boolean theme5 = false;
    public static boolean theme6 = false;
    public static boolean theme7 = false;
    public static boolean theme8 = false;

    public static int[] backgroundColors={R.color.colorPrimary, R.color.colorPrimaryInverse, R.color.colorPrimary2, R.color.colorPrimary3,
            R.color.colorPrimary4, R.color.colorPrimary5, R.color.colorPrimary6, R.color.colorPrimary7};

    public static int[] textColors={R.color.textColorPrimary, R.color.textColorPrimaryInverse, R.color.textColorPrimary2, R.color.textColorPrimary3,
            R.color.textColorPrimary4, R.color.textColorPrimary5, R.color.textColorPrimary6, R.color.textColorPrimary7};

    public static String[] texts={"Light", "Dark", "Walnut", "Matterhorn", "Orchid White", "Pickled Bluewood", "Lochmara", "Karry"};

    public static int[] encrypted = {88,87,90,96,96,74,84,80,81,69,66,49,61,
                                     53,58,101,112,55,133,92,82,83,108,102,84,
                                     95,92,97,104,81,141,95,94,107,96,98,86,57,
                                     88,97,104,81,80,95,61,100,111,111,80,50,130,
                                     70,100,65,92,79,134,58,100,77,91,106,93,77,78,
                                     58,68,138,64,137,122,78,68,57,105,113,104,87,129,
                                     52,53,105,48,53,86,137,82,76,106,71,87,140,134,87,
                                     130,117,132,59,99,50,62,65,51,113,139,96,81,101,84,
                                     89,95,114,120,78,56,136,103,64,96,81,103,137,48,101,
                                     61,88,140,137,133,110,123,107,87,138,59,106,125,71,87,
                                     52,75,49,58,52,71,49,58,58,111,56,100,57,55,75,62,108,
                                     134,140,84,52,57,85,71,89,61,73,83,85,108,135,94,67,61,
                                     83,128,79,50,109,66,119,94,134,66,67,91,108,60,77,120,48,
                                     99,117,140,55,97,90,49,76,62,113,60,100,90,50,93,111,80,80,
                                     49,66,140,75,128,97,92,56,96,91,105,104,78,113,66,109,60,80,
                                     48,70,65,58,81,75,83,143,51,139,82,73,109,80,77,76,85,64,70,
                                     74,56,73,55,83,59,77,83,111,128,76,103,82,87,117,92,108,100,67,
                                     88,80,80,87,126,53,89,134,64,115,70,50,122,111,105,67,71,80,82,79,
                                     62,108,95,58,126,110,65,110,83,83,102,137,97,102,124,82,99,55,89,79,
                                     92,73,60,102,94,55,134,143,66,105,97,110,130,76,93,58,100,73,61,89,134,
                                     85,87,52,70,72,77,108,71,118,109,80,135,79,50,80,76,109,89,102,56,110,74,
                                     109,102,95,73,111,75,108,122,80,89,141,65,50,97,49,65,87,63,79,61,100,95,106,
                                     93,77,98,48,107,91,92,78,98,70,87,106,68,71,55,111,90,102,104,81,84,92};

    public static String decrypt(int[] input) {
        String output = "";
        String key = "encrypt";
        for(int i = 0; i < input.length; i++) {
            output += (char) ((input[i] - 48) ^ (int) key.charAt(i % (key.length() - 1)));
        }
        return output;
    }

}
