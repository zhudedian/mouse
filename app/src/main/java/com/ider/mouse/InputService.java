package com.ider.mouse;

import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.Button;

public class InputService extends InputMethodService implements View.OnClickListener {
    private InputConnection inputConnection;
    public InputService() {
    }

    @Override
    public void onCreate(){


    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        if (intent !=null){
            String info = intent.getStringExtra("info");
            inputConnection = getCurrentInputConnection();
            inputConnection.commitText(info,0);
        }
        return super.onStartCommand(intent,flags,startId);
    }
    @Override
    public void onClick(View v) {

    }

}
