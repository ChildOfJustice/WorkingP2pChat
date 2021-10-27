package com.example.chattest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chattest.networkLogic.protocol.Protocol;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{

    private final LayoutInflater inflater;
    private final List<Protocol> protocols;
    private Context context;

    //Конструктор адаптера
    ChatAdapter(Context context, List<Protocol> protocols){
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.protocols = protocols; //Хранит лист сообщений
    }


    //Сделал разметку для чата и устанавливаю его как стандарт для "заполнителя" RecyclerView т.к. Adapter заполняет его
    @NonNull
    @NotNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.chat_layout, parent, false);
        return new ViewHolder(view);
    }

    //"чтение" листа сообщений и заполнение разметки
    @Override
    public void onBindViewHolder(@NonNull @NotNull ChatAdapter.ViewHolder holder, int position) {
        Protocol protocol = protocols.get(position);

        if(protocol.isFromThisDevice()) {
            holder.textViewYou.setText(new String(protocol.getData()));
            holder.textViewYouTime.setText(protocol.getTime());
            //if(protocol.getMsgCode() == 3) holder.viewYou.setImageBitmap(Bitmap bmp);
        } else {
            holder.textViewAnother.setText(new String(protocol.getData()));
            holder.textViewAnotherTime.setText(protocol.getTime());
            //if(protocol.getMsgCode() == 3) holder.viewYou.setImageBitmap(Bitmap bmp);
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView textViewYou, textViewAnother, textViewYouTime, textViewAnotherTime;
        final ImageView viewYou, viewAnother;

        ViewHolder(View view){
            super(view);
            textViewYou = (TextView) view.findViewById(R.id.textViewYou);
            textViewAnother = (TextView) view.findViewById(R.id.textViewAnother);
            textViewYouTime = (TextView) view.findViewById(R.id.textViewYouTime);
            textViewAnotherTime = (TextView) view.findViewById(R.id.textViewAnotherTime);
            viewYou = (ImageView) view.findViewById(R.id.viewYou);
            viewAnother = (ImageView) view.findViewById(R.id.viewAnother);
        }
    }
}
