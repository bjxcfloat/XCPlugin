package com.plugin.test;

import androidx.appcompat.app.AppCompatActivity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import xc.lib.plugin.PluginActivity;
import xc.lib.plugin.PluginFragmentActivity;

public class ThirdActivity extends PluginFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        CenterFragment frg = new CenterFragment();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager. beginTransaction();
        transaction.replace(R.id.right_layout, frg);
        transaction.commit();


        TextView textView2 = this.findViewById(R.id.textView2);

        Log.e("xasdasd","k1-"+getIntent().getStringExtra("k1"));

        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.putExtra("k1","4321");
                setResult(100,i);
                Log.e("xasdasd","thirdactivity-onClick");
                finish();
            }
        });



    }

    @Override
    protected void onPause()
    {
        super.onPause();

        Log.e("xasdasd","thirdactivity-onPause");


    }



    @Override
    protected void onDestroy()
    {
        Log.e("xasdasd","thirdactivity-onDestroy");
        super.onDestroy();



    }
}
