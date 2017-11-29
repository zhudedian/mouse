package com.ider.mouse.util;

import android.util.Log;

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

/**
 * Created by Eric on 2017/10/16.
 */

public class RequestAppIconHandler implements RequestHandler {
    private String mFilePath = MyData.serverPath;
    private String parentPath;
    private String comments;
    private String downLoadPath = "";
    private File downLoadFile;
    private File mFile;
    private String fileName;
    private String info="";
    public RequestAppIconHandler() {

    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        // You can according to the client param can also be downloaded.
        comments = request.getFirstHeader("comment").getValue();
        Log.i("comments",comments);
        comments = unicodetoString(comments);
        Log.i("comments",comments);
            downLoadPath= "/sdcard/Picture/icons/"+comments+".jpg";
            Log.i("downLoadPath",downLoadPath);
            downLoadFile = new File(downLoadPath);
            if (downLoadFile.exists()) {
                response.setStatusCode(200);
                long contentLength = downLoadFile.length();
                response.setHeader("ContentLength", Long.toString(contentLength));
                response.setEntity(new FileEntity(downLoadFile, HttpRequestParser.getMimeType(downLoadFile.getName())));
            }
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
