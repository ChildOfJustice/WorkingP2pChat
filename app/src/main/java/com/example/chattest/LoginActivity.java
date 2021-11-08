package com.example.chattest;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chattest.networkLogic.ServerClass;
import com.example.chattest.utils.Constants;
import com.example.chattest.utils.Utils;

public class LoginActivity extends AppCompatActivity {

    EditText editTextPortYou, editTextPortAnother, editTextAnotherIP;

    ServerClass server;

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



    public void ClickListenPort(View view) {
//        String port = editTextPortYou.getText().toString(); // getting the port from edittext
//
//        if (TextUtils.isEmpty(port)) {
//            editTextPortYou.requestFocus(); // focusing as an error
//            editTextPortYou.setError("Please write your receive port first"); // showing what need to avoid the error
//        }
//        // if there's a valid input then create a server class on that port so that the client can take data from that port
//        else {
//            try {
//                server = new ServerClass(Integer.parseInt(port));
//                server.start();
//                Log.i(Constants.TAG, "Server started");
//                Toast.makeText(this, "Server started", Toast.LENGTH_SHORT).show();
//
//            } catch (Exception e) {
//                Log.e(Constants.TAG, e.getMessage());
//                Toast.makeText(this, "Can't start server, please check the port number first", Toast.LENGTH_SHORT).show();
//                e.printStackTrace();
//            }
//        }
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
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("YouPort", editTextPortYou.getText().toString());
                intent.putExtra("AnotherPort", editTextPortAnother.getText().toString());
                intent.putExtra("AnotherIP", editTextAnotherIP.getText().toString());

//                intent.putExtra("Server", server);
                //intent.putExtra("KeyValue", ***);

                startActivity(intent);
            } catch (Exception e) {
                Log.e(Constants.TAG, "ERROR: " + e);
                Toast.makeText(this, "Can't connect with server, please check all the requirements", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }









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
}