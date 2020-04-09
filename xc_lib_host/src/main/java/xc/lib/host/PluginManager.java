package xc.lib.host;

import android.app.Activity;
import android.app.Application;
import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import xc.lib.common.plugins.ComConstant;
import xc.lib.common.plugins.PluginContext;
import xc.lib.common.task.Task;
import xc.lib.common.util.FileUtils;
import xc.lib.common.util.ReflectUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import xc.lib.host.activity.ActivityPitManager;
import xc.lib.host.broadcast.ReceiverInfo;
import xc.lib.host.broadcast.StaticBroadCastReceiverManager;
import xc.lib.host.contentprovider.ContentProviderInfo;
import xc.lib.host.contentprovider.ContentProviderManager;
import xc.lib.host.parser.ManifestParser;
import xc.lib.host.service.ServicePitManager;

// 插件管理总入口类
public final class PluginManager {

    private static PluginManager pluginManager = null;
    private static byte[] _lock = new byte[0];
    private Context appContext;
    private ConcurrentHashMap<String, PluginInfo> plugins = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();
    private PluginInstallListener installListener = null;

    private PluginManager() {

    }

    public void init(BaseApplication app) throws PackageManager.NameNotFoundException, NoSuchFieldException, IllegalAccessException {
//        hostConfig = new HostConfig();
        appContext = app;
        injectAppClassLoader();
        HostConfig.load();


    }

    public void setInstallListener(PluginInstallListener lis)
    {
        this.installListener = lis;
    }


    // 获取已安装的插件的Application
    public List<Application> getInstalledPluginApplication() {
        List<Application> applist = new ArrayList<>();
        if (plugins != null && plugins.size() > 0)
            for (Map.Entry<String, PluginInfo> entry : plugins.entrySet()) {
                if (entry.getValue().application != null)
                    applist.add(entry.getValue().application);

            }

        return applist;
    }

    public static PluginManager getInstance() {
        if (pluginManager == null) {
            synchronized (_lock) {
                if (pluginManager == null) {
                    pluginManager = new PluginManager();

                }
            }
        }
        return pluginManager;
    }


    public Class<?> loadClass(String pluginName, String clsName) throws ClassNotFoundException {

        PluginInfo pinfo = plugins.get(pluginName);
        return pinfo.clsLoader.loadClass(clsName, false);
    }

    public PluginInfo getPluginInfo(String pkgName) {
        return plugins.get(pkgName);
    }

    public Intent getWrapperIntent(Intent intent) {
        String clsName = intent.getComponent().getClassName();
        String pkgName = intent.getComponent().getPackageName();

        if (pkgName.equals(BaseApplication.getBaseApp().getPackageName())) {
            return intent;
        }
        PitInfo p = ActivityPitManager.getInstance().occupyActivityPit(pkgName, clsName);

        Intent newIntent = new Intent(intent);
        newIntent.setClassName(BaseApplication.getBaseApp().getPackageName(), p.pitClsName);
        newIntent.putExtra(ComConstant.RealActivity, p.realClsName);
        newIntent.putExtra(ComConstant.PluginName, p.pluginName);
//        newIntent.putExtra(ComConstant.TimeStamp,p.timestamp);
        newIntent.putExtra(ComConstant.PitActivityKey, p.pitKey);
        newIntent.putExtra(ComConstant.LauchMode, p.lauchmode);
        int actTheme = plugins.get(p.pluginName).activityInfos.get(p.realClsName).theme;

        newIntent.putExtra(ComConstant.ThemeId, actTheme > 0 ? actTheme : plugins.get(p.pluginName).themeId);

        p.intent = newIntent;

        ActivityPitManager.getInstance().setIntent(p);

        return newIntent;
    }

    public Intent getServiceWrapperIntent(Intent intent) {
        String clsName = intent.getComponent().getClassName();
        String pkgName = intent.getComponent().getPackageName();
        Log.e("asdasasd", "PluginContext-startService-getServiceWrapperIntent-clsname-" + clsName + "-pkgName-" + pkgName);
        if (pkgName.equals(BaseApplication.getBaseApp().getPackageName())) {
            return intent;
        }

        PitInfo p = ServicePitManager.getInstance().occupyServicePit(pkgName, clsName);

        Intent newIntent = new Intent(intent);
        newIntent.setClassName(BaseApplication.getBaseApp().getPackageName(), p.pitClsName);
        newIntent.putExtra(ComConstant.RealActivity, p.realClsName);
        newIntent.putExtra(ComConstant.PluginName, p.pluginName);
//        newIntent.putExtra(ComConstant.TimeStamp,p.timestamp);
        newIntent.putExtra(ComConstant.PitActivityKey, p.pitKey);

        return newIntent;
    }

    private boolean isHost(String pkgName) {
        return pkgName.equals(appContext.getPackageName());
    }

    // 开启服务,支持打开宿主或插件
    public void startService(Context ctx, Intent intent) {
        if (intent == null)
            throw new IllegalArgumentException("intent is not null");


        String clsName = intent.getComponent().getClassName();
        String pkgName = intent.getComponent().getPackageName();

        if (isHost(pkgName)) {
            ctx.startService(intent);
            return;
        }

        PitInfo p = ServicePitManager.getInstance().occupyServicePit(pkgName, clsName);

//        Log.e("asdsada",new Gson().toJson(p));

        Intent newIntent = new Intent(intent);
        newIntent.setClassName(BaseApplication.getBaseApp().getPackageName(), p.pitClsName);
        newIntent.putExtra(ComConstant.RealActivity, p.realClsName);
        newIntent.putExtra(ComConstant.PluginName, p.pluginName);
        newIntent.putExtra(ComConstant.PitActivityKey, p.pitKey);
        p.intent = newIntent;

        ctx.startService(newIntent);

    }

    // 开启Activity,支持打开宿主或插件
    public void startActivity(Context act, Intent intent) {
        if (intent == null)
            throw new IllegalArgumentException("intent is not null");


        String clsName = intent.getComponent().getClassName();
        String pkgName = intent.getComponent().getPackageName();
        if (isHost(pkgName)) {
            act.startActivity(intent);
            return;
        }

        PitInfo p = ActivityPitManager.getInstance().occupyActivityPit(pkgName, clsName);

//        Log.e("asdsada",new Gson().toJson(p));

        Intent newIntent = new Intent(intent);
        newIntent.setClassName(BaseApplication.getBaseApp().getPackageName(), p.pitClsName);
        newIntent.putExtra(ComConstant.RealActivity, p.realClsName);
        newIntent.putExtra(ComConstant.PluginName, p.pluginName);
//        newIntent.putExtra(ComConstant.TimeStamp,p.timestamp);
        newIntent.putExtra(ComConstant.PitActivityKey, p.pitKey);
        newIntent.putExtra(ComConstant.LauchMode, p.lauchmode);
        int actTheme = PluginManager.getInstance().plugins.get(p.pluginName).activityInfos.get(p.realClsName).theme;

        newIntent.putExtra(ComConstant.ThemeId, actTheme > 0 ? actTheme : PluginManager.getInstance().plugins.get(p.pluginName).themeId);

        p.intent = newIntent;

        ActivityPitManager.getInstance().setIntent(p);

        act.startActivity(newIntent);

//        Log.e("asdasd", "startActivity-clsName-" + clsName + "-pkgName-" + pkgName+"-"+new Gson().toJson(p));

    }

    public void startActivityForResult(Activity act,Intent intent, int requestCode )
    {
        if (intent == null)
            throw new IllegalArgumentException("intent is not null");


        String clsName = intent.getComponent().getClassName();
        String pkgName = intent.getComponent().getPackageName();
        if (isHost(pkgName)) {
            act.startActivityForResult(  intent,   requestCode);
            return;
        }

        PitInfo p = ActivityPitManager.getInstance().occupyActivityPit(pkgName, clsName);

//        Log.e("asdsada",new Gson().toJson(p));

        Intent newIntent = new Intent(intent);
        newIntent.setClassName(BaseApplication.getBaseApp().getPackageName(), p.pitClsName);
        newIntent.putExtra(ComConstant.RealActivity, p.realClsName);
        newIntent.putExtra(ComConstant.PluginName, p.pluginName);
//        newIntent.putExtra(ComConstant.TimeStamp,p.timestamp);
        newIntent.putExtra(ComConstant.PitActivityKey, p.pitKey);
        newIntent.putExtra(ComConstant.LauchMode, p.lauchmode);
        int actTheme = PluginManager.getInstance().plugins.get(p.pluginName).activityInfos.get(p.realClsName).theme;

        newIntent.putExtra(ComConstant.ThemeId, actTheme > 0 ? actTheme : PluginManager.getInstance().plugins.get(p.pluginName).themeId);

        p.intent = newIntent;

        ActivityPitManager.getInstance().setIntent(p);

        act.startActivityForResult(newIntent,requestCode);
    }


    // 此安装根据情况切换主子线程
    public void install(String path) {
        final PluginInfo pInfo = new PluginInfo();
        try {
            createPluginDirAndFile(path, pInfo);

            parseApkAndInitPluginContainer(pInfo.pluginInstallPath, pInfo);



            if (this.installListener != null) {

                Task.execOnUiThrad(new Runnable() {
                    @Override
                    public void run() {
                        installListener.onInstallSuccess(pInfo);
                    }
                });

            }
        } catch (final Exception e) {
            if (this.installListener != null) {
                Task.execOnUiThrad(new Runnable() {
                    @Override
                    public void run() {
                        installListener.onInstallFailure(e, pInfo);
                    }
                });
            }

        }
    }

    // 卸载某插件
    public void unInstall(String pluginName) throws IOException {

        if(plugins.containsKey(pluginName)) {
            //释放插件关联的资源对象
            plugins.get(pluginName).recycle();
            //删除插件相关物理文件
            FileUtils.delFile(new File(plugins.get(pluginName).packagePath));
            try {
                delPluginVersionInfoFromFile(plugins.get(pluginName));
            }
            catch (Exception e){}
            plugins.remove(pluginName);
        }
    }

    public void checkVersion() {

    }

    private void delPluginVersionInfoFromFile(PluginInfo pi) throws IOException {
        String configPath = pi.pluginRootDir + "/config.txt";
        File f = new File(configPath);
        if (f.exists()) {
            StringBuilder content = new StringBuilder();
            InputStream instream = new FileInputStream(f);
            if (instream != null) {
                InputStreamReader inputreader
                        = new InputStreamReader(instream, "UTF-8");
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line = "";
                //分行读取
                while ((line = buffreader.readLine()) != null) {
                    content.append(line);
                }
                instream.close();//关闭输入流
            }

            ArrayList<InstalledPluginConfig> configList = gson.fromJson(content.toString(), new TypeToken<ArrayList<InstalledPluginConfig>>() {
            }.getType());


            boolean isFinded = false;

            int index = -1;
            for (InstalledPluginConfig item : configList) {
                index++;
                if (item.PluginPkgName.equals(pi.pkgName)
                        && item.PluginApkPath.equals(pi.pluginInstallPath)) {

                    isFinded = true;
                    break;
                }

            }
            if(isFinded&&index>=0)
            {
                configList.remove(index);
            }
            f.delete();
            File f2 = new File(configPath);
            f2.createNewFile();
            RandomAccessFile raf = new RandomAccessFile(f2, "rwd");
            raf.seek(f2.length());
            raf.write(gson.toJson(configList).getBytes());
            raf.close();
        }


    }

    // 将安装成功的插件路径写到物理配置文件，下次不用走原来流程
    private void writeInstalledInfo(PluginInfo pInfo) throws IOException {

        String configPath = pInfo.pluginRootDir + "/config.txt";

        InstalledPluginConfig config = new InstalledPluginConfig();
        config.PluginApkPath = pInfo.pluginInstallPath;
        config.PluginOptPath = pInfo.optdir;
        config.PluginPkgName = pInfo.pkgName;
        config.VersionCode = pInfo.versionCode;
        config.VersionName = pInfo.versionName;
        ArrayList<InstalledPluginConfig> configList = new ArrayList();

        File f = new File(configPath);
        if (f.exists()) {

            StringBuilder content = new StringBuilder();
            InputStream instream = new FileInputStream(f);
            if (instream != null) {
                InputStreamReader inputreader
                        = new InputStreamReader(instream, "UTF-8");
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line = "";
                //分行读取
                while ((line = buffreader.readLine()) != null) {
                    content.append(line);
                }
                instream.close();//关闭输入流
            }

            configList = gson.fromJson(content.toString(), new TypeToken<ArrayList<InstalledPluginConfig>>() {
            }.getType());

            boolean isExists = false;

            for (InstalledPluginConfig item : configList) {

                if (item.PluginPkgName.equals(config.PluginPkgName)
                        && item.PluginApkPath.equals(config.PluginApkPath)) {

                    item.PluginApkPath = config.PluginApkPath;
                    item.PluginPkgName = config.PluginPkgName;
                    item.VersionName = config.VersionName;
                    item.VersionCode = config.VersionCode;
                    item.PluginOptPath = config.PluginOptPath;
                    isExists = true;
                    break;
                }
            }
            if (!isExists) {
                configList.add(config);
            }
            f.delete();
            File f2 = new File(configPath);
            f2.createNewFile();
            RandomAccessFile raf = new RandomAccessFile(f2, "rwd");
            raf.seek(f2.length());
            raf.write(gson.toJson(configList).getBytes());
            raf.close();

//            Log.e("asdasd", "gson.toJson(config)1-" + gson.toJson(configList));

        } else {
            configList.add(config);
//            Log.e("asdasd", "gson.toJson(config)2-" + gson.toJson(configList));
            f.createNewFile();
            RandomAccessFile raf = new RandomAccessFile(f, "rwd");
            raf.seek(f.length());
            raf.write(gson.toJson(configList).getBytes());
            raf.close();
        }


    }

    // 唯一注入点
    private void injectAppClassLoader() throws NoSuchFieldException, IllegalAccessException {
        Context oBase = BaseApplication.getBaseApp().getBaseContext();

        // 1. ApplicationContext - Android 2.1
        // 2. ContextImpl - Android 2.2 and higher
        // 3. AppContextImpl - Android 2.2 and higher
        Object oPackageInfo = ReflectUtils.readField(oBase, "mPackageInfo");

        ClassLoader oClassLoader = (ClassLoader) ReflectUtils.readField(oPackageInfo, "mClassLoader");

        ClassLoader cl = new XcPluginClassLoader(oClassLoader.getParent(), oClassLoader);

        // 将新的ClassLoader写入mPackageInfo.mClassLoader
        ReflectUtils.writeField(oPackageInfo, "mClassLoader", cl);


        // 防止在个别Java库用到了Thread.currentThread().getContextClassLoader()时，“用了原来的PathClassLoader”，或为空指针
        Thread.currentThread().setContextClassLoader(cl);
    }


    private void parseApkAndInitPluginContainer(String path, PluginInfo pInfo) throws PackageManager.NameNotFoundException, IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        PackageManager pm = BaseApplication.getBaseApp().getPackageManager();

        PackageInfo mPackageInfo = pm.getPackageArchiveInfo(path,
                PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES
                        | PackageManager.GET_PROVIDERS | PackageManager.GET_RECEIVERS | PackageManager.GET_META_DATA);

        pInfo.pkgName = mPackageInfo.applicationInfo.packageName;
        String appClass = mPackageInfo.applicationInfo.className;
        if (TextUtils.isEmpty(mPackageInfo.applicationInfo.processName)) {
            mPackageInfo.applicationInfo.processName = mPackageInfo.applicationInfo.packageName;
        }
        mPackageInfo.applicationInfo.sourceDir = path;
        mPackageInfo.applicationInfo.publicSourceDir = path;

//         设置so库路径
//        mPackageInfo.applicationInfo.nativeLibraryDir = ld.getAbsolutePath();
        Resources mPkgResources = pm.getResourcesForApplication(mPackageInfo.applicationInfo);

        pInfo.resources = mPkgResources;
        pInfo.versionName = mPackageInfo.versionName;
        pInfo.versionCode = mPackageInfo.versionCode;
//        Log.e("asdasd", "mPackageInfo.versionCode-" + mPackageInfo.versionCode);
        pInfo.appInfo = mPackageInfo.applicationInfo;
        pInfo.clsLoader = new PluginDexClassLoader(pInfo.tempDir, pInfo.pluginInstallPath, pInfo.optdir,
                null, appContext.getClass().getClassLoader().getParent());
        if (mPackageInfo.receivers != null) {
            HashMap<String, List<IntentFilter>> receHm = ManifestParser.getFilters(path);
            HashMap<String, ReceiverInfo> hm = new HashMap<>();
            for (ActivityInfo ai : mPackageInfo.receivers) {
                Log.e("asdasd", "receiver-" + ai.name);
                ReceiverInfo info = new ReceiverInfo();
                info.aInfo = ai;
                info.intentFilters = receHm.get(ai.name);
                hm.put(ai.name, info);

                StaticBroadCastReceiverManager.getInstance().registerReceiver(ai.name, info.intentFilters,
                        pInfo.clsLoader.loadClass(ai.name, false));

            }
            pInfo.receiverInfos = hm;
        }
//        Log.e("asdasd","getFilters-"+new Gson().toJson( pInfo.receiverInfos));


        if (mPackageInfo.activities != null) {
            HashMap<String, ActivityInfo> hm = new HashMap<>();
            for (ActivityInfo ai : mPackageInfo.activities) {
//                Log.e("asdasd", "ai.launchMode-" + ai.launchMode);
                hm.put(ai.name, ai);
            }
            pInfo.activityInfos = hm;
        }

        if (mPackageInfo.services != null) {
            HashMap<String, ServiceInfo> servs = new HashMap<>();
            for (ServiceInfo ai : mPackageInfo.services) {
//                Log.e("asdasd", "service.name-" + ai.name);
                servs.put(ai.name, ai);
            }
            pInfo.serviceInfos = servs;
        }

        ProviderInfo[] providerInfos = mPackageInfo.providers;
        if (providerInfos != null) {
            HashMap<String, ProviderInfo> cp = new HashMap<>();
            for (ProviderInfo pi : providerInfos) {
//                Log.e("asdasd", "service.name-" + ai.name);

                cp.put(pi.name, pi);
                ContentProviderInfo info = new ContentProviderInfo();
                info.authority = pi.authority;
                Class<?> cls = pInfo.clsLoader.loadClass(pi.name, false);
                info.cp = (ContentProvider) cls.newInstance();
                info.clsName = pi.name;
                ContentProviderManager.getInstance().put(pi.authority, info);
            }
            pInfo.providerInfos = cp;
        }


        pInfo.themeId = mPackageInfo.applicationInfo.theme;
        pInfo.appClass = appClass;

//        Log.e("asdasasd", "pInfo.pkgName-" + appClass);
        plugins.put(pInfo.pkgName, pInfo);
        Class<?> appcls = null;
        PluginContext mPkgContext = new PluginContext(BaseApplication.getBaseApp()
                , android.R.style.Theme, pInfo.clsLoader, mPkgResources, pInfo.pkgName, "");
        if(appClass!=null) {
            appcls = pInfo.clsLoader.loadClass(appClass, false);
            final Application app = (Application) appcls.newInstance();
            pInfo.application = app;
            ReflectUtils.getMethod(Application.class, "attachBaseContext", new Class<?>[]{Context.class}).invoke(app, mPkgContext);
            Task.execOnUiThrad(new Runnable() {
                @Override
                public void run() {
                    app.onCreate();
                }
            });
        }

        writeInstalledInfo(pInfo);
    }

    private void createPluginDirAndFile(String path, PluginInfo pInfo) throws IOException {
        PackageManager pm = BaseApplication.getBaseApp().getPackageManager();

        PackageInfo mPackageInfo = pm.getPackageArchiveInfo(path,
                PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES
                        | PackageManager.GET_PROVIDERS | PackageManager.GET_RECEIVERS | PackageManager.GET_META_DATA);

        File f = appContext.getFilesDir();


        String dirPlugin = f.getAbsolutePath() + "/plugin";
        String apkDir = dirPlugin + "/" + mPackageInfo.applicationInfo.packageName;
        pInfo.optdir = apkDir + "/optdir";
        pInfo.tempDir = apkDir + "/tempdir";
        pInfo.pluginInstallPath = apkDir + "/" + mPackageInfo.applicationInfo.packageName + ".apk";

        pInfo.pluginRootDir = dirPlugin;
        pInfo.packagePath = apkDir;

        File pluginFile = new File(dirPlugin);

        if (!pluginFile.exists()) {
            pluginFile.mkdir();
        }
        File apkDirFile = new File(apkDir);

        if (!apkDirFile.exists()) {
            apkDirFile.mkdir();
        }
        File optdirF = new File(pInfo.optdir);

        if (optdirF.exists()) {
            FileUtils.delFile(optdirF);

        }
        optdirF.mkdir();
        File apkfile = new File(pInfo.pluginInstallPath);
        if (apkfile.exists()) apkfile.delete();

//        Log.e("sad", "newApk-" + pInfo.pluginInstallPath);
        File oriFile = new File(path);

        FileUtils.copyFile(oriFile, new File(pInfo.pluginInstallPath));
        oriFile.delete();


    }


}
