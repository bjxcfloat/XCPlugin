package com.plugin.test;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import xc.lib.plugin.PluginAppCompatActivity;

public class SecondActivity extends PluginAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);


        Button btn = (Button)this.findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setClassName("com.plugin.test","com.plugin.test.MainActivity");
                startActivity(i);
            }
        });

    }
}
