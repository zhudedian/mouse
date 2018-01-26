package com.ider.mouse.util;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Eric on 2018/1/25.
 */

public class NetUtil {

    public static boolean isWifiConnect(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Service.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = manager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        System.out.println("----------wifiInfo" + wifiInfo);
        return wifiInfo.isConnected() && wifiInfo.isAvailable();
    }

    /**
     * �ж�Ethernet�Ƿ����
     */
    @SuppressLint("InlinedApi")
    public static boolean isEthernetConnect(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Service.CONNECTIVITY_SERVICE);
        NetworkInfo etherInfo = manager
                .getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        return etherInfo.isConnected() && etherInfo.isAvailable();
    }

    public static boolean isNetworkAvailable(Context context) {
        return isWifiConnect(context) || isEthernetConnect(context);
        //  return checkNetworkState(context);
    }
}
