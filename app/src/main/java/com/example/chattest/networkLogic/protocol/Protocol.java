package com.example.chattest.networkLogic.protocol;

import androidx.annotation.Nullable;

import com.example.chattest.utils.Constants;
import com.example.chattest.utils.Utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Protocol implements Serializable {



    private byte[] data;
    public int actualDataSize = 0;
    private boolean fromThisDevice;
    private byte msgCode;

    @Nullable
    public byte[] serialize(){
        byte[] result = null;

        try {
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);
            objectOutputStream.writeObject(this);
            objectOutputStream.flush();
            objectOutputStream.close();

            result = byteOutputStream.toByteArray();
            byteOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Nullable
    public static Protocol deserialize(byte[] protocolDataBytes){
        InputStream targetStream = new ByteArrayInputStream(protocolDataBytes);
        Protocol protocol = null;
        try {
            ObjectInputStream objectInputStream
                    = new ObjectInputStream(targetStream);
            protocol = (Protocol) objectInputStream.readObject();
            objectInputStream.close();
            targetStream.close();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return protocol;
    }

//    public void setCurrentTime() {
//        time = Utils.getTime(false);
//    }

    public void setData(byte[] data) throws Exception {
        actualDataSize = data.length;
        if(data.length > Constants.BUFFER_SIZE)
            throw new Exception("Exceeded buffer size!!!");
        this.data = new byte[Constants.BUFFER_SIZE];
        for (int i = 0; i < data.length; i++) {
            this.data[i] = data[i];
        }
        //FUCK THIS:
//        System.arraycopy(this.data, 0, data, 0, data.length);
    }
    public void setAnySizeData_notForSending_(byte[] data) {
        System.out.println("Setting the full size image in one node: " + data.length);
        this.data = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            this.data[i] = data[i];
        }
        //FUCK THIS:
//        System.arraycopy(this.data, 0, data, 0, data.length);
    }
    public byte[] getData() {
        byte[] newData = new byte[actualDataSize];
        for (int i = 0; i < newData.length; i++) {
            newData[i] = data[i];
        }
        return newData;
    }
//    public byte[] getActualData() {
//        byte[] newData = new byte[actualDataSize];
//        for (int i = 0; i < newData.length; i++) {
//            newData[i] = data[i];
//        }
//        return newData;
//    }

//    public String getTime() {
//        return time;
//    }
//    public void setTime(String time) {
//        this.time = time;
//    }

    public boolean isFromThisDevice() {
        return fromThisDevice;
    }
    public void setFromThisDevice(boolean fromThisDevice) {
        this.fromThisDevice = fromThisDevice;
    }

    public byte getMsgCode() {
        return msgCode;
    }
    public void setMsgCode(byte msgCode) {
        this.msgCode = msgCode;
    }
}
