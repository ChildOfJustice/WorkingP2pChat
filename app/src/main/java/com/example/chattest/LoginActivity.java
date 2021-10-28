package com.example.chattest;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
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
import java.io.Serializable;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

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

        String port = editTextPortAnother.getText().toString();
        String targetIP = editTextAnotherIP.getText().toString();
//        String encryptKey = encrypKeyEditText.getText().toString(); // taking the encryption key shift number
//        shift = Integer.parseInt(encryptKey);

        // checking is empty or not
        if (TextUtils.isEmpty(port)) {
            editTextPortAnother.requestFocus();
            editTextPortAnother.setError("Please write your target port first");
        }
        // connect, and redirect to chat screen
        else {
            try {
//                networkManager.createClientThread(targetIP, port);
//                networkManager.clientClass.start();

                // show success message
                Toast.makeText(this, "your sending port and listening port has been set successfully", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("YouPort", editTextPortYou.getText().toString());
                intent.putExtra("AnotherPort", editTextPortAnother.getText().toString());
                intent.putExtra("AnotherIP", editTextAnotherIP.getText().toString());
//                intent.putExtra("NetworkManager", networkManager);
                //intent.putExtra("KeyValue", ***);
                startActivity(intent);
            } catch (Exception e) {
                Log.e(Constants.TAG, "ERROR: " + e);
                Toast.makeText(this, "Can't connect with server, please check all the requirements", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void ClickListenPort(View view) {
        if(true)
            return;


        String port = editTextPortYou.getText().toString(); // getting the port from edittext

        //TODO
        // checking if port is empty or not
        if (TextUtils.isEmpty(port)) {
            editTextPortYou.requestFocus(); // focusing as an error
            editTextPortYou.setError("Please write your receive port first"); // showing what need to avoid the error
        }
        // if there's a valid input then create a server class on that port so that the client can take data from that port
        else {
            try {
//                networkManager.createServerThread(port);
//                networkManager.serverClass.start();
                Toast.makeText(this, "Server started", Toast.LENGTH_SHORT).show();
                // showing the further information
//                targetIPEditText.setVisibility(View.VISIBLE);
//                targetPortEditText.setVisibility(View.VISIBLE);
//                connectBtn.setVisibility(View.VISIBLE);
//                clickHereBtn.setVisibility(View.VISIBLE);
//                getIPBtn.setVisibility(View.VISIBLE);
//                encrypKeyEditText.setVisibility(View.VISIBLE);

            } catch (Exception e) {
                Log.e(Constants.TAG, e.getMessage());
                Toast.makeText(this, "Can't start server, please check the port number first", Toast.LENGTH_SHORT).show();
            }
        }
    }
}