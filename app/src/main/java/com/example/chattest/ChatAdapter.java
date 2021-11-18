package com.example.chattest;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chattest.networkLogic.protocol.MsgCodes;
import com.example.chattest.networkLogic.protocol.Protocol;
import com.example.chattest.utils.Constants;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{
    private boolean isImageScaled = false;

    private final LayoutInflater inflater;
    private final List<Protocol> protocols;
    private Context context;
    MainActivity core;

    //Конструктор адаптера
    ChatAdapter(Context context, List<Protocol> protocols){
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.protocols = protocols; //Хранит лист сообщений
    }

    @Override
    public int getItemViewType(int position) {
        return protocols.get(position).isFromThisDevice() ? 1 : 0;
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

        if(getItemViewType(position) == 1) {
            if(protocol.getMsgCode() == MsgCodes.fileEndCode) {
                byte[] imgData = ((MainActivity)this.context).byteArrayBufferFileYours.toByteArray();
                Bitmap bmp = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
                holder.viewYou.setImageBitmap(bmp);
                holder.textViewYouTime.setVisibility(View.INVISIBLE);
                holder.textViewAnother.setVisibility(View.INVISIBLE);
                holder.textViewAnotherTime.setVisibility(View.INVISIBLE);
                holder.textViewYou.setVisibility(View.INVISIBLE);
            } else {
                holder.textViewYou.setText(new String(protocol.getData()));
//                holder.textViewYouTime.setText(protocol.getTime());
                holder.textViewAnother.setVisibility(View.INVISIBLE);
                holder.textViewAnotherTime.setVisibility(View.INVISIBLE);
                holder.viewYou.setVisibility(View.INVISIBLE);
                holder.viewAnother.setVisibility(View.INVISIBLE);
            }
        } else {
            if(protocol.getMsgCode() == MsgCodes.fileEndCode) {
                byte[] imgData = ((MainActivity)this.context).byteArrayBufferFileTheir.toByteArray();
                Bitmap bmp = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
                Log.e(Constants.TAG, "WHY2::::" + bmp.describeContents());
                holder.viewAnother.setImageBitmap(bmp);
                holder.textViewAnotherTime.setVisibility(View.INVISIBLE);
                holder.textViewYou.setVisibility(View.INVISIBLE);
                holder.textViewYouTime.setVisibility(View.INVISIBLE);
                holder.textViewAnother.setVisibility(View.INVISIBLE);
            } else {
                holder.textViewAnother.setText(new String(protocol.getData()));
//                holder.textViewAnotherTime.setText(protocol.getTime());
                holder.textViewYou.setVisibility(View.INVISIBLE);
                holder.textViewYouTime.setVisibility(View.INVISIBLE);
                holder.viewYou.setVisibility(View.INVISIBLE);
                holder.viewAnother.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return protocols.size();
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
            viewYou.setOnClickListener(v -> {
                AlertDialog.Builder ImageDialog = new AlertDialog.Builder(context);
                ImageDialog.setTitle("Picture");
                viewYou.buildDrawingCache();
                ImageView showImage = new ImageView(context);
                showImage.setMinimumHeight(2000);
                showImage.setImageBitmap(viewYou.getDrawingCache());
                ImageDialog.setView(showImage);
                ImageDialog.show();
                showImage.setOnClickListener(v1 -> {
                    if (!isImageScaled) v1.animate().scaleX(1.4f).scaleY(1.4f).setDuration(500);
                    if (isImageScaled) v1.animate().scaleX(1f).scaleY(1f).setDuration(500);
                    isImageScaled = !isImageScaled;
                });
            });

            viewAnother.setOnClickListener(v -> {
                AlertDialog.Builder ImageDialog = new AlertDialog.Builder(context);
                ImageDialog.setTitle("Picture");
                viewAnother.buildDrawingCache();
                ImageView showImage = new ImageView(context);
                showImage.setMinimumHeight(2000);
                showImage.setImageBitmap(viewAnother.getDrawingCache());
                ImageDialog.setView(showImage);
                ImageDialog.show();
                showImage.setOnClickListener(v1 -> {
                    if (!isImageScaled) v1.animate().scaleX(1.4f).scaleY(1.4f).setDuration(500);
                    if (isImageScaled) v1.animate().scaleX(1f).scaleY(1f).setDuration(500);
                    isImageScaled = !isImageScaled;
                });
            });

        }
    }
}
