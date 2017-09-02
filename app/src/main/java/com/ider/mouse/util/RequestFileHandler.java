package com.ider.mouse.util;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.ider.mouse.db.MyData;
import com.yanzhenjie.andserver.RequestHandler;
import com.yanzhenjie.andserver.upload.HttpUploadContext;
import com.yanzhenjie.andserver.util.HttpRequestParser;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import java.io.File;
import java.io.IOException;

import static android.R.attr.imeFullscreenBackground;
import static android.R.attr.key;

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
                    if (parentPath.equals("/")) {
                        mFilePath = parentPath + files[i];
                    } else {
                        mFilePath = parentPath + "/" + files[i];
                    }
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
        }else if (comments.equals("\"commonBack\"")){
            comments= comments.replace("\"commonBack\"","");
            mFile = new File(comments);
            mFilePath = mFile.getParent();
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
                        info=info+"type="+"1"+"name="+f.getName()+"size="+FileUtil.getSize(f);
                    }else if (FileUtil.getFileType(f).equals(FileUtil.str_video_type)){
                        info=info+"type="+"2"+"name="+f.getName()+"size="+FileUtil.getSize(f);
                    }else {
                        info=info+"type="+"3"+"name="+f.getName()+"size="+FileUtil.getSize(f);
                    }
                }
                Log.i("info",info);
                response.setEntity(new StringEntity(info, "utf-8"));
            }else {
                if (mFilePath.equals("/storage/emulated")){
                    String respos = mFilePath+"type=1name=0size=0B";
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
