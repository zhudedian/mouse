package com.ider.mouse.clean;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.ider.mouse.R;

import java.text.DecimalFormat;

public class CleanActivity extends Activity {
    private static final String TAG = "CleanActivity";
    private TextView cleaned_memory = null;
    private ImageView rotation = null;
    private float oldMemory;
    private Animation rotationAnim = null;
    private DecimalFormat df;
    private Handler mHandler;
    Runnable clean = new Runnable() {
        public void run() {
            cleanMemory();
            float newMemory = (float) getAvailableMemory() / 1024.0F / 1024.0F;
            String newAvailableMemory = df.format((double)newMemory);
            Log.i("CleanActivity", "run: new = " + newMemory);
            cleaned_memory.setVisibility(View.VISIBLE);
            String mMemoryFormat = getString(R.string.CleanedMemory);
            float memorys = newMemory - oldMemory;
            String cleaned;
            if(memorys < 0.0F) {
                cleaned = "0";
            } else {
                cleaned = df.format((double)memorys);
            }

            cleaned_memory.setText(String.format(mMemoryFormat, new Object[]{cleaned, newAvailableMemory}));
        }
    };
    Runnable finishSelf = new Runnable() {
        public void run() {
            finish();
        }
    };

    public CleanActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_clean);
        this.df = new DecimalFormat("##0.0");
        this.mHandler = new Handler();
        this.cleaned_memory = (TextView)this.findViewById(R.id.cleanInfo);
        this.rotation = (ImageView)this.findViewById(R.id.rotation_image);
        this.initAnimation();
        this.startAnimation();

    }

    public void cleanMemory() {
        ProcessManager.cleanMemory(this, true);
    }

    public long getAvailableMemory() {
        ActivityManager manager = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        manager.getMemoryInfo(mi);
        return mi.availMem;
    }

    private void initAnimation() {
        this.rotationAnim = new RotateAnimation(0.0F, 720.0F, 1, 0.5F, 1, 0.5F);
        this.rotationAnim.setDuration(700L);
        this.rotationAnim.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                oldMemory = (float) getAvailableMemory() / 1024.0F / 1024.0F;
                Log.i("CleanActivity", "run: old = " + oldMemory);
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                mHandler.post(clean);
                mHandler.postDelayed(finishSelf, 3000L);
            }
        });
    }

    public void startAnimation() {
        this.rotation.startAnimation(this.rotationAnim);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == 23) {
            this.startAnimation();
            this.mHandler.removeCallbacks(this.finishSelf);
        }

        return super.onKeyDown(keyCode, event);
    }
}
