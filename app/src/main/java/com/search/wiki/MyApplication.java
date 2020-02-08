package com.search.wiki;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;

import com.search.wiki.utils.MyPrefs;

public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        init();
    }

    private void init() {
        context = this;
        new MyPrefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
    }

    public static Context getContext() {
        return context;
    }
}
