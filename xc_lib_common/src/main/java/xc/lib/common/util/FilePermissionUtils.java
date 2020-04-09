package xc.lib.common.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class FilePermissionUtils {

    public static final int S_IRWXU = 00700;
    public static final int S_IRUSR = 00400;
    public static final int S_IWUSR = 00200;
    public static final int S_IXUSR = 00100;

    public static final int S_IRWXG = 00070;
    public static final int S_IRGRP = 00040;
    public static final int S_IWGRP = 00020;
    public static final int S_IXGRP = 00010;

    public static final int S_IRWXO = 00007;
    public static final int S_IROTH = 00004;
    public static final int S_IWOTH = 00002;
    public static final int S_IXOTH = 00001;

    private static Class<?> sFileUtilsClass;
    private static Method sSetPermissionMethod;
    private static Method sGetPermissionMethod;

    /**
     * 设置文件的访问权限，使用反射调用系统隐藏同名函数。
     * @param filePath 需要被设置访问权限的文件
     * @param mode 文件访问权限，如0777，0755
     * @param uid
     * @param gid
     * @return -1 表示设置失败
     */
    public static int setPermissions(String filePath, int mode, int uid, int gid) {
        try {
            initClass();
            if (sSetPermissionMethod == null) {
                sSetPermissionMethod = ReflectUtils.getMethod(sFileUtilsClass, "setPermissions",
                        String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE);
            }
            Object retObj = sSetPermissionMethod.invoke(null, filePath, mode, uid, gid);
            if (retObj != null && retObj instanceof Integer) {
                return (int) retObj;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 查询文件的访问权限，这个接口只能在4.1.2或之前有效
     * @param filePath
     * @param outPermissions
     * @return -1 表示查询失败
     */
    public static int getPermissions(String filePath, int[] outPermissions) {
        try {
            initClass();
            if (sGetPermissionMethod == null) {
                sGetPermissionMethod = ReflectUtils.getMethod(sFileUtilsClass, "getPermissions",
                        String.class, int[].class);
            }
            Object retObj = sGetPermissionMethod.invoke(null, filePath, outPermissions);
            if (retObj != null && retObj instanceof Integer) {
                return (int) retObj;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private static void initClass() throws ClassNotFoundException {
        if (sFileUtilsClass == null) {
            sFileUtilsClass = ReflectUtils.getClass("android.os.FileUtils");
        }
    }
}

