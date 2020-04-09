package xc.lib.host;

import android.app.Application;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;

import java.util.HashMap;
import java.util.Map;

import xc.lib.host.activity.ActivityPitManager;
import xc.lib.host.broadcast.ReceiverInfo;
import xc.lib.host.contentprovider.ContentProviderManager;
import xc.lib.host.service.ServicePitManager;

// 存储插件信息
public class PluginInfo {

    public Resources resources;
    public HashMap<String, ActivityInfo> activityInfos;
    public HashMap<String, ReceiverInfo> receiverInfos ;
    public HashMap<String, ServiceInfo> serviceInfos;
    public HashMap<String, ProviderInfo> providerInfos;


    // apk安装路径
    public String pluginInstallPath = "";
    // 临时目录
    public String tempDir="";
    public String optdir="";
    // 单个插件应用包根目录
    public String packagePath="";
    public String pluginRootDir = "";
    public String pkgName = "";
    public String versionName = "";
    public int versionCode = -1;
    public ApplicationInfo appInfo ;
    public int themeId = -1;
    public PluginDexClassLoader clsLoader;
    public Application application;
    public String appClass = "";

    public int getLauchMode(String clsName)
    {
        if(activityInfos==null)
        {
            throw new IllegalArgumentException("activityInfos is null");
        }

        int lancumode =  activityInfos.get(clsName).launchMode;


        return lancumode;
    }


    public void recycle()
    {
        resources = null;
        if(activityInfos!=null&&activityInfos.size()>0) {

            for (Map.Entry<String, ActivityInfo> entry : activityInfos.entrySet()) {
                if (entry.getValue()!=null) {

                     ActivityPitManager.getInstance().recycleActivityPitByClsName(entry.getValue().name);
                }

            }

            activityInfos.clear();
        }
        if(receiverInfos!=null&&receiverInfos.size()>0)
            receiverInfos.clear();
        if(serviceInfos!=null&&serviceInfos.size()>0) {

            ServicePitManager.getInstance().forceDisposeServicePit(serviceInfos);

            serviceInfos.clear();
        }
        if(providerInfos!=null&&providerInfos.size()>0)
        {
            ContentProviderManager.getInstance().disposeContentProvider(providerInfos);
            providerInfos.clear();
        }

        appInfo = null;
        clsLoader = null;
        application = null;

    }

}
