package com.ider.mouse.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by Eric on 2017/9/4.
 */

public class FileCopy {
    public static boolean startCopy;
    public static boolean copy(String oldPath,String newPath){
        startCopy = true;
        File file = new File(oldPath);
        if (file.isDirectory()){
            return copyFolder(oldPath,newPath);
        }else {
            return copyFile(oldPath,newPath);
        }
    }
    public static boolean copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            File newFile = new File(newPath);
            if (oldfile.exists()&&startCopy) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[4096];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    if (startCopy) {
                        bytesum += byteread; //字节数 文件大小
                        fs.write(buffer, 0, byteread);
                    }else {
                        newFile.delete();
                        return false;
                    }
                }
                fs.close();
                inStream.close();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean copyFolder(String oldPath, String newPath) {

        try {
            (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹
            File a = new File(oldPath);
            String[] file = a.list();
            File temp;
            for (int i = 0; i < file.length&&startCopy; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }
                if (temp.isFile()) {
                    copyFile(temp.getPath(),newPath + File.separator + temp.getName());
                } else if (temp.isDirectory()) {//如果是子文件夹
                    copyFolder(oldPath + File.separator + file[i], newPath + File.separator + file[i]);
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean cut(String oldPath,String newPath){
        startCopy = true;
        File file = new File(oldPath);
        if (file.isDirectory()){
            return cutFolder(oldPath,newPath);
        }else {
            return cutFile(oldPath,newPath);
        }
    }
    public static boolean cutFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            File newFile = new File(newPath);
            if (oldfile.exists()&&startCopy) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[4096];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    if (startCopy) {
                        bytesum += byteread; //字节数 文件大小
                        fs.write(buffer, 0, byteread);
                    }else {
                        newFile.delete();
                        return false;
                    }
                }
                fs.close();
                inStream.close();
                oldfile.delete();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean cutFolder(String oldPath, String newPath) {

        try {
            (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹
            File oldFile = new File(oldPath);
            String[] file = oldFile.list();
            File temp;
            for (int i = 0; i < file.length&&startCopy; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }
                if (temp.isFile()) {
                    copyFile(temp.getPath(),newPath + File.separator + temp.getName());
                } else if (temp.isDirectory()) {//如果是子文件夹
                    copyFolder(oldPath + File.separator + file[i], newPath + File.separator + file[i]);
                }
            }
            oldFile.delete();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean move(String oldPath,String savePath){
        File file = new File(oldPath);
        if (file.isDirectory()){
            return moveDirectory(oldPath,savePath);
        }else {
            return moveFile(oldPath,savePath);
        }
    }

    private static boolean moveFile(String oldPath, String savePath) {
        File srcFile = new File(oldPath);
        if(!srcFile.exists()) {
            return false;
        }
        File destDir = new File(savePath);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        return srcFile.renameTo(new File(savePath + File.separator + srcFile.getName()));
    }
    private static boolean moveDirectory(String oldPath, String savePath) {
        File srcDir = new File(oldPath);
        if(!srcDir.exists()){
            return false;
        }
        File destDir = new File(savePath);
        if(!destDir.exists()) {
            destDir.mkdirs();
        }
        /**
         * 如果是文件则移动，否则递归移动文件夹。删除最终的空源文件夹
         * 注意移动文件夹时保持文件夹的树状结构
         */
        File[] sourceFiles = srcDir.listFiles();
        if (sourceFiles!=null) {
            for (File sourceFile : sourceFiles) {
                if (sourceFile.isDirectory()) {
                    moveDirectory(sourceFile.getAbsolutePath(), destDir.getAbsolutePath() + File.separator + sourceFile.getName());
                } else {
                    moveFile(sourceFile.getAbsolutePath(), destDir.getAbsolutePath() + File.separator + srcDir.getName());
                }
            }
        }
        return srcDir.delete();
    }
}