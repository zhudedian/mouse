package com.ider.mouse;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {
    private Context mContext = MyApplication.getContext();
    public MyReceiver() {
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent("networkChange");
        mContext.sendBroadcast(intent1);
//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                try {
//                    Thread.sleep(1000);
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//
//                try {
//                    Thread.sleep(1000);
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//                Intent service = new Intent(mContext,MouseService.class);
//                mContext.startService(service);
//            }
//        }.start();

    }
}
