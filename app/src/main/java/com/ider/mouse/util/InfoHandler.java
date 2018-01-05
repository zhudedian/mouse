package com.ider.mouse.util;

import android.content.Intent;
import android.util.Log;

import com.ider.mouse.MyApplication;
import com.ider.mouse.db.MyData;
import com.yanzhenjie.andserver.RequestHandler;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

/**
 * Created by Eric on 2017/9/7.
 */

public class InfoHandler implements RequestHandler {
    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {

        String info = request.getFirstHeader("info").getValue();
        info = unicodetoString(info);
        if (info.equals("\"requestInfo\"")){
            response.setEntity(new StringEntity(MyData.editText, "utf-8"));
        }else if (info.contains("\"closeIME\"")){
            String infos = info.replace("\"closeIME\"","");
            Intent intent = new Intent("commitMobileInfo");
            intent.putExtra("info",infos);
            MyApplication.getContext().sendBroadcast(intent);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            intent = new Intent("closeInputIME");
            MyApplication.getContext().sendBroadcast(intent);
            response.setEntity(new StringEntity("success", "utf-8"));
        }else {
            Intent intent = new Intent("commitMobileInfo");
            intent.putExtra("info",info);
            MyApplication.getContext().sendBroadcast(intent);
            response.setEntity(new StringEntity("success", "utf-8"));
        }

    }
    public String unicodetoString(String s){
        String ss[] =  s.split("\\\\u");
        StringBuilder sb=new StringBuilder(ss[0]);
        for (int i = 1; i<ss.length; i++) {
            String uCode = ss[i].substring(0,4);
            StringBuffer sCode = new StringBuffer(ss[i]);
            sCode.delete(0,4);
            sb.append((char)Integer.parseInt(uCode, 16));
            sb.append(sCode);
        }
        Log.i("string",sb.toString());
        return sb.toString();
    }
}
