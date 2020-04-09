package com.plugin.test;

import android.content.Context;
import android.widget.Toast;

public class Util {


    public static void toast(Context ctx,String info)
    {
        Toast.makeText(ctx, info, Toast.LENGTH_LONG).show();
    }

    public static int getVal()
    {
        return 100;
    }

}
