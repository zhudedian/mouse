package com.ider.mouse;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;


import static com.ider.mouse.MyApplication.getContext;

public class MouseService extends Service {

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private LayoutInflater mLayoutInflater;
    private ImageView mFloatView;
    private Bitmap mMouseBitmap;
    private int mCurrentX;
    private int mCurrentY;
    private static int mFloatViewWidth = 50;
    private static int mFloatViewHeight = 80;
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
        mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        mLayoutInflater = LayoutInflater.from(this);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        createView();
        return super.onStartCommand(intent,flags,startId);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    private void createView() {
        // TODO Auto-generated method stub
        //加载布局文件
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
        mLayoutParams.gravity = Gravity.CENTER ;
        //设置window type
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //设置背景为透明
        mLayoutParams.format = PixelFormat.RGBA_8888;
        //注意该属性的设置很重要，FLAG_NOT_FOCUSABLE使浮动窗口不获取焦点,若不设置该属性，屏幕的其它位置点击无效，应为它们无法获取焦点
        //mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //设置视图的显示位置，通过WindowManager更新视图的位置其实就是改变(x,y)的值
        mCurrentX = mLayoutParams.x = 50;
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
    }
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
