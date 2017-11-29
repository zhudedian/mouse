package com.ider.mouse.db;

import android.os.Environment;

import com.ider.mouse.MouseService;
import com.ider.mouse.util.SocketServer;
import com.ider.mouse.view.MouseView;
import com.yanzhenjie.andserver.AndServer;

/**
 * Created by Eric on 2017/8/29.
 */

public class MyData {
    public static MouseView mouseView;
    public static SocketServer server;
    public static AndServer andServer;
    public static String serverPath= Environment.getExternalStorageDirectory().getPath();
    public static String editText;
    public static String appIconPath = "/sdcard/Pictures/icons";
}
