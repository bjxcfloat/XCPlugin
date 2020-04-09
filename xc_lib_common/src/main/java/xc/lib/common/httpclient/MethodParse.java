package xc.lib.common.httpclient;

import android.util.Log;

import androidx.annotation.Nullable;

import xc.lib.common.httpclient.annations.Delete;
import xc.lib.common.httpclient.annations.Field;
import xc.lib.common.httpclient.annations.FileParm;
import xc.lib.common.httpclient.annations.FormUrlEncoded;
import xc.lib.common.httpclient.annations.Get;
import xc.lib.common.httpclient.annations.Head;
import xc.lib.common.httpclient.annations.HeadParm;
import xc.lib.common.httpclient.annations.JsonEncoded;
import xc.lib.common.httpclient.annations.JsonParm;
import xc.lib.common.httpclient.annations.MediaType;
import xc.lib.common.httpclient.annations.Multipart;
import xc.lib.common.httpclient.annations.Post;
import xc.lib.common.httpclient.annations.Put;
import xc.lib.common.httpclient.annations.Url;
import xc.lib.common.util.ReflectHelper;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class MethodParse {

    private RequestInfo info;

    private Type[] types;
    private HashMap<Annotation, ParmBean> hmAnn = new HashMap<>();

    protected static class ParmBean {
        public int index;
        public Object obj;
    }

    public void parse(Method method, Bridge context) {

        info = new RequestInfo();

        info.baseUrl = context.getBaseUrl();

        parseMethodAnnotions(info, method, context);
        parseMethodParmsAnnotions(info, method, context);

    }


    //  检查格式
    public void checkParms(Type[] types) {
        int i = 0;
        Log.e("a", "checkParms-type-len-" + types.length);
        for (Type t : types
        ) {
            Class<?> cls = ReflectHelper.getRawType(t);
            if ((cls.isPrimitive() || cls == String.class || cls == Integer.class
                    || cls.isArray()
                    || cls == File.class) && i <= types.length - 1) {
                Log.e("a", "checkParms-type-" + t + "-i-" + i);
            } else if (CallBack.class.isAssignableFrom(cls) && i == types.length - 1) {
                Log.e("a", "checkParms-type-11--" + t + "-i-" + i);
            } else {
                throw new IllegalArgumentException("method format is wrong");

            }
            i++;

        }
    }

    public RequestInfo loadParms(@Nullable Object[] args) {

//        int index = 0;
        for (Map.Entry<Annotation, ParmBean> entry : hmAnn.entrySet()) {
            if (entry.getKey() instanceof Field) {


                info.hmBody.put(entry.getValue().obj.toString(), args[entry.getValue().index]);

                Log.e("a", "loadParms-parm-" + entry.getValue().obj.toString() + "-args-" + args[entry.getValue().index] + "-index-" + entry.getValue().index);
            } else if (entry.getKey() instanceof HeadParm) {


                if (info.hmHead == null) info.hmHead = new HashMap<>();
                info.hmHead.put(entry.getValue().obj.toString(), args[entry.getValue().index].toString());

//                    Log.e("a", "iinvkde-parm-" + item.toString() + "-args-" + info.hmBody.get(key));
            } else if (entry.getKey() instanceof Url) {
//                    hmAnn.put(item, null);
                info.dynamicUrl = (String) args[entry.getValue().index];

            } else if (entry.getKey() instanceof MediaType) {
//                    hmAnn.put(item, null);

                info.dynamicMediaType = (String) args[entry.getValue().index];

            }
            // 如传入参数为json格式，则不继续轮询
            else if (entry.getKey() instanceof JsonParm) {
//                    hmAnn.put(item, null);

                // 此处有bug,会把文件删除
                info.hmBody.clear();

                info.hmBody.put(Constant.JsonFormKey, args[entry.getValue().index].toString());

                break;
            } else if (entry.getKey() instanceof FileParm) {
//                        String[] sarray = ((FileParm) item).value();

//                    hmAnn.put(item, ((FileParm) item).value());
                String[] sarray = (String[]) entry.getValue().obj;

                if (args[entry.getValue().index] == null)
                    throw new IllegalArgumentException("File parms is null");
                if (args[entry.getValue().index] instanceof File[]) {

                    if (sarray.length != ((File[]) args[entry.getValue().index]).length) {
                        throw new IllegalArgumentException("File parms is wrong");
                    }
                    File[] fs = ((File[]) args[entry.getValue().index]);
                    for (int k = 0; k < fs.length; k++) {
                        if (fs[k] == null)
                            throw new IllegalArgumentException("File parms is null");
                        info.hmBody.put(sarray[k], fs[k]);
                    }


                } else if (args[entry.getValue().index] instanceof File) {

                    info.hmBody.put(sarray[0], args[entry.getValue().index]);
                }


            }

        }

        if (args.length > hmAnn.size()) {

            info.callbackType = types[types.length - 1];
            info.callBack = (CallBack<?>) args[args.length - 1];
        }
        return info;
    }


    // 解析方法参数注解及参数
    private void parseMethodParmsAnnotions(RequestInfo info, Method method, Bridge context) {
        Annotation[][] parmsAnnotions = method.getParameterAnnotations();
        types = method.getGenericParameterTypes();
        if (info.hmBody == null) info.hmBody = new HashMap<String, Object>();

        int parmAnnCount = 0;
        checkParms(types);

        if (parmsAnnotions != null)
            for (int i = 0; i < parmsAnnotions.length; i++) {
                for (Annotation item : parmsAnnotions[i]
                ) {

                    if (item instanceof Field) {

                        String key = ((Field) item).value();
                        ParmBean bean = new ParmBean();
                        bean.index = i;
                        bean.obj = key;
                        hmAnn.put(item, bean);
//                        info.hmBody.put(key, args[i]);

                        Log.e("a", "iinvkde-parm-" + item.toString() + "-args-" + i);
                    } else if (item instanceof HeadParm) {

                        String key = ((HeadParm) item).value();
                        ParmBean bean = new ParmBean();
                        bean.index = i;
                        bean.obj = key;
                        hmAnn.put(item, bean);


//                        if (info.hmHead == null) info.hmHead = new HashMap<>();
//                        info.hmHead.put(key, args[i].toString());

//                        Log.e("a", "iinvkde-parm-" + item.toString() + "-args-" + info.hmBody.get(key));
                    } else if (item instanceof Url) {
                        ParmBean bean = new ParmBean();
                        bean.index = i;
                        hmAnn.put(item, bean);
//                        info.dynamicUrl = (String) args[parmAnnCount];

                    } else if (item instanceof MediaType) {
                        ParmBean bean = new ParmBean();
                        bean.index = i;
                        hmAnn.put(item, bean);

//                        info.dynamicMediaType = (String) args[parmAnnCount];

                    }
                    // 如传入参数为json格式，则不继续轮询
                    else if (item instanceof JsonParm) {
                        ParmBean bean = new ParmBean();
                        bean.index = i;
                        hmAnn.put(item, bean);

//                        info.hmBody.clear();
//
//                        info.hmBody.put(Constant.JsonFormKey, args[i].toString());

                        break;
                    } else if (item instanceof FileParm) {
//                        String[] sarray = ((FileParm) item).value();
                        ParmBean bean = new ParmBean();
                        bean.index = i;
                        bean.obj = ((FileParm) item).value();
                        hmAnn.put(item, bean);

                    }

                    parmAnnCount++;
                }
            }

    }


    // 设置方法注解
    private void parseMethodAnnotions(RequestInfo info, Method method, Bridge context) {
        Annotation[] methodAnnotions = method.getDeclaredAnnotations();

        Type t = method.getGenericReturnType();
        Class<?> cls = (Class<?>) t;
        if (!Cancelable.class.isAssignableFrom(cls)) {

            throw new IllegalArgumentException("return type must be Cancelable interface");
        }


        boolean isJsonFormType = false;
        for (Annotation item : methodAnnotions) {

            if (item instanceof Get) {
                info.reqMethod = Constant.GET;
                info.reqUrl = ((Get) item).value();
            } else if (item instanceof Post) {
                info.reqMethod = Constant.POST;
                info.reqUrl = ((Post) item).value();
            } else if (item instanceof Put) {
                info.reqMethod = Constant.PUT;
                info.reqUrl = ((Put) item).value();
            } else if (item instanceof Delete) {
                info.reqMethod = Constant.DELETE;
                info.reqUrl = ((Delete) item).value();
            } else if (item instanceof Head) {

                String[] val = ((Head) item).value();
                if (info.hmHead == null) info.hmHead = new HashMap<String, String>();
                if (val != null && val.length > 0) {
                    for (String s : val
                    ) {
                        if (s.indexOf("=") <= 0) {
                            throw new IllegalArgumentException("Head format is not right , it is must have \"=\"");
                        }
                        String[] array = s.split("=");
                        info.hmHead.put(array[0], array[1]);
                    }

                }

            } else if (item instanceof Multipart) {

                info.mediaType = Constant.Mulitpart;

            } else if (item instanceof FormUrlEncoded) {

                info.mediaType = Constant.FormUrlEncoded;

            } else if (item instanceof JsonEncoded) {
                isJsonFormType = true;

            } else if (item instanceof Multipart) {

                info.mediaType = Constant.Mulitpart;

            }
            Log.e("a", "iinvkde-" + item.annotationType().toString());
        }

        if (isJsonFormType) {
            info.reqMethod = Constant.POST;
            info.mediaType = Constant.JsonFormType;
        }

        // Mulitpart 类型必须用Post格式
        if (info.mediaType.equals(Constant.Mulitpart)) {
            info.reqMethod = Constant.POST;
        }

    }


}
