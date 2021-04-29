package com.bytedance.practice5.socket;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.Socket;

import static android.content.ContentValues.TAG;

public class ClientSocketThread extends Thread {
    public ClientSocketThread(SocketActivity.SocketCallback callback) {
        this.callback = callback;
    }

    private SocketActivity.SocketCallback callback;

    private boolean stopFlag = false;
    private volatile String message = "";

    public void disconnect(){ stopFlag = true;}
    public synchronized void clearMsg(){ this.message = "";}

    //head请求内容
    private static String content = "HEAD /xxjj/index.html HTTP/1.1\r\nHost:www.sjtu.edu.cn\r\n\r\n";


    @Override
    public void run() {
        // TODO 6 用socket实现简单的HEAD请求（发送content）
        //  将返回结果用callback.onresponse(result)进行展示
        Log.d("socket", "run: 客户端线程 start");
        try{
            Socket socket = new Socket("www.sjtu.edu.cn", 80);
            BufferedOutputStream os = new BufferedOutputStream(socket.getOutputStream());
            BufferedInputStream is = new BufferedInputStream(socket.getInputStream());

            Log.d("socket", "客户端请求 "+ content);

            double n = 1;
            byte[] data = new byte[1024*5];
            int len=-1;
            while(!stopFlag && socket.isConnected()){
                if (!content.isEmpty()){
                    Log.d("socket", "客户端发送 "+ content);
                    os.write(content.getBytes());
                    os.flush();
                    clearMsg();
                    int reciveLen = is.read(data);
                    if (reciveLen!=-1) {
                        String receive = new String(data, 0, reciveLen);
                        Log.d("socket", "客户端收到 " + receive);
                        callback.onResponse(receive);
                    }else {
                        Log.d("socket", "客户端收到-1");
                    }
                }
                sleep(300);
            }
            Log.d("socket", "客户端断开");
            os.flush();
            os.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}