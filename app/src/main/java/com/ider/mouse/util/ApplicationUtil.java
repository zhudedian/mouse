package com.ider.mouse.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.ider.mouse.MyApplication;
import com.ider.mouse.db.App;
import com.ider.mouse.db.MyData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static android.R.attr.versionCode;
import static android.R.attr.versionName;
import static android.content.ContentValues.TAG;


public class ApplicationUtil {

    private static Context context = MyApplication.getContext();
    private static PackageManager packageManager = context.getPackageManager();

    public static List<App> queryApplication() {
        List<App> enties = new ArrayList<>();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, 0);
        File dir = new File(MyData.appIconPath);
        dir.mkdirs();
        for (int i = 0; i<resolveInfos.size();i++) {
            ResolveInfo resolveInfo = resolveInfos.get(i);
            String packageName = resolveInfo.activityInfo.applicationInfo.packageName;
            String labelName = resolveInfo.activityInfo.applicationInfo.loadLabel(packageManager).toString();
            Drawable drawable = resolveInfo.activityInfo.applicationInfo.loadIcon(packageManager);
            PackageInfo packageInfo;
            try {
                packageInfo = MyApplication.getContext().getPackageManager().getPackageInfo(packageName,0);
            }catch (PackageManager.NameNotFoundException e){
                e.printStackTrace();
                packageInfo = null;
            }
            String type = "1";
            int versionCode;
            String versionName;
            if (packageInfo!=null) {
                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                    type = "1";
                } else {
                    type = "0";
                }
                versionCode = packageInfo.versionCode;
                versionName = packageInfo.versionName;
                Log.i(TAG,  "versionCode="+versionCode+"versionName="+versionName);
            }else {
                versionCode = 0;
                versionName = "0";
                Log.i(TAG, "versionCode==null");
            }
            Log.i(TAG, "labelName ="+labelName+"type ="+type);
            String picName = packageName + ".jpg";
            saveBitmap(drawableToBitmap(drawable),picName);
            enties.add(new App(packageName,labelName,type,versionCode,versionName));
        }
        HashSet h = new HashSet(enties);//删除重复元素
        enties.clear();
        enties.addAll(h);
        return enties;
    }

    public static String getRequestedPermission(String packageName){
        String[] requestedPermissions = null;
        String info = "";
        try {
            requestedPermissions = packageManager.getPackageInfo(packageName,PackageManager.GET_PERMISSIONS).requestedPermissions;
        }catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (requestedPermissions!=null){
            for (String permission:requestedPermissions){
                info = info+permission+"\n";
            }
        }
        return info;
    }
    public static void saveBitmap(Bitmap bm,String picName) {
        File f = new File("/sdcard/Pictures/icons/", picName);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 50, out);
            out.flush();
            out.close();
//            Log.i(TAG, "已经保存");
        } catch (FileNotFoundException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public static Bitmap drawableToBitmap(Drawable drawable) {



        Bitmap bitmap = Bitmap.createBitmap(

                drawable.getIntrinsicWidth(),

                drawable.getIntrinsicHeight(),

                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888

                        : Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bitmap);

        //canvas.setBitmap(bitmap);

        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        drawable.draw(canvas);

        return bitmap;

    }

}
