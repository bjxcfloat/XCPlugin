package xc.lib.host;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import xc.lib.host.activity.ActivityPitManager;

public class BaseApplication extends Application{

   private static BaseApplication baseApp;

    public static BaseApplication getBaseApp() {
        return baseApp;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        baseApp = this;
        try {
            PluginManager.getInstance().init(this);
            ActivityPitManager.getInstance();

        } catch (PackageManager.NameNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        // 管理插件Application
        try {
            List<Application> applications = PluginManager.getInstance().getInstalledPluginApplication();

            for (Application a : applications) {
                a.onLowMemory();
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        try {
            List<Application> applications = PluginManager.getInstance().getInstalledPluginApplication();

            for (Application a : applications) {
                a.onTrimMemory(level);
            }
        } catch (Exception e) {

        }
    }


}
