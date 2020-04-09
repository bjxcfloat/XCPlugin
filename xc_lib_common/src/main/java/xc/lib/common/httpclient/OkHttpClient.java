package xc.lib.common.httpclient;


import android.util.Log;

import xc.lib.common.httpclient.json.IJsonStrategy;
import xc.lib.common.httpclient.test.TestOkHttp;
import xc.lib.common.util.ReflectHelper;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public final class OkHttpClient implements IHttpClientStrategy {

    private static okhttp3.OkHttpClient client;
    private okhttp3.OkHttpClient.Builder builder = null;
    private IJsonStrategy jsonStrategy = null;
    private HashMap<String, String> hmCommonHeader = null;

    public OkHttpClient() {
        if (client == null)
            builder = new okhttp3.OkHttpClient().newBuilder();

    }

    @Override
    public void setReadTimeout(long seconds) {
        if (builder != null)
            builder.readTimeout(seconds, TimeUnit.SECONDS);

    }

    @Override
    public void setConnectTimeout(long seconds) {
        if (builder != null)
            builder.connectTimeout(seconds, TimeUnit.SECONDS);
    }

    @Override
    public void setCallTimeout(long seconds) {
        if (builder != null)
            builder.callTimeout(seconds, TimeUnit.SECONDS);
    }

    @Override
    public void setHeaders(HashMap<String, String> hmHeader) {

        this.hmCommonHeader = hmHeader;

    }

    @Override
    public boolean isInit() {
        return client != null;
    }


    @Override
    public void init() {
        synchronized (OkHttpClient.class) {

            if (client == null)
                client = builder.build();


        }
    }

    @Override
    public void setJsonStrategy(IJsonStrategy jsonStrategy) {
        this.jsonStrategy = jsonStrategy;
    }

    private String getUrlByGetReq(String rootUrl, RequestInfo requestInfo) {
        if (rootUrl.length() - 1 == rootUrl.lastIndexOf("/")) {
            rootUrl = rootUrl.substring(0, rootUrl.length() - 1);
        }
        StringBuilder builder = new StringBuilder();
        builder.append(rootUrl);

        if (requestInfo.hmBody != null && requestInfo.hmBody.size() > 0) {
            builder.append("?");
            for (Map.Entry<String, ?> entry : requestInfo.hmBody.entrySet()) {
                builder.append(entry.getKey().trim());
                builder.append("=");

                builder.append(entry.getValue().toString().trim());
                builder.append("&");
            }
            return builder.substring(0, builder.length() - 1);
        }
        return builder.substring(0, builder.length());
    }

    private RequestBody getRequestBodyByPost(RequestInfo requestInfo) {
        MediaType type = null;
        if (requestInfo.dynamicMediaType != null && requestInfo.dynamicMediaType.length() > 0) {
            type = MediaType.parse(requestInfo.dynamicMediaType+";"+requestInfo.charset);
        } else if (requestInfo.mediaType != null && requestInfo.mediaType.length() > 0) {
            type = MediaType.parse(requestInfo.mediaType+";"+requestInfo.charset);
        } else {
            type = MediaType.parse(Constant.FormUrlEncoded+";"+requestInfo.charset);
        }
        StringBuilder builder = new StringBuilder();
        if (requestInfo.hmBody != null && requestInfo.hmBody.size() > 0) {


            if (requestInfo.hmBody.containsKey(Constant.JsonFormKey)) {
                type = MediaType.parse(Constant.JsonFormType+";"+requestInfo.charset);

                return RequestBody.create(requestInfo.hmBody.get(Constant.JsonFormKey).toString()
                        , type);
            }

            for (Map.Entry<String, ?> entry : requestInfo.hmBody.entrySet()) {
                builder.append(entry.getKey().trim());
                builder.append("=");

                builder.append(entry.getValue().toString().trim());
                builder.append("&");
            }
        }
        Log.e("a", "getRequestBodyByPost:" + builder.toString() + "-mediatype-" + type.toString());
        return RequestBody.create(builder.length() == 0 ? "" : builder.substring(0, builder.length() - 1)
                , type);
    }

    private MultipartBody getMultipartBodyByPost(RequestInfo requestInfo) {


        if (requestInfo.hmBody != null && requestInfo.hmBody.size() > 0) {

            MultipartBody.Builder multipartBody = new MultipartBody.Builder();

            multipartBody.setType(MediaType.parse(Constant.Mulitpart+";"+requestInfo.charset ));
            for (Map.Entry<String, ?> entry : requestInfo.hmBody.entrySet()) {

                Log.e("a", "getMultipartBodyByPost:key-"+entry.getKey()+"-value-"+entry.getValue() );
                multipartBody.addFormDataPart(entry.getKey().trim()
                        , entry.getValue().toString().trim());

                if (entry.getValue() != null
                ) {
                    if (entry.getValue() instanceof File[]) {
                        for (File f : (File[]) entry.getValue()
                        ) {
                            multipartBody.addFormDataPart(entry.getKey().trim(), f.getName(),
                                    RequestBody.create(MediaType.parse("file/*"), f));//添加文件
                        }
                        Log.e("a", "getMultipartBodyByPost:File[]" );
                    } else if (entry.getValue() instanceof File) {
                        multipartBody.addFormDataPart(entry.getKey().trim(), ((File) entry.getValue()).getName(),
                                RequestBody.create(MediaType.parse("file/*"), (File) entry.getValue()));//添加文件

                        Log.e("a", "getMultipartBodyByPost:File" );
                    }

                }


            }
            return multipartBody.build();
        }

        return null;
    }

    private RequestBody getRequestBodyByPut(RequestInfo requestInfo) {
        MediaType type = null;
        if (requestInfo.dynamicMediaType != null && requestInfo.dynamicMediaType.length() > 0) {
            type = MediaType.parse(requestInfo.dynamicMediaType+";"+requestInfo.charset);
        } else if (requestInfo.mediaType != null && requestInfo.mediaType.length() > 0) {
            type = MediaType.parse(requestInfo.mediaType+";"+requestInfo.charset);
        } else {
            type = MediaType.parse(Constant.FormUrlEncoded+";"+requestInfo.charset);
        }
        StringBuilder builder = new StringBuilder();
        if (requestInfo.hmBody != null && requestInfo.hmBody.size() > 0) {

            for (Map.Entry<String, ?> entry : requestInfo.hmBody.entrySet()) {
                builder.append(entry.getKey().trim());
                builder.append("=");

                builder.append(entry.getValue().toString().trim());
                builder.append("&");
            }
        }
        Log.e("a", "getRequestBodyByPut:" + builder.toString() + "-mediatype-" + type.toString());
        return RequestBody.create(builder.length() == 0 ? "" : builder.substring(0, builder.length() - 1)
                , type);
    }

    private RequestBody getRequestBodyByDelete(RequestInfo requestInfo) {
        MediaType type = null;
        if (requestInfo.dynamicMediaType != null && requestInfo.dynamicMediaType.length() > 0) {
            type = MediaType.parse(requestInfo.dynamicMediaType+";"+requestInfo.charset);
        } else if (requestInfo.mediaType != null && requestInfo.mediaType.length() > 0) {
            type = MediaType.parse(requestInfo.mediaType+";"+requestInfo.charset);
        } else {
            type = MediaType.parse(Constant.FormUrlEncoded+";"+requestInfo.charset);
        }
        StringBuilder builder = new StringBuilder();
        if (requestInfo.hmBody != null && requestInfo.hmBody.size() > 0) {

            for (Map.Entry<String, ?> entry : requestInfo.hmBody.entrySet()) {
                builder.append(entry.getKey().trim());
                builder.append("=");

                builder.append(entry.getValue().toString().trim());
                builder.append("&");
            }
        }
        Log.e("a", "getRequestBodyByPut:" + builder.toString() + "-mediatype-" + type.toString());
        return RequestBody.create(builder.length() == 0 ? "" : builder.substring(0, builder.length() - 1)
                , type);
    }

    private void setHeaders(Request.Builder builder, RequestInfo requestInfo) {

        HashMap<String, String> finalHeaders = new HashMap<>();
        if (hmCommonHeader != null && hmCommonHeader.size() > 0)
            for (Map.Entry<String, String> entry : hmCommonHeader.entrySet()) {
                finalHeaders.put(entry.getKey(), entry.getValue());
            }


        if (requestInfo.hmHead != null && requestInfo.hmHead.size() > 0)
            for (Map.Entry<String, String> entry : requestInfo.hmHead.entrySet()) {
                finalHeaders.put(entry.getKey(), entry.getValue());
            }


        for (Map.Entry<String, String> entry : finalHeaders.entrySet()) {
            Log.e("a", "head:key-" + entry.getKey() + "-val-" + entry.getValue());
            builder.addHeader(entry.getKey(), entry.getValue());
        }

    }


    // 仅支持异步请求
    @Override
    public <T> Cancelable request(RequestInfo requestInfo, final CallBack<T> callBack) {

        String rootUrl = requestInfo.getRealRootUrl();
        String finalUrl = rootUrl;
        if (requestInfo.reqMethod.equals(Constant.GET)) {
            finalUrl = getUrlByGetReq(rootUrl, requestInfo);
        }

        Request.Builder builder = new Request.Builder()
                .url(finalUrl);

        if (requestInfo.reqMethod.equals(Constant.POST)) {
            if ((requestInfo.dynamicMediaType != null && requestInfo.dynamicMediaType.trim().equals(Constant.Mulitpart))
            ) {
                builder.post(getMultipartBodyByPost(requestInfo));
            } else if ((requestInfo.dynamicMediaType == null || requestInfo.dynamicMediaType.length() <= 0) &&
                    requestInfo.mediaType != null && requestInfo.mediaType.trim().equals(Constant.Mulitpart)) {
                builder.post(getMultipartBodyByPost(requestInfo));
            } else
                builder.post(getRequestBodyByPost(requestInfo));

        } else if (requestInfo.reqMethod.equals(Constant.PUT)) {
            builder.put(getRequestBodyByPut(requestInfo));
        } else if (requestInfo.reqMethod.equals(Constant.DELETE)) {
            builder.delete(getRequestBodyByDelete(requestInfo));
        }

        setHeaders(builder, requestInfo);

        Request request = builder.build();
        Log.e("a", "finalUrl:" + finalUrl);

        final Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callBack == null) {
                    return;
                }
                callBack.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (callBack == null) {
                    Log.e("a", "body:callBack == null");
                    return;
                }
                TestOkHttp.TestBean bean = new TestOkHttp.TestBean();
                bean.h1 = "xuchen";
//                String content = response.body().string();

                Type pType = ReflectHelper.getClassParameterizedType(callBack.getClass())[0];

                if (((Class<?>) pType).getName().equals(Constant.StringPkgName)) {
                    callBack.onSuccess((T) response.body().string());
                    return;
                }


                String content = jsonStrategy.toJson(bean);
                if (callBack != null) {


                    T result = null;

                    try {
                        result = jsonStrategy.fromJson(content, pType);

                    } catch (Exception e) {
                        Log.e("a", "body:" + callBack.getClass());
                        callBack.onFailure(e);
                        return;
                    }
                    callBack.onSuccess(result);
                }
                Log.e("a", "body:" + content);
            }
        });

        return new Cancelable() {
            @Override
            public void cancel() {
                WeakReference<Call> _call = new WeakReference<Call>(call);
                if (_call != null && _call.get() != null)
                    _call.get().cancel();
            }
        };
    }
}
