package com.example.chattest.networkLogic;

import android.os.Looper;
import android.util.Log;

import com.example.chattest.MainActivity;
import com.example.chattest.utils.Constants;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkManager implements Serializable{
    private MainActivity core;

    public SendingQueue sendingQueue;

    public Receiver receiver;

    public ClientClass clientClass;
    public ServerClass serverClass;

    public NetworkManager(MainActivity core) {
        this.core = core;
    }

    // server class for listening
    public class ServerClass extends Thread implements Serializable{

        public Socket socket;
        ServerSocket serverSocket;
        int port;

        public ServerClass(int port) {
            this.port = port;
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
                Log.d(Constants.TAG, "Connection established from server");
                receiver = new Receiver(socket, core);
                receiver.start();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(Constants.TAG, "ERROR: " + e);
            } catch (Exception e) {
                Log.d(Constants.TAG, "ERROR: " + e);
            }
        }
    }

    // client class for sending
    public class ClientClass extends Thread implements Serializable{
        public Socket socket;
        String hostAdd;
        int port;

        public ClientClass(String hostAddress, int port) {
            this.port = port;
            this.hostAdd = hostAddress;
        }

        @Override
        public void run() {
            try {
                Log.d(Constants.TAG, "Client is trying to connect ot the server: " + hostAdd + ":" + port);
                socket = new Socket(hostAdd, port);

                sendingQueue = new SendingQueue(socket, core);

                //TODO:
                //core.showToast("Connected to other device. You can now exchange messages.");

                Log.d(Constants.TAG, "Client is connected to server");

            } catch (IOException e) {
                e.printStackTrace();
                Log.d(Constants.TAG, "Can't connect to server. Check the IP address and Port number and try again: " + e);
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

    public void createServerThread(String port) {
        serverClass = new ServerClass(Integer.parseInt(port));
    }

    public void createClientThread(String targetIp, String port) {
        clientClass = new ClientClass(targetIp, Integer.parseInt(port));
    }
}
