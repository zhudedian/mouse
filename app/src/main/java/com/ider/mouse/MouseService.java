package com.ider.mouse;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.InputMethodService;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.ider.mouse.db.MyData;
import com.ider.mouse.util.PackageUtils;
import com.ider.mouse.util.QRCodeUtil;
import com.ider.mouse.util.RequestFileHandler;
import com.ider.mouse.util.RequestUploadHandler;
import com.ider.mouse.util.SocketServer;
import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.Server;
import com.yanzhenjie.andserver.website.StorageWebsite;
import com.yanzhenjie.andserver.website.WebSite;

import java.io.File;

import static com.ider.mouse.MainActivity.getHostIP;
import static com.ider.mouse.MyApplication.getContext;
import static com.ider.mouse.R.id.default_activity_button;
import static com.ider.mouse.R.id.test;
import static com.ider.mouse.db.MyData.andServer;
import static com.ider.mouse.util.PackageUtils.installSlient;

@SuppressLint("NewApi")
public class MouseService extends AccessibilityService {

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
    private static SocketServer server;
    private String info,infoMobi;
    private int size;
    private KeyEvent ctrlDown,ctrlUp,aDown,aUp,vDown ,vUp ;
    private boolean startCountEnd = false,isCountGone=true,isStart = false;
    private int endCount,goneTimes;
    public static Server mServer;
    private boolean isEnd =false;
    public static File installApk ;


    public MouseService() {
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

        ctrlDown = new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_CTRL_LEFT);
        ctrlUp = new KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_CTRL_LEFT);
        aDown = new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_A);
        aUp = new KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_A);
        vDown = new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_V);
        vUp = new KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_V);
        try {

            server=new SocketServer ( 7777 );
            /**socket服务端开始监听*/
            server.beginListen ( );

        }catch (Exception e){
//            Toast.makeText ( MainActivity.this,"请输入数字", Toast.LENGTH_SHORT ).show ();

            e.printStackTrace ();
        }
        File file = new File("/system/", "preinstall");
        String websiteDirectory = file.getAbsolutePath();
        WebSite wesite = new StorageWebsite(websiteDirectory);
        MyData.andServer = new AndServer.Build()
                .port(8080) // 默认是8080，Android平台允许的端口号都可以。
                .registerHandler("upload", new RequestUploadHandler(handler))
                .registerHandler("down", new RequestFileHandler())
                .timeout(10 * 1000) // 默认10 * 1000毫秒。
                .website(wesite)
                .build();
        mServer = MyData.andServer.createServer();
        mServer.start();
        SocketServer.ServerHandler = new Handler( ){
            @Override
            public void handleMessage(Message msg) {
                Log.i("taggg",msg.obj.toString());
                String info = msg.obj.toString();
                if (info.length()>15){
                    String longinfo = info.substring(0,8);
                    if (longinfo.equals("longinfo")) {
                        server.sendMessage("sendSuccess ");
                        SocketServer.size = 15;
                        size = Integer.parseInt(info.substring(8, 13));
                        infoMobi = info.substring(13, 13+size);
                        Log.i("infoMobi", infoMobi);
                        ClipData clip = ClipData.newPlainText("simple text", infoMobi);
                        cm.setPrimaryClip(clip);
                        Intent intent = new Intent("commitMobileInfo");
                        intent.putExtra("info",infoMobi);
                        sendBroadcast(intent);
//                        copy();
//                        sendBack();
                        endCount = 0;
                        return;
                    }else if (info.contains("inEndEndEndEndE")){
                        endCount = 0;
                        return;
                    }else {
                        server.sendMessage("recieveOready");
                        endCount = 0;
                        return;
                    }
                }
                if (info.contains("inBeginBeginBeg")) {
                    SocketServer.size = 300;
                    server.sendMessage("recieveOready");
                    endCount = 0;
                    return;
                } else {
                    String[] pos = info.split(" ");
                    for (int i = 0; i < pos.length; i++) {
                        if (pos[i].contains("c")) {
                            handCom(pos[i]);
                            endCount = 0;
                            return;
                        }
                    }
                }

                if (endCount >= 4){
                    if (!isEnd){
                        isEnd = true;
                        server.endListen();
                        try {
                            new Thread(){
                                @Override
                                public void run(){
                                    try {
                                        sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    isEnd = false;
                                    if (server==null){
                                        server=new SocketServer ( 7777 );
                                    }
                                    /**socket服务端开始监听*/
                                    server.beginListen ( );
                                    Log.i("begin","beginListen");
                                }
                            }.start();

                        }catch (Exception e){
                            e.printStackTrace ();
                        }
                    }
                    endCount = 0;
                }else {
                    endCount++;
                    Log.i("count",endCount+"");
                }

            }

        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("InputMethodOpen");
        intentFilter.addAction("InputMethodClose");
        intentFilter.addAction("TextInfo");
        intentFilter.addAction("WebTextInfo");
        registerReceiver(myReceiver,intentFilter);


    }

    BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("InputMethodOpen")){
                server.sendMessage("InOp ");
                Intent intent1 = new Intent("getWebTextInfo");
                sendBroadcast(intent1);
            }else if (intent.getAction().equals("InputMethodClose")){
                server.sendMessage("InCl ");
            }else if (intent.getAction().equals("TextInfo")){
//                if (info == null){
//                    info = intent.getStringExtra("info");
//                    String msg = "Info"+info+"R,T;Y.";
//                    server.sendMessage(msg);
//                }else {
//                    String infor = intent.getStringExtra("info");
//                    Log.i("infor",infor);
//                    if (!info.equals(infor)){
//                        info = infor;
//                        String msg = "Info"+info+"R,T;Y.";
//                        server.sendMessage(msg);
//                    }
//                }
            }else if (intent.getAction().equals("WebTextInfo")){
                info = intent.getStringExtra("info");
                Log.i("WebTextInfo", info);
                String msg = "Info"+info+"R,T;Y.";
                server.sendMessage(msg);

            }

        }
    };
    @Override

    public void onInterrupt() {

        /* do nothing */
        Log.i("onInterrupt","onInterrupt");

    }
//    private static XC_MethodHook sInputMethodManagerServiceInitHook = new XC_MethodHook() {
//
//        @Override
//        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//
//
//            Message message = (Message) param.args[0];
//
//            if (message.what == MSG_SHOW_SOFT_INPUT) {
//
//                //输入法打开
//
//            }else if (message.what == MSG_HIDE_SOFT_INPUT){
//
//                //输入法关闭
//
//            }
//
//        }
//
//    };
    @SuppressLint("NewApi")
    @Override
    public void onAccessibilityEvent(final AccessibilityEvent accessibilityEvent) {

        boolean isClickEditable = false;
        boolean isFocusedEditable = false;
        Log.i("accessibilityEvent",accessibilityEvent.getSource()+"");

        if(accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) {

            if(accessibilityEvent.getSource() != null){
                Log.i("accessibilityEvent",accessibilityEvent.getSource()+"");

                isClickEditable = accessibilityEvent.getSource().isEditable();

            }

        }else if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_VIEW_FOCUSED){


            if(accessibilityEvent.getSource() != null){

                isFocusedEditable = accessibilityEvent.getSource().isEditable();

            }

        }

        if(isClickEditable || isFocusedEditable){

            //输入法已打开
            server.sendMessage("输入法已打开");

        }else {

            //输入法已关闭
            server.sendMessage("输入法已关闭");
        }

    }
    @Override
    protected void onServiceConnected() {
        AccessibilityServiceInfo serviceInfo = new AccessibilityServiceInfo();
        serviceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        serviceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        serviceInfo.packageNames = new String[]{"org.chromium.webview_shell"};
        serviceInfo.notificationTimeout = 100;
        setServiceInfo(serviceInfo);
    }


    private void handCom(String com){
        //Log.i("handCom",com);
        int x;
        int y;
        if (com.contains("cm")){
            try {
                String[] pos = com.split("P");
                if (pos.length==3) {
                    x = Integer.parseInt(pos[1]);
                    y = Integer.parseInt(pos[2]);
                    //Log.i("cm", "x=" + x + "y=" + y);
                    move(x, y);
//                    server.sendMessage("success");
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            return;
        }
        if (com.contains("cs")){
            try {
                String[] pos = com.split("P");
                x = Integer.parseInt(pos[1]);
                y = Integer.parseInt(pos[2]);
                    //Log.i("cm", "y=" + y);
                moves(x,y);

            }catch (Exception e){
                e.printStackTrace();
            }

            return;
        }
        if (com.contains("cd")){
            resert();
            down();
            return;
        }
        if (com.contains("cu")){
            try {
                String[] pos = com.split("P");
                x = Integer.parseInt(pos[1]);
                y = Integer.parseInt(pos[2]);
                up(x,y);
                resert();
            }catch (Exception e){
                e.printStackTrace();
            }

            return;
        }
        if (com.contains("cc")){
            click();
            resert();
            return;
        }
        if (com.contains("cn")){
            resert();
            return;
        }
        if (com.contains("cb")){
            server.sendMessage("back");
            sendBack();
            return;
        }
        if (com.contains("coup")){
            sendUp();
            return;
        }
        if (com.contains("codown")){
            sendDown();
            return;
        }
        if (com.contains("coleft")){
            sendLeft();
            return;
        }
        if (com.contains("coright")){
            sendRight();
            return;
        }
        if (com.contains("cocenter")){
            sendCenter();
            return;
        }

    }
    private void copy(){
        new Thread(){
            public void run() {
                try{
                    instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_KANA);
                }
                catch (Exception e) {
                }
            }
        }.start();
    }
    private void sendBack(){
        new Thread(){
            public void run() {
                try{
                    instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                }
                catch (Exception e) {
                }
            }
        }.start();
    }
    private void sendCenter(){
        new Thread(){
            public void run() {
                try{
                    instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_CENTER);
                }
                catch (Exception e) {
                }
            }
        }.start();
    }
    private void sendUp(){
        new Thread(){
            public void run() {
                try{
                    instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_UP);
                }
                catch (Exception e) {
                }
            }
        }.start();
    }
    private void sendDown(){
        new Thread(){
            public void run() {
                try{
                    instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);
                }
                catch (Exception e) {
                }
            }
        }.start();
    }
    private void sendLeft(){
        new Thread(){
            public void run() {
                try{
                    instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_LEFT);
                }
                catch (Exception e) {
                }
            }
        }.start();
    }
    private void sendRight(){
        new Thread(){
            public void run() {
                try{
                    instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_RIGHT);
                }
                catch (Exception e) {
                }
            }
        }.start();
    }
    private void down(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                instrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, mCurrentX+8, mCurrentY+5, 0));
            }
        }).start();
        updateFloatView();
    }
    private void moves(int x, int y){
        moveX = x;
        moveY = y;
        new Thread(new Runnable() {
            @Override
            public void run() {
                instrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, mCurrentX+moveX+8, mCurrentY+moveY+5, 0));
//                inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, mMouseX+8, mMouseY-25, 0));
            }
        }).start();
        updateFloatView();
    }

    private void up(int x,int y){
        moveX = x;
        moveY = y;
        new Thread(new Runnable() {
            @Override
            public void run() {
                instrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, mCurrentX+moveX+8, mCurrentY+moveY+5, 0));
            }
        }).start();
        updateFloatView();
    }
    private void click(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    instrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, mCurrentX+8, mCurrentY+5, 0));
                    instrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, mCurrentX+8, mCurrentY+5, 0));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
        updateFloatView();
    }
    private void resert(){
        mLastMouseX = mCurrentX;
        mLastMouseY = mCurrentY;
    }
    private void move(int x, int y){
        mFloatView.setVisibility(View.VISIBLE);
        mCurrentX = mLastMouseX+x;
        mCurrentY = mLastMouseY+y;
        if (mCurrentY<0){
            mCurrentY=0;
        }else if (mCurrentY>displayHeight){
            mCurrentY=displayHeight;
        }
        if (mCurrentX < 0){
            mCurrentX = 0;
        }else if (mCurrentX>displayWidth){
            mCurrentX = displayWidth;
        }
        updateFloatView();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        createView();
        updateFloatView();

        return super.onStartCommand(intent,flags,startId);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }

    private void createView() {
        // TODO Auto-generated method stub
        //加载布局文件
        if (mFloatView !=null){
            return;
        }
        Drawable drawable =	getResources().getDrawable(
                R.mipmap.shubiao);
        mMouseBitmap = drawableToBitamp(drawable);
        mFloatView = new ImageView(getContext());
        mFloatView.setImageBitmap(mMouseBitmap);
        //mFloatView = mLayoutInflater.inflate(R.layout.mouse, null);
        //为View设置监听，以便处理用户的点击和拖动
        mFloatView.setOnTouchListener(new OnFloatViewTouchListener());
       /*为View设置参数*/
        mLayoutParams = new WindowManager.LayoutParams();
        //设置View默认的摆放位置
        mLayoutParams.gravity = Gravity.LEFT|Gravity.TOP ;
        //设置window type
        mLayoutParams.type = WindowManager.LayoutParams.LAST_SYSTEM_WINDOW;//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //设置背景为透明
        mLayoutParams.format = PixelFormat.RGBA_8888;
        //注意该属性的设置很重要，FLAG_NOT_FOCUSABLE使浮动窗口不获取焦点,若不设置该属性，屏幕的其它位置点击无效，应为它们无法获取焦点
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        //设置视图的显示位置，通过WindowManager更新视图的位置其实就是改变(x,y)的值
        mCurrentX = mLayoutParams.x = 500;
        mCurrentY = mLayoutParams.y = 50;
        //设置视图的宽、高
        mLayoutParams.width = 30;
        mLayoutParams.height = 30;
        //将视图添加到Window中
        mWindowManager.addView(mFloatView, mLayoutParams);

    }
    private Bitmap drawableToBitamp(Drawable drawable) {

        BitmapDrawable bd = (BitmapDrawable) drawable;
        Bitmap bitmap = bd.getBitmap();
        return Bitmap.createScaledBitmap(bitmap, 30, 30 ,true);
    }

    private void updateFloatView() {
        mLayoutParams.x = mCurrentX;
        mLayoutParams.y = mCurrentY;
        mWindowManager.updateViewLayout(mFloatView, mLayoutParams);
        if (!isStart){
            goneTimes = 0;
            isStart = true;
            new Thread(){
                @Override
                public void run(){
                    isCountGone =false;
                    try {
                        sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    isStart = false;
                    isCountGone = true;
                    new Thread(){
                        @Override
                        public void run(){
                            while (isCountGone){
                                goneTimes ++;
                                try {
                                    sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                if (goneTimes>=10){
                                    goneTimes = 0;
                                    handler.sendEmptyMessage(1);
                                }
                            }
                        }
                    }.start();
                }
            }.start();
        }
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
    /*处理视图的拖动，这里只对Move事件做了处理，用户也可以对点击事件做处理，例如：点击浮动窗口时，启动应用的主Activity*/
    private class OnFloatViewTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            Log.i("baiyuliang", "mCurrentX: " + mCurrentX + ",mCurrentY: "
                    + mCurrentY + ",mFloatViewWidth: " + mFloatViewWidth
                    + ",mFloatViewHeight: " + mFloatViewHeight);
           /*
            * getRawX(),getRawY()这两个方法很重要。通常情况下，我们使用的是getX(),getY()来获得事件的触发点坐标，
            * 但getX(),getY()获得的是事件触发点相对与视图左上角的坐标；而getRawX(),getRawY()获得的是事件触发点
            * 相对与屏幕左上角的坐标。由于LayoutParams中的x,y是相对与屏幕的，所以需要使用getRawX(),getRawY()。
            */
            mCurrentX = (int) event.getRawX() - mFloatViewWidth;
            mCurrentY = (int) event.getRawY() - mFloatViewHeight;
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_MOVE:
                    updateFloatView();
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return true;
        }
    }
}
