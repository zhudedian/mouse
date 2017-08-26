package com.ider.mouse.util;

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
 * Created by Eric on 2017/8/24.
 */

public class RequestFileHandler implements RequestHandler {

    private String mFilePath;

    public RequestFileHandler(String filePath) {
        this.mFilePath = filePath;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        // You can according to the client param can also be downloaded.

        File file = new File(mFilePath);
        if (file.exists()) {
            response.setStatusCode(200);

            long contentLength = file.length();
            response.setHeader("ContentLength", Long.toString(contentLength));
            response.setEntity(new FileEntity(file, HttpRequestParser.getMimeType(file.getName())));
        } else {
            response.setStatusCode(404);
            response.setEntity(new StringEntity(""));
        }
    }
}
