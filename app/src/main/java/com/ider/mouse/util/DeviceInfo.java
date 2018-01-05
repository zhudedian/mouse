package com.ider.mouse.util;

import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Eric on 2017/10/19.
 */

public class DeviceInfo {

    public static String getAllInfo(Context context){
        return "设备型号："+Build.MODEL+"\n"+
                "固件版本："+Build.DISPLAY+"\n"+
                "当前分辨率："+getResolutionValue()+"\n"+
                "网络连接："+getConnectInfo(context);

    }
    public static String getResolutionValue(){
        if(!getCpuName().equals("sun8i")) {
            String path = getHDMIFile();
            if (path == null) {
                return "";
            }
            File file = new File(path);
            try {
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);
                String mode = br.readLine();
                return mode;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
           return  "HDMI 720P 50Hz";
        }
        return "";
    }
    private static String getHDMIFile() {
        if (Build.DEVICE.contains("rk")) {
            return "/sys/class/display/HDMI/mode";
        } else if (getCpuName().contains("Amlogic")) {
            return "/sys/class/display/mode";
        } else if(getCpuName().equals("sun8i")) {
            return null;
        }
        return null;
    }
    public static String getCpuName() {
        File file = new File("/proc/cpuinfo");
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("Hardware")) {
                    String cpu = line.split(":")[1];
                    return cpu.trim();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "unknown";
    }
    public static String getSDCardStorage(Context context) {
        String[] sdCardInfo = new String[2];
        String state = Environment.getExternalStorageState();
        File sdcardDir = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(sdcardDir.getPath());
        long bSize = sf.getBlockSize();
        long bCount = sf.getBlockCount();
        long availBlocks = sf.getAvailableBlocks();

        long total = bSize * bCount;
        long available = bSize * availBlocks;

        sdCardInfo[0] = String.valueOf(formatTotalSd(total));
        sdCardInfo[1] = getSDAvailableSize(context);

        return sdCardInfo[0] + "/" + sdCardInfo[1];

    }
    private static String getSDAvailableSize(Context context) {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(context, blockSize * totalBlocks);
    }

    private static String formatTotalSd(long total) {
        int totalM = (int) (total / 1024 / 1024);
        if (totalM < 4096) {
            return "4GB";
        } else if (totalM > 4096 && totalM < 8129) {
            return "8GB";
        } else if (totalM > 8129 && totalM < 16384) {
            return "16GB";
        } else if (totalM > 16384 && totalM < 32768) {
            return "32GB";
        } else {
            return "64GB";
        }
    }
    public static String getTotalMemorySize(Context context) {
        String dir = "/proc/meminfo";
        try {
            FileReader fr = new FileReader(dir);
            BufferedReader br = new BufferedReader(fr, 2048);
            String memoryLine = br.readLine().replace(" ", "");
//            Log.i("tag", "memoryLine = " + memoryLine);
            String subMemoryLine = memoryLine.split(":")[1];
            br.close();
            String kb = subMemoryLine.replaceAll("[a-zA-Z]", "");
//            Log.i("tag", "kb = " + kb);
            return kb2mb(kb);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "不可用";
    }

    private static String kb2mb(String kb) {
        Long kbl = Long.valueOf(kb);
        int mb = (int) (kbl / 1024);
        return String.valueOf(mb) + "M";
    }
    public static String getConnectInfo(Context context){
        if (isEthernetConnected(context)){
            return "有线网络已连接";
        }else if (isWifiConnected(context)){
            return "无线网络已连接";
        }
        return "网络未连接";
    }
    private static boolean isEthernetConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Service.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        return info.isConnected() && info.isAvailable();
    }
    private static boolean isWifiConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Service.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return info.isConnected() && info.isAvailable();

    }
}
