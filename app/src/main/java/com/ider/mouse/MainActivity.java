package com.ider.mouse;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ider.mouse.util.DiskUtil;
import com.ider.mouse.util.QRCodeUtil;
import com.ider.mouse.util.SocketServer;
import com.yanzhenjie.andserver.Server;

import java.io.File;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private TcMouseManager mMouseManager;

    public static ViewGroup contentView;
    private WebView webView;
    private View mLoginStatusView;
    TextView textView,test;
    private ImageView imageView;
    private TextView mLoaddingMessageView;

    private int pite;
    private SocketServer server;
    private String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar= getSupportActionBar();
        if (actionBar!=null){
            actionBar.hide();
        }
        Log.i("Environment.getPath()=",Environment.getExternalStorageDirectory().getPath());
        LayoutInflater inflater = getLayoutInflater();
        contentView = (ViewGroup) inflater.inflate(R.layout.activity_main, null);
        setContentView(contentView);
        test = (TextView) findViewById(R.id.test);
        imageView = (ImageView)findViewById(R.id.image_view);
        Intent intent = new Intent(MainActivity.this,MouseService.class);
        startService(intent);
        //finish();
        Log.i("getHOstIP",getHostIP()+" ");
        if (getHostIP() == null){
            imageView.setVisibility(View.GONE);
            test.setVisibility(View.VISIBLE);
            test.setText("未连接网络");
            return;
        }

        ip = "http://"+getHostIP()+":8080/app";

//        final String filePath = getFileRoot(MainActivity.this) + File.separator
//                + "qr_" + System.currentTimeMillis() + ".jpg";
        final String filePath = getFileRoot(MainActivity.this) + File.separator
                + "qr_downloadPathPic.jpg";

        //二维码图片较大时，生成图片、保存文件的时间可能较长，因此放在新线程中
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean success = QRCodeUtil.createQRImage(ip, 300, 300,
                        BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher),
                        filePath);

                if (success) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(BitmapFactory.decodeFile(filePath));
                        }
                    });
                }
            }
        }).start();

//       init();
        //初始化
//        initMouse();
//        showMouse();
//        try {
//
//            server=new SocketServer ( 7777 );
//            /**socket服务端开始监听*/
//            server.beginListen ( );
//
//        }catch (Exception e){
////            Toast.makeText ( MainActivity.this,"请输入数字", Toast.LENGTH_SHORT ).show ();
//            e.printStackTrace ();
//        }
//        SocketServer.ServerHandler = new Handler( ){
//            @Override
//            public void handleMessage(Message msg) {
//                Log.i("taggg",msg.obj.toString());
//                String[] pos = msg.obj.toString().split(" ");
//                int x;
//                int y;
//                Log.i("tag", "pos.length=" + pos.length);
//                if (pos[0].equals("onSingleTapUp")){
//                    mMouseManager.click();
//                    mMouseManager.resert();
//                    return;
//                }
//                if (pos[0].equals("nexttouch")){
//                    mMouseManager.resert();
//                    return;
//                }
//                if (pos[0].equals("scolor")){
//                    y = Integer.parseInt(pos[1]);
//                    mMouseManager.move(y);
//
//                    return;
//                }
//                try {
//                    if (pos.length == 3) {
//                        if (!pos[0].equals("") && !pos[1].equals("")) {
//                            x = Integer.parseInt(pos[0]);
//                            y = Integer.parseInt(pos[1]);
//                            Log.i("tag", "x=" + x + "y=" + y);
//                            mMouseManager.move(x, y);
//                        }
//                    }
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//
//
//
//
////                    Log.i("tag", "x=" + x + "y=" + y);
////                    mMouseManager.move(x,x2, y ,y2);
//
//
//
////                server.sendMessage ( "success" );
//            }
//        };
        //registReceivers();
//        finish();



    }
    protected void onResume(){
        super.onResume();
//        List<DiskUtil.StorageInfo> list = DiskUtil.listAllStorage();
//        DiskUtil.getAvaliableStorage(list);
    }
    private String getFileRoot(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File external = context.getExternalFilesDir(null);
            if (external != null) {
                return external.getAbsolutePath();
            }
        }

        return context.getFilesDir().getAbsolutePath();
    }

    public static String getHostIP() {

        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            Log.i("yao", "SocketException");
            e.printStackTrace();
        }
        return hostIp;

    }

    private Server.Listener mListener = new Server.Listener() {
        @Override
        public void onStarted() {

        }

        @Override
        public void onStopped() {

        }

        @Override
        public void onError(Exception e) {
            // Ports may be occupied.
        }
    };
    public View getViewAtActivity(int x, int y) {
        // 从Activity里获取容器
        View root = getWindow().getDecorView();
        return findViewByXY(root, x, y);
    }
    private View findViewByXY(View view, int x, int y) {
        View targetView = null;
        if (view instanceof ViewGroup) {
            // 父容器,遍历子控件
            ViewGroup v = (ViewGroup) view;
            for (int i = 0; i < v.getChildCount(); i++) {
                targetView = findViewByXY(v.getChildAt(i), x, y);
                if (targetView != null) {
                    break;
                }
            }
        } else {
            targetView = getTouchTarget(view, x, y);
        }
        return targetView;

    }
    private View getTouchTarget(View view, int x, int y) {
        View targetView = null;
        // 判断view是否可以聚焦
        ArrayList<View> TouchableViews = view.getTouchables();
        for (View child : TouchableViews) {
            if (isTouchPointInView(child, x, y)) {
                targetView = child;
                break;
            }
        }
        return targetView;
    }
    private boolean isTouchPointInView(View view, int x, int y) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + view.getMeasuredWidth();
        int bottom = top + view.getMeasuredHeight();
        if (view.isClickable() && y >= top && y <= bottom && x >= left
                && x <= right) {
            return true;
        }
        return false;
    }

    public void registReceivers() {
        IntentFilter filter;


        // 外接u盘广播
        filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_EJECT);
        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        filter.addAction(Intent.ACTION_MEDIA_CHECKING);
        filter.addDataScheme("file");
        registerReceiver(mediaReciever, filter);

    }
    BroadcastReceiver mediaReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("tag", action);

        }
    };

    private void init() {

//        webView = (WebView) contentView.findViewById(R.id.web);
        mLoginStatusView = this.findViewById(R.id.login_status);
        mLoaddingMessageView = (TextView) this
                .findViewById(R.id.login_status_message);
        textView = (TextView) contentView.findViewById(R.id.btn_onclick);

        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"click",Toast.LENGTH_SHORT).show();
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "onclicked ", Toast.LENGTH_SHORT).show();
                showProgress(true);
//                webView.setVisibility(View.VISIBLE);
//
//                webView.loadUrl("https://www.baidu.com/");
//                WebSettings settings = webView.getSettings();
//                settings.setJavaScriptEnabled(true);
//                webView.setWebViewClient(new WebViewClient() {
//                    @Override
//                    public boolean shouldOverrideUrlLoading(WebView view,
//                                                            String url) {
//                        // 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
//                        view.loadUrl(url);
//                        return true;
//                    }
//
//                    @Override
//                    public void onPageFinished(WebView view, String url) {
//
//                        super.onPageFinished(view, url);
//                    }
//
//                    @Override
//                    public void onReceivedError(WebView view, int errorCode,
//                                                String description, String failingUrl) {
//                        Toast.makeText(MainActivity.this, "加载失败 ",
//                                Toast.LENGTH_LONG).show();
//                        super.onReceivedError(view, errorCode, description,
//                                failingUrl);
//                    }
//
//                });
//
//                webView.setWebChromeClient(new WebChromeClient() {
//                    @Override
//                    public void onProgressChanged(WebView view, int newProgress) {
//                        // TODO Auto-generated method stub
//                        if (newProgress == 100) {
//                            showProgress(false);
//
//                        }
//                    }
//
//                });

            }
        });

    }

    @SuppressLint("NewApi")
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);

            mLoginStatusView.setVisibility(View.VISIBLE);
            mLoginStatusView.animate().setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            webView.setVisibility(show ? View.VISIBLE
                                    : View.GONE);
                        }
                    });

            webView.setVisibility(View.VISIBLE);
            webView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            webView.setVisibility(show ? View.GONE
                                    : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            webView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void showMouse() {

        mMouseManager.showMouseView();

    }

    public void initMouse() {
        initMouseMrg();
    }

    public void initMouseMrg() {

        mMouseManager = new TcMouseManager();
        mMouseManager.init(contentView, TcMouseManager.MOUSE_TYPE);
        mMouseManager.setShowMouse(true);

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (mMouseManager != null && mMouseManager.isShowMouse()) {
            return mMouseManager.onDpadClicked(event);
        }
        return super.dispatchKeyEvent(event);
    }

}
