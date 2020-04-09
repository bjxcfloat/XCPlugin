package com.plugin.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.plugin.test.R;

import xc.lib.plugin.PluginAppCompatActivity;

public class MainActivity extends PluginAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Intent i = new Intent();
        i.setClass(this, TestService.class);
//        i.setClassName("com.plugin.test", TestService.class);
        i.putExtra("kk", "vvvv");

//        this.startService(i);
//        bindService(i, connection, Context.BIND_AUTO_CREATE);

        TextView textView = this.findViewById(R.id.textView);
        textView.setText("click me");
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent i = new Intent();
//                i.putExtra("xasd","adasdasd");
//
//                setResult(120,i);
//                finish();

//                String uri = "content://privoders0/com.plugin.test/delete";
//                int i =getContentResolver().delete(Uri.parse(uri), null, null);
//
//                Util.toast(MainActivity.this,i+"");

//                stopService(i);
//                unbindService(connection);
//                Toast.makeText(MainActivity.this,"hello  ",Toast.LENGTH_LONG).show();

//                Intent i = new Intent("com.asdasd");
//                i.putExtra("k1","kkkkkkkkk");
//                sendBroadcast(i);
                Intent is = new Intent();
                is.setClassName("com.plugin.test","com.plugin.test.SecondActivity");
                is.putExtra("k1","xuxu");
//                i.setClassName("com.mine.test","com.example.myapplication.MainActivity");
                startActivity(is);
//                startActivityForResult(i,110);

            }
        });


    }

    TestService ser;
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            ser.disconnect();
            Log.e("asdasasd", "Maincticty--onServiceDisconnected-");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("asdasasd", "Maincticty--onServiceConnected-");
            ser = ((TestService.MyBinder) service).getService();
            ser.SHow();
        }
    };
    TestRecever testRecever;

    private static class TestRecever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Toast.makeText(context, "plugin-MainActivity-broadcast:" + action + "-" + intent.getStringExtra("asd"), Toast.LENGTH_LONG).show();

        }
    }

    @Override
    protected void onDestroy() {

//        unregisterReceiver(testRecever);
//        unbindService(connection);

        super.onDestroy();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 110) {

//            Log.e("xasdasd","resultCode-"+resultCode+"-"+data.getStringExtra("k1"));
        }
    }
}
