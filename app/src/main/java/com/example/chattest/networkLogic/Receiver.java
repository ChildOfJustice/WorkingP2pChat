package com.example.chattest.networkLogic;

import static java.util.concurrent.Executors.newFixedThreadPool;

import android.graphics.Color;
import android.util.Log;

import com.example.chattest.LoginActivity;
import com.example.chattest.networkLogic.protocol.Protocol;
import com.example.chattest.utils.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ThreadPoolExecutor;

public class Receiver extends Thread {

    public static final int bufferSize = 16238460;

    private final ThreadPoolExecutor deserializationQueue;

    private Socket socket;
    private InputStream inputStream;
    private final LoginActivity core;
    private boolean running = true;

    public Receiver(Socket skt, LoginActivity core) {
        deserializationQueue = (ThreadPoolExecutor) newFixedThreadPool(1);

        socket = skt;
        this.core = core;
        try {
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        byte[] buffer = new byte[bufferSize];
        int bytes;

        while (socket != null) {
            try {
                if(!running)
                    break;

                while(inputStream.available() != 0);

                bytes = inputStream.read(buffer);

                if (bytes > 0) {
                    Log.d(Constants.TAG, "Received bytes: " + bytes);

                    scheduleDeserializationTask(buffer, bytes);
                }
            } catch (SocketException se){
                if(se.getMessage().contains("Socket closed")){
                    running = false;
                    socket = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void scheduleDeserializationTask(byte[] data, int receivedBytes){
        Runnable task = () -> {
            Protocol protocol = Protocol.deserialize(data);
            if(protocol == null){
                Log.d(Constants.TAG, "Protocol deserialization FAILED!!!: ");
            } else {
                Log.d(Constants.TAG, "Received a msg: " + new String(protocol.getData()));
                protocol.setFromThisDevice(false);
                core.addProtocolNode(Color.parseColor("#000000"), protocol);
                //core.handler.obtainMessage(Constants.MESSAGE_READ, receivedBytes, -1, data).sendToTarget();
            }
        };
        deserializationQueue.execute(task);
    }

    public void dispose(){
        Log.d(Constants.TAG, "Receiver was destroyed");
        running = false;
        socket = null;
    }
}