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
    private File mFile;

    public RequestFileHandler() {

    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        // You can according to the client param can also be downloaded.
        Log.i("MyData.serverPath",MyData.serverPath);
        String fileName = request.getFirstHeader("comment").getValue();
        Log.i("method",fileName);
        fileName = unicodetoString(fileName);
        Log.i("method",fileName);
        if (fileName.contains("\"delete=\"")){
            fileName= fileName.replace("\"delete=\"","");
            if (mFilePath.equals("/")){
                mFilePath = mFilePath+fileName;
            }else {
                mFilePath = mFilePath+"/"+fileName;
            }
            mFile = new File(mFilePath);
            mFilePath = mFile.getParent();
            mFile.delete();
        }else if (fileName.equals("comment_back")){
            if (mFilePath.equals("/")){
                response.setEntity(new StringEntity("top", "utf-8"));
                return;
            }
            mFilePath = mFile.getParent();
            Log.i("mFilePath",mFilePath);
        }else {
            if (!fileName.equals("")){
                if (mFilePath.equals("/")){
                    mFilePath = mFilePath+fileName;
                }else {
                    mFilePath = mFilePath+"/"+fileName;
                }

            }

        }
        Log.i("mFilePath",mFilePath);
        mFile = new File(mFilePath);
        String info="";
        if (mFile.isDirectory()){
            Log.i("info","info");
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
                    response.setEntity(new StringEntity("type=0name=0size=0B", "utf-8"));
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
        }
        Log.i("info",info);
        MyData.serverPath=mFilePath;
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
