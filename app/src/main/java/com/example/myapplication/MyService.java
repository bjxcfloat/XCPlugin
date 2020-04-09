package com.example.myapplication;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.myapplication.aidl.ITestAidl;

public class MyService extends Service {
    public MyService() {
    }
    @Override
    public void onCreate() {
        super.onCreate();

        Log.e("TAG", "MyService-onCreate");

    }
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("TAG", "MyService-onBind");
        return stub;
    }
    private ITestAidl.Stub stub = new ITestAidl.Stub()
    {

        @Override
        public int getRes(int i) throws RemoteException {
            return i;
        }
    };
    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);

        Log.e("TAG", "MyService-unbindService");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.e("TAG", "MyService-onDestroy");
    }
}
