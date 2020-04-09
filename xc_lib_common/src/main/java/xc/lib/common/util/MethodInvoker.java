package xc.lib.common.util;

import android.util.Log;

import java.lang.reflect.Method;

public class MethodInvoker {


    private static final String TAG = "MethodInvoker";

    private ClassLoader mLoader;

    private String mClassName;

    private String mMethodName;

    private Class<?>[] mParamTypes;

    private Method mMethod;

    private boolean mInitialized;

    private boolean mAvailable;

    public MethodInvoker(ClassLoader loader, String className, String methodName, Class<?>[] paramTypes) {
        mLoader = loader;
        mClassName = className;
        mMethodName = methodName;
        mParamTypes = paramTypes;
        mMethod = null;
        mInitialized = false;
        mAvailable = false;
    }

    public Object call(Object methodReceiver, Object... methodParamValues) {
        if (!mInitialized) {
            try {
                mInitialized = true;
                mMethod = ReflectUtils.getMethod(mLoader, mClassName, mMethodName, mParamTypes);

//                Log.e("sad","Factory-call-enter-"+(mMethod==null) );

                mAvailable = true;
            } catch (Exception e) {
                Log.e("sxasasd","Factory-call-ex-"+e.toString() );
//                if (LogDebug.LOG) {
//                    LogDebug.d(TAG, "get method error !!! (Maybe the version of replugin-host-lib is too low)", e);
//                }
            }
        }

        if (mMethod != null) {
            try {
                return ReflectUtils.invokeMethod(mMethod, methodReceiver, methodParamValues);
            } catch (Exception e) {

                throw new IllegalArgumentException(e);

//                Log.e("sad","Factory-call-ex2-"+e.toString() );
//                if (LogDebug.LOG) {
//                    LogDebug.d(TAG, "invoker method error !!! (Maybe the version of replugin-host-lib is too low)", e);
//                }
            }
        }

        return null;
    }

    public ClassLoader getClassLoader() {
        return mLoader;
    }

    public boolean isAvailable() {
        return mAvailable;
    }
}
