package com.example.chattest.sendFile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.chattest.MainActivity;
import com.example.chattest.R;
import com.example.chattest.networkLogic.SendingQueue;
import com.example.chattest.networkLogic.protocol.MsgCodes;
import com.example.chattest.networkLogic.protocol.Protocol;
import com.example.chattest.utils.Constants;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.Reader;

public class FileSender {
    AppCompatActivity core;

    public static int openFileRequestCode = 83;

    public FileSender(AppCompatActivity core){
        this.core = core;
    }

    public void sendFile(Uri uri, SendingQueue sendingQueue, ProgressDialog progressDialog) {
        int fileSize = 0;
        int bytesRead = 0;
        Reader in = null;

        byte[] bytes = new byte[0];
        try {
            Bitmap bitmap = getBitmapFromUri(uri);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            bytes = stream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(Constants.TAG, bytes.length + " SIZE");

//        Protocol ourMsgProtocol = new Protocol();
//        ourMsgProtocol.setMsgCode(MsgCodes.fileCode);
//        ourMsgProtocol.setCurrentTime();
//
////                    byte[] aaa = toBytes(myBuffer);
//        ourMsgProtocol.setData(bytes);//toBytes(myBuffer)
//        sendingQueue.sendNode(ourMsgProtocol);



        String fileLengthStr = String.valueOf(bytes.length);
        Protocol startFileMsg = new Protocol();
        startFileMsg.setMsgCode(MsgCodes.fileStartCode);
        startFileMsg.setCurrentTime();
        startFileMsg.setData(fileLengthStr.getBytes());

        sendingQueue.send(startFileMsg);


//        try {
//            Thread.sleep(200);//TODO FUCK
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

//        Protocol ourMsgProtocol = new Protocol();
//        ourMsgProtocol.setMsgCode(MsgCodes.fileCode);
//        ourMsgProtocol.setCurrentTime();
//
//        ourMsgProtocol.setData(bytes);//toBytes(myBuffer)
//        networkManager.sendingQueue.sendNode(ourMsgProtocol);

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        byte[] myBuffer = new byte[1024*7];
        while ((bytesRead = byteArrayInputStream.read(myBuffer, 0, myBuffer.length)) != -1)
        {
            double p = myBuffer.length;
            double pp = bytes.length;
            progressDialog.incrementProgressBy((int) ((p/pp)*10000));
            try {
                Thread.sleep(50);//TODO FUCK
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            Protocol ourMsgProtocol = new Protocol();
            ourMsgProtocol.setMsgCode(MsgCodes.fileCode);
            ourMsgProtocol.setCurrentTime();

//                    byte[] aaa = toBytes(myBuffer);
            ourMsgProtocol.setData(myBuffer);//toBytes(myBuffer)

            fileSize += bytesRead;
            sendingQueue.send(ourMsgProtocol); // send the todo encrypted message

        }

        String lastNodeBytesCount = String.valueOf(fileSize);
        Protocol endFileMsg = new Protocol();
        endFileMsg.setMsgCode(MsgCodes.fileEndCode);
        endFileMsg.setCurrentTime();
        endFileMsg.setData(lastNodeBytesCount.getBytes());
        sendingQueue.send(endFileMsg);
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                core.getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }
}
