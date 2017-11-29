package com.ider.mouse.clean;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Eric on 2017/6/5.
 */

public class InstallUtil {
    public static final String TAG = "PackageUtils";
    public static final int INSTALL_SUCCESS = 0;
    public static final int INSTALL_FILE_NOTFOUND = 1;
    public static final int INSTALL_ERROR = 2;

    private InstallUtil() {
        throw new AssertionError();
    }

    public static int installSlient(String filePath) {
        Log.i("install", "path = " + filePath);
        File file;
        if(filePath != null && filePath.length() != 0 && (file = new File(filePath)).exists() && file.length() > 0L && file.isFile()) {
            String[] args = new String[]{"pm", "install", "-r", filePath};
            ProcessBuilder processBuilder = new ProcessBuilder(args);
            Process process = null;
            BufferedReader successResult = null;
            BufferedReader errorResult = null;
            StringBuilder successMsg = new StringBuilder();
            StringBuilder errorMsg = new StringBuilder();

            try {
                process = processBuilder.start();
                successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
                errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));

                String e;
                while((e = successResult.readLine()) != null) {
                    successMsg.append(e);
                }

                while((e = errorResult.readLine()) != null) {
                    errorMsg.append(e);
                }
            } catch (Exception var19) {
                var19.printStackTrace();
            } finally {
                try {
                    if(successResult != null) {
                        successResult.close();
                    }

                    if(errorResult != null) {
                        errorResult.close();
                    }
                } catch (IOException var18) {
                    var18.printStackTrace();
                }

                if(process != null) {
                    process.destroy();
                }

            }

            byte result;
            if(successMsg.toString().contains("Success")) {
                result = 0;
            } else {
                result = 2;
            }

            Log.d("install", "successMsg:" + successMsg + ", ErrorMsg:" + errorMsg);
            return result;
        } else {
            return 1;
        }
    }

    public static boolean isPackageExists(Context context, String pkgname) {
        PackageManager pm = context.getPackageManager();

        try {
            pm.getPackageInfo(pkgname, 0);
            return true;
        } catch (PackageManager.NameNotFoundException var4) {
            var4.printStackTrace();
            return false;
        }
    }
}
