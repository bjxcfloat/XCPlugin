package xc.lib.host.broadcast;

import android.content.IntentFilter;

import java.util.List;

public interface IStaticBroadCastReceiverManager {

    void registerReceiver(String clsName, List<IntentFilter> intentFilters, Class<?> cls)  throws  Exception ;
    void unRegisterReceiver(String clsName);
}
