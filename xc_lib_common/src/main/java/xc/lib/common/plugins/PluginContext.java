package xc.lib.common.plugins;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;

import xc.lib.common.util.FilePermissionUtils;
import xc.lib.common.util.MethodInvoker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;

public class PluginContext extends ContextThemeWrapper {

    private final ClassLoader mNewClassLoader;

    private final Resources mNewResources;
    private HashMap<String, Constructor<?>> mConstructors = new HashMap<String, Constructor<?>>();
    private final String mPlugin;
    private Intent curIntent;

    HashSet<String> mIgnores = new HashSet<String>();
    private final Object mSync = new Object();

    private File mFilesDir;

    private File mCacheDir;

    private File mDatabasesDir;

    private LayoutInflater mInflater;

    private String curClsName ;



    LayoutInflater.Factory mFactory = new LayoutInflater.Factory() {

        @Override
        public View onCreateView(String name, Context context, AttributeSet attrs) {
            return handleCreateView(name, context, attrs);
        }
    };

    public PluginContext(Context base, int themeres, ClassLoader cl, Resources r, String plugin,String clsname) {
        super(base, themeres);

        mNewClassLoader = cl;
        mNewResources = r;
        mPlugin = plugin;
        curClsName = clsname;

    }

//    @Override
//    public String getOpPackageName() {
//        return "tttt";
//    }

    @Override
    public ClassLoader getClassLoader() {
        if (mNewClassLoader != null) {
            return mNewClassLoader;
        }
        return super.getClassLoader();
    }

    @Override
    public Resources getResources() {
        if (mNewResources != null) {
            return mNewResources;
        }
        return super.getResources();
    }

    @Override
    public AssetManager getAssets() {
        if (mNewResources != null) {
            return mNewResources.getAssets();
        }
        return super.getAssets();
    }
    @Override
    public PackageManager getPackageManager()
    {

        return super.getPackageManager();
    }

    @Override
    public Object getSystemService(String name) {
        if (LAYOUT_INFLATER_SERVICE.equals(name)) {
            if (mInflater == null) {
                LayoutInflater inflater = (LayoutInflater) super.getSystemService(name);
                // 新建一个，设置其工厂
                mInflater = inflater.cloneInContext(this);
                mInflater.setFactory(mFactory);
                // 再新建一个，后续可再次设置工厂
                mInflater = mInflater.cloneInContext(this);
            }
            return mInflater;
        }
        return super.getSystemService(name);
    }

    @Override
    public SharedPreferences getSharedPreferences(String name, int mode) {
        name = "plugin_" + name;
        return super.getSharedPreferences(name, mode);
    }

    @Override
    public FileInputStream openFileInput(String name) throws FileNotFoundException {
        File f = makeFilename(getFilesDir(), name);
        return new FileInputStream(f);
    }

    @Override
    public FileOutputStream openFileOutput(String name, int mode) throws FileNotFoundException {
        final boolean append = (mode & MODE_APPEND) != 0;
        File f = makeFilename(getFilesDir(), name);
        try {
            FileOutputStream fos = new FileOutputStream(f, append);
            setFilePermissionsFromMode(f.getPath(), mode, 0);
            return fos;
        } catch (FileNotFoundException e) {
            //
        }

        File parent = f.getParentFile();
        parent.mkdir();
        FilePermissionUtils.setPermissions(parent.getPath(), FilePermissionUtils.S_IRWXU | FilePermissionUtils.S_IRWXG, -1, -1);
        FileOutputStream fos = new FileOutputStream(f, append);
        setFilePermissionsFromMode(f.getPath(), mode, 0);
        return fos;
    }

    @Override
    public boolean deleteFile(String name) {
        File f = makeFilename(getFilesDir(), name);
        return f.delete();
    }

    @Override
    public File getFilesDir() {
        synchronized (mSync) {
            if (mFilesDir == null) {
                mFilesDir = new File(getDataDirFile(), "files");
            }
            if (!mFilesDir.exists()) {
                if (!mFilesDir.mkdirs()) {
                    if (mFilesDir.exists()) {
                        return mFilesDir;
                    }
                    return null;
                }
                FilePermissionUtils.setPermissions(mFilesDir.getPath(), FilePermissionUtils.S_IRWXU | FilePermissionUtils.S_IRWXG | FilePermissionUtils.S_IXOTH, -1, -1);
            }
            return mFilesDir;
        }
    }

    @Override
    public File getCacheDir() {
        synchronized (mSync) {
            if (mCacheDir == null) {
                mCacheDir = new File(getDataDirFile(), "cache");
            }
            if (!mCacheDir.exists()) {
                if (!mCacheDir.mkdirs()) {
                    if (mCacheDir.exists()) {
                        return mCacheDir;
                    }
                    return null;
                }
                FilePermissionUtils.setPermissions(mCacheDir.getPath(), FilePermissionUtils.S_IRWXU | FilePermissionUtils.S_IRWXG | FilePermissionUtils.S_IXOTH, -1, -1);
            }
        }
        return mCacheDir;
    }


    @Override
    public File getFileStreamPath(String name) {
        return makeFilename(getFilesDir(), name);
    }

    @Override
    public File getDir(String name, int mode) {
        name = "app_" + name;
        File file = makeFilename(getDataDirFile(), name);
        if (!file.exists()) {
            file.mkdir();
            setFilePermissionsFromMode(file.getPath(), mode, FilePermissionUtils.S_IRWXU | FilePermissionUtils.S_IRWXG | FilePermissionUtils.S_IXOTH);
        }
        return file;
    }

    private File getDatabasesDir() {
        synchronized (mSync) {
            if (mDatabasesDir == null) {
                mDatabasesDir = new File(getDataDirFile(), "databases");
            }
            if (mDatabasesDir.getPath().equals("databases")) {
                mDatabasesDir = new File("/data/system");
            }
            return mDatabasesDir;
        }
    }

    private File validateFilePath(String name, boolean createDirectory) {
        File dir;
        File f;

        if (name.charAt(0) == File.separatorChar) {
            String dirPath = name.substring(0, name.lastIndexOf(File.separatorChar));
            dir = new File(dirPath);
            name = name.substring(name.lastIndexOf(File.separatorChar));
            f = new File(dir, name);
        } else {
            dir = getDatabasesDir();
            f = makeFilename(dir, name);
        }

        if (createDirectory && !dir.isDirectory() && dir.mkdir()) {
            FilePermissionUtils.setPermissions(dir.getPath(), FilePermissionUtils.S_IRWXU | FilePermissionUtils.S_IRWXG | FilePermissionUtils.S_IXOTH, -1, -1);
        }

        return f;
    }

    private final File makeFilename(File base, String name) {
        if (name.indexOf(File.separatorChar) < 0) {
            return new File(base, name);
        }
        throw new IllegalArgumentException("File " + name + " contains a path separator");
    }


    private final void setFilePermissionsFromMode(String name, int mode, int extraPermissions) {
        int perms = FilePermissionUtils.S_IRUSR | FilePermissionUtils.S_IWUSR | FilePermissionUtils.S_IRGRP | FilePermissionUtils.S_IWGRP | extraPermissions;

        FilePermissionUtils.setPermissions(name, perms, -1, -1);
    }


    private final File getDataDirFile() {

        File dir0 = getBaseContext().getFilesDir();

        // v3 data
        File dir = new File(dir0, Constant.LOCAL_PLUGIN_DATA_SUB_DIR);
        if (!dir.exists()) {
            if (!dir.mkdir()) {

                return null;
            }
            setFilePermissionsFromMode(dir.getPath(), 0, FilePermissionUtils.S_IRWXU | FilePermissionUtils.S_IRWXG | FilePermissionUtils.S_IXOTH);
        }

        // 插件名
        File file = makeFilename(dir, mPlugin);
        if (!file.exists()) {
            if (!file.mkdir()) {

                return null;
            }
            setFilePermissionsFromMode(file.getPath(), 0, FilePermissionUtils.S_IRWXU | FilePermissionUtils.S_IRWXG | FilePermissionUtils.S_IXOTH);
        }

        return file;
    }

    private final View handleCreateView(String name, Context context, AttributeSet attrs) {
        // 忽略表命中，返回
        if (mIgnores.contains(name)) {

            return null;
        }

        // 构造器缓存
        Constructor<?> construct = mConstructors.get(name);

        // 缓存失败
        if (construct == null) {
            // 找类
            Class<?> c = null;
            boolean found = false;
            do {
                try {
                    c = mNewClassLoader.loadClass(name);
                    if (c == null) {
                        // 没找到，不管
                        break;
                    }
                    if (c == ViewStub.class) {
                        // 系统特殊类，不管
                        break;
                    }
                    if (c.getClassLoader() != mNewClassLoader) {
                        // 不是插件类，不管
                        break;
                    }
                    // 找到
                    found = true;
                } catch (ClassNotFoundException e) {
                    // 失败，不管
                    break;
                }
            } while (false);
            if (!found) {

                mIgnores.add(name);
                return null;
            }
            // 找构造器
            try {
                construct = c.getConstructor(Context.class, AttributeSet.class);

                mConstructors.put(name, construct);
            } catch (Exception e) {
                InflateException ie = new InflateException(attrs.getPositionDescription() + ": Error inflating mobilesafe class " + name, e);
                throw ie;
            }
        }

        // 构造
        try {
            View v = (View) construct.newInstance(context, attrs);

            return v;
        } catch (Exception e) {
            InflateException ie = new InflateException(attrs.getPositionDescription() + ": Error inflating mobilesafe class " + name, e);
            throw ie;
        }
    }

    @Override
    public String getPackageName() {

        return mPlugin;// 插件包名
//        return super.getPackageName();-- 宿主包名
    }


    @Override
    public Context getApplicationContext() {
//        MethodInvoker getIntentInvoke = new MethodInvoker(mNewClassLoader, ComConstant.PluginProxy, "getPluginApplication",
//                new Class<?>[]{String.class});
//        Application application = (Application) getIntentInvoke.call(null, mPlugin);

        return this;

    }


    @Override
    public void startActivity(Intent intent) {

        Log.e("asdsada", "plugincontext-startActivity");

        //只有插件Application才会走这里

        MethodInvoker startActivityInvoke = new MethodInvoker(this.getClass().getClassLoader(), ComConstant.PluginProxy, "startActivity",
                new Class<?>[]{Intent.class, Activity.class});
        Intent newIntent = (Intent) startActivityInvoke.call(null, intent, null);

        super.startActivity(newIntent);
    }

    @Override
    public void startActivity(Intent intent, Bundle options) {

        Log.e("asdsada", "plugincontext-startActivity(Intent intent, Bundle options)");
        MethodInvoker startActivityInvoke = new MethodInvoker(this.getClass().getClassLoader(), ComConstant.PluginProxy, "startActivity",
                new Class<?>[]{Intent.class, Activity.class});
        Intent newIntent = (Intent) startActivityInvoke.call(null, intent, null);

        super.startActivity(newIntent,options);
    }

    @Override
    public ComponentName startService(Intent service) {

        MethodInvoker startActivityInvoke = new MethodInvoker(this.getClass().getClassLoader(), ComConstant.PluginProxy, "getServiceWrapperIntent",
                new Class<?>[]{Intent.class});
        Intent newIntent = (Intent) startActivityInvoke.call(null, service);
//        Log.e("asdasasd","PluginContext-startService-clsname-"+newIntent.getStringExtra(ComConstant.RealActivity));
        curIntent = newIntent;
        // 若打开插件出错，则直接走系统逻辑
        return super.startService(newIntent);
    }

    @Override
    public boolean stopService(Intent name) {

        boolean retcal = super.stopService(curIntent);
        curIntent = null;

        return retcal;
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
//        service.setClassName(mPlugin,curClsName);
        Log.e("asdasasd", "plugincontext-bindService()-"+service.getComponent().getPackageName());


        MethodInvoker startActivityInvoke = new MethodInvoker(this.getClass().getClassLoader(), ComConstant.PluginProxy, "getServiceWrapperIntent",
                new Class<?>[]{Intent.class});
        Intent newIntent = (Intent) startActivityInvoke.call(null, service);

        return super.bindService(newIntent, conn, flags);
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        Log.e("asdasasd", "plugincontext-unbindService()-" );

        super.unbindService(conn);
    }

    @Override
    public String getPackageCodePath() {
        MethodInvoker getIntentInvoke = new MethodInvoker(mNewClassLoader,  ComConstant.PluginProxy, "getPluginApkPath",
                new Class<?>[]{String.class});
        return (String) getIntentInvoke.call(null, mPlugin);
    }

    @Override
    public ApplicationInfo getApplicationInfo() {
        MethodInvoker getIntentInvoke = new MethodInvoker(mNewClassLoader,  ComConstant.PluginProxy, "getPluginApplicationInfo",
                new Class<?>[]{String.class});
        return (ApplicationInfo) getIntentInvoke.call(null, mPlugin);
    }
}
