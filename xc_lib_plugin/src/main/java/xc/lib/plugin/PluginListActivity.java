

package xc.lib.plugin;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import xc.lib.common.plugins.ComConstant;
import xc.lib.common.util.MethodInvoker;

public abstract class PluginListActivity extends ListActivity {
    private PluginComAcivityProxy proxy;

    @Override
    protected void attachBaseContext(Context newBase) {
        proxy = new PluginComAcivityProxy(this);

        super.attachBaseContext(proxy.getBaseContext(newBase));
        proxy.resetIntent(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        proxy.resetIntent(this);

        proxy.onCreateBefore(savedInstanceState);
        super.onCreate(savedInstanceState);
        proxy.onCreateAfter(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        proxy.onDestory();

        super.onDestroy();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        proxy.onRestoreInstanceState(savedInstanceState);

        try {
            super.onRestoreInstanceState(savedInstanceState);
        } catch (Throwable e) {

        }
    }


    @Override
    public void startActivity(Intent intent) {
        MethodInvoker startActivityInvoke = new MethodInvoker(this.getClass().getClassLoader(), ComConstant.PluginProxy, "startActivity",
                new Class<?>[]{Intent.class, Activity.class});
        Intent newIntent = (Intent) startActivityInvoke.call(null, intent, this);

//        Log.e("ssaas","getPackageName()-"+inten.getComponent().getPackageName()+"-getClassName-"+inten.getComponent().getClassName());


        super.startActivity(newIntent);

    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        MethodInvoker startActivityInvoke = new MethodInvoker(this.getClass().getClassLoader(), ComConstant.PluginProxy, "startActivityForResult",
                new Class<?>[]{Intent.class});
        Intent newIntent = (Intent) startActivityInvoke.call(null, intent);


        super.startActivityForResult(newIntent, requestCode);
    }
}
