package com.plugin.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.plugin.test.R;

import java.io.File;
import java.io.IOException;

import xc.lib.plugin.PluginAppCompatActivity;

public class MainActivity extends PluginAppCompatActivity {

    ImageView imageView;
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

          imageView = this.findViewById(R.id.imageView);


        TextView textView = this.findViewById(R.id.textView);
        textView.setText("click me");
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (hasPermission())
                    takePic();
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
//                Intent is = new Intent();
//                is.setClassName("com.plugin.test","com.plugin.test.SecondActivity");
//                is.putExtra("k1","xuxu");
////                i.setClassName("com.mine.test","com.example.myapplication.MainActivity");
//                startActivity(is);
//                startActivityForResult(i,110);

            }
        });


    }

    private boolean hasPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 123);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 123:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //takePhoto();
                    takePic();
                }
                break;
        }
    }

    File outputImage;
    public void takePic() {
        Uri imageUri;
        int reqCode = 12;
        //创建File对象，用于存储拍照后的照片
          outputImage = new File(getExternalCacheDir(), "output_image.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(this, "com.fileprovider", outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }
        //启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, reqCode);
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
        if (requestCode == 12) {
            Bitmap bitmap = BitmapFactory.decodeFile(outputImage.getAbsolutePath());
            imageView.setImageBitmap(bitmap);
            Log.e("xasdasd","resultCode-"+resultCode+"-" );
        }
    }
}
