package com.ider.mouse.util;

import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.ider.mouse.MouseService;
import com.ider.mouse.db.MyData;
import com.yanzhenjie.andserver.RequestHandler;
import com.yanzhenjie.andserver.upload.HttpFileUpload;
import com.yanzhenjie.andserver.upload.HttpUploadContext;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.ider.mouse.MouseService.installApk;

/**
 * Created by Eric on 2017/8/31.
 */

public class RequestInstallHandler implements RequestHandler {
    private Handler mHandler;

    public RequestInstallHandler(Handler handler){
        super();
        mHandler = handler;
    }
    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        // HttpFileUpload.isMultipartContent(request) // DELETE、PUT、POST method。
        Log.i("filepath","handle(HttpRequest request");
        if (!HttpFileUpload.isMultipartContentWithPost(request)) { // Is POST and upload.
            response(403, "You must upload file.", response);
        } else {
            // File save directory.
            final File saveDirectory = new File(Environment.getExternalStorageDirectory().getPath()+"/install_");
            if (saveDirectory.exists()){
                dirDelete(saveDirectory);
                saveDirectory.mkdirs();
            }else {
                saveDirectory.mkdirs();
            }
            Log.i("filepath", Environment.getExternalStorageDirectory().getPath());
            Log.i("filepath","saveDirectory.isDirectory()");
            if (saveDirectory.isDirectory()) {
                Log.i("filepath","saveDirectory.isDirectory()2");
                try {
                    Log.i("filepath","saveDirectory.isDirectory()3");
                    processFileUpload(request, saveDirectory,response);
                } catch (Exception e) {
                    e.printStackTrace();
                    response(500, "Save the file when the error occurs.", response);
                }
            } else {
                Log.i("filepath","The server can not save the file.");
                response(500, "The server can not save the file.", response);
            }
        }
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

    private void response(int responseCode, String message, HttpResponse response) throws IOException {
        response.setStatusCode(responseCode);
        response.setEntity(new StringEntity(message, "utf-8"));
    }

    /**
     * Parse file and save.
     *
     * @param request       request.
     * @param saveDirectory save directory.
     * @throws Exception may be.
     */
    private void processFileUpload(HttpRequest request, File saveDirectory,HttpResponse response) throws Exception {
        FileItemFactory factory = new DiskFileItemFactory(1024 * 1024, saveDirectory);
        HttpFileUpload fileUpload = new HttpFileUpload(factory);

        // Set upload process listener.
        // fileUpload.setProgressListener(new ProgressListener(){...});
        Log.i("filepath","for (FileItem");
        List<FileItem> fileItems = fileUpload.parseRequest(new HttpUploadContext((HttpEntityEnclosingRequest) request));
        Log.i("filepath","for (FileItem2");
        Log.i("FileItem",fileItems.size()+"");
        for (FileItem fileItem : fileItems) {
            Log.i("filepath","for (FileItem3");
            if (!fileItem.isFormField()) { // File param.
                Log.i("filepath","if (!fileItem.isFormField())");
                // Attribute.
                // fileItem.getContentType();
                // fileItem.getFieldName();
                // fileItem.getName();
                // fileItem.getSize();
                // fileItem.getString();

                File uploadedFile = new File(saveDirectory, fileItem.getName());
                // 把流写到文件上。
                fileItem.write(uploadedFile);
                int result = PackageUtils.installSlient(uploadedFile.getPath());
                if (result == 0){
                    uploadedFile.delete();
                    response(200, "Ok.", response);
                }else {
                    response(500, "installFailed", response);
                }
            } else { // General param.
                String key = fileItem.getName();
                String value = fileItem.getString();
            }
        }
    }
}
