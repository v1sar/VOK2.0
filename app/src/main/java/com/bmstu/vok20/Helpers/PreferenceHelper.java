package com.bmstu.vok20.Helpers;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by qwerty on 14.12.16.
 */

public class PreferenceHelper {

    public static final String ENABLE_INVISIBLE = "enable_invisible";
    public static final String BACKGROUND_COLOR = "background_color";

    private static PreferenceHelper instance;

    private Context context;

    private SharedPreferences preferences;

    private PreferenceHelper() {

    }

    public static PreferenceHelper getInstance() {
        if(instance==null) {
            instance = new PreferenceHelper();
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
    }

    public void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key,value);
        editor.apply();
    }

    public boolean getBoolean(String key){
           return preferences.getBoolean(key,false);
    }

    public void putInt(String key, int value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key,value);
        editor.apply();
    }

    public int getInt(String key) {
        return preferences.getInt(key, 0xFFFFFFFF);
    }

}
