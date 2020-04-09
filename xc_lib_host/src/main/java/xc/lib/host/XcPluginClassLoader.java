package xc.lib.host;

import android.os.Build;
import android.util.Log;

import xc.lib.common.util.ReflectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.regex.Pattern;

import dalvik.system.PathClassLoader;
import xc.lib.host.activity.ActivityPitManager;
import xc.lib.host.service.ServicePitManager;

public class XcPluginClassLoader extends PathClassLoader {

    private static final String TAG = "RePluginClassLoader";

    private final ClassLoader mOrig;
    // 匹配Activity
    private final Pattern pattern = Pattern.compile("[a-z]{1,1}_\\d+");
    // 匹配service
    private final Pattern spattern = Pattern.compile("s_\\d+");


    private Method findResourceMethod;

    private Method findResourcesMethod;

    private Method findLibraryMethod;

    private Method getPackageMethod;

    public XcPluginClassLoader(ClassLoader parent, ClassLoader orig) {

        // 由于PathClassLoader在初始化时会做一些Dir的处理，所以这里必须要传一些内容进来
        // 但我们最终不用它，而是拷贝所有的Fields
        super("", "", parent);
        mOrig = orig;

        // 将原来宿主里的关键字段，拷贝到这个对象上，这样骗系统以为用的还是以前的东西（尤其是DexPathList）
        // 注意，这里用的是“浅拷贝”
        copyFromOriginal(orig);

        initMethods(orig);
    }

    private void initMethods(ClassLoader cl) {
        Class<?> c = cl.getClass();
        findResourceMethod = ReflectUtils.getMethod(c, "findResource", String.class);
        findResourceMethod.setAccessible(true);
        findResourcesMethod = ReflectUtils.getMethod(c, "findResources", String.class);
        findResourcesMethod.setAccessible(true);
        findLibraryMethod = ReflectUtils.getMethod(c, "findLibrary", String.class);
        findLibraryMethod.setAccessible(true);
        getPackageMethod = ReflectUtils.getMethod(c, "getPackage", String.class);
        getPackageMethod.setAccessible(true);
    }

    private void copyFromOriginal(ClassLoader orig) {
//        if (LOG && IPC.isPersistentProcess()) {
////            LogDebug.d(TAG, "copyFromOriginal: Fields=" + StringUtils.toStringWithLines(ReflectUtils.getAllFieldsList(orig.getClass())));
//        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            // Android 2.2 - 2.3.7，有一堆字段，需要逐一复制
            // 以下方法在较慢的手机上用时：8ms左右
            copyFieldValue("libPath", orig);
            copyFieldValue("libraryPathElements", orig);
            copyFieldValue("mDexs", orig);
            copyFieldValue("mFiles", orig);
            copyFieldValue("mPaths", orig);
            copyFieldValue("mZips", orig);
        } else {
            // Android 4.0以上只需要复制pathList即可
            // 以下方法在较慢的手机上用时：1ms
            copyFieldValue("pathList", orig);
        }
    }

    private void copyFieldValue(String field, ClassLoader orig) {
        try {
            Field f = ReflectUtils.getField(orig.getClass(), field);
            if (f == null) {

                return;
            }

            // 删除final修饰符
            ReflectUtils.removeFieldFinalModifier(f);

            // 复制Field中的值到this里
            Object o = ReflectUtils.readField(f, orig);
            ReflectUtils.writeField(f, this, o);

        } catch (IllegalAccessException e) {
        }
    }

    @Override
    protected Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {

        Class<?> c = null;

        if (className.contains(BaseApplication.getBaseApp().getPackageName())) {
            String clsN = className.substring(className.lastIndexOf('.') + 1, className.length());

            if (pattern.matcher(clsN).find()) {

//                Log.e("dasdassad", "host-loadClass-clsName-" + className);

                PitInfo p = new PitInfo();
                if (spattern.matcher(clsN).find()) {

                    ServicePitManager.getInstance().findRealServClass(p, className);
                } else
                    ActivityPitManager.getInstance().findRealActivityClass(p, className);
                c = PluginManager.getInstance().loadClass(p.pluginName, p.realClsName);
            }
        }

        if (c != null) {
            return c;
        }
        //
        try {
            c = mOrig.loadClass(className);
            // 只有开启“详细日志”才会输出，防止“刷屏”现象
//            if (LogDebug.LOG && RePlugin.getConfig().isPrintDetailLog()) {
//                LogDebug.d(TAG, "loadClass: load other class, cn=" + className);
//            }
            return c;
        } catch (Throwable e) {
            //
        }
        //
        return super.loadClass(className, resolve);
    }

    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {
        // INFO Never reach here since override loadClass , unless not found class
//        if (LOGR) {
//            LogRelease.w(PLUGIN_TAG, "NRH lcl.fc: c=" + className);
//        }
        return super.findClass(className);
    }

    @Override
    protected URL findResource(String resName) {
        try {
            return (URL) findResourceMethod.invoke(mOrig, resName);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return super.findResource(resName);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Enumeration<URL> findResources(String resName) {
        try {
            return (Enumeration<URL>) findResourcesMethod.invoke(mOrig, resName);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return super.findResources(resName);
    }

    @Override
    public String findLibrary(String libName) {
        try {
            return (String) findLibraryMethod.invoke(mOrig, libName);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return super.findLibrary(libName);
    }

    @Override
    protected Package getPackage(String name) {
        // 金立手机的某些ROM(F103,F103L,F303,M3)代码ClassLoader.getPackage去掉了关键的保护和错误处理(2015.11~2015.12左右)，会返回null
        // 悬浮窗某些draw代码触发getPackage(...).getName()，getName出现空指针解引，导致悬浮窗进程出现了大量崩溃
        // 此处实现和AOSP一致确保不会返回null
        // SONGZHAOCHUN, 2016/02/29
        if (name != null && !name.isEmpty()) {
            Package pack = null;
            try {
                pack = (Package) getPackageMethod.invoke(mOrig, name);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            if (pack == null) {
                pack = super.getPackage(name);
            }
            if (pack == null) {
                return definePackage(name, "Unknown", "0.0", "Unknown", "Unknown", "0.0", "Unknown", null);
            }
            return pack;
        }
        return null;
    }


}
