package xc.lib.common.util;

import android.util.Log;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class Random {


    // 取随机数
    public static int getRanNumber(long seed,int bound)  {
        java.util.Random ran = new java.util.Random(seed);

        int i = ran.nextInt(bound);
        return i;
    }


}
