package xc.lib.plugin;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import xc.lib.common.plugins.ComConstant;
import xc.lib.common.plugins.PluginContext;
import xc.lib.common.util.MethodInvoker;

public class PluginService extends Service {


    @Override
    protected void attachBaseContext(Context base) {

        PluginContext pc = null;
        String clsname = this.getClass().getName();
        String pkgname = this.getClass().getPackage().getName();


        Log.e("asdasasd", "TestService-attachBaseContext-clsname-" + clsname + "-pkgname-" + pkgname);

        MethodInvoker createActivityContext = new MethodInvoker(this.getClass().getClassLoader(), ComConstant.PluginProxy, "getResource",
                new Class<?>[]{String.class, String.class});
        Resources res = (Resources) createActivityContext.call(null, pkgname, clsname);
        pc = new PluginContext(base, android.R.style.Theme,
                this.getClass().getClassLoader(), res, pkgname,clsname);

        super.attachBaseContext(pc);
    }

    @Override
    public Context getBaseContext() {

        return super.getBaseContext();
    }

    @Override
    public void onCreate() {

        String clsName = this.getClass().getName();
        String pkgname = this.getClass().getPackage().getName();
        MethodInvoker setPitStateByKeyInvoke = new MethodInvoker(this.getClass().getClassLoader(), ComConstant.PluginProxy, "setServicePitStateByKey",
                new Class<?>[]{String.class,String.class,String.class, Service.class});
        setPitStateByKeyInvoke.call(null,pkgname,clsName,
                ComConstant.ActivityRunning,this);


        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {

        // 释放坑位
        String clsName = this.getClass().getName();
        String pkgname = this.getClass().getPackage().getName();
        MethodInvoker setPitStateByKeyInvoke = new MethodInvoker(this.getClass().getClassLoader(), ComConstant.PluginProxy, "setServicePitStateByKey",
                new Class<?>[]{String.class,String.class,String.class, Service.class});
        setPitStateByKeyInvoke.call(null,pkgname,clsName,
                ComConstant.ActivityDestory,this);

        super.onDestroy();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {


        return null;
    }

    @Nullable
    @Override
    public boolean onUnbind(Intent intent) {


        return super.onUnbind(intent);
    }

}
