package com.ider.mouse.util;

import android.app.Service;
import android.content.Context;
import android.os.storage.StorageManager;
import android.util.Log;

import com.ider.mouse.MyApplication;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.description;

/**
 * Created by Eric on 2017/12/22.
 */

public class DiskUtil {

    private static String TAG = "DiskUtil";
    public static List<StorageInfo> listAllStorage() {
        ArrayList<StorageInfo> storages = new ArrayList<StorageInfo>();
        StorageManager storageManager = (StorageManager) MyApplication.getContext().getSystemService(Context.STORAGE_SERVICE);
        try {
            Class<?>[] paramClasses = {};
            Method getVolumeList = StorageManager.class.getMethod("getVolumeList", paramClasses);
            Object[] params = {};
            Object[] invokes = (Object[]) getVolumeList.invoke(storageManager, params);

            if (invokes != null) {
                StorageInfo info = null;
                for (int i = 0; i < invokes.length; i++) {
                    Object obj = invokes[i];
                    Method getPath = obj.getClass().getMethod("getPath", new Class[0]);
                    String path = (String) getPath.invoke(obj, new Object[0]);
                    info = new StorageInfo(path);

                    Method getVolumeState = StorageManager.class.getMethod("getVolumeState", String.class);
                    String state = (String) getVolumeState.invoke(storageManager, info.path);
                    info.state = state;

                    Method isRemovable = obj.getClass().getMethod("isRemovable", new Class[0]);
                    info.isRemoveable = ((Boolean) isRemovable.invoke(obj, new Object[0])).booleanValue();

                    Method getUserLabel = obj.getClass().getMethod("getUserLabel", new Class[0]);
                    String label = (String) getUserLabel.invoke(obj, new Object[0]);
                    info.label = label;

//                    Method getMaxFileSize = obj.getClass().getMethod("getMaxFileSize", new Class[0]);
//                    long size = (long) getMaxFileSize.invoke(obj, new Object[0]);
//                    info.size = size;
                    storages.add(info);
//                    Log.i(TAG, info.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        storages.trimToSize();
        return storages;
    }

    public static List<StorageInfo> getAvaliableStorage(){
        List<StorageInfo> infos = listAllStorage();
        List<StorageInfo> storages = new ArrayList<StorageInfo>();
        for(StorageInfo info : infos){
            File file = new File(info.path);
            if ((file.exists()) && (file.isDirectory()) && (file.canWrite())) {
                if (info.isMounted()) {
                    info.size = file.getTotalSpace();
                    info.avaSize = file.getFreeSpace();
                    storages.add(info);
//                    Log.i(TAG, "getAvaliableStorage="+info.toString());
                }
            }
        }
//        checkAmlogic6Usb();
        return storages;
    }
    private static boolean checkAmlogic6Usb() {
        StorageManager mStorageManager = (StorageManager) MyApplication.getContext().getSystemService(Service.STORAGE_SERVICE);
        try {
            Class StorageManager = Class.forName("android.os.storage.StorageManager");
            Class VolumeInfo = Class.forName("android.os.storage.VolumeInfo");
            Class DiskInfo = Class.forName("android.os.storage.DiskInfo");

            Method getVolumes = StorageManager.getMethod("getVolumes");
            Method isMountedReadable = VolumeInfo.getMethod("isMountedReadable");
            Method getType = VolumeInfo.getMethod("getType");
            Method getDisk = VolumeInfo.getMethod("getDisk");


            Method isUsb = DiskInfo.getMethod("isUsb");
            Method getDescription = DiskInfo.getMethod("getDescription");
            List volumes = (List) getVolumes.invoke(mStorageManager);
            for(int i = 0; i < volumes.size(); i++) {
                if(volumes.get(i) != null && (boolean) isMountedReadable.invoke(volumes.get(i))
                        && (int) getType.invoke(volumes.get(i)) == 0) {
                    Object diskInfo = getDisk.invoke(volumes.get(i));
                    String label = (String)getFieldValue(diskInfo,"label");
                    boolean usbExists = (boolean) isUsb.invoke(diskInfo);
                    String description = (String) getDescription.invoke(diskInfo);
                    Log.i(TAG, "isUsbExists: " + usbExists + ":" + description+";label:"+label);
                    if(usbExists) {
                        return true;
                    }
                }
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return false;
    }
    public static Object getFieldValue(Object object, String fieldName){

        //根据 对象和属性名通过反射 调用上面的方法获取 Field对象
        Field field = getDeclaredField(object, fieldName) ;

        //抑制Java对其的检查
        field.setAccessible(true) ;

        try {
            //获取 object 中 field 所代表的属性值
            return field.get(object) ;

        } catch(Exception e) {
            e.printStackTrace() ;
        }

        return null;
    }
    public static Field getDeclaredField(Object object, String fieldName){
        Field field = null ;

        Class<?> clazz = object.getClass() ;

        for(; clazz != Object.class ; clazz = clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredField(fieldName) ;
                return field ;
            } catch (Exception e) {
                //这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
                //如果这里的异常打印或者往外抛，则就不会执行clazz = clazz.getSuperclass(),最后就不会进入到父类中了

            }
        }

        return null;
    }
    public static class StorageInfo {
        public String path;
        public String state;
        public String label;
        public long size;
        public long avaSize;
        public boolean isRemoveable;
        public StorageInfo(String path) {
            this.path = path;
        }
        public boolean isMounted() {
            return "mounted".equals(state);
        }
        @Override
        public String toString() {
            return "StorageInfo [path=" + path + ", state=" + state
                    + ", isRemoveable=" + isRemoveable +", label="+label +", size="+FileUtil.getSize(size) +"]";
        }
    }
}
