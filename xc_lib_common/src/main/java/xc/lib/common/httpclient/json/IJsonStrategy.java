package xc.lib.common.httpclient.json;

import java.lang.reflect.Type;

public interface IJsonStrategy {


   <T> String toJson(T t);
   <T> T fromJson(String string, Type type);

}
