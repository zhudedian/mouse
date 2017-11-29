package com.ider.mouse.util;

import android.app.Instrumentation;
import android.view.KeyEvent;

/**
 * Created by Eric on 2017/9/7.
 */

public class SendKey {
    public static Instrumentation instrumentation = new Instrumentation();

    public static void sendBack(){
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
    public static void sendMenu(){
        new Thread(){
            public void run() {
                try{
                    instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
                }
                catch (Exception e) {
                }
            }
        }.start();
    }
    public static void sendCenter(){
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
    public static void sendUp(){
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
    public static void sendDown(){
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
    public static void sendLeft(){
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
    public static void sendRight(){
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
    public static void sendSYSRQ(){
        new Thread(){
            public void run() {
                try{
                    instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_SYSRQ);
                }
                catch (Exception e) {
                }
            }
        }.start();
    }
}
