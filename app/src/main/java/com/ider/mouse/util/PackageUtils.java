package com.ider.mouse.util;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;


public class PackageUtils {

    public static final String TAG = "PackageUtils";

    private PackageUtils() {
        throw new AssertionError();
    }
   
/**
 *
 * @param filePath
 * @return 0 means normal, 1 means file not exist, 2 means other exception error
 */
public static int installSlient(String filePath) {
	File file = new File(filePath);
	if (filePath == null || filePath.length() == 0 || file == null || file.length() <= 0
		|| !file.exists() || !file.isFile()) {
		return 1;
	}

	String[] args ;

	if (Build.VERSION.SDK_INT>=24){
		args = new String[]{ "pm", "install", "-i","com.example.bootinit", filePath };
	}else {
		args = new String[]{ "pm", "install", "-r", filePath };
	}
	ProcessBuilder processBuilder = new ProcessBuilder(args);
 
	Process process = null;
	BufferedReader successResult = null;
	BufferedReader errorResult = null;
	StringBuilder successMsg = new StringBuilder();
	StringBuilder errorMsg = new StringBuilder();
	int result;
	try {
		process = processBuilder.start();
		successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
		errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		String s;
 
		while ((s = successResult.readLine()) != null) {
			successMsg.append(s);
		}
 
		while ((s = errorResult.readLine()) != null) {
			errorMsg.append(s);
		}
	} catch (IOException e) {
		e.printStackTrace();
		result = 2;
	} catch (Exception e) {
		e.printStackTrace();
		result = 2;
	} finally {
		try {
			if (successResult != null) {
				successResult.close();
			}
			if (errorResult != null) {
				errorResult.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (process != null) {
			process.destroy();
		}
	}
 
	// TODO should add memory is not enough here
	if (successMsg.toString().contains("Success") || successMsg.toString().contains("success")) {
		result = 0;
	} else {
		result = 2;
	}
	Log.d("install", "successMsg:" + successMsg + ", ErrorMsg:" + errorMsg);
	return result;
}


}