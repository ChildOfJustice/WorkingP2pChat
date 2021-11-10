package com.example.chattest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    boolean checkFabs;
    EditText editTextMessage;
    RecyclerView recyclerView;
    ChatAdapter adapter;
    Uri uri;
    ByteArrayOutputStream byteArrayBufferFileYours;
    ByteArrayOutputStream byteArrayBufferFileTheir;
    boolean startedFileReceiving = false;
    boolean startedFileSending = false;
    FragmentManager manager;
    DialogFragment dialogBar;
    List<Protocol> protocols;
    ProgressDialog progressDialog;
    ServerClass serverObject;
    ClientClass clientObject;
    View mView;
    FileSender fileSender;

    FloatingActionButton fab1, fab2, fab3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton fab =  findViewById(R.id.fab);
        Bundle arguments = getIntent().getExtras();

//        serverObject = (ServerClass)getIntent().getSerializableExtra("Server");
//        Log.d(Constants.TAG, "Got the ServerClass object: " + serverObject.toString());

        manager = getSupportFragmentManager();

        String AnotherIP = arguments.get("AnotherIP").toString();
        String YouPort = arguments.get("YouPort").toString();
        String AnotherPort = arguments.get("AnotherPort").toString();
        fab1 =  findViewById(R.id.fab_1);
        fab2 =  findViewById(R.id.fab_2);
        fab3 =  findViewById(R.id.fab_3);

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



        //Обработчик нижней кнопки
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Loading..."); // Setting Message
                progressDialog.setTitle("ProgressDialog"); // Setting Title
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                progressDialog.show(); // Display Progress Dialog
                progressDialog.setCancelable(false);
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                }).start();
            }
        });

        //Обработчик средней кнопки
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Средняя кнопка", Toast.LENGTH_SHORT).show();
            }
        });

        //Обработчик верхняя кнопки
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    // creating new gallery intent for selecting text file only
                    Intent intent = new Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT);
                    // called a override method for starting gallery intent
                    startActivityForResult(intent, FileSender.openFileRequestCode);


            }
        });

        //Анимация кнопки файла
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkFabs){
                    ShowButtons();
                    checkFabs = true;
                }
                else {
                    HideButtons();
                    checkFabs = false;
                }
            }
        });

    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
            if (requestCode == FileSender.openFileRequestCode && resultCode == RESULT_OK) {

                uri = intent.getData();
                /*runOnUiThread(() -> {
                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setMessage("Loading..."); // Setting Message
                    progressDialog.setTitle("ProgressDialog"); // Setting Title
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                    progressDialog.show(); // Display Progress Dialog
                    progressDialog.setCancelable(false);
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                Thread.sleep(100000);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            progressDialog.dismiss();
                        }
                    }).start();
                });*/
                runOnUiThread(() -> {
                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setMax(10000); // Progress Dialog Max Value
                    progressDialog.setMessage("Loading..."); // Setting Message
                    progressDialog.setTitle("ProgressDialog"); // Setting Title
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); // Progress Dialog Style Horizontal
                    progressDialog.show(); // Display Progress Dialog
                    progressDialog.setCancelable(false);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                while (progressDialog.getProgress() <= progressDialog.getMax()) {
                                    Thread.sleep(200);
                                    if (progressDialog.getProgress() == progressDialog.getMax()) {
                                        progressDialog.dismiss();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                });


                new Thread(new Runnable() {
                    public void run() {
                        try {
                            fileSender.sendFile(uri, clientObject.sendingQueue, progressDialog);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                }).start();

            }



        // if this is a gallery opening request

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

        @SuppressLint("ResourceType") Animation show_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.animator.fab1_chow);
        @SuppressLint("ResourceType") Animation show_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.animator.fab2_show);
        @SuppressLint("ResourceType") Animation show_fab_3 = AnimationUtils.loadAnimation(getApplication(), R.animator.fab3_show);
        StartAnimationShow(fab1, show_fab_1, fab2, show_fab_2, fab3, show_fab_3);

    }

    private void HideButtons(){
        FloatingActionButton fab1 =  findViewById(R.id.fab_1);
        FloatingActionButton fab2 =  findViewById(R.id.fab_2);
        FloatingActionButton fab3 =  findViewById(R.id.fab_3);
        @SuppressLint("ResourceType") Animation hide_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.animator.fab1_hide);
        @SuppressLint("ResourceType") Animation hide_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.animator.fab2_hide);
        @SuppressLint("ResourceType") Animation hide_fab_3 = AnimationUtils.loadAnimation(getApplication(), R.animator.fab3_hide);
        StartAnimationHide(fab1, hide_fab_1, fab2, hide_fab_2, fab3, hide_fab_3);
    }

    private void StartAnimationShow(FloatingActionButton fab1, Animation show_fab1, FloatingActionButton fab2,
                                    Animation show_fab2, FloatingActionButton fab3, Animation show_fab3)
    {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab1.getLayoutParams();
        layoutParams.rightMargin += (int) (fab1.getWidth() * 1.7);
        layoutParams.bottomMargin += (int) (fab1.getHeight() * 0.25);
        fab1.setLayoutParams(layoutParams);
        fab1.startAnimation(show_fab1);
        fab1.setClickable(true);

        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) fab2.getLayoutParams();
        layoutParams2.rightMargin += (int) (fab2.getWidth() * 1.5);
        layoutParams2.bottomMargin += (int) (fab2.getHeight() * 1.5);
        fab2.setLayoutParams(layoutParams2);
        fab2.startAnimation(show_fab2);
        fab2.setClickable(true);

        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) fab3.getLayoutParams();
        layoutParams3.rightMargin += (int) (fab3.getWidth() * 0.25);
        layoutParams3.bottomMargin += (int) (fab3.getHeight() * 1.7);
        fab3.setLayoutParams(layoutParams3);
        fab3.startAnimation(show_fab3);
        fab3.setClickable(true);
    }

    private void StartAnimationHide(FloatingActionButton fab1, Animation hide_fab1, FloatingActionButton fab2,
                                    Animation hide_fab2, FloatingActionButton fab3, Animation hide_fab3)
    {
        //Floating Action Button 1
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab1.getLayoutParams();
        layoutParams.rightMargin -= (int) (fab1.getWidth() * 1.7);
        layoutParams.bottomMargin -= (int) (fab1.getHeight() * 0.25);
        fab1.setLayoutParams(layoutParams);
        fab1.startAnimation(hide_fab1);
        fab1.setClickable(false);

        //Floating Action Button 2
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) fab2.getLayoutParams();
        layoutParams2.rightMargin -= (int) (fab2.getWidth() * 1.5);
        layoutParams2.bottomMargin -= (int) (fab2.getHeight() * 1.5);
        fab2.setLayoutParams(layoutParams2);
        fab2.startAnimation(hide_fab2);
        fab2.setClickable(false);

        //Floating Action Button 3
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) fab3.getLayoutParams();
        layoutParams3.rightMargin -= (int) (fab3.getWidth() * 0.25);
        layoutParams3.bottomMargin -= (int) (fab3.getHeight() * 1.7);
        fab3.setLayoutParams(layoutParams3);
        fab3.startAnimation(hide_fab3);
        fab3.setClickable(false);
    }


    private void RSA(){
        Key publicKey = null;
        Key privateKey = null;
        // Generate key pair for 1024-bit RSA encryption and decryption
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(1024);
            KeyPair kp = kpg.genKeyPair();
            publicKey = kp.getPublic();
            privateKey = kp.getPrivate();
        } catch (Exception e) {
            Log.e("Crypto", "RSA key pair error");
        }

        // Encode the original data with RSA private key
        /*byte[] encodedBytes = null;
        try {
            Cipher c = Cipher.getInstance("RSA");
            c.init(Cipher.ENCRYPT_MODE, privateKey);
            encodedBytes = c.doFinal(testText.getBytes());
        } catch (Exception e) {
            Log.e("Crypto", "RSA encryption error");
        }
        TextView encodedTextView = (TextView)findViewById(R.id.textViewEncoded);
        encodedTextView.setText("[ENCODED]:\n" +
                Base64.encodeToString(encodedBytes, Base64.DEFAULT) + "\n");

        // Decode the encoded data with RSA public key
        byte[] decodedBytes = null;
        try {
            Cipher c = Cipher.getInstance("RSA");
            c.init(Cipher.DECRYPT_MODE, publicKey);
            decodedBytes = c.doFinal(encodedBytes);
        } catch (Exception e) {
            Log.e("Crypto", "RSA decryption error");
        }*/

    }


}