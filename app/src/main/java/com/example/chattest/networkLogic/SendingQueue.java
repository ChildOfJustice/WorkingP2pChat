package com.example.chattest.networkLogic;

import static java.util.concurrent.Executors.newFixedThreadPool;

import android.graphics.Color;
import android.util.Log;

import com.example.chattest.LoginActivity;
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
    private final LoginActivity core;

    private synchronized void writeToSocketStreamSync(byte[] data) throws IOException {
        //Log.d(Constants.TAG, "OPENED SYNC METHOD to send: " + data.length + " bytes");
        outputStream.write(data);
        outputStream.flush();//TODO???
    }

    public SendingQueue(Socket skt, LoginActivity core) {
        threadPoolExecutor = (ThreadPoolExecutor) newFixedThreadPool(1);

        socket = skt;
        this.core = core;
        try {
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(Protocol data) {
        Runnable task = () -> {
            try {

                //Log.d(Constants.TAG, "Sending msg: " + new String(data.getData()));

                byte[] serializedData = data.serialize();
                if(serializedData == null){
                    Log.d(Constants.TAG, "Protocol node !serialization! FAILED!");
                } else {
                    writeToSocketStreamSync(serializedData);
                    //Log.d(Constants.TAG, "msg size is: " + serializedData.length);

                    data.setFromThisDevice(true);

                    core.addProtocolNode(Color.parseColor("#FCE4EC"), data);
                    core.runOnUiThread(() ->
                            core.editTextMessage.setText("")
                    );
                }
            } catch (IOException e) {
                Log.d(Constants.TAG, "Can't send message: " + e);
            } catch (Exception e) {
                Log.d(Constants.TAG, "Error: " + e);
            }
        };

        threadPoolExecutor.execute(task);
    }

    //will send a piece of protocol without showing it on the sender screen
    public void sendNode(Protocol data) {
        Runnable task = () -> {
            //Log.d(Constants.TAG, "Task has been started to send (pure, not protocol size): " + data.getData().length + " nsg code is: " + data.getMsgCode());
            try {

                byte[] serialized = data.serialize();
                writeToSocketStreamSync(serialized);
                //Log.d(Constants.TAG, "Sending a node, protocol size is: " + serialized.length);

            } catch (IOException e) {
                Log.d(Constants.TAG, "Can't send a node: " + e);
            } catch (Exception e) {
                Log.d(Constants.TAG, "Error: " + e);
            }
        };

        threadPoolExecutor.execute(task);
    }

    // writing
//    public void write(String msg) {
//        new Thread(() -> {
//            try {
//                byte[] bytes = new byte[0];
//                outputStream.write(msg.getBytes());
//                core.addMessage(Color.parseColor("#FCE4EC"), msg, bytes);
//                core.runOnUiThread(() ->
//                        core.messageEditText.setText("")
//                );
//            } catch (IOException e) {
//                Log.d(Constants.TAG, "Can't send message: " + e);
//            } catch (Exception e) {
//                Log.d(Constants.TAG, "Error: " + e);
//            }
//        }).start();
//
//    }
//
//    public void sendFile(File file) {
//
//        String fileLengthStr = String.valueOf(file.length());
//        Protocol startFileMsg = new Protocol();
//        startFileMsg.setMsgCode(MsgCodes.fileStartCode);
//        startFileMsg.setCurrentTime();
//        startFileMsg.setData(fileLengthStr.getBytes());
//        sendNode(startFileMsg);
//
//        Runnable task = () -> {
//            char[] myBuffer = new char[512];
//            int bytesRead = 0;
//            BufferedReader in = null;
//            try {
//                in = new BufferedReader(new FileReader(file));
//
//                while ((bytesRead = in.read(myBuffer, 0, myBuffer.length)) != -1)
//                {
//                    Protocol ourMsgProtocol = new Protocol();
//                    ourMsgProtocol.setMsgCode(MsgCodes.fileCode);
//                    ourMsgProtocol.setCurrentTime();
//                    ourMsgProtocol.setData(toBytes(myBuffer));
//
//                    sendNode(ourMsgProtocol); // send the todo encrypted message
//                }
//
//                String lastNodeBytesCount = String.valueOf(bytesRead);
//                Protocol endFileMsg = new Protocol();
//                endFileMsg.setMsgCode(MsgCodes.fileEndCode);
//                endFileMsg.setCurrentTime();
//                endFileMsg.setData(lastNodeBytesCount.getBytes());
//                sendNode(endFileMsg);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        };
//        threadPoolExecutor.execute(task);
//    }
    byte[] toBytes(char[] chars) {
        CharBuffer charBuffer = CharBuffer.wrap(chars);
        ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);
        byte[] bytes = Arrays.copyOfRange(byteBuffer.array(),
                byteBuffer.position(), byteBuffer.limit());
        Arrays.fill(byteBuffer.array(), (byte) 0); // clear sensitive data
        return bytes;
    }


    public boolean allMsgsAreSent(){
        long submitted = threadPoolExecutor.getTaskCount();
        long completed = threadPoolExecutor.getCompletedTaskCount();
        return (submitted - completed) == 0; // approximate
    }

    public void dispose(){
        threadPoolExecutor.shutdown();
    }
}
