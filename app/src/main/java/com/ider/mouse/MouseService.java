package com.ider.mouse;


import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.inputmethodservice.InputMethodService;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.ider.mouse.db.MyData;
import com.ider.mouse.net.Connect;
import com.ider.mouse.util.HandCom;
import com.ider.mouse.util.InfoHandler;
import com.ider.mouse.util.PackageUtils;
import com.ider.mouse.util.RequestAppIconHandler;
import com.ider.mouse.util.RequestFileHandler;
import com.ider.mouse.util.RequestInstallHandler;
import com.ider.mouse.util.RequestUploadHandler;
import com.ider.mouse.util.SocketServer;
import com.ider.mouse.view.MouseView;
import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.Server;
import com.yanzhenjie.andserver.website.StorageWebsite;
import com.yanzhenjie.andserver.website.WebSite;

import java.io.File;

import static com.ider.mouse.MyApplication.getContext;

@SuppressLint("NewApi")
public class MouseService extends Service {

    private WindowManager mWindowManager;
    private InputMethodManager inputMethodManager;
    private InputConnection inputConnection;
    private ClipboardManager cm;
    private WindowManager.LayoutParams mLayoutParams;
    private LayoutInflater mLayoutInflater;
    private static ImageView mFloatView;
    private Bitmap mMouseBitmap;
    private int displayWidth;
    private int displayHeight;
    private int mCurrentX;
    private int mCurrentY;
    private int mLastMouseX;
    private int mLastMouseY;
    private int moveX,moveY ;
    private Instrumentation instrumentation = new Instrumentation();
    private static int mFloatViewWidth = 50;
    private static int mFloatViewHeight = 80;
    private boolean isCountGone=true,isStart = false;
    private int endCount,goneTimes;
    public static Server mServer;
    private boolean isEnd =false;
    public static File installApk ;


    public MouseService() {
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate(){
        super.onCreate();
        inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        InputMethodService inputMethodService = new InputMethodService();
        inputConnection = inputMethodService.getCurrentInputConnection();
        mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        mLayoutInflater = LayoutInflater.from(this);
        displayHeight = mWindowManager.getDefaultDisplay().getHeight();
        displayWidth = mWindowManager.getDefaultDisplay().getWidth();


        try {
            if(MyData.server==null) {
                MyData.server = new SocketServer(7777);
            }
            /**socket服务端开始监听*/
            MyData.server.beginListen ( );

        }catch (Exception e){
//            Toast.makeText ( MainActivity.this,"请输入数字", Toast.LENGTH_SHORT ).show ();

            e.printStackTrace ();
        }
        startConnect();
        File file = new File("/system/", "preinstall");
        String websiteDirectory = file.getAbsolutePath();
        WebSite wesite = new StorageWebsite(websiteDirectory);
        MyData.andServer = new AndServer.Build()
                .port(8080) // 默认是8080，Android平台允许的端口号都可以。
                .registerHandler("upload", new RequestUploadHandler(handler))
                .registerHandler("install",new RequestInstallHandler(handler))
                .registerHandler("down", new RequestFileHandler())
                .registerHandler("yzg",new RequestAppIconHandler())
                .registerHandler("info",new InfoHandler())
                .timeout(10 * 1000) // 默认10 * 1000毫秒。
                .website(wesite)
                .build();
        mServer = MyData.andServer.createServer();
        mServer.start();
        SocketServer.ServerHandler = new Handler( ){
            @Override
            public void handleMessage(Message msg) {
//                Log.i("taggg",msg.obj.toString());
                String info = msg.obj.toString();
                String[] pos = info.split(" ");
                for (int i = 0; i < pos.length; i++) {
                    if (pos[i].contains("c")) {
                        HandCom.hand(pos[i]);
                        endCount = 0;
                        return;
                    }
                }

//                if (endCount >= 4){
//                    if (!isEnd){
//                        isEnd = true;
//                        MyData.server.endListen();
//                        try {
//                            new Thread(){
//                                @Override
//                                public void run(){
//                                    try {
//                                        sleep(1000);
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                    }
//                                    isEnd = false;
//                                    if (MyData.server==null){
//                                        MyData.server=new SocketServer ( 7777 );
//                                    }
//                                    /**socket服务端开始监听*/
//                                    MyData.server.beginListen ( );
//                                    Log.i("begin","beginListen");
//                                }
//                            }.start();
//
//                        }catch (Exception e){
//                            e.printStackTrace ();
//                        }
//                        endCount = 0;
//                    }
//                    if (endCount ==30){
//                        isEnd = false;
//                    }
//                }else {
//                    endCount++;
//                    Log.i("count",endCount+"");
//                }

            }

        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("InputMethodOpen");
        intentFilter.addAction("InputMethodClose");
        intentFilter.addAction("TextInfo");
        intentFilter.addAction("uninstall_complete_info");
        intentFilter.addAction("Uninstall_Dialog_onDismiss");
        intentFilter.addAction("WebTextInfo");
        intentFilter.addAction("networkChange");
        intentFilter.addAction("screenshot_image_name");
        registerReceiver(myReceiver,intentFilter);
        if (MyData.mouseView==null) {
            MyData.mouseView = new MouseView();
            MyData.mouseView.createView();
        }

    }

    BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("myReceiver", "intent.getAction()="+intent.getAction());
            if (intent.getAction().equals("InputMethodOpen")){
                MyData.server.sendMessage("InOp ");
                Intent intent1 = new Intent("getWebTextInfo");
                sendBroadcast(intent1);
            }else if (intent.getAction().equals("InputMethodClose")){
                MyData.server.sendMessage("InCl ");
            }else if (intent.getAction().equals("WebTextInfo")){
                MyData.editText = intent.getStringExtra("info");
//                Log.i("WebTextInfo", info);
                MyData.server.sendMessage("InFo ");
            }else if (intent.getAction().equals("uninstall_complete_info")){
                String info = intent.getStringExtra("info");
                Log.i("uninstall_complete_info", info);
                MyData.server.sendMessage("InUnCp ");
            }else if (intent.getAction().equals("Uninstall_Dialog_onDismiss")){
                String info = intent.getStringExtra("info");
                Log.i("myReceiver", "Uninstall_Dialog_onDismiss");
                MyData.server.sendMessage("InDiaDis ");
            }else if (intent.getAction().equals("networkChange")){
//                stopSelf();
                MyData.server.beginListen ( );
                startConnect();
            }else if (intent.getAction().equals("screenshot_image_name")){
                Log.i("myReceiver", "screenshot_image_name");
            }
        }
    };




    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
//        createView();
//        updateFloatView();

        return super.onStartCommand(intent,flags,startId);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    mFloatView.setVisibility(View.GONE);
                    break;
                case 2:
                    int result = 2;
                    if (installApk != null){
                        result = PackageUtils.installSlient(installApk.getPath());
                    }
                    Log.i("result","result= "+result);
                    if (result==0){
                        installApk.delete();
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private void startConnect(){
        new Thread(){
            @Override
            public void run(){
                boolean isConnect = false;
                while (!isConnect) {
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (isWifiConnected()||isEthernetConnected()){
                        Connect.onBrodacastReceiver();
                        isConnect = true;
                    }
                }
            }
        }.start();
    }

    public boolean isEthernetConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Service.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        return info.isConnected() && info.isAvailable();
    }
    public boolean isWifiConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Service.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return info.isConnected() && info.isAvailable();

    }

}
