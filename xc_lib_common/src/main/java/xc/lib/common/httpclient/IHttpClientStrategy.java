package xc.lib.common.httpclient;

import xc.lib.common.httpclient.json.IJsonStrategy;

import java.util.HashMap;

public interface IHttpClientStrategy {

   void setJsonStrategy(IJsonStrategy jsonStrategy);

   void setReadTimeout(long seconds);
   void setConnectTimeout(long seconds);
   void setCallTimeout(long seconds);
   void setHeaders(HashMap<String,String> hmHeader);


   boolean isInit();

   void init();

   <T> Cancelable request(RequestInfo requestInfo,CallBack<T> callBack);



}
