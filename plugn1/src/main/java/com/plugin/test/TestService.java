package com.plugin.test;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.plugin.test.pp.test;

import xc.lib.plugin.PluginService;

public class TestService extends PluginService {

    @Override
    public void onCreate()
    {
        Log.e("asdasasd","TestService-onCreate");
        super.onCreate();

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {


        Log.e("asdasasd","TestService-onStartCommand-kk-"+ intent.getStringExtra("kk"));

      return   super.onStartCommand(  intent,   flags,   startId);
    }


    @Override
    public void onDestroy()
    {

        Log.e("asdasasd","TestService-onDestroy");

        super.onDestroy();

    }

    public void SHow()
    {
        Util.toast(this,"connection success-"+ test.get(0));
    }
    public void disconnect()
    {
        Util.toast(this,"disconnect success");
    }

    private MyBinder myBinder = new MyBinder();
    public class MyBinder extends Binder {

        TestService getService() {
            return TestService.this;
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {


        Log.e("asdasasd","TestService-onBind-"+intent.getComponent().getClassName());
        return myBinder;
    }


}
