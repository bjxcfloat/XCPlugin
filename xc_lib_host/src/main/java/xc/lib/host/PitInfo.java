package xc.lib.host;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

// 坑实体
public class PitInfo   {
    // 是否占用了
    public boolean isOccupied = false;
    public String pluginName = "";
    public int lauchmode = -1;
    public String realClsName = "";
    public String pitClsName = "";
    transient  public Intent intent;
    public int pitKey = -1;
    public long timestamp = 0L;
    transient   public WeakReference<Activity> curAct=null;
    transient  public WeakReference<Service> curServ=null;

    //暂时对应 oncreate,ondestory
    public String runningState = "";


}
