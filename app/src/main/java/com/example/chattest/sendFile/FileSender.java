package com.example.chattest.sendFile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chattest.MainActivity;
import com.example.chattest.R;
import com.example.chattest.networkLogic.SendingQueue;
import com.example.chattest.networkLogic.protocol.MsgCodes;
import com.example.chattest.networkLogic.protocol.Protocol;
import com.example.chattest.utils.Constants;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.Random;

public class FileSender {
    AppCompatActivity core;

    public static int openFileRequestCode = 83;

    public FileSender(AppCompatActivity core){
        this.core = core;
    }

    public void sendImage(Uri uri, SendingQueue sendingQueue) {
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

        String fileName = uri.getPath();
        int cut = fileName.lastIndexOf('/');
        if (cut != -1) {
            fileName = fileName.substring(cut + 1);
        }
        Log.d(Constants.TAG, "URI path: " + uri.toString());
        String fileLengthStr = fileName;
        Protocol startFileMsg = new Protocol();
        startFileMsg.setMsgCode(MsgCodes.imgStartCode);
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

            try {
                Thread.sleep(50);//TODO FUCK
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            Protocol ourMsgProtocol = new Protocol();
            ourMsgProtocol.setMsgCode(MsgCodes.imgPartCode);
            ourMsgProtocol.setCurrentTime();

//                    byte[] aaa = toBytes(myBuffer);
            ourMsgProtocol.setData(myBuffer);//toBytes(myBuffer)

            fileSize += bytesRead;
            sendingQueue.send(ourMsgProtocol); // send the todo encrypted message

        }

        String lastNodeBytesCount = String.valueOf(fileSize);
        Protocol endFileMsg = new Protocol();
        endFileMsg.setMsgCode(MsgCodes.imgEndCode);
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





    public void saveFile(String path, String fileName, String data) {
//        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
//        File myDir = new File(root + "/saved_images");
//        myDir.mkdirs();
//        Random generator = new Random();
//        int n = 10000;
//        n = generator.nextInt(n);
//        String fname = "Some-" + n + ".txt";
//        File file = new File(myDir, fname);

        File mFolder = new File(core.getExternalFilesDir(null) + path);
        File imgFile = new File(mFolder.getAbsolutePath() + "/" + fileName);
        if (!mFolder.exists()) {
            mFolder.mkdir();
        }
        if(imgFile.exists())
            imgFile.delete();
        if (!imgFile.exists()) {
            try {
                imgFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream out = new FileOutputStream(imgFile);
//            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.write(data.getBytes()); // writing
            out.flush();
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }


        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        MediaScannerConnection.scanFile(core, new String[] { imgFile.toString() }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });



//
//        String root = Environment.getExternalStorageDirectory().toString();
//        File myDir = new File(root + "/saved_images");
//        if (!myDir.exists()) {
//            myDir.mkdirs();
//        }
//        Random generator = new Random();
//        int n = 10000;
//        n = generator.nextInt(n);
////        String fname = "Image-"+ n +".jpg";
//        String fname = "Some-"+ n +".txt";
//        File file = new File (myDir, fname);
//        if (file.exists ())
//            file.delete ();
//        try {
//            FileOutputStream out = new FileOutputStream(file);
//            //finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
//            out.write(data.getBytes()); // writing
//            out.flush();
//            out.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//            final Uri contentUri = Uri.fromFile(file);
//            scanIntent.setData(contentUri);
//            core.sendBroadcast(scanIntent);
//        } else {
//            final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()));
//            core.sendBroadcast(intent);
//        }
//        core.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
//                Uri.parse("file://" + Environment.getExternalStorageDirectory())));

//        File path = core.getExternalFilesDir(null); // getting file path
//        File path = core.getFilesDir(); // getting file path
//        String filePath = path.toString();
//
//        File file = new File(path, fileName); // saving shared files
//
//        FileOutputStream stream; // creating a file output stream for sending file
//        try {
//            stream = new FileOutputStream(file, false);
//            stream.write(data.getBytes()); // writing
//            stream.close(); // closing the stream
//            Toast.makeText(core, "File Succcessfully Saved!", Toast.LENGTH_SHORT).show();
//            Log.d(Constants.TAG, "File size is: " + data.length());
//        } catch (FileNotFoundException e) {
//            Log.e(Constants.TAG, e.toString());
//        } catch (IOException e) {
//            Log.e(Constants.TAG, e.toString());
//        }
    }
    public void saveImage(String path, String fileName, byte[] imgData) {

        if(!fileName.contains(".jpg"))
            fileName+=".jpg";
        File mFolder = new File(core.getExternalFilesDir(null) + path);
        File imgFile = new File(mFolder.getAbsolutePath() + "/" + fileName);
        if (!mFolder.exists()) {
            mFolder.mkdir();
        }
        if(imgFile.exists())
            imgFile.delete();
        if (!imgFile.exists()) {
            try {
                imgFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream out = new FileOutputStream(imgFile);
            Bitmap bmp = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }


        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        MediaScannerConnection.scanFile(core, new String[] { imgFile.toString() }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }
}
