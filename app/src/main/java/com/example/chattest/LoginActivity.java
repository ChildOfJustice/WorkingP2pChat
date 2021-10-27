package com.example.chattest;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chattest.networkLogic.NetworkManager;
import com.example.chattest.networkLogic.protocol.MsgCodes;
import com.example.chattest.networkLogic.protocol.Protocol;
import com.example.chattest.utils.Constants;
import com.example.chattest.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    NetworkManager networkManager = new NetworkManager(this);

    ByteArrayOutputStream byteArrayOutputStream;
    boolean startedFileReceiving = false;

    EditText editTextPortYou, editTextPortAnother, editTextAnotherIP;
    //Получение IP адреса нажатием кнопки
    public void GetIP(View view){
        Toast.makeText(this, Utils.getIPAddress(true), Toast.LENGTH_LONG).show();
    }



    public void downloadFileEnc(View view) {
        openStorage();
    }

    private void openStorage() {
        // creating new gallery intent for selecting text file only
        Intent intent = new Intent().setType("text/plain").setAction(Intent.ACTION_GET_CONTENT);
        // called a override method for starting gallery intent
        startActivityForResult(Intent.createChooser(intent, "Select a TXT file"), 123);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editTextPortYou = findViewById(R.id.editTextListenPort);
        editTextPortAnother = findViewById(R.id.editTextPort);
        editTextAnotherIP = findViewById(R.id.editTextIP);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==123){
            Toast.makeText(this, "WOW", Toast.LENGTH_LONG).show();
        }
    }

    public void ClickConnect(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("YouPort", editTextPortYou.getText().toString());
        intent.putExtra("AnotherPort", editTextPortAnother.getText().toString());
        intent.putExtra("AnotherIP", editTextAnotherIP.getText().toString());
        //intent.putExtra("KeyValue", ***);
        startActivity(intent);




        String port = editTextPortAnother.getText().toString();
        String targetIP = editTextAnotherIP.getText().toString();
//        String encryptKey = encrypKeyEditText.getText().toString(); // taking the encryption key shift number
//        shift = Integer.parseInt(encryptKey);

        // checking is empty or not
//        if (TextUtils.isEmpty(port)) {
//            targetPortEditText.requestFocus();
//            targetPortEditText.setError("Please write your target port first");
//        }

        // checking self ip or not
//        else if (targetIP.equals(ip)) {
//            targetIPEditText.requestFocus();
//            targetIPEditText.setError("This is your self IP, please change it");
//        }

        // connect, and redirect to chat screen
//        else {
            try {
                networkManager.createClientThread(targetIP, port);
                networkManager.clientClass.start();
                // show success message
                Toast.makeText(this, "your sending port and listening port has been set successfully", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e(Constants.TAG, "ERROR: " + e);
                Toast.makeText(this, "Can't connect with server, please check all the requirements", Toast.LENGTH_SHORT).show();
            }
//        }
    }

    public void ClickListenPort(View view) {
        //нажатие кнопки listenPort
        String port = editTextPortAnother.getText().toString(); // getting the port from edittext

        //TODO
        // checking if port is empty or not
//        if (TextUtils.isEmpty(port)) {
//            receivePortEditText.requestFocus(); // focusing as an error
//            receivePortEditText.setError("Please write your receive port first"); // showing what need to avoid the error
//        }

        // if there's a valid input then create a server class on that port so that the client can take data from that port
//        else {
            try {
                networkManager.createServerThread(port);
                networkManager.serverClass.start();

                // showing the further information
//                targetIPEditText.setVisibility(View.VISIBLE);
//                targetPortEditText.setVisibility(View.VISIBLE);
//                connectBtn.setVisibility(View.VISIBLE);
//                clickHereBtn.setVisibility(View.VISIBLE);
//                getIPBtn.setVisibility(View.VISIBLE);
//                encrypKeyEditText.setVisibility(View.VISIBLE);

            } catch (Exception e) {
                Log.e(Constants.TAG, e.getMessage());
//                Toast.makeText(MainActivity.this, "Can't start server, please check the port number first", Toast.LENGTH_SHORT).show();
            }
//        }
    }


    public void addProtocolNode(int color, Protocol messageProtocol) {
        Log.d(Constants.TAG, "got protocol node with code: " + messageProtocol.getMsgCode());
        runOnUiThread(() -> {
                    TextView textView = new TextView(this);
                    ImageView imageView = new ImageView(this);
                    TextView msgTime = new TextView(this);

                    // if it's a sender message
                    if (color == Color.parseColor("#FCE4EC")) {
                        Log.d(Constants.TAG, "Your sent msg: " + new String(messageProtocol.getData()));

                        //UI part of YOUR new msg
                        {
                            textView.setPadding(200, 20, 10, 10);
                            //textView.setMaxLines(5);
                            textView.setGravity(Gravity.RIGHT);
//                            textView.setBackgroundResource(R.drawable.sender_messages_layout);
                            textView.setTextIsSelectable(true);

                            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            // lp1.setMargins(10, 10, 10, 10);
                            // lp1.setMargins(10, 10, 10, 10);
                            //lp1.width = 400;
                            lp1.leftMargin = 200;
                            //lp1.rightMargin = 50;
                            textView.setLayoutParams(lp1);

                            msgTime.setPadding(0, 0, 0, 0);

                            msgTime.setTextSize(14);
                            msgTime.setTextColor(Color.parseColor("#FCE4EC"));
                            msgTime.setTypeface(textView.getTypeface(), Typeface.ITALIC);
//                            conversationLayout.setGravity(View.TEXT_ALIGNMENT_CENTER);
                            msgTime.setGravity(Gravity.LEFT);

                            LinearLayout.LayoutParams lp4 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            // lp1.setMargins(10, 10, 10, 10);
                            // lp1.setMargins(10, 10, 10, 10);
                            //lp1.width = 400;
                            lp4.leftMargin = 200;
                            msgTime.setLayoutParams(lp4);
                        }
                        //textView.setBackgroundResource(R.drawable.sender_messages_layout);
                    }
                    // else if receiver message
//                    else if(!(caesarCipherDecryption(message, shift).contains("bg@%@bg"))
//                            && !(caesarCipherDecryption(message, shift).contains("diconnect@%@d"))
//                            && !(caesarCipherDecryption(message, shift).contains("file@%@"))
//                            && !(caesarCipherDecryption(message, shift).contains("remove@%@")))
                    // else if it is incoming message
                    else {
                        Log.d(Constants.TAG, "Got a msg: " + new String(messageProtocol.getData()));

                        //UI part of a new incoming msg
                        {
                            textView.setPadding(10, 20, 200, 10);
                            //textView.setMaxLines(5);
                            textView.setGravity(Gravity.LEFT);
//                            textView.setBackgroundResource(R.drawable.receiver_messages_layout);
                            textView.setTextIsSelectable(true);

                            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            //lp1.setMargins(10, 10, 10, 10);
                            //lp1.width = 400;
                            //lp1.leftMargin = 150;
                            lp2.rightMargin = 200;
                            textView.setLayoutParams(lp2);

                            msgTime.setTextSize(14);
                            msgTime.setTextColor(Color.parseColor("#FFFFFF"));
                            msgTime.setTypeface(textView.getTypeface(), Typeface.ITALIC);
//                            conversationLayout.setGravity(View.TEXT_ALIGNMENT_CENTER);
                            LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            //lp1.setMargins(10, 10, 10, 10);
                            //lp1.width = 400;
                            //lp1.leftMargin = 150;
                            lp3.rightMargin = 200;
                            msgTime.setGravity(Gravity.RIGHT);
                            msgTime.setLayoutParams(lp3);
                        }
                    }


                    //UI part of a new msg
                    {
                        textView.setTextColor(color);
                    }
//                    Log.d(Constants.TAG, "encrypted msg: " + message);
//                    String actualMessage = caesarCipherDecryption(message, shift);
//                    Log.d(Constants.TAG, "decrypted msg: " + actualMessage);


//                    String[] messages = actualMessage.split("@%@", 0);

                    switch (messageProtocol.getMsgCode()){
                        case MsgCodes.fileStartCode:
                            Log.d(Constants.TAG, "Got a file sending start request, file size is: " + new String(messageProtocol.getData()));

                            byteArrayOutputStream = new ByteArrayOutputStream();
                            startedFileReceiving = true;
                            break;
                        case MsgCodes.fileCode:
                            Log.d(Constants.TAG, "Receiving a file: " + messageProtocol.getData().length);


                            if(startedFileReceiving){
                                try {
                                    byteArrayOutputStream.write(messageProtocol.getData());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
//                            textView.setTextSize(20);
//                            textView.setText(new String(messageProtocol.getData())); // setting message on the message textview
//
//                            msgTime.setText("(" + Utils.getTime(false) + ")"); // setting messing time
                            break;
                        case MsgCodes.fileEndCode:
                            Log.d(Constants.TAG, "Received the file! file size is: " + new String(messageProtocol.getData()));
                            textView.setPadding(0,0,0,0);
                            textView.setTextSize(15);
                            textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
//                            conversationLayout.setGravity(View.TEXT_ALIGNMENT_CENTER);
                            textView.setGravity(Gravity.CENTER);
//                            conversationLayout.addView(imageView);

                            byte[] imgBytes = byteArrayOutputStream.toByteArray();
                            Log.d(Constants.TAG, "Img size is: " + imgBytes.length);
                            Bitmap bmp = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
                            imageView.setImageBitmap(bmp);
                            startedFileReceiving = false;


//                            Log.d(Constants.TAG, "File Name: "+messages[1]);

//                            if(color == Color.parseColor("#FCE4EC"))
//                                textView.setText(messages[1]+" has been sent");
//                            else{
//                                textView.setText(messages[1]+" has been received and downloaded on android/data/com.example.p2p/");
//                                //writeToFile(messages[2], false, messages[1]);
//                            }




                            break;
                        case MsgCodes.disconnectCode:
                            textView.setPadding(0, 0, 0, 0);

                            textView.setTextSize(13);
//                            conversationLayout.setGravity(View.TEXT_ALIGNMENT_CENTER);
                            textView.setGravity(Gravity.CENTER);
                            textView.setText("Your Pair has been disconnected.");

                            //TODO close everything and return to the main page (or not???)
//                            disconnectHim();
                            break;
                        case MsgCodes.textCode:
                            Log.d(Constants.TAG, "got an ordinary msg: " + new String(messageProtocol.getData()));

                            textView.setTextSize(20);
                            textView.setText(new String(messageProtocol.getData())); // setting message on the message textview

                            msgTime.setText("(" + Utils.getTime(false) + ")"); // setting messing time

                            // creating divider between two messages
                            //TODO
//                            addDividerBetweenTwoMessages();

                            // adding 2 more views in linear layout every time
                            //TODO
//                            conversationLayout.addView(textView);
//                            conversationLayout.addView(msgTime);
//                            conversations.post(() -> conversations.fullScroll(View.FOCUS_DOWN)); // for getting last message in first
                            break;
                    }
                    // if its a file
//                    if (messageProtocol.getMsgCode() == MsgCodes.fileCode) {
//                        textView.setPadding(0,0,0,0);
//
//                        textView.setTextSize(15);
//                        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
//                        conversationLayout.setGravity(View.TEXT_ALIGNMENT_CENTER);
//                        textView.setGravity(Gravity.CENTER);
//                        conversationLayout.addView(imageView);
//                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                        imageView.setImageBitmap(bmp);
//                        Log.d(Constants.TAG, "File Name: "+messages[1]);
//
//                        if(color == Color.parseColor("#FCE4EC"))
//                            textView.setText(messages[1]+" has been sent");
//                        else{
//                            textView.setText(messages[1]+" has been received and downloaded on android/data/com.example.p2p/");
//                            //writeToFile(messages[2], false, messages[1]);
//                        }

//                    }
                    // if its a remove message
//                    else if (messages[0].equals("remove")) {
//                        textView.setPadding(0, 0, 0, 0);
//
//                        textView.setTextSize(15);
//                        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
//                        conversationLayout.setGravity(View.TEXT_ALIGNMENT_CENTER);
//                        textView.setGravity(Gravity.CENTER);
//                        removeAllChatForHim();
//
//                        if (color == Color.parseColor("#FCE4EC"))
//                            textView.setText("You have removed all the previous message");
//                        else {
//                            textView.setText("Your pair has removed all the previous message");
//                        }
//
//                    }
                    // if its a bg change message
//                    else if (actualMessage.contains("bg@%@bg")) {
//                        changeBGforHim(actualMessage);
//                        textView.setPadding(0, 0, 0, 0);
//
//                        textView.setTextSize(13);
//                        textView.setTextSize(15);
//                        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
//                        conversationLayout.setGravity(View.TEXT_ALIGNMENT_CENTER);
//                        textView.setGravity(Gravity.CENTER);
//                        if (actualMessage.equals("bg@%@bg0")) {
//                            textView.setText("Background reset to default");
//                        } else
//                            textView.setText("Background has been changed");
//
//                    }
                    // if its a disconnect message


























//                    else if (messageProtocol.getMsgCode() == MsgCodes.disconnectCode) {
//                        textView.setPadding(0, 0, 0, 0);
//
//                        textView.setTextSize(13);
//                        conversationLayout.setGravity(View.TEXT_ALIGNMENT_CENTER);
//                        textView.setGravity(Gravity.CENTER);
//                        textView.setText("Your Pair has been disconnected.");
//
//                        //TODO close everything and return to the main page (or not???)
//                        disconnectHim();
//                    }
                    // else it's a normal message
//                    else if(messageProtocol.getMsgCode() == MsgCodes.textCode){
//                        Log.d(Constants.TAG, "got an ordinary msg: " + messageProtocol.getMessage());
//
//                        textView.setTextSize(20);
//                        textView.setText(messageProtocol.getMessage()); // setting message on the message textview
//
//                        msgTime.setText("(" + Utils.getTime(false) + ")"); // setting messing time
//                    }



                }
        );
    }

}