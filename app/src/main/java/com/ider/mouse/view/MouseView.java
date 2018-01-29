package com.ider.mouse.view;

import android.app.Instrumentation;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;


import com.ider.mouse.MyApplication;
import com.ider.mouse.R;


/**
 * Created by Eric on 2017/9/7.
 */

public class MouseView {

    private Context context = MyApplication.getContext();
    private static ImageView mFloatView;
    private WindowManager.LayoutParams mLayoutParams;
    private static Instrumentation instrumentation = new Instrumentation();
    private WindowManager mWindowManager;
    private Bitmap mMouseBitmap;
    private int displayWidth;
    private int displayHeight;
    private int mCurrentX;
    private int mCurrentY;
    private int mLastMouseX;
    private int mLastMouseY;
    private int moveX,moveY ;
    private boolean isCountGone=true,isStart = false;
    private int goneTimes;
    public void createView() {
        // TODO Auto-generated method stub
        //加载布局文件
//        if (mFloatView !=null){
//            return;
//        }
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        displayHeight = mWindowManager.getDefaultDisplay().getHeight();
        displayWidth = mWindowManager.getDefaultDisplay().getWidth();
        Drawable drawable =	context.getResources().getDrawable(
                R.mipmap.shubiao);
        mMouseBitmap = drawableToBitamp(drawable);
        mFloatView = new ImageView(context);
        mFloatView.setImageBitmap(mMouseBitmap);
        //mFloatView = mLayoutInflater.inflate(R.layout.mouse, null);
        //为View设置监听，以便处理用户的点击和拖动
//        mFloatView.setOnTouchListener(new MouseService.OnFloatViewTouchListener());
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
        handler.sendEmptyMessage(1);
        updateFloatView();
    }
    public void down(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                instrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, mCurrentX+8, mCurrentY+5, 0));
            }
        }).start();
        updateFloatView();
    }
    public void moves(int x, int y){
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

    public void up(int x,int y){
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
    public void click(){
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
    public void resert(){
        mLastMouseX = mCurrentX;
        mLastMouseY = mCurrentY;
    }
    public void move(int x, int y){
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
    public Bitmap drawableToBitamp(Drawable drawable) {

        BitmapDrawable bd = (BitmapDrawable) drawable;
        Bitmap bitmap = bd.getBitmap();
        return Bitmap.createScaledBitmap(bitmap, 30, 30 ,true);
    }

    public void updateFloatView() {
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
                default:
                    break;
            }
        }
    };
    /*处理视图的拖动，这里只对Move事件做了处理，用户也可以对点击事件做处理，例如：点击浮动窗口时，启动应用的主Activity*/
//    private class OnFloatViewTouchListener implements View.OnTouchListener {
//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//            // TODO Auto-generated method stub
//            Log.i("baiyuliang", "mCurrentX: " + mCurrentX + ",mCurrentY: "
//                    + mCurrentY + ",mFloatViewWidth: " + mFloatViewWidth
//                    + ",mFloatViewHeight: " + mFloatViewHeight);
//           /*
//            * getRawX(),getRawY()这两个方法很重要。通常情况下，我们使用的是getX(),getY()来获得事件的触发点坐标，
//            * 但getX(),getY()获得的是事件触发点相对与视图左上角的坐标；而getRawX(),getRawY()获得的是事件触发点
//            * 相对与屏幕左上角的坐标。由于LayoutParams中的x,y是相对与屏幕的，所以需要使用getRawX(),getRawY()。
//            */
//            mCurrentX = (int) event.getRawX() - mFloatViewWidth;
//            mCurrentY = (int) event.getRawY() - mFloatViewHeight;
//            int action = event.getAction();
//            switch (action) {
//                case MotionEvent.ACTION_DOWN:
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    updateFloatView();
//                    break;
//                case MotionEvent.ACTION_UP:
//                    break;
//            }
//            return true;
//        }
//    }
}
