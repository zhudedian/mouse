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
    public RequestAppIconHandler() {

    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        // You can according to the client param can also be downloaded.
        File downLoadFile = new File("/system/media/yzg.apk");
        if (downLoadFile.exists()) {
            response.setStatusCode(200);
            long contentLength = downLoadFile.length();
            response.setHeader("ContentLength", Long.toString(contentLength));
            response.setEntity(new FileEntity(downLoadFile, HttpRequestParser.getMimeType(downLoadFile.getName())));
        }
    }


}
