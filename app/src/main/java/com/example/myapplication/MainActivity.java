package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import xc.lib.common.task.Task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import xc.lib.host.PluginManager;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static boolean isLogin = true;
    public static Context ctx;
    private CountDownLatch dl = new CountDownLatch(2);
    protected TextView tv;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ctx = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        new TestOkHttp().test();


//        Toast.makeText(MainActivity.this,"hello xuchen",Toast.LENGTH_LONG).show();

//        this.startActivityForResult();

        btn = this.findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                try {
//                    PluginManager.getInstance().unInstall("com.plugin.test");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Log.e("xasdasddas","unInstall -e");
//                }


                Intent i = new Intent();
                i.setClassName("com.plugin.test", "com.plugin.test.MainActivity");
                PluginManager.getInstance().startActivityForResult(MainActivity.this, i, 110);
//                Intent i = new Intent();
//                i.setClassName("com.plugin.test", "com.plugin.test.TestService");
//                i.putExtra("kk","vvvv");
//                PluginManager.getInstance().startService(MainActivity.this, i);

//                Intent i = new Intent("com.asdasd");
//                i.putExtra("k1","kkkkkkkkk");
//                sendBroadcast(i);

            }
        });

        Log.e("xc", "handleData-result-" + handleData(123));


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 110) {

            if (data != null)
                Toast.makeText(MainActivity.this, resultCode + "-onActivityResult-" + data.getStringExtra("xasd"), Toast.LENGTH_LONG).show();
//            Log.e("xasdasd","resultCode-"+resultCode+"-"+data.getStringExtra("k1"));
        }
    }

    private static class TestRecever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Toast.makeText(context, "host-MainActivity-broadcast:" + action + "-" + intent.getStringExtra("asd"), Toast.LENGTH_LONG).show();

        }
    }

    @Override
    protected void onDestroy() {


        super.onDestroy();

    }

    int findTreeNodeDeep(TreeNode node) {

        if (node == null)
            return 0;
        TreeNode leftNode = node.leftNode;
        TreeNode rightNode = node.rightNode;


        return Math.max(findTreeNodeDeep(leftNode), findTreeNodeDeep(rightNode)) + 1;

    }


    private static class TreeNode {
        public String name = "";
        public TreeNode leftNode = null;
        public TreeNode rightNode = null;
    }

    private static class Node {
        public String name = "";
        public Node nextNode = null;
    }

    private void quickSort(Integer[] list2, int left, int right) {
        if (right <= 0 || left >= right) return;

        int temp = list2[left];
        int _left = left;
        int _right = right;
        while (_left < _right) {
            while (_left < _right && temp < list2[_right]) {
                _right--;
            }
            list2[_left] = list2[_right];
            while (_left < _right && temp > list2[_left]) {
                _left++;
            }
            list2[_right] = list2[_left];

        }
        list2[_left] = temp;
        quickSort(list2, left, _left - 1);
        quickSort(list2, left + 1, right);

    }

    List<Integer> list = new ArrayList<>();

    private void findAllNumGroup(int[] array, int startindex) {
        if (startindex >= array.length) return;
        for (int i = startindex; i < array.length - 1; i++) {
            //  for (int j = i + 1; j < array.length; j++) {

            int[] array2 = Arrays.copyOf(array, array.length);

            int temp = array[startindex];
            array2[startindex] = array[i + 1];
            array2[i + 1] = temp;
            StringBuilder sb = new StringBuilder();
            for (int as : array2)
                sb.append(as);
            list.add(Integer.valueOf(sb.toString()));
            findAllNumGroup(array2, startindex + 1);
        }
    }


    @JudgeLogin(tipType = JudgeLogin.SHOW_DIALOG, loginActivity = MainActivity.class)
    public int handleData(int i) {
        Log.e("xc", "handleData-" + i);
        return 0;
    }

    public void testAspect() {
        Log.e("xc", "testAspect");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        this.setIntent(intent);
    }


}
