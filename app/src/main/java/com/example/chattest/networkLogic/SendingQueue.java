package com.example.chattest.networkLogic;

import static java.util.concurrent.Executors.newFixedThreadPool;

import android.util.Log;

import com.example.chattest.MainActivity;
import com.example.chattest.cryptography.CipherModule;
import com.example.chattest.networkLogic.protocol.Protocol;
import com.example.chattest.utils.Constants;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.concurrent.ThreadPoolExecutor;

public class SendingQueue {
    private final ThreadPoolExecutor threadPoolExecutor;

    private Socket socket;
    private OutputStream outputStream;
    private final MainActivity core;

    public boolean encEnabled = false;
    private CipherModule cipher;

    private synchronized void writeToSocketStreamSync(byte[] data) throws IOException {
        //Log.d(Constants.TAG, "OPENED SYNC METHOD to send: " + data.length + " bytes");
        outputStream.write(data);
        outputStream.flush();
    }

    public SendingQueue(Socket skt, MainActivity core) {
        threadPoolExecutor = (ThreadPoolExecutor) newFixedThreadPool(1);

        socket = skt;
        this.core = core;
        try {
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SendingQueue(Socket skt, MainActivity core, CipherModule cipher) {
        this.cipher = cipher;
        encEnabled = true;

        threadPoolExecutor = (ThreadPoolExecutor) newFixedThreadPool(1);

        socket = skt;
        this.core = core;
        try {
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void send(Protocol protocol) {
        Runnable task = () -> {
            try {

                Log.d(Constants.TAG, "Sending a node, size is: " + protocol.getData().length);

                if(cipher != null){
                    byte[] encryptedData = cipher.encrypt(protocol.getData());
                    protocol.setData(encryptedData);
                } else {
                    Log.w(Constants.TAG, "Cipher module was not set!!! sending UNENCRYPTED msg: " + protocol.getData().length);
                }

                byte[] serialized = protocol.serialize();
                if(serialized == null){
                    Log.e(Constants.TAG, "Protocol node !serialization! FAILED!");
                } else {
                    writeToSocketStreamSync(serialized);

                    Log.d(Constants.TAG, "Sent a node, full serialized size is: " + serialized.length);

                    protocol.setFromThisDevice(true);

                    core.addProtocolNode(protocol);
                }
            } catch (IOException e) {
                Log.e(Constants.TAG, "Can't send message: " + e);
            } catch (Exception e) {
                Log.e(Constants.TAG, "Error: " + e);
            }
        };

        threadPoolExecutor.execute(task);
    }

    public void dispose(){
        threadPoolExecutor.shutdown();
    }
}
