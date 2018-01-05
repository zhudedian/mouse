package com.ider.mouse.util;




import android.content.SharedPreferences;
import android.os.Build;

import java.io.File;
import java.text.SimpleDateFormat;



/**
 * Created by Eric on 2017/10/17.
 */

public class Screenshot {
    public static String takeScreenShot() {
        File dir = new File("/sdcard/Pictures/Screenshots");
        dir.mkdirs();
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        String date = sDateFormat.format(new java.util.Date());
//        Log.i("date",date);
        String mSavedPath = "/sdcard/Pictures/Screenshots/"+date+".png";
        try {
            Runtime.getRuntime().exec("screencap -p " + mSavedPath);
            return "/sdcard/Pictures/Screenshots"+"\"name=\""+date+".png";
        } catch (Exception e) {
            e.printStackTrace();
            return "failed";
        }
    }
    public static String screenshotForKey(){
        String picPath;
        if (DeviceInfo.getCpuName().equals("Rockchip RK3229")&& Build.VERSION.SDK_INT==25){
            picPath = "/sdcard/Screenshots";
        }else {
            picPath = "/sdcard/Pictures/Screenshots";
        }
        File file = new File(picPath);
        File[] files;
        if (file.exists()){
            delete(file);
        }
        SendKey.sendSYSRQ();
        do{
            try {
                Thread.sleep(500);
            }catch (Exception e){
                e.printStackTrace();
            }
            files = file.listFiles();
        }while (files==null||(files!=null&&files.length !=1));
        String name = files[0].getName();
        return picPath+"\"name=\""+name;
    }

    private static void delete(File file){
        if (file.isDirectory()){
            dirDelete(file);
        }else {
            file.delete();
        }
    }

    private static void dirDelete(File dir){
        File[] files = dir.listFiles();
        for (File file:files){
            if (file.isDirectory()){
                dirDelete(file);
            }else {
                file.delete();
            }
        }
        dir.delete();
    }

}
