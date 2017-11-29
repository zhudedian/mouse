package com.ider.mouse.util;

import com.ider.mouse.db.MyData;

/**
 * Created by Eric on 2017/9/7.
 */

public class HandCom {
    public static void hand(String com){
        int x;
        int y;
        if (com.contains("cm")){
            try {
                String[] pos = com.split("P");
                if (pos.length==3) {
                    x = Integer.parseInt(pos[1]);
                    y = Integer.parseInt(pos[2]);
                    //Log.i("cm", "x=" + x + "y=" + y);
                    MyData.mouseView.move(x, y);
//                    server.sendMessage("success");
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            return;
        }
        if (com.contains("cs")){
            try {
                String[] pos = com.split("P");
                x = Integer.parseInt(pos[1]);
                y = Integer.parseInt(pos[2]);
                //Log.i("cm", "y=" + y);
                MyData.mouseView.moves(x,y);

            }catch (Exception e){
                e.printStackTrace();
            }

            return;
        }
        if (com.contains("cd")){
            MyData.mouseView.resert();
            MyData.mouseView.down();
            return;
        }
        if (com.contains("cu")){
            try {
                String[] pos = com.split("P");
                x = Integer.parseInt(pos[1]);
                y = Integer.parseInt(pos[2]);
                MyData.mouseView.up(x,y);
                MyData.mouseView.resert();
            }catch (Exception e){
                e.printStackTrace();
            }
            return;
        }
        if (com.contains("cc")){
            MyData.mouseView.click();
            MyData.mouseView.resert();
            return;
        }
        if (com.contains("cn")){
            MyData.mouseView.resert();
            return;
        }
        if (com.contains("cb")){
            SendKey.sendBack();
            return;
        }
        if (com.contains("coup")){
            SendKey.sendUp();
            return;
        }
        if (com.contains("codown")){
            SendKey.sendDown();
            return;
        }
        if (com.contains("coleft")){
            SendKey.sendLeft();
            return;
        }
        if (com.contains("coright")){
            SendKey.sendRight();
            return;
        }
        if (com.contains("cocenter")){
            SendKey.sendCenter();
            return;
        }
        if (com.contains("comenubt")){
            SendKey.sendMenu();
            return;
        }
    }
}
