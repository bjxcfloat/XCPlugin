package xc.lib.plugin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import xc.lib.common.plugins.ComConstant;
import xc.lib.common.plugins.PluginContext;
import xc.lib.common.util.MethodInvoker;

import java.util.Set;


// Activity的通用代理类
public final class PluginComAcivityProxy {

    private Activity act;
    private Intent newIntent;

    public PluginComAcivityProxy(Activity act) {
        this.act = act;
    }

    // 处理intent丢失的情况
    public void resetIntent(Activity act)
    {
        act.setIntent(newIntent);
    }

    public Context getBaseContext(Context newBase) {


        String clsName = this.act.getClass().getName();
        MethodInvoker getIntentInvoke = new MethodInvoker(this.act.getClass().getClassLoader(), ComConstant.PluginProxy, "getActivityIntent",
                new Class<?>[]{String.class});
        Intent intent = (Intent) getIntentInvoke.call(null, clsName);
        this.act.setIntent(intent);
//        Log.e("xasdsad","getBaseContext-"+ new Gson().toJson(this.act.getIntent()));

        newIntent = intent;

        String pluginName =  intent.getStringExtra(ComConstant.PluginName);
        int themeid =intent.getIntExtra(ComConstant.ThemeId,android.R.style.Theme);


        PluginContext pc = null;

        MethodInvoker createActivityContext = new MethodInvoker(this.act.getClass().getClassLoader(), ComConstant.PluginProxy, "getResource",
                new Class<?>[]{String.class, String.class});
        Resources res = (Resources) createActivityContext.call(null, pluginName, clsName);

//        Log.e("adasdasd","pluginName-"+pluginName+"-clsName-"+clsName+"-res-"+res==null?"res==null":"res !=null");

        pc = new PluginContext(newBase, themeid,
                this.act.getClass().getClassLoader(), res, pluginName,clsName);
        return pc;


    }

    public void onDestory()
    {
        String clsName = this.act.getClass().getName();
        MethodInvoker setPitStateByKeyInvoke = new MethodInvoker(this.act.getClass().getClassLoader(), ComConstant.PluginProxy, "setPitStateByKey",
                new Class<?>[]{Integer.class,String.class,Integer.class,Activity.class});
        setPitStateByKeyInvoke.call(null,newIntent.getIntExtra(ComConstant.PitActivityKey,-1),
                ComConstant.ActivityDestory,newIntent.getIntExtra(ComConstant.LauchMode,-1),this.act);
    }

    public void onCreateBefore(Bundle savedInstanceState) {

        String clsName = this.act.getClass().getName();
        MethodInvoker setPitStateByKeyInvoke = new MethodInvoker(this.act.getClass().getClassLoader(), ComConstant.PluginProxy, "setPitStateByKey",
                new Class<?>[]{Integer.class,String.class,Integer.class,Activity.class});
        setPitStateByKeyInvoke.call(null,newIntent.getIntExtra(ComConstant.PitActivityKey,-1),
                ComConstant.ActivityRunning,newIntent.getIntExtra(ComConstant.LauchMode,-1),this.act);


        // 对FragmentActivity做特殊处理
        if (savedInstanceState != null) {
            //
            savedInstanceState.setClassLoader(this.act.getClass().getClassLoader());
            //
            try {
                savedInstanceState.remove("android:support:fragments");
            } catch (Throwable e) {
//                if (LOGR) {
//                    LogRelease.e(PLUGIN_TAG, "a.c.b1: " + e.getMessage(), e);
//                }
            }
        }

        // 对FragmentActivity做特殊处理
        Intent intent = this.act.getIntent();
        if (intent != null) {
            intent.setExtrasClassLoader(this.act.getClass().getClassLoader());
            int themeid = newIntent.getIntExtra(ComConstant.ThemeId,android.R.style.Theme);
            this.act.setTheme(themeid);
        }
    }

    public void onCreateAfter(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            savedInstanceState.setClassLoader(this.act.getClass().getClassLoader());
        }
        Intent intent = this.act.getIntent();
        if (intent != null) {

            intent.setExtrasClassLoader(this.act.getClass().getClassLoader());
        }
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            savedInstanceState.setClassLoader(this.act.getClass().getClassLoader());
            // 二级修正
            Set<String> set = savedInstanceState.keySet();
            if (set != null) {
                for (String key : set) {
                    Object obj = savedInstanceState.get(key);
                    if (obj instanceof Bundle) {
                        ((Bundle) obj).setClassLoader(this.act.getClass().getClassLoader());
                    }
                }
            }
        }
    }

}
