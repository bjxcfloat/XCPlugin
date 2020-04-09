package com.example.myapplication;

import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import xc.lib.host.BaseApplication;
import xc.lib.host.PluginInfo;
import xc.lib.host.PluginInstallListener;
import xc.lib.host.PluginManager;

public class HostApplication extends BaseApplication  implements PluginInstallListener {


    @Override
    public void onCreate() {
        super.onCreate();

        new Thread(){
            @Override
            public void run() {
                super.run();

                // 安装示例
                try {
                    String dir = HostApplication.this.getFilesDir().getAbsolutePath() + "/temp";
                    String newApk = dir + "/temp.apk";
                    if (!new File(dir).exists()) {
                        new File(dir).mkdir();
                    }


                    InputStream file = getAssets().open("test.apk");
                    FileOutputStream fos = new FileOutputStream(newApk);
                    byte[] buffer = new byte[1024];
                    int byteCount;
                    while ((byteCount = file.read(buffer)) != -1) {
                        fos.write(buffer, 0, byteCount);
                    }
                    fos.flush();
                    file.close();
                    fos.close();

                    PluginManager.getInstance().setInstallListener(HostApplication.this);
                    PluginManager.getInstance().install(newApk);

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }.start();



    }
    @Override
    public void onInstallSuccess(PluginInfo pi) {

        Toast.makeText(this, "install success", Toast.LENGTH_SHORT).show();
        ;
    }

    @Override
    public void onInstallFailure(Exception e, PluginInfo pi) {

        if (e != null)
            throw new IllegalArgumentException(e);

        Toast.makeText(this, "install Failure", Toast.LENGTH_SHORT).show();
        ;
    }
}
