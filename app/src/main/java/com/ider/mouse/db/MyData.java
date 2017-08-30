package com.ider.mouse.db;

import android.os.Environment;

import com.yanzhenjie.andserver.AndServer;

/**
 * Created by Eric on 2017/8/29.
 */

public class MyData {
    public static AndServer andServer;
    public static String serverPath= Environment.getExternalStorageDirectory().getPath();
}
