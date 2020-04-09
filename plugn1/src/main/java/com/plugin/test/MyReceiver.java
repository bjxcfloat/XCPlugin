package com.plugin.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("com.asdasd"))
        {
            Util.toast(context,"this is plugin MyReceiver-"+intent.getStringExtra("k1"));
        }
    }
}
