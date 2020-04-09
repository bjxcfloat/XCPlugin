package xc.lib.common.httpclient;

public interface CallBack<T> {

    void onFailure(Exception ex);

    void onSuccess(T t);

}
