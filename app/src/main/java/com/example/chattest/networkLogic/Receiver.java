package com.example.chattest.networkLogic;

import static java.util.concurrent.Executors.newFixedThreadPool;

import android.util.Log;

import com.example.chattest.MainActivity;
import com.example.chattest.cryptography.CipherModule;
import com.example.chattest.networkLogic.protocol.MsgCodes;
import com.example.chattest.networkLogic.protocol.Protocol;
import com.example.chattest.utils.Constants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ThreadPoolExecutor;

public class Receiver extends Thread implements Serializable {

    public static final int bufferSize = 5294;

    private final ThreadPoolExecutor deserializationQueue;

    private Socket socket;
    private InputStream inputStream;
    private final MainActivity core;
    private boolean running = true;

    public boolean encEnabled = false;
    private CipherModule cipher;

//    public AddProtocolNodeHandler showProtocolNodeHandler;

    public Receiver(Socket skt, MainActivity core) {
        deserializationQueue = (ThreadPoolExecutor) newFixedThreadPool(1);

        socket = skt;
        this.core = core;
        try {
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Receiver(Socket skt, MainActivity core, CipherModule cipher) {
        this.cipher = cipher;

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
        int curReceivedBytes;
        int allReceivedBytes = 0;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        while (socket != null) {
            try {
                if(!running)
                    break;

                while(inputStream.available() != 0);

                byte[] result;


                while(true){
                    curReceivedBytes = inputStream.read(buffer);
                    if(curReceivedBytes + allReceivedBytes > bufferSize){
                        System.out.println("smth is WRONG: " + curReceivedBytes);
                        int nextObjectBytesReceived = curReceivedBytes + allReceivedBytes - bufferSize;
                        baos.write(buffer, 0, nextObjectBytesReceived);
                        result = baos.toByteArray();

                        baos.reset();
                        baos.write(buffer, nextObjectBytesReceived-1, curReceivedBytes);
                        allReceivedBytes = curReceivedBytes;
                        break;
                    } else {
                        baos.write(buffer, 0, curReceivedBytes);
                        allReceivedBytes += curReceivedBytes;
                        System.out.println("RCVD: " + curReceivedBytes);
                        if(allReceivedBytes == bufferSize){
                            result = baos.toByteArray();
                            baos.reset();
                            allReceivedBytes = 0;
                            break;
                        }
                    }
                }



                //bytes = inputStream.read(buffer);

                //if (bytes > 0) {
                    Log.d(Constants.TAG, "Received bytes from inputStream: " + result.length);

                    scheduleDeserializationTask(result);
                //}
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

    private void scheduleDeserializationTask(byte[] data){
        Runnable task = () -> {
            Protocol protocol = Protocol.deserialize(data);
            if(protocol == null){
                Log.e(Constants.TAG, "Protocol deserialization FAILED!!!: ");
            } else {
                Log.d(Constants.TAG, "Received a protocol node with size: " + protocol.getData().length);

                if(cipher != null){
                    byte[] decryptedData = cipher.decrypt(protocol.getData());
                    try {
                        protocol.setData(decryptedData);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(Constants.TAG, "Cannot set data after decryption: " + e.getMessage());
                    }
                } else {
                    Log.w(Constants.TAG, "Cipher module was not set!!! will not DECRYPT msg: " + protocol.getData().length);
                }

                protocol.setFromThisDevice(false);

                core.addProtocolNode(protocol);

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
