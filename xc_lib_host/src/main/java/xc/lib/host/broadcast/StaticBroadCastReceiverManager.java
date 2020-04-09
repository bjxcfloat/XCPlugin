package xc.lib.host.broadcast;


import android.content.BroadcastReceiver;
import android.content.IntentFilter;

import java.util.HashMap;
import java.util.List;

import xc.lib.host.BaseApplication;

//主要用于管理在插件配置文件静态注册的广播类，静态广播在插件安装时就会注册，生命周期跟宿主一致,当然中途如果卸载插件后，此插件的广播会解除注册
public class StaticBroadCastReceiverManager implements IStaticBroadCastReceiverManager {

    private HashMap<String, BroadcastReceiver> broadcasts;
    private static Byte[] lock = new Byte[0];
    private static StaticBroadCastReceiverManager staticBroadCastReceiverManager = null;

    private StaticBroadCastReceiverManager() {
        broadcasts = new HashMap<String, BroadcastReceiver>();

    }

    public static StaticBroadCastReceiverManager getInstance() {
        if (staticBroadCastReceiverManager == null) {
            synchronized (lock) {
                if (staticBroadCastReceiverManager == null) {
                    staticBroadCastReceiverManager = new StaticBroadCastReceiverManager();
                }
            }
        }

        return staticBroadCastReceiverManager;
    }

    // 注册插件静态广播
    public void registerReceiver(String clsName, List<IntentFilter> intentFilters, Class<?> cls) throws InstantiationException, IllegalAccessException {

        BroadcastReceiver rece = (BroadcastReceiver) cls.newInstance();

        int i = 0;
        IntentFilter newIntentFilters = new IntentFilter();
        for (IntentFilter filter : intentFilters) {

            newIntentFilters.addAction(filter.getAction(i++));

        }

        BaseApplication.getBaseApp().registerReceiver(rece, newIntentFilters);
        broadcasts.put(clsName, rece);

    }

    // 卸载插件静态广播
    public void unRegisterReceiver(String clsName) {
        if (broadcasts.get(clsName) != null)
            BaseApplication.getBaseApp().unregisterReceiver(broadcasts.get(clsName));
        broadcasts.remove(clsName);
    }


}
