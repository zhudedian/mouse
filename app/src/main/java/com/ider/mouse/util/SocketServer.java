package com.ider.mouse.util;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Eric on 2017/8/9.
 */

public class SocketServer {
    private static ServerSocket server;
    private Socket socket;
    private InputStream in;
    private String str=null;
    private boolean isClint=false;
    public static int size =15;
    public static boolean beginrecieve = false;
    public static Handler ServerHandler;

    /**
     * @steps bind();绑定端口号
     * @effect 初始化服务端
     * @param port 端口号
     * */
    public SocketServer(int port){
        try {
            server= new ServerSocket ( port );
        }catch (IOException e){
            e.printStackTrace ();
        }

    }

    /**
     * @steps listen();
     * @effect socket监听数据
     * */
    public void beginListen()
    {
        isClint=true;
        new Thread ( new Runnable ( )
        {
            @Override
            public void run()
            {
                try {
                    /**
                     * accept();
                     * 接受请求
                     * */
                while (true) {
                    Socket soc = server.accept();
                    socket = soc;
                    new ServerThread(soc);
                }
//                    socket=server.accept ();
//                    try {
//                        /**得到输入流*/
//                        in =socket.getInputStream();
//                        /**
//                         * 实现数据循环接收
//                         * */
//                        while (isClint&&!socket.isClosed())
//                        {
//                            byte[] bt=new byte[size];
//                            in.read ( bt );
//                            str=new String ( bt,"UTF-8" );                  //编码方式  解决收到数据乱码
//                            if (str!=null&&str!="exit")
//                            {
//                                returnMessage ( str );
//                            }else if (str==null||str=="exit"){
//                                break;                                     //跳出循环结束socket数据接收
//                            }
//                            //System.out.println(str);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace ( );
////                        socket.isClosed ();
//                    }
                } catch (Exception e) {
                    e.printStackTrace ( );
//                    socket.isClosed ();
                }
            }
        } ).start ();
    }


    /**
     * @steps write();
     * @effect socket服务端发送信息
     * */
    public void sendMessage(final String chat)
    {
        Thread thread=new Thread ( new Runnable ( )
        {
            @Override
            public void run()
            {
                try {
                    PrintWriter out=new PrintWriter ( socket.getOutputStream () );
                    out.print ( chat );
                    out.flush ();
                } catch (Exception e) {
                    e.printStackTrace ( );
                }
            }
        } );
        thread.start ();
    }

    /**
     * @steps read();
     * @effect socket服务端得到返回数据并发送到主界面
     * */
    public void returnMessage(String chat){
        Message msg=new Message ();
        msg.obj=chat;
        ServerHandler.sendMessage ( msg );
    }
    public void endListen(){
        isClint = false;

    }
    class ServerThread extends Thread{
        Socket socket;
        InputStream in;
        String str=null;
        boolean isClint=false;
        int endCount ;
        public ServerThread(Socket socket){
            super();
            this.socket = socket;
            try {
                this.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void run() {
            isClint=true;
            try {
                in =socket.getInputStream();
                while (isClint&&!socket.isClosed()) {
                    byte[] bt=new byte[size];
                    in.read ( bt );
                    str=new String ( bt,"UTF-8" );                  //编码方式  解决收到数据乱码
                    if (str!=null&&str!="exit") {
                        if (str.contains("c")) {
                            returnMessage ( str );
                            endCount = 0;
                        }else {
                            endCount++;
                            Log.i("count",endCount+"");
                        }
                        if (endCount>=5){
                            isClint = false;
                        }
                    }else if (str==null||str=="exit"){
                        break;                                     //跳出循环结束socket数据接收
                    }
                }
            } catch (Exception e) {
                e.printStackTrace ( );
            }
        }
    }
}
