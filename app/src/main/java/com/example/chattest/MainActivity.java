package com.example.chattest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.chattest.networkLogic.ClientClass;
import com.example.chattest.networkLogic.ServerClass;
import com.example.chattest.networkLogic.protocol.MsgCodes;
import com.example.chattest.networkLogic.protocol.Protocol;
import com.example.chattest.utils.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    boolean checkFabs;
    EditText editTextMessage;
    RecyclerView recyclerView;
    ChatAdapter adapter;

    ByteArrayOutputStream byteArrayOutputStream;
    boolean startedFileReceiving = false;

    List<Protocol> protocols;

    ServerClass serverObject;
    ClientClass clientObject;



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
            protocols.add(messageProtocol);
            //adapter.notifyDataSetChanged();
            //adapter.notifyItemInserted(protocols.size());
            adapter = new ChatAdapter(this, protocols);
            recyclerView.setAdapter(adapter);
        });


//        runOnUiThread(() -> {
//                    TextView textView = new TextView(this);
//                    ImageView imageView = new ImageView(this);
//                    TextView msgTime = new TextView(this);
//
//                    // if it's a sender message
//                    if (color == Color.parseColor("#FCE4EC")) {
//                        Log.d(Constants.TAG, "Your sent msg: " + new String(messageProtocol.getData()));
//
//                        //UI part of YOUR new msg
//                        {
//                            textView.setPadding(200, 20, 10, 10);
//                            //textView.setMaxLines(5);
//                            textView.setGravity(Gravity.RIGHT);
////                            textView.setBackgroundResource(R.drawable.sender_messages_layout);
//                            textView.setTextIsSelectable(true);
//
//                            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                            // lp1.setMargins(10, 10, 10, 10);
//                            // lp1.setMargins(10, 10, 10, 10);
//                            //lp1.width = 400;
//                            lp1.leftMargin = 200;
//                            //lp1.rightMargin = 50;
//                            textView.setLayoutParams(lp1);
//
//                            msgTime.setPadding(0, 0, 0, 0);
//
//                            msgTime.setTextSize(14);
//                            msgTime.setTextColor(Color.parseColor("#FCE4EC"));
//                            msgTime.setTypeface(textView.getTypeface(), Typeface.ITALIC);
////                            conversationLayout.setGravity(View.TEXT_ALIGNMENT_CENTER);
//                            msgTime.setGravity(Gravity.LEFT);
//
//                            LinearLayout.LayoutParams lp4 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                            // lp1.setMargins(10, 10, 10, 10);
//                            // lp1.setMargins(10, 10, 10, 10);
//                            //lp1.width = 400;
//                            lp4.leftMargin = 200;
//                            msgTime.setLayoutParams(lp4);
//                        }
//                        //textView.setBackgroundResource(R.drawable.sender_messages_layout);
//                    }
//                    // else if receiver message
////                    else if(!(caesarCipherDecryption(message, shift).contains("bg@%@bg"))
////                            && !(caesarCipherDecryption(message, shift).contains("diconnect@%@d"))
////                            && !(caesarCipherDecryption(message, shift).contains("file@%@"))
////                            && !(caesarCipherDecryption(message, shift).contains("remove@%@")))
//                    // else if it is incoming message
//                    else {
//                        Log.d(Constants.TAG, "Got a msg: " + new String(messageProtocol.getData()));
//
//                        //UI part of a new incoming msg
//                        {
//                            textView.setPadding(10, 20, 200, 10);
//                            //textView.setMaxLines(5);
//                            textView.setGravity(Gravity.LEFT);
////                            textView.setBackgroundResource(R.drawable.receiver_messages_layout);
//                            textView.setTextIsSelectable(true);
//
//                            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                            //lp1.setMargins(10, 10, 10, 10);
//                            //lp1.width = 400;
//                            //lp1.leftMargin = 150;
//                            lp2.rightMargin = 200;
//                            textView.setLayoutParams(lp2);
//
//                            msgTime.setTextSize(14);
//                            msgTime.setTextColor(Color.parseColor("#FFFFFF"));
//                            msgTime.setTypeface(textView.getTypeface(), Typeface.ITALIC);
////                            conversationLayout.setGravity(View.TEXT_ALIGNMENT_CENTER);
//                            LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                            //lp1.setMargins(10, 10, 10, 10);
//                            //lp1.width = 400;
//                            //lp1.leftMargin = 150;
//                            lp3.rightMargin = 200;
//                            msgTime.setGravity(Gravity.RIGHT);
//                            msgTime.setLayoutParams(lp3);
//                        }
//                    }
//
//
//                    //UI part of a new msg
//                    {
//                        textView.setTextColor(color);
//                    }
////                    Log.d(Constants.TAG, "encrypted msg: " + message);
////                    String actualMessage = caesarCipherDecryption(message, shift);
////                    Log.d(Constants.TAG, "decrypted msg: " + actualMessage);
//
//
////                    String[] messages = actualMessage.split("@%@", 0);
//
//                    switch (messageProtocol.getMsgCode()){
//                        case MsgCodes.fileStartCode:
//                            Log.d(Constants.TAG, "Got a file sending start request, file size is: " + new String(messageProtocol.getData()));
//
//                            byteArrayOutputStream = new ByteArrayOutputStream();
//                            startedFileReceiving = true;
//                            break;
//                        case MsgCodes.fileCode:
//                            Log.d(Constants.TAG, "Receiving a file: " + messageProtocol.getData().length);
//
//
//                            if(startedFileReceiving){
//                                try {
//                                    byteArrayOutputStream.write(messageProtocol.getData());
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                            }
////                            textView.setTextSize(20);
////                            textView.setText(new String(messageProtocol.getData())); // setting message on the message textview
////
////                            msgTime.setText("(" + Utils.getTime(false) + ")"); // setting messing time
//                            break;
//                        case MsgCodes.fileEndCode:
//                            Log.d(Constants.TAG, "Received the file! file size is: " + new String(messageProtocol.getData()));
//                            textView.setPadding(0,0,0,0);
//                            textView.setTextSize(15);
//                            textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
////                            conversationLayout.setGravity(View.TEXT_ALIGNMENT_CENTER);
//                            textView.setGravity(Gravity.CENTER);
////                            conversationLayout.addView(imageView);
//
//                            byte[] imgBytes = byteArrayOutputStream.toByteArray();
//                            Log.d(Constants.TAG, "Img size is: " + imgBytes.length);
//                            Bitmap bmp = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
//                            imageView.setImageBitmap(bmp);
//                            startedFileReceiving = false;
//
//
////                            Log.d(Constants.TAG, "File Name: "+messages[1]);
//
////                            if(color == Color.parseColor("#FCE4EC"))
////                                textView.setText(messages[1]+" has been sent");
////                            else{
////                                textView.setText(messages[1]+" has been received and downloaded on android/data/com.example.p2p/");
////                                //writeToFile(messages[2], false, messages[1]);
////                            }
//
//
//
//
//                            break;
//                        case MsgCodes.disconnectCode:
//                            textView.setPadding(0, 0, 0, 0);
//
//                            textView.setTextSize(13);
////                            conversationLayout.setGravity(View.TEXT_ALIGNMENT_CENTER);
//                            textView.setGravity(Gravity.CENTER);
//                            textView.setText("Your Pair has been disconnected.");
//
//                            //TODO close everything and return to the main page (or not???)
////                            disconnectHim();
//                            break;
//                        case MsgCodes.textCode:
//                            Log.d(Constants.TAG, "got an ordinary msg: " + new String(messageProtocol.getData()));
//
//                            textView.setTextSize(20);
//                            textView.setText(new String(messageProtocol.getData())); // setting message on the message textview
//
//                            msgTime.setText("(" + Utils.getTime(false) + ")"); // setting messing time
//
//                            // creating divider between two messages
//                            //TODO
////                            addDividerBetweenTwoMessages();
//
//                            // adding 2 more views in linear layout every time
//                            //TODO
////                            conversationLayout.addView(textView);
////                            conversationLayout.addView(msgTime);
////                            conversations.post(() -> conversations.fullScroll(View.FOCUS_DOWN)); // for getting last message in first
//                            break;
//                    }
//                    // if its a file
////                    if (messageProtocol.getMsgCode() == MsgCodes.fileCode) {
////                        textView.setPadding(0,0,0,0);
////
////                        textView.setTextSize(15);
////                        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
////                        conversationLayout.setGravity(View.TEXT_ALIGNMENT_CENTER);
////                        textView.setGravity(Gravity.CENTER);
////                        conversationLayout.addView(imageView);
////                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
////                        imageView.setImageBitmap(bmp);
////                        Log.d(Constants.TAG, "File Name: "+messages[1]);
////
////                        if(color == Color.parseColor("#FCE4EC"))
////                            textView.setText(messages[1]+" has been sent");
////                        else{
////                            textView.setText(messages[1]+" has been received and downloaded on android/data/com.example.p2p/");
////                            //writeToFile(messages[2], false, messages[1]);
////                        }
//
////                    }
//                    // if its a remove message
////                    else if (messages[0].equals("remove")) {
////                        textView.setPadding(0, 0, 0, 0);
////
////                        textView.setTextSize(15);
////                        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
////                        conversationLayout.setGravity(View.TEXT_ALIGNMENT_CENTER);
////                        textView.setGravity(Gravity.CENTER);
////                        removeAllChatForHim();
////
////                        if (color == Color.parseColor("#FCE4EC"))
////                            textView.setText("You have removed all the previous message");
////                        else {
////                            textView.setText("Your pair has removed all the previous message");
////                        }
////
////                    }
//                    // if its a bg change message
////                    else if (actualMessage.contains("bg@%@bg")) {
////                        changeBGforHim(actualMessage);
////                        textView.setPadding(0, 0, 0, 0);
////
////                        textView.setTextSize(13);
////                        textView.setTextSize(15);
////                        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
////                        conversationLayout.setGravity(View.TEXT_ALIGNMENT_CENTER);
////                        textView.setGravity(Gravity.CENTER);
////                        if (actualMessage.equals("bg@%@bg0")) {
////                            textView.setText("Background reset to default");
////                        } else
////                            textView.setText("Background has been changed");
////
////                    }
//                    // if its a disconnect message
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
////                    else if (messageProtocol.getMsgCode() == MsgCodes.disconnectCode) {
////                        textView.setPadding(0, 0, 0, 0);
////
////                        textView.setTextSize(13);
////                        conversationLayout.setGravity(View.TEXT_ALIGNMENT_CENTER);
////                        textView.setGravity(Gravity.CENTER);
////                        textView.setText("Your Pair has been disconnected.");
////
////                        //TODO close everything and return to the main page (or not???)
////                        disconnectHim();
////                    }
//                    // else it's a normal message
////                    else if(messageProtocol.getMsgCode() == MsgCodes.textCode){
////                        Log.d(Constants.TAG, "got an ordinary msg: " + messageProtocol.getMessage());
////
////                        textView.setTextSize(20);
////                        textView.setText(messageProtocol.getMessage()); // setting message on the message textview
////
////                        msgTime.setText("(" + Utils.getTime(false) + ")"); // setting messing time
////                    }
//
//
//
//                }
//        );
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