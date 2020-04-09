package xc.lib.host.service;

import android.app.Service;
import android.content.pm.ServiceInfo;

import xc.lib.common.plugins.ComConstant;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import xc.lib.host.BaseApplication;
import xc.lib.host.HostConfig;
import xc.lib.host.PitInfo;
import xc.lib.host.PluginInfo;
import xc.lib.host.PluginManager;


//  Service的坑位管理器
public class ServicePitManager {

    private static ServicePitManager servicePitManager = null;
    private static byte[] _lock = new byte[0];
    private HashMap<Integer, PitInfo> serviceContainer = new HashMap<Integer, PitInfo>(HostConfig.ServicePitCount);

    private ServicePitManager() {
        for (int i = 0; i < HostConfig.ServicePitCount; i++) {
            PitInfo p1 = new PitInfo();

            p1.timestamp = 0;
            p1.pitClsName = BaseApplication.getBaseApp().getPackageName() + ".s_" + i;
            serviceContainer.put(i, p1);
        }
    }

    public static ServicePitManager getInstance() {
        if (servicePitManager == null) {
            synchronized (_lock) {
                if (servicePitManager == null) {
                    servicePitManager = new ServicePitManager();

                }
            }
        }
        return servicePitManager;
    }

    public synchronized void forceDisposeServicePit(HashMap<String, ServiceInfo> hmService) {
        for (Map.Entry<Integer, PitInfo> entry : serviceContainer.entrySet()) {
            for (String s : hmService.keySet()) {
                if (s.equals(entry.getValue().realClsName)) {

                    try {
                        if (entry.getValue().curServ != null && entry.getValue().curServ.get() != null) {
                            entry.getValue().curServ.get().stopSelf();
                            entry.getValue().curServ = null;
                        }
                    } catch (Exception e) {
                    }

                    entry.getValue().isOccupied = false;

                }
            }

        }

    }

    // 设置Activity的运行状态
    public synchronized void setServicePitStateByKey(String pkg, String cls, String runningState, Service service) {

        PitInfo p = null;
        long ts = 0L;
        int key = 0;
        for (Map.Entry<Integer, PitInfo> entry : serviceContainer.entrySet()) {
            if (entry.getValue().pitClsName.equals(cls)) {

                ts = Math.max(ts, entry.getValue().timestamp);
                if (ts == entry.getValue().timestamp) {
                    key = entry.getKey();
                }
            }

        }
        p = serviceContainer.get(key);
        if (p != null) {
            if (runningState.equals(ComConstant.ActivityDestory)) {
//                if(service==null&&p.curServ!=null&&p.curServ.get()!=null)
//                {
//                    Log.e("asdasasd","TestService-pitmanager-stopSelf");
//                    p.curServ.get().stopSelf();
//                }

                p.isOccupied = false;
                p.curServ = null;
//                Log.e("sxasasd",new Gson().toJson(standardActivityContainer));
            } else {
                p.runningState = runningState;
                p.curServ = new WeakReference<Service>(service);
            }
        }


    }

    public synchronized void findRealServClass(PitInfo p, String pitClass) {

        long ts = -1;
        String clsName = "";
        String pluginName = "";
        for (Map.Entry<Integer, PitInfo> entry : serviceContainer.entrySet()) {
            if (entry.getValue().pitClsName.equals(pitClass)) {

                ts = Math.max(ts, entry.getValue().timestamp);
                if (ts == entry.getValue().timestamp) {
                    clsName = entry.getValue().realClsName;
                    pluginName = entry.getValue().pluginName;
                }
            }

        }

        p.pluginName = pluginName;
        p.realClsName = clsName;
    }

    private int getHashCodeKey(String pkgName, String clsName, long ts) {
        // 构造key
        StringBuilder sKey = new StringBuilder();
        sKey.append(pkgName);
        sKey.append("_");
        sKey.append(clsName);
        sKey.append("_");
        sKey.append(ts);
        int ikey = sKey.hashCode();

        return ikey;
    }

    // 占坑
    public synchronized PitInfo occupyServicePit(String pkgName, String clsName) {

        long ts = System.currentTimeMillis();
        int key = getHashCodeKey(pkgName, clsName, ts);
        PitInfo pi = new PitInfo();
        pi.pluginName = pkgName;
        pi.realClsName = clsName;
        pi.timestamp = ts;
        pi.pitKey = key;
        //standardActivity =0;singleTopActivity=1;singleTaskActivity=2;singleInstanceActivity=3

        PluginInfo pluginInfo = PluginManager.getInstance().getPluginInfo(pkgName);
        if (pluginInfo == null)
            throw new IllegalArgumentException("pluginInfo is null");

        occupyPit(pi, key, pkgName, clsName, serviceContainer);


        return pi;

    }

    private void occupyPit(PitInfo pi, int key, String pkgName, String clsName, HashMap<Integer, PitInfo> col) {


        int k = -1;
        boolean isFind = false;
        long minTimeSpan = 0;
        int minTimeSpanKey = 0;
        for (Map.Entry<Integer, PitInfo> entry : col.entrySet()) {

            PitInfo p = entry.getValue();
            if (k == -1) {
                minTimeSpan = p.timestamp;
            }
            k = entry.getKey();
            minTimeSpan = Math.min(minTimeSpan, p.timestamp);
            if (p.timestamp == minTimeSpan)
                minTimeSpanKey = k;
            if (!p.isOccupied) {

                isFind = true;
                break;
            }
        }

        // 有空的坑位
        if (isFind) {
            PitInfo oldPit = col.remove(k);
            pi.isOccupied = true;
            pi.pitClsName = oldPit.pitClsName;
            col.put(key, pi);

        }
        // 无空的坑位
        else {
            PitInfo p = col.remove(minTimeSpanKey);

            // 如果还在运行状态，则强制结束
            if (p.runningState.equals(ComConstant.ActivityRunning)) {
                if (p.curServ != null && p.curServ.get() != null) {
                    Service a = p.curServ.get();
                    a.stopSelf();
                    a = null;
                }
            }
            pi.isOccupied = true;
            pi.pitClsName = p.pitClsName;
            col.put(key, pi);
        }
    }

}
