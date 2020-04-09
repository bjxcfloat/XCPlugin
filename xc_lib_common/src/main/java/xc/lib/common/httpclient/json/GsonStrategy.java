package xc.lib.common.httpclient.json;

import com.google.gson.Gson;

import java.lang.reflect.Type;

public final class GsonStrategy implements IJsonStrategy {

    private Gson gson = null;

    public GsonStrategy() {
        gson = new Gson();
    }

    @Override
    public <T> String toJson(T t) {


        return gson.toJson(t);
    }

    @Override
    public <T>  T fromJson(String string,Type type) {



        return (T) gson.fromJson(string, type);
    }
}
