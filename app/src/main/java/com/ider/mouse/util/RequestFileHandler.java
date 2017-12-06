package com.ider.mouse.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

import com.ider.mouse.MainActivity;
import com.ider.mouse.MyApplication;
import com.ider.mouse.clean.CleanActivity;
import com.ider.mouse.db.App;
import com.ider.mouse.db.MyData;

import com.yanzhenjie.andserver.RequestHandler;
import com.yanzhenjie.andserver.util.HttpRequestParser;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import static com.ider.mouse.util.Screenshot.takeScreenShot;

/**
 * Created by Eric on 2017/8/24.
 */

public class RequestFileHandler implements RequestHandler {

    private String mFilePath = MyData.serverPath;
    private String parentPath;
    private String comments;
    private String downLoadPath = "";
    private File downLoadFile;
    private File mFile;
    private String fileName;
    private String info="";
    public RequestFileHandler() {

    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        // You can according to the client param can also be downloaded.
        Log.i("MyData.serverPath",MyData.serverPath);
        comments = request.getFirstHeader("comment").getValue();
        Log.i("comments",comments);
        comments = unicodetoString(comments);
        Log.i("comments",comments);
        if (comments.contains("\"downLoad=\"")){
            downLoadPath= comments.replace("\"downLoad=\"","");
            Log.i("downLoadPath",downLoadPath);
            downLoadFile = new File(downLoadPath);
            if (downLoadFile.exists()) {
                response.setStatusCode(200);
                long contentLength = downLoadFile.length();
                response.setHeader("ContentLength", Long.toString(contentLength));
                response.setEntity(new FileEntity(downLoadFile, HttpRequestParser.getMimeType(downLoadFile.getName())));
            }
            return;
        }else if (comments.contains("\"delete=\"")){
            comments= comments.replace("\"delete=\"","");
            String[] files = comments.split("name=");
            parentPath = files[0];
            for (int i=1;i<files.length;i++){
                if (!files[i].equals("")) {
                    mFilePath = parentPath.endsWith("/")?(parentPath+files[i]):(parentPath+"/"+files[i]);
                    mFile = new File(mFilePath);;
                    if (mFile.isDirectory()&&mFile.exists()){
                        dirDelete(mFile);
                    }else {
                        if (mFile.exists()) {
                            mFile.delete();
                        }
                    }
                }
            }
            mFilePath = parentPath;
        }else if (comments.contains("\"uninstall=\"")){
            comments= comments.replace("\"uninstall=\"","");
            Intent intent = new Intent(Intent.ACTION_DELETE, Uri.parse("package:" + comments));
//            intent.putExtra("no_confirm",true);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MyApplication.getContext().startActivity(intent);
            return;
        }else if (comments.contains("\"createDir=\"")){
            comments= comments.replace("\"createDir=\"","");
            mFile = new File(comments);
            if (!mFile.exists()){
                mFile.mkdirs();
            }
            mFilePath = mFile.getParent();
            Log.i("mFilePath",mFilePath);
        }else if (comments.contains("\"moveFile=\"")){
            comments= comments.replace("\"moveFile=\"","");
            String[] files = comments.split("\"newPath=\"");
            String savePath;
            if (files[1].lastIndexOf(File.separator)==0){
                savePath=File.separator;
            }else {
                savePath = files[1].substring(0,files[1].lastIndexOf(File.separator));
            }
            boolean result = FileCopy.move(files[0],savePath);
            Log.i("result",result+"");
            if (result){
                response.setStatusCode(200);
                response.setEntity(new StringEntity("success", "utf-8"));
            }else {
                boolean result2 = FileCopy.cut(files[0],files[1]);
                if (result2){
                    response.setStatusCode(200);
                    response.setEntity(new StringEntity("success", "utf-8"));
                }else {
                    response.setStatusCode(500);
                    response.setEntity(new StringEntity("failed", "utf-8"));
                }
            }
            return;
        } else if (comments.contains("\"reNameFile=\"")){
            comments= comments.replace("\"reNameFile=\"","");
            String[] files = comments.split("\"newName=\"");
            File old = new File(files[0]);
            mFilePath = old.getParent();
            File newFile = new File(mFilePath+File.separator+files[1]);
            boolean result = old.renameTo(newFile);
            Log.i("result",result+"");
        } else if (comments.contains("\"copyFile=\"")){
            comments= comments.replace("\"copyFile=\"","");
            String[] files = comments.split("\"newPath=\"");
            boolean result = FileCopy.copy(files[0],files[1]);
            Log.i("result",result+"");
            if (result){
                response.setStatusCode(200);
                response.setEntity(new StringEntity("success", "utf-8"));
            }else {
                response.setStatusCode(500);
                response.setEntity(new StringEntity("failed", "utf-8"));
            }
            return;
        } else if (comments.equals("\"stopCopyFile\"")){
            FileCopy.startCopy = false;
            response.setStatusCode(200);
            response.setEntity(new StringEntity("success", "utf-8"));
            return;
        }else if (comments.equals("\"commonBack\"")){
            comments= comments.replace("\"commonBack\"","");
            mFile = new File(comments);
            mFilePath = mFile.getParent();
        }else if (comments.contains("\"RequestAllApps\"")){
            response.setStatusCode(403);
            List<App> apps = ApplicationUtil.queryApplication();
            info = MyData.appIconPath;
            for (App app : apps){
                info=info+"\"type=\""+app.getType()+"\"label=\""+app.getLabelName()+"\"pckn=\""+
                        app.getPackageName()+"\"verC=\""+app.getVersionCode()+"\"verN=\""+app.getVersionName();
            }
            response.setEntity(new StringEntity(info, "utf-8"));
            return;
        }else if (comments.contains("\"requestAppInfo=\"")){
            comments= comments.replace("\"requestAppInfo=\"","");
            info = ApplicationUtil.getRequestedPermission(comments);
            response.setStatusCode(403);
            response.setEntity(new StringEntity(info, "utf-8"));
            return;
        }else if (comments.equals("screenshot")){
            String result = Screenshot.screenshotForKey();
            response.setStatusCode(200);
            response.setEntity(new StringEntity(result, "utf-8"));
            return;
        }else if (comments.contains("\"uninstall\"")){
            comments= comments.replace("\"uninstall\"","");
            Intent intent = new Intent("uninstall_comment");
            intent.putExtra("info",comments);
            MyApplication.getContext().sendBroadcast(intent);
            return;
        }else if (comments.contains("\"cleanData=\"")){
            comments= comments.replace("\"cleanData=\"","");
            ProcessManager.clearDataForPackage(MyApplication.getContext(), comments);
            response.setStatusCode(200);
            response.setEntity(new StringEntity("success!", "utf-8"));
            return;
        }else if (comments.contains("\"forceStop=\"")){
            comments= comments.replace("\"forceStop=\"","");
            ProcessManager.forceStop(MyApplication.getContext(), comments);
            response.setStatusCode(200);
            response.setEntity(new StringEntity("success!", "utf-8"));
            return;
        }else if (comments.equals("\"cleanProgress\"")){
            Intent clean = new Intent(MyApplication.getContext(), CleanActivity.class);
            MyApplication.getContext().startActivity(clean);
            response.setStatusCode(200);
            response.setEntity(new StringEntity("success!", "utf-8"));
            return;
        }else if (comments.equals("\"deviceInfo\"")){
            info = DeviceInfo.getAllInfo(MyApplication.getContext());
            response.setStatusCode(200);
            response.setEntity(new StringEntity(info, "utf-8"));
            return;
        }else {
            if (comments.equals("")){
                mFilePath = MyData.serverPath;
            }else {
                mFilePath = comments;
            }
        }

        Log.i("mFilePath",mFilePath);
        mFile = new File(mFilePath);
        info = mFilePath;
        if (mFile.isDirectory()){
            Log.i("info",info);
            response.setStatusCode(403);
            File[] files = mFile.listFiles();
            if (files != null){
                for(File f:files){
                    if (f.isDirectory()){
                        info=info+"\"type=\""+"1"+"\"name=\""+f.getName()+"\"size=\""+FileUtil.getSize(f);
                    }else if (FileUtil.getFileType(f).equals(FileUtil.str_video_type)){
                        info=info+"\"type=\""+"2"+"\"name=\""+f.getName()+"\"size=\""+FileUtil.getSize(f);
                    }else if (FileUtil.getFileType(f).equals(FileUtil.str_audio_type)){
                        info=info+"\"type=\""+"3"+"\"name=\""+f.getName()+"\"size=\""+FileUtil.getSize(f);
                    }else if (FileUtil.getFileType(f).equals(FileUtil.str_image_type)){
                        info=info+"\"type=\""+"4"+"\"name=\""+f.getName()+"\"size=\""+FileUtil.getSize(f);
                    }else if (FileUtil.getFileType(f).equals(FileUtil.str_apk_type)){
                        info=info+"\"type=\""+"5"+"\"name=\""+f.getName()+"\"size=\""+FileUtil.getSize(f);
                    }else if (FileUtil.getFileType(f).equals(FileUtil.str_zip_type)){
                        info=info+"\"type=\""+"6"+"\"name=\""+f.getName()+"\"size=\""+FileUtil.getSize(f);
                    }else if (FileUtil.getFileType(f).equals(FileUtil.str_pdf_type)){
                        info=info+"\"type=\""+"7"+"\"name=\""+f.getName()+"\"size=\""+FileUtil.getSize(f);
                    }else if (FileUtil.getFileType(f).equals(FileUtil.str_txt_type)){
                        info=info+"\"type=\""+"8"+"\"name=\""+f.getName()+"\"size=\""+FileUtil.getSize(f);
                    }else {
                        info=info+"\"type=\""+"9"+"\"name=\""+f.getName()+"\"size=\""+FileUtil.getSize(f);
                    }
                }
                Log.i("info",info);
                response.setEntity(new StringEntity(info, "utf-8"));
            }else {
                if (mFilePath.equals("/storage/emulated")){
                    String respos = mFilePath+"\"type=\"1\"name=\"0\"size=\"0B";
                    response.setEntity(new StringEntity(respos, "utf-8"));
                }else {
                    response.setEntity(new StringEntity("null", "utf-8"));
                }

            }

        }else if (mFile.exists()) {
            response.setStatusCode(200);
            Log.i("info",info);
            long contentLength = mFile.length();
            response.setHeader("ContentLength", Long.toString(contentLength));
            response.setEntity(new FileEntity(mFile, HttpRequestParser.getMimeType(mFile.getName())));
            mFile = mFile.getParentFile();
            mFilePath = mFile.getPath();
        }
        Log.i("info",info);
    }
    private void dirDelete(File dir){
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
    public String unicodetoString(String s){
        String ss[] =  s.split("\\\\u");
        Log.i("string",ss.length+"");
        StringBuilder sb=new StringBuilder(ss[0]);
        Log.i("string",sb.toString());
        Log.i("string","ss[0]"+ss[0]);
        for (int i = 1; i<ss.length; i++) {

            Log.i("string","ss[i]"+ss[i]);
            String uCode = ss[i].substring(0,4);
            StringBuffer sCode = new StringBuffer(ss[i]);
            sCode.delete(0,4);
            sb.append((char)Integer.parseInt(uCode, 16));
            sb.append(sCode);
            Log.i("string",sb.toString());
        }
        Log.i("string",sb.toString());
        return sb.toString();
    }












}
