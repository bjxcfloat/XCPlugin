package xc.lib.common.httpclient;

import android.util.Log;

import androidx.annotation.Nullable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public final class ServiceFactory {

    private static ConcurrentHashMap<String,MethodParse> methodCache = new ConcurrentHashMap<>();


    public static <T, Invoker> T createService(Class<T> t, final Bridge context, final Invoker cls) {


        return (T) Proxy.newProxyInstance(t.getClassLoader(), new Class<?>[]{t},
                new InvocationHandler() {

                    @Override
                    public @Nullable
                    Object invoke(Object proxy, Method method,
                                  @Nullable Object[] args) throws Throwable {
                        // If the method is a method from Object then defer to normal invocation.
                        if (method.getDeclaringClass() == Object.class) {
                            return method.invoke(this, args);
                        }
                        MethodParse parser = null;
                        RequestInfo info;
                        String cacheKey = cls.getClass().toString() + "_" + method.getName();
                        if (methodCache.containsKey(cacheKey)) {
                            parser = methodCache.get(cacheKey);
                            info = parser.loadParms(args);

                            Log.e("a", "createService-1-" + cls.getClass());
                        } else {
                            parser = new MethodParse();
                            parser.parse(method, context);
                            info = parser.loadParms(args);
                            methodCache.put(cacheKey,parser);

                            Log.e("a", "createService-2-" + cls.getClass());
                        }

                        info.charset = "charset="+context.getCharset();
                        info.context = context;
                        return ServiceInvoke.invoke(info);
                    }
                });


    }


}
