package xc.lib.common.httpclient;

import java.lang.reflect.Type;
import java.util.HashMap;

public class RequestInfo {

    // 跟地址
    public String baseUrl = "";

    // 相对路径或全地址
    public String reqUrl = "";

    public Bridge context;

    public String charset = "";

    // http请求头Head
    public HashMap<String, String> hmHead = new HashMap<String, String>();

    // http请求体
    public HashMap<String, Object> hmBody = new HashMap<String, Object>();

    public String reqMethod = "";

    public String mediaType = "";

    // 参数传入的动态url
    public String dynamicUrl = "";
    // 参数传入的动态url
    public String dynamicMediaType = "";

    public Type callbackType;

    public CallBack<?> callBack;

    // 获取根网址
    public String getRealRootUrl() {
        String rootUrl = "";
        if (dynamicUrl != null && dynamicUrl.length() > 0) {
            dynamicUrl = dynamicUrl.toLowerCase().trim();
            if (dynamicUrl.indexOf("http") == 0) {
                rootUrl = dynamicUrl;
            }
        } else {

            if (reqUrl != null && reqUrl.length() > 0 && reqUrl.toLowerCase().indexOf("http") == 0) {

                rootUrl = reqUrl;

            } else {

                rootUrl = baseUrl + reqUrl;
            }
        }
        return rootUrl;
    }

}
