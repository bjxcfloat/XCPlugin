package xc.lib.common.task;

import android.os.Handler;
import android.os.Looper;

public class Task {

    public static void execOnUiThrad(Runnable call) {

        if (Looper.myLooper() == Looper.getMainLooper()) {
            call.run();
        } else {
            new Handler(Looper.getMainLooper()).post(call);
        }
    }

    public static void execOnUiThrad(Runnable call, long delay) {

        new Handler(Looper.getMainLooper()).postDelayed(call, delay);
    }

}
