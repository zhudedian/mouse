package com.ider.mouse.clean;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.ider.mouse.R;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Eric on 2017/6/5.
 */

public class ProcessManager {
    private static final String TAG = "ProcessManager";

    public ProcessManager() {
    }

    public static long getAvailableMemory(Context context) {
        ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        manager.getMemoryInfo(mi);
        return mi.availMem;
    }

    public static void cleanMemory(Context context, boolean forceStop) {
        Log.i("ProcessManager", "cleanMemory: ");
        ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List list = manager.getRunningAppProcesses();

        for(int i = 0; i < list.size(); ++i) {
            ActivityManager.RunningAppProcessInfo appInfo = (ActivityManager.RunningAppProcessInfo)list.get(i);
            Log.i("ProcessManager", appInfo.processName);
            if(forceStop) {
                if(!appInfo.processName.equals(context.getPackageName()) && !appInfo.processName.equals("com.android.inputmethod.latin")
                        && !appInfo.processName.equals("com.ider.launcherpackage")) {
                    forceStop(context, appInfo.processName);
                }
            } else {
                killProcess(context, appInfo.processName);
            }
        }

    }

    public static void killProcess(Context context, String pkgName) {
        ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        new ActivityManager.MemoryInfo();
        manager.killBackgroundProcesses(pkgName);
    }

    public static List<String> getRunningProcess(Context context) {
        ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList list = new ArrayList();
        List infos = manager.getRunningAppProcesses();
        PackageManager pkgManager = context.getPackageManager();
        Iterator var5 = infos.iterator();

        while(var5.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo)var5.next();
            String pkg = info.processName;
            Intent intent = pkgManager.getLaunchIntentForPackage(pkg);
            if(intent != null) {
                list.add(pkg);
            }
        }

        return list;
    }

    public static void forceStop(Context context, String packageName) {
        ActivityManager mActivityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);

        try {
            Method e = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", new Class[]{String.class});
            e.invoke(mActivityManager, new Object[]{packageName});
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    public static void clearDataForPackage(Context context, String packageName) {
        try {
            execCommand("pm clear " + packageName);
        } catch (IOException var3) {
            var3.printStackTrace();
        }

        forceStop(context, packageName);
        Toast.makeText(context, R.string.app_data_cleaned, Toast.LENGTH_SHORT).show();
    }

    private static void execCommand(String command) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec(command);

        try {
            if(proc.waitFor() != 0) {
                Log.i("ProcessManager", "execCommand: exit value = " + proc.exitValue());
            }
        } catch (InterruptedException var4) {
            var4.printStackTrace();
        }

    }
}
