package com.plugin.test;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class PluginApplication extends Application {


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Log.e("asdasasd","PluginApplication-attachBaseContext");
    }

    @Override
    public void onCreate() {
        super.onCreate();


        Util.toast(this,"this is a plugin application");
        Log.e("asdasasd","PluginApplication-oncratae");

    }

}
