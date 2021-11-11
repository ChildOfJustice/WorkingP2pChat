package com.example.chattest.networkLogic;

import android.util.Log;

import com.example.chattest.MainActivity;
import com.example.chattest.cryptography.CipherModule;
import com.example.chattest.utils.Constants;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;

public class ClientClass extends Thread implements Serializable {

    public SendingQueue sendingQueue;
    public Socket socket;
    String hostAdd;
    MainActivity core;
    int port;

    private CipherModule cipher;

    public ClientClass(String hostAddress, int port, MainActivity core) {
        this.core = core;
        this.port = port;
        this.hostAdd = hostAddress;
    }

    @Override
    public void run() {
        boolean working = true;
        while(working){
            try {
                Log.d(Constants.TAG, "Client is trying to connect ot the server: " + hostAdd + ":" + port);
                socket = new Socket(hostAdd, port);

                if(cipher == null)
                    sendingQueue = new SendingQueue(socket, core);
                else
                    sendingQueue = new SendingQueue(socket, core, cipher);

                //TODO:
                //core.showToast("Connected to other device. You can now exchange messages.");

                Log.d(Constants.TAG, "Client is connected to server");
                working = false;
            } catch (IOException e) {
                //e.printStackTrace();
                Log.d(Constants.TAG, "Can't connect to server. Will retry... or you can stop, check the IP address and Port number and try again: " + e);

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }

            } catch (Exception e) {
                Log.d(Constants.TAG, "ERROR: " + e); //watch for this ERROR!!!
                e.printStackTrace();
                try {
                    socket = new Socket("localhost", port);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                sendingQueue = new SendingQueue(socket, core);
//                core.showToast("Connected to other device. You can now exchange messages.");
            }
        }
    }

    public void setCipher(CipherModule cipher){
        this.cipher = cipher;

        sendingQueue = new SendingQueue(socket, core, cipher);
    }
}

