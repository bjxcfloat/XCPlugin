package xc.lib.host.activity;


import android.app.Activity;
import android.util.Log;

import com.google.gson.Gson;
import xc.lib.common.plugins.ComConstant;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import xc.lib.host.BaseApplication;
import xc.lib.host.HostConfig;
import xc.lib.host.PitInfo;
import xc.lib.host.PluginInfo;
import xc.lib.host.PluginManager;

//Activity坑位管理器
public class ActivityPitManager {


    private static ActivityPitManager activityPitManager = null;
    private static byte[] _lock = new byte[0];
    private HashMap<Integer, PitInfo> standardActivityContainer = new HashMap<Integer, PitInfo>(HostConfig.StandardActivityPitCount);
    private HashMap<Integer, PitInfo> singleTopActivityContainer = new HashMap<Integer, PitInfo>(HostConfig.SingleTopActivityPitCount);
    private HashMap<Integer, PitInfo> singleTaskActivityContainer = new HashMap<Integer, PitInfo>(HostConfig.SingleTaskActivityPitCount);
    private HashMap<Integer, PitInfo> singleInstanceActivityContainer = new HashMap<Integer, PitInfo>(HostConfig.SingleInstanceActivityPitCount);


    private ActivityPitManager() {
        for (int i = 0; i < HostConfig.StandardActivityPitCount; i++) {
            PitInfo p1 = new PitInfo();

            p1.pitClsName = BaseApplication.getBaseApp().getPackageName() + ".a_" + i;
            p1.lauchmode = 0;
            standardActivityContainer.put(i, p1);
        }
        for (int i = 0; i < HostConfig.SingleTopActivityPitCount; i++) {
            PitInfo p1 = new PitInfo();
            p1.pitClsName = BaseApplication.getBaseApp().getPackageName() + ".b_" + i;
            p1.lauchmode = 1;
            singleTopActivityContainer.put(i, p1);
        }
        for (int i = 0; i < HostConfig.SingleTaskActivityPitCount; i++) {
            PitInfo p1 = new PitInfo();
            p1.pitClsName = BaseApplication.getBaseApp().getPackageName() + ".c_" + i;
            p1.lauchmode = 2;
            singleTaskActivityContainer.put(i, p1);
        }
        for (int i = 0; i < HostConfig.SingleInstanceActivityPitCount; i++) {
            PitInfo p1 = new PitInfo();
            p1.pitClsName = BaseApplication.getBaseApp().getPackageName() + ".d_" + i;
            p1.lauchmode = 3;
            singleInstanceActivityContainer.put(i, p1);
        }

    }

    private synchronized void recycleActivityPitByClsName(String clsName, HashMap<Integer, PitInfo> hm) {
        for (Map.Entry<Integer, PitInfo> entry : hm.entrySet()) {
            if (entry.getValue().realClsName.equals(clsName) && entry.getValue().isOccupied) {
                if (entry.getValue().curAct != null && entry.getValue().curAct.get() != null) {
                    Activity act = entry.getValue().curAct.get();
                    if (entry.getValue().runningState.equals(ComConstant.ActivityRunning)) {
                        if (!act.isFinishing()) {
                            act.finish();

                        }
                    }
                    act = null;
                }

                entry.getValue().isOccupied = false;

            }

        }
    }


    // 回收坑位
    public synchronized void recycleActivityPitByClsName(String clsName) {
        recycleActivityPitByClsName(clsName, standardActivityContainer);
        recycleActivityPitByClsName(clsName, singleTopActivityContainer);
        recycleActivityPitByClsName(clsName, singleTaskActivityContainer);
        recycleActivityPitByClsName(clsName, singleInstanceActivityContainer);

    }

    // 得到最新的坑位信息
    public synchronized PitInfo getLatestPitInfoByRealClsName(String clsName) {
        long ts = -1;
        PitInfo pitInfo = null;
        for (Map.Entry<Integer, PitInfo> entry : standardActivityContainer.entrySet()) {
            if (entry.getValue().realClsName.equals(clsName)) {

                ts = Math.max(ts, entry.getValue().timestamp);
                if (ts == entry.getValue().timestamp) {
                    pitInfo = entry.getValue();
                }
            }

        }
        if (ts < 0) {
            for (Map.Entry<Integer, PitInfo> entry : singleTopActivityContainer.entrySet()) {
                if (entry.getValue().realClsName.equals(clsName)) {

                    ts = Math.max(ts, entry.getValue().timestamp);
                    if (ts == entry.getValue().timestamp) {
                        pitInfo = entry.getValue();
                    }
                }

            }
        }
        if (ts < 0) {
            for (Map.Entry<Integer, PitInfo> entry : singleTaskActivityContainer.entrySet()) {
                if (entry.getValue().realClsName.equals(clsName)) {

                    ts = Math.max(ts, entry.getValue().timestamp);
                    if (ts == entry.getValue().timestamp) {
                        pitInfo = entry.getValue();
                    }
                }

            }
        }
        if (ts < 0) {
            for (Map.Entry<Integer, PitInfo> entry : singleInstanceActivityContainer.entrySet()) {
                if (entry.getValue().realClsName.equals(clsName)) {

                    ts = Math.max(ts, entry.getValue().timestamp);
                    if (ts == entry.getValue().timestamp) {
                        pitInfo = entry.getValue();
                    }
                }

            }
        }

//        Log.e("xasdsad","setIntent-"+new Gson().toJson(pitInfo.intent));
        return pitInfo;

    }

    // 设置Activity的运行状态
    public synchronized void setPitStateByKey(int key, String runningState, int lauchmode, Activity act) {

        PitInfo p = null;
        switch (lauchmode) {
            case 0:
                p = standardActivityContainer.get(key);

                break;
            case 1:
                p = singleTopActivityContainer.get(key);


                break;
            case 2:
                p = singleTaskActivityContainer.get(key);
                break;
            case 3:
                p = singleInstanceActivityContainer.get(key);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + lauchmode);
        }
        if (p != null) {
            if (runningState.equals(ComConstant.ActivityDestory)) {

                p.isOccupied = false;
                p.curAct = null;
//                Log.e("sxasasd",new Gson().toJson(standardActivityContainer));
            } else {
                p.runningState = runningState;
                p.curAct = new WeakReference<Activity>(act);
            }
        }


    }


    public synchronized void findRealActivityClass(PitInfo p, String pitClass) {

        long ts = -1;
        String clsName = "";
        String pluginName = "";
        for (Map.Entry<Integer, PitInfo> entry : standardActivityContainer.entrySet()) {
            if (entry.getValue().pitClsName.equals(pitClass)) {

                ts = Math.max(ts, entry.getValue().timestamp);
                if (ts == entry.getValue().timestamp) {
                    clsName = entry.getValue().realClsName;
                    pluginName = entry.getValue().pluginName;
                }
            }

        }
        if (ts < 0) {
            for (Map.Entry<Integer, PitInfo> entry : singleTopActivityContainer.entrySet()) {
                if (entry.getValue().pitClsName.equals(pitClass)) {

                    ts = Math.max(ts, entry.getValue().timestamp);
                    if (ts == entry.getValue().timestamp) {
                        clsName = entry.getValue().realClsName;
                        pluginName = entry.getValue().pluginName;
                    }
                }

            }
        }
        if (ts < 0) {
            for (Map.Entry<Integer, PitInfo> entry : singleTaskActivityContainer.entrySet()) {
                if (entry.getValue().pitClsName.equals(pitClass)) {

                    ts = Math.max(ts, entry.getValue().timestamp);
                    if (ts == entry.getValue().timestamp) {
                        clsName = entry.getValue().realClsName;
                        pluginName = entry.getValue().pluginName;
                    }
                }

            }
        }
        if (ts < 0) {
            for (Map.Entry<Integer, PitInfo> entry : singleInstanceActivityContainer.entrySet()) {
                if (entry.getValue().pitClsName.equals(pitClass)) {

                    ts = Math.max(ts, entry.getValue().timestamp);
                    if (ts == entry.getValue().timestamp) {
                        clsName = entry.getValue().realClsName;
                        pluginName = entry.getValue().pluginName;
                    }
                }

            }
        }
        p.pluginName = pluginName;
        p.realClsName = clsName;
    }


    public static ActivityPitManager getInstance() {
        if (activityPitManager == null) {
            synchronized (_lock) {
                if (activityPitManager == null) {
                    activityPitManager = new ActivityPitManager();

                }
            }
        }
        return activityPitManager;
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
    public synchronized PitInfo occupyActivityPit(String pkgName, String clsName) {

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
        int lauchmode = pluginInfo.getLauchMode(clsName);
        switch (lauchmode) {
            //standardActivity
            case 0:

                occupyPit(pi, key, pkgName, clsName, standardActivityContainer);


                break;
            //singleTopActivity
            case 1:

                occupyPit(pi, key, pkgName, clsName, singleTopActivityContainer);


                break;
            //singleTaskActivity
            case 2:

                occupyPit(pi, key, pkgName, clsName, singleTaskActivityContainer);


                break;
            //singleInstanceActivity
            case 3:

                occupyPit(pi, key, pkgName, clsName, singleInstanceActivityContainer);


                break;

        }


        return pi;

    }


    public synchronized void setIntent(PitInfo info) {
        PitInfo oldpit;
        switch (info.lauchmode) {
            case 0:
                oldpit = standardActivityContainer.get(info.pitKey);
                oldpit.intent = info.intent;

//                Log.e("xasdsad","setIntent-"+new Gson().toJson(oldpit));
                break;
            case 1:
                oldpit = singleTopActivityContainer.get(info.pitKey);
                oldpit.intent = info.intent;

                break;
            case 2:
                oldpit = singleTaskActivityContainer.get(info.pitKey);
                oldpit.intent = info.intent;

                break;
            case 3:
                oldpit = singleInstanceActivityContainer.get(info.pitKey);
                oldpit.intent = info.intent;

                break;

        }
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
            pi.lauchmode = oldPit.lauchmode;
            col.put(key, pi);

        }
        // 无空的坑位
        else {


            PitInfo p = col.remove(minTimeSpanKey);

//            // 如果还在运行状态，则强制结束
            if (p.runningState.equals(ComConstant.ActivityRunning)) {
                if (p.curAct != null && p.curAct.get() != null) {
                    Activity a = p.curAct.get();
                    a.finish();
                    a = null;
                }
            }
            pi.isOccupied = true;
            pi.pitClsName = p.pitClsName;
            pi.lauchmode = p.lauchmode;
            col.put(key, pi);

//            Log.e("xsadasd", "minTimeSpanKey-" + minTimeSpanKey + "--" + new Gson().toJson(col));
        }
    }


}
