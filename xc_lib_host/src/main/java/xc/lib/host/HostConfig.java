package xc.lib.host;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

// 宿主配置信息
public final class HostConfig {

    public static int StandardActivityPitCount = 0;
    public static int SingleTopActivityPitCount = 0;
    public static int SingleTaskActivityPitCount = 0;
    public static int SingleInstanceActivityPitCount = 0;
    public static int ServicePitCount = 0;
    public static boolean IsUseAppCompat = false;

//    public HostConfig()
//    {
//
//    }


    public static void load() throws PackageManager.NameNotFoundException {
        ApplicationInfo appInfo = BaseApplication.getBaseApp().getBaseContext().getPackageManager().getApplicationInfo(
                BaseApplication.getBaseApp().getBaseContext().getPackageName()
                , PackageManager.GET_META_DATA);
        StandardActivityPitCount =  appInfo.metaData.getInt(Constant.StandardActivityPitCount);
        SingleTopActivityPitCount =  appInfo.metaData.getInt(Constant.SingleTopActivityPitCount);
        SingleTaskActivityPitCount =  appInfo.metaData.getInt(Constant.SingleTaskActivityPitCount);
        SingleInstanceActivityPitCount =  appInfo.metaData.getInt(Constant.SingleInstanceActivityPitCount);
        ServicePitCount =  appInfo.metaData.getInt(Constant.ServicePitCount);
        IsUseAppCompat =  appInfo.metaData.getBoolean(Constant.IsUseAppCompat);

    }


}
