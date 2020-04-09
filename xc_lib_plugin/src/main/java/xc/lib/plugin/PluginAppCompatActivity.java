package xc.lib.plugin;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import xc.lib.common.plugins.ComConstant;
import xc.lib.common.util.MethodInvoker;

import java.lang.reflect.Field;
import java.util.Set;

public abstract class PluginAppCompatActivity extends AppCompatActivity {

    private PluginComAcivityProxy proxy;

    @Override
    protected void attachBaseContext(Context newBase) {

        proxy = new PluginComAcivityProxy(this);
        Context ctx = proxy.getBaseContext(newBase);

        super.attachBaseContext(ctx);
        proxy.resetIntent(this);
    }

    @Override
    public Context getBaseContext() {

        return super.getBaseContext();
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
//        Log.e("xasdsad", "onDestroy-" + new Gson().toJson(getIntent()));

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
        startActivityForResult(intent, requestCode, null);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {

        MethodInvoker startActivityInvoke = new MethodInvoker(this.getClass().getClassLoader(), ComConstant.PluginProxy, "startActivityForResult",
                new Class<?>[]{Intent.class});
        Intent newIntent = (Intent) startActivityInvoke.call(null, intent);

        if (Build.VERSION.SDK_INT >= 16) {
            super.startActivityForResult(newIntent, requestCode, options);
        } else {
            super.startActivityForResult(newIntent, requestCode);
        }
    }


    @Override
    public void startActivityFromFragment(Fragment fragment, Intent intent, int requestCode) {
        if (requestCode == -1) {
            startActivityForResult(intent, -1);
        } else if ((requestCode & -65536) != 0) {
            throw new IllegalArgumentException("Can only use lower 16 bits for requestCode");
        } else {
            int newRequestCode = -1;
            try {
                Field f = Fragment.class.getDeclaredField("mIndex");
                boolean acc = f.isAccessible();
                if (!acc) {
                    f.setAccessible(true);
                }
                Object o = f.get(fragment);
                if (!acc) {
                    f.setAccessible(acc);
                }
                int index = (Integer) o;
                newRequestCode = ((index + 1) << 16) + (requestCode & '\uffff');
            } catch (Throwable e) {
                // Do Noting
            }
            startActivityForResult(intent, newRequestCode);
        }
    }

    @Override
    public String getPackageCodePath() {
        return super.getPackageCodePath();
    }
}
