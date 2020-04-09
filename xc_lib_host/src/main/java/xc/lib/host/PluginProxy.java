package xc.lib.host;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;

import xc.lib.host.activity.ActivityPitManager;
import xc.lib.host.service.ServicePitManager;

//供插件调用的代理类
public final class PluginProxy {

    public static Intent getActivityIntent(String clsName) {
        PitInfo pi = ActivityPitManager.getInstance().getLatestPitInfoByRealClsName(clsName);
        return pi.intent;
    }

    public static  Intent startActivity(Intent intent,Activity act)
    {
        return  PluginManager.getInstance().getWrapperIntent(intent);
    }

    public static  Intent getServiceWrapperIntent(Intent intent)
    {
        return  PluginManager.getInstance().getServiceWrapperIntent(intent);
    }

    public static  Intent startActivityForResult(Intent intent)
    {
        return  PluginManager.getInstance().getWrapperIntent(intent);
    }

    public static String getPluginApkPath(String pluginName)
    {

        return  PluginManager.getInstance().getPluginInfo(pluginName).pluginInstallPath;
    }

    public static ApplicationInfo getPluginApplicationInfo(String pluginName)
    {

        return  PluginManager.getInstance().getPluginInfo(pluginName).appInfo;
    }

    public static Application getPluginApplication(String pluginName)
    {

        return  PluginManager.getInstance().getPluginInfo(pluginName).application;
    }

    public static void setPitStateByKey(Integer key, String runningState, Integer lauchmode, Activity act) {

//        Log.e("sdaads","clsName-"+clsName);
        ActivityPitManager.getInstance().setPitStateByKey(key, runningState, lauchmode,act);


    }
    public static void setServicePitStateByKey(String pkg,String cls, String runningState , Service act) {

//        Log.e("sdaads","clsName-"+clsName);
        ServicePitManager.getInstance().setServicePitStateByKey(  pkg,  cls,   runningState ,   act);


    }
    public static Resources getResource(String pluginName, String clsName) {


        return PluginManager.getInstance().getPluginInfo(pluginName).resources;


    }


}

//            PackageManager pm = BaseApplication.getBaseApp().getPackageManager();
//
//            PackageInfo mPackageInfo = pm.getPackageArchiveInfo(BaseApplication.pluginPath,
//                    PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES
//                            | PackageManager.GET_PROVIDERS | PackageManager.GET_RECEIVERS | PackageManager.GET_META_DATA);
//
//
//
//            if (TextUtils.isEmpty(mPackageInfo.applicationInfo.processName)) {
//                mPackageInfo.applicationInfo.processName = mPackageInfo.applicationInfo.packageName;
//            }
//            mPackageInfo.applicationInfo.sourceDir = BaseApplication.pluginPath;
//            mPackageInfo.applicationInfo.publicSourceDir = BaseApplication.pluginPath;

// 设置so库路径
//        mPackageInfo.applicationInfo.nativeLibraryDir = ld.getAbsolutePath();