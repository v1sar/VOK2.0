package com.bmstu.vok20.Helpers;

/**
 * Created by qwerty on 07.11.16.
 */
import android.os.Handler;
import android.os.Looper;

public class Ui {

    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    public static void run(Runnable runnable) {
        HANDLER.post(runnable);
    }

}