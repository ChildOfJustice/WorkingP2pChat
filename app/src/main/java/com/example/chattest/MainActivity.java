package com.example.chattest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.chattest.networkLogic.ClientClass;
import com.example.chattest.networkLogic.ServerClass;
import com.example.chattest.networkLogic.protocol.MsgCodes;
import com.example.chattest.networkLogic.protocol.Protocol;
import com.example.chattest.sendFile.FileSender;
import com.example.chattest.utils.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    boolean checkFabs;
    EditText editTextMessage;
    RecyclerView recyclerView;
    ChatAdapter adapter;

    ByteArrayOutputStream byteArrayBufferFileYours;
    ByteArrayOutputStream byteArrayBufferFileTheir;
    boolean startedFileReceiving = false;
    boolean startedFileSending = false;

    List<Protocol> protocols;

    ServerClass serverObject;
    ClientClass clientObject;

    FileSender fileSender;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton fab =  findViewById(R.id.fab);
        Bundle arguments = getIntent().getExtras();

//        serverObject = (ServerClass)getIntent().getSerializableExtra("Server");
//        Log.d(Constants.TAG, "Got the ServerClass object: " + serverObject.toString());



        String AnotherIP = arguments.get("AnotherIP").toString();
        String YouPort = arguments.get("YouPort").toString();
        String AnotherPort = arguments.get("AnotherPort").toString();



        startServer(AnotherPort);

        fileSender = new FileSender(this);

        clientObject = new ClientClass(AnotherIP, Integer.parseInt(AnotherPort), this);
        clientObject.start();


        //byte[] key = (byte[]) arguments.get("KeyValue");
        checkFabs = false;
        editTextMessage = findViewById(R.id.editTextMessage);
        recyclerView = (RecyclerView) findViewById(R.id.ChatView);
        //создается адаптер для наполнения чата, к нему на вход приходит лист протоколов
        protocols = new ArrayList<>();
        adapter = new ChatAdapter(this, protocols);
        recyclerView.setAdapter(adapter);



        //Анимация кнопки файла
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkFabs){
                    //ShowButtons();
//                    checkFabs = true;
                    // creating new gallery intent for selecting text file only
                    Intent intent = new Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT);
                    // called a override method for starting gallery intent
                    startActivityForResult(Intent.createChooser(intent, "Select a TXT file"), FileSender.openFileRequestCode);
                }
                else {
//                    HideButtons();
//                    checkFabs = false;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // if this is a gallery opening request
        if (requestCode == FileSender.openFileRequestCode && resultCode == RESULT_OK) {
            Uri uri = intent.getData();
            fileSender.sendFile(uri, clientObject.sendingQueue);
        }
    }





    private void startServer(String port){

        // if there's a valid input then create a server class on that port so that the client can take data from that port

        try {
            serverObject = new ServerClass(Integer.parseInt(port), this);
            serverObject.start();
            Log.e(Constants.TAG, "Server started");
            Toast.makeText(this, "Server started", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e(Constants.TAG, e.getMessage());
            Toast.makeText(this, "Can't start server, please check the port number first", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }



    //Отправка сообщения
    public void SendMessage(View view) {
        //editTextMessage.getText().toString();
        String msg = editTextMessage.getText().toString().trim(); // getting the typed messages excluding new line first and last

        // you need to type something before sending
        if (TextUtils.isEmpty(msg)) {
            editTextMessage.requestFocus();
            editTextMessage.setError("Please write your message first");
        } else {
            Protocol ourMsgProtocol = new Protocol();
            ourMsgProtocol.setMsgCode(MsgCodes.textCode);
            ourMsgProtocol.setCurrentTime();
            ourMsgProtocol.setData(msg.getBytes());

            clientObject.sendingQueue.send(ourMsgProtocol); // send the todo encrypted message

            editTextMessage.setText("");
        }
    }














    public void addProtocolNode(Protocol messageProtocol) {
        Log.d(Constants.TAG, "got protocol node with code: " + messageProtocol.getMsgCode());
        runOnUiThread(() -> {
            switch (messageProtocol.getMsgCode()){
                case MsgCodes.fileStartCode:
                    Log.d(Constants.TAG, "Got a file sending start request, file size is: " + new String(messageProtocol.getData()));

                    if(messageProtocol.isFromThisDevice()) {
                        byteArrayBufferFileYours = new ByteArrayOutputStream();
                        startedFileSending = true;
                    } else {
                        byteArrayBufferFileTheir = new ByteArrayOutputStream();
                        startedFileReceiving = true;
                    }
                    break;

                case MsgCodes.fileCode:
                    Log.d(Constants.TAG, "Receiving a file: " + messageProtocol.getData().length);

                    if(messageProtocol.isFromThisDevice() && startedFileSending) {
                        try {
                            byteArrayBufferFileYours.write(messageProtocol.getData());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(!messageProtocol.isFromThisDevice() && startedFileReceiving) {
                        try {
                            byteArrayBufferFileTheir.write(messageProtocol.getData());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case MsgCodes.fileEndCode:
                    Log.d(Constants.TAG, "Received the file! file size is: " + new String(messageProtocol.getData()));

                    if(messageProtocol.isFromThisDevice()) {
                        byte[] imgBytes = byteArrayBufferFileYours.toByteArray();
                        Log.d(Constants.TAG, "You sent an Img with size: " + imgBytes.length);
    //                    Bitmap bmp = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
    //                    imageView.setImageBitmap(bmp);

                        Protocol fullImageProtocol = new Protocol();
                        fullImageProtocol.setFromThisDevice(true);
                        fullImageProtocol.setData(imgBytes);
                        fullImageProtocol.setMsgCode(MsgCodes.fileEndCode);
                        protocols.add(fullImageProtocol);
                        adapter = new ChatAdapter(this, protocols);
                        recyclerView.setAdapter(adapter);

                        startedFileSending = false;
                    } else {
                        byte[] imgBytes = byteArrayBufferFileTheir.toByteArray();
                        Log.d(Constants.TAG, "You received an Img with size: " + imgBytes.length);
                        //                    Bitmap bmp = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
                        //                    imageView.setImageBitmap(bmp);

                        Protocol fullImageProtocol = new Protocol();
                        fullImageProtocol.setFromThisDevice(false);
                        fullImageProtocol.setData(imgBytes);
                        fullImageProtocol.setMsgCode(MsgCodes.fileEndCode);
                        protocols.add(fullImageProtocol);
                        adapter = new ChatAdapter(this, protocols);
                        recyclerView.setAdapter(adapter);

                        startedFileReceiving = false;
                    }
                    break;
                case MsgCodes.disconnectCode:
//                    textView.setPadding(0, 0, 0, 0);
//
//                    textView.setTextSize(13);
//                    conversationLayout.setGravity(View.TEXT_ALIGNMENT_CENTER);
//                    textView.setGravity(Gravity.CENTER);
//                    textView.setText("Your Pair has been disconnected.");

                    //TODO close everything and return to the main page (or not???)
//                    disconnectHim();
                    break;
                case MsgCodes.textCode:
                    Log.d(Constants.TAG, "got an ordinary msg: " + new String(messageProtocol.getData()));

                    protocols.add(messageProtocol);
                    //adapter.notifyDataSetChanged();
                    //adapter.notifyItemInserted(protocols.size());
                    adapter = new ChatAdapter(this, protocols);
                    recyclerView.setAdapter(adapter);
//
//                    textView.setTextSize(20);
//                    textView.setText(new String(messageProtocol.getData())); // setting message on the message textview
//
//                    msgTime.setText("(" + Utils.getTime(false) + ")"); // setting messing time
//
//                    // creating divider between two messages
//                    addDividerBetweenTwoMessages();
//
//                    // adding 2 more views in linear layout every time
//                    conversationLayout.addView(textView);
//                    conversationLayout.addView(msgTime);
//                    conversations.post(() -> conversations.fullScroll(View.FOCUS_DOWN)); // for getting last message in first
                    break;
            }
        });
//
//
//        switch (messageProtocol.getMsgCode()){
//            case MsgCodes.fileStartCode:
//                Log.d(Constants.TAG, "Got a file sending start request, file size is: " + new String(messageProtocol.getData()));
//
//                byteArrayOutputStream = new ByteArrayOutputStream();
//                startedFileReceiving = true;
//                break;
//
//            case MsgCodes.fileCode:
//                Log.d(Constants.TAG, "Receiving a file: " + messageProtocol.getData().length);
//
//                if(startedFileReceiving){
//                    try {
//                        byteArrayOutputStream.write(messageProtocol.getData());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//                break;
//
//            case MsgCodes.fileEndCode:
//                Log.d(Constants.TAG, "Received the file! file size is: " + new String(messageProtocol.getData()));
//
//                byte[] imgBytes = byteArrayOutputStream.toByteArray();
//                Log.d(Constants.TAG, "Img size is: " + imgBytes.length);
////                    Bitmap bmp = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
////                    imageView.setImageBitmap(bmp);
//
//                Protocol fullImageProtocol = new Protocol();
//                fullImageProtocol.setFromThisDevice(false);
//                fullImageProtocol.setData(imgBytes);
//                fullImageProtocol.setMsgCode(MsgCodes.fileEndCode);
//                protocols.add(fullImageProtocol);
//
//                runOnUiThread(() -> {
//                    adapter = new ChatAdapter(this, protocols);
//                    recyclerView.setAdapter(adapter);
//                });
//
//
//
//                startedFileReceiving = false;
//
//                break;
//            case MsgCodes.disconnectCode:
////                    textView.setPadding(0, 0, 0, 0);
////
////                    textView.setTextSize(13);
////                    conversationLayout.setGravity(View.TEXT_ALIGNMENT_CENTER);
////                    textView.setGravity(Gravity.CENTER);
////                    textView.setText("Your Pair has been disconnected.");
//
//                //TODO close everything and return to the main page (or not???)
////                    disconnectHim();
//                break;
//            case MsgCodes.textCode:
//                Log.d(Constants.TAG, "got an ordinary msg: " + new String(messageProtocol.getData()));
//
//                protocols.add(messageProtocol);
//                //adapter.notifyDataSetChanged();
//                //adapter.notifyItemInserted(protocols.size());
//                runOnUiThread(() -> {
//                    adapter = new ChatAdapter(this, protocols);
//                    recyclerView.setAdapter(adapter);
//                });
//
////
////                    textView.setTextSize(20);
////                    textView.setText(new String(messageProtocol.getData())); // setting message on the message textview
////
////                    msgTime.setText("(" + Utils.getTime(false) + ")"); // setting messing time
////
////                    // creating divider between two messages
////                    addDividerBetweenTwoMessages();
////
////                    // adding 2 more views in linear layout every time
////                    conversationLayout.addView(textView);
////                    conversationLayout.addView(msgTime);
////                    conversations.post(() -> conversations.fullScroll(View.FOCUS_DOWN)); // for getting last message in first
//                break;
//        }
    }



    private void ShowButtons(){
        FloatingActionButton fab1 =  findViewById(R.id.fab_1);
        FloatingActionButton fab2 =  findViewById(R.id.fab_2);
        FloatingActionButton fab3 =  findViewById(R.id.fab_3);
        @SuppressLint("ResourceType") Animation show_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.animator.fab1_chow);
        @SuppressLint("ResourceType") Animation show_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.animator.fab2_show);
        @SuppressLint("ResourceType") Animation show_fab_3 = AnimationUtils.loadAnimation(getApplication(), R.animator.fab3_show);
        StartAnimationShow(fab1, show_fab_1);
        StartAnimationShow(fab2, show_fab_2);
        StartAnimationShow(fab3, show_fab_3);
    }

    private void HideButtons(){
        FloatingActionButton fab1 =  findViewById(R.id.fab_1);
        FloatingActionButton fab2 =  findViewById(R.id.fab_2);
        FloatingActionButton fab3 =  findViewById(R.id.fab_3);
        @SuppressLint("ResourceType") Animation hide_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.animator.fab1_hide);
        @SuppressLint("ResourceType") Animation hide_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.animator.fab2_hide);
        @SuppressLint("ResourceType") Animation hide_fab_3 = AnimationUtils.loadAnimation(getApplication(), R.animator.fab3_hide);
        StartAnimationHide(fab1, hide_fab_1);
        StartAnimationHide(fab2, hide_fab_2);
        StartAnimationHide(fab3, hide_fab_3);
    }

    private void StartAnimationShow(FloatingActionButton fab, Animation show_fab){

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab.getLayoutParams();
        layoutParams.rightMargin += (int) (fab.getWidth() * 1.7);
        layoutParams.bottomMargin += (int) (fab.getHeight() * 0.25);
        fab.setLayoutParams(layoutParams);
        fab.startAnimation(show_fab);
        fab.setClickable(true);
    }

    private void StartAnimationHide(FloatingActionButton fab, Animation hide_fab) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab.getLayoutParams();
        layoutParams.rightMargin -= (int) (fab.getWidth() * 1.7);
        layoutParams.bottomMargin -= (int) (fab.getHeight() * 0.25);
        fab.setLayoutParams(layoutParams);
        fab.startAnimation(hide_fab);
        fab.setClickable(false);
    }

}