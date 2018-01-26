package com.ider.mouse;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.ider.mouse.util.NetUtil;

public class MyReceiver extends BroadcastReceiver {
    private Context mContext = MyApplication.getContext();
    public MyReceiver() {
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (NetUtil.isNetworkAvailable(context)) {
            Intent service = new Intent(context, MouseService.class);
            context.startService(service);
        }
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
