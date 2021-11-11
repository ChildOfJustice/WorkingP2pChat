package com.example.chattest.networkLogic;

import android.os.Looper;
import android.util.Log;

import com.example.chattest.MainActivity;
import com.example.chattest.cryptography.CipherModule;
import com.example.chattest.utils.Constants;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;

// server class for listening
public class ServerClass extends Thread {

    public Socket socket;
    public Receiver receiver;
    ServerSocket serverSocket;
    int port;
    volatile MainActivity core;

    private CipherModule cipher;
    public boolean encEnabled = false;

    public ServerClass(int port, MainActivity core) {
        this.port = port;
        this.core = core;
    }

    @Override
    public void run() {
        try {
            if (serverSocket != null)
                serverSocket.close();
            serverSocket = new ServerSocket(port);
            Looper.prepare();

            //core.showToast("Server Started. Waiting for client...");

            Log.d(Constants.TAG, "Waiting for client...");
            socket = serverSocket.accept();
            Log.d(Constants.TAG, "Accepted a Client");

//            if(cipher == null)
                receiver = new Receiver(socket, core);
//            else
//                receiver = new Receiver(socket, core, cipher);

            receiver.start();
            Log.d(Constants.TAG, "Receiver has been started");

        } catch (IOException e) {
            e.printStackTrace();
            Log.d(Constants.TAG, "ERROR: " + e);
        } catch (Exception e) {
            Log.d(Constants.TAG, "ERROR: " + e);
        }
    }

    public void setCipher(CipherModule cipher){
        this.cipher = cipher;
        receiver.setCipher(cipher);
//        receiver.dispose();
//        receiver = new Receiver(socket, core, cipher);
//        receiver.start();
    }
}