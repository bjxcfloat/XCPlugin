package xc.lib.host;

import android.os.Build;
import android.util.Log;

import xc.lib.common.util.CloseableUtils;
import xc.lib.common.util.FileUtils;
import xc.lib.common.util.ReflectUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import dalvik.system.DexClassLoader;

public class PluginDexClassLoader extends DexClassLoader {

    private static final String TAG = "PluginDexClassLoader";

    private final ClassLoader mHostClassLoader;

    private static Method sLoadClassMethod;
    private  String optimizedDirectory;
    private String mPluginName;
    private String tempDir;

    /**
     * 初始化插件的DexClassLoader的构造函数。插件化框架会调用此函数。
     */
    public PluginDexClassLoader(String tempDir,String dexPath, String optimizedDirectory, String librarySearchPath, ClassLoader parent) {
        super(dexPath, optimizedDirectory, librarySearchPath, parent);
        this.tempDir = tempDir;
//        mPluginName = pi.getName();
        this.optimizedDirectory = optimizedDirectory;
        installMultiDexesBeforeLollipop(dexPath, parent);

        mHostClassLoader = BaseApplication.getBaseApp().getClassLoader();

        initMethods(mHostClassLoader);
    }

    private static void initMethods(ClassLoader cl) {
        Class<?> clz = cl.getClass();
        if (sLoadClassMethod == null) {
            sLoadClassMethod = ReflectUtils.getMethod(clz, "loadClass", String.class, Boolean.TYPE);
            if (sLoadClassMethod == null) {
                throw new NoSuchMethodError("loadClass");
            }
        }
    }

    @Override
    protected Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {
        // 插件自己的Class。从自己开始一直到BootClassLoader，采用正常的双亲委派模型流程，读到了就直接返回
        Class<?> pc = null;
        ClassNotFoundException cnfException = null;


        try {

            pc = super.loadClass(className, resolve);
            if (pc != null) {

//                Log.e("dasdassad", "plugin-loadClass-clsName-" + className);
                return pc;
            }
        } catch (ClassNotFoundException e) {
            // Do not throw "e" now
            cnfException = e;

            try {
//                Log.e("dasdassad", "plugin-loadClassFromHost-className-" + className);
                return loadClassFromHost(className, resolve);
            } catch (ClassNotFoundException e1) {
                // Do not throw "e1" now
                cnfException = e1;

            }
        }

        // At this point we can throw the previous exception
        if (cnfException != null) {
            throw cnfException;
        }
        return null;
    }

    private Class<?> loadClassFromHost(String className, boolean resolve) throws ClassNotFoundException {
        Class<?> c;
        try {
            c = (Class<?>) sLoadClassMethod.invoke(mHostClassLoader, className, resolve);

        } catch (IllegalAccessException e) {

            throw new ClassNotFoundException("Calling the loadClassFromHost method failed (IllegalAccessException)", e);
        } catch (InvocationTargetException e) {

            throw new ClassNotFoundException("Calling the loadClassFromHost method failed (InvocationTargetException)", e);
        }
        return c;
    }

    /**
     * install extra dexes
     *
     * @param dexPath
     * @param parent
     * @deprecated apply to ROM before Lollipop,may be deprecated
     */
    private void installMultiDexesBeforeLollipop(String dexPath, ClassLoader parent) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        try {

            // get paths of extra dex
            List<File> dexFiles = getExtraDexFiles( dexPath);

            if (dexFiles != null && dexFiles.size() > 0) {

                List<Object[]> allElements = new LinkedList<>();

                // get dexElements of main dex
                Class<?> clz = Class.forName("dalvik.system.BaseDexClassLoader");
                Object pathList = ReflectUtils.readField(clz, this, "pathList");
                Object[] mainElements = (Object[]) ReflectUtils.readField(pathList.getClass(), pathList, "dexElements");
                allElements.add(mainElements);

                // get dexElements of extra dex (need to load dex first)


                for (File file : dexFiles) {

                    DexClassLoader dexClassLoader = new DexClassLoader(file.getAbsolutePath(), optimizedDirectory, optimizedDirectory, parent);

                    Object obj = ReflectUtils.readField(clz, dexClassLoader, "pathList");
                    Object[] dexElements = (Object[]) ReflectUtils.readField(obj.getClass(), obj, "dexElements");
                    allElements.add(dexElements);
                }

                // combine Elements
                Object combineElements = combineArray(allElements);

                // rewrite Elements combined to classLoader
                ReflectUtils.writeField(pathList.getClass(), pathList, "dexElements", combineElements);

                // delete extra dex, after optimized
                FileUtils.forceDelete(new File(tempDir));

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * combine dexElements Array
     *
     * @param allElements all dexElements of dexes
     * @return the combined dexElements
     */
    private Object combineArray(List<Object[]> allElements) {

        int startIndex = 0;
        int arrayLength = 0;
        Object[] originalElements = null;

        for (Object[] elements : allElements) {

            if (originalElements == null) {
                originalElements = elements;
            }

            arrayLength += elements.length;
        }

        Object[] combined = (Object[]) Array.newInstance(
                originalElements.getClass().getComponentType(), arrayLength);

        for (Object[] elements : allElements) {

            System.arraycopy(elements, 0, combined, startIndex, elements.length);
            startIndex += elements.length;
        }

        return combined;
    }

    /**
     * get paths of extra dex
     *

     * @param dexPath
     * @return the File list of the extra dexes
     */
    private List<File> getExtraDexFiles( String dexPath) {

        ZipFile zipFile = null;
        List<File> files = null;

        try {

            if (tempDir != null) {
                zipFile = new ZipFile(dexPath);
                files = traverseExtraDex(tempDir, zipFile);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseableUtils.closeQuietly(zipFile);
        }

        return files;

    }

    /**
     * traverse extra dex files
     *

     * @param zipFile
     * @return the File list of the extra dexes
     */
    private static List<File> traverseExtraDex(String tempDir, ZipFile zipFile) {

        String dir = null;
        List<File> files = new LinkedList<>();
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String name = entry.getName();
            if (name.contains("../")) {
                // 过滤，防止被攻击
                continue;
            }

            try {
                if (name.contains(".dex") && !name.equals("classes.dex")) {

                    if (dir == null) {
                        dir = tempDir;

                        File tempDirF = new File(dir);
                        if(!tempDirF.exists())
                        {
                            tempDirF.mkdir();
                        }

                    }

                    File file = new File(dir, name);
                    extractFile(zipFile, entry, file);
                    files.add(file);

//                    if (LogDebug.LOG && RePlugin.getConfig().isPrintDetailLog()) {
//                        LogDebug.d(TAG, "dex path:" + file.getAbsolutePath());
//                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return files;
    }

    /**
     * extract File
     *
     * @param zipFile
     * @param ze
     * @param outFile
     * @throws IOException
     */
    private static void extractFile(ZipFile zipFile, ZipEntry ze, File outFile) throws IOException {
        InputStream in = null;
        try {
            in = zipFile.getInputStream(ze);
            FileUtils.copyInputStreamToFile(in, outFile);
        } finally {
            CloseableUtils.closeQuietly(in);
        }
    }

}
