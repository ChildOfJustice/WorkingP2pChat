package com.example.chattest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.example.chattest.networkLogic.protocol.MsgCodes;
import com.example.chattest.networkLogic.protocol.Protocol;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    boolean checkFabs;
    EditText editTextMessage;
    RecyclerView recyclerView;
    ChatAdapter adapter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton fab =  findViewById(R.id.fab);
        Bundle arguments = getIntent().getExtras();
        String AnotherIP = arguments.get("AnotherIP").toString();
        String YouPort = arguments.get("YouPort").toString();
        String AnotherPort = arguments.get("AnotherPort").toString();
        //byte[] key = (byte[]) arguments.get("KeyValue");
        checkFabs = false;
        editTextMessage = findViewById(R.id.editTextMessage);
        recyclerView = (RecyclerView) findViewById(R.id.ChatView);
        //создается адаптер для наполнения чата, к нему на вход приходит лист протоколов
        //adapter = new ChatAdapter(this, protocols);
        //recyclerView.setAdapter(adapter);
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

            //TODO networkManager должен быть или в MainActivity или в LoginActivity, либо как-то через Intent intent = new Intent(this, MainActivity.class); получать его в LoginActivity из MainActivity, но я не понимаю что такое Intent вообще
            networkManager.sendingQueue.send(ourMsgProtocol); // send the todo encrypted message
        }
    }
}