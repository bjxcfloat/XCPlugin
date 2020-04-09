package xc.lib.common.httpclient;

public final class ServiceInvoke {



    public static Object invoke(RequestInfo info)
    {

        IHttpClientStrategy httpClient = info.context.getHttpClientStrategy();


        return  httpClient.request(info,info.callBack);
    }

}
