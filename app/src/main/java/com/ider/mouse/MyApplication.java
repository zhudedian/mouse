package com.ider.mouse;

import android.app.Application;
import android.content.Context;

/**
 * Created by Eric on 2017/7/26.
 */

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate(){
        context = getApplicationContext();
    }
    public static Context getContext(){
        return context;
    }
}
