package com.example.chattest.utils;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.example.chattest.MainActivity;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utils {


    /**
     * Get IP address from first non-localhost interface
     *
     * @param useIPv4 true=return ipv4, false=return ipv6
     * @return address or empty string
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':') < 0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        } // for now eat exceptions
        return "";
    }


    // getting time function
    public static String getTime(boolean need) {
        int minute, hour, second;
        String zone = "am";
        String time = "";

        Calendar calendar = Calendar.getInstance();
        minute = calendar.get(Calendar.MINUTE);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour >= 12) {
            zone = "pm";
        }
        if (hour > 12) {
            hour = hour % 12;
        }
        second = calendar.get(Calendar.SECOND);

        if (need)
            time = hour + ":" + minute + ":" + second + " " + zone;
        else
            time = hour + ":" + minute + " " + zone;

        return time;
    }

    public static String getFilePathFromUri(Uri uri){
        String path = uri.getPathSegments().get(1);
        path = Environment.getExternalStorageDirectory().getPath()+"/"+path.split(":")[1];
        return path;
    }

    public static String readTextFile(Uri uri, MainActivity core){
        String ret = "";

        try {

            InputStream inputStream = core.getContentResolver().openInputStream(uri);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                Stream<String> lines = bufferedReader.lines();

                //Log.e("ARRR", "lines: " + lines.count());

                //lines = bufferedReader.lines();


                //lines.collect( Collectors.joining( "\n" ) );
                ret = lines.collect( Collectors.joining( "" ) );
                Log.e("ARRR2", "res: " + ret);
                inputStream.close();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        return ret;
//
//        BufferedReader reader = null;
//        StringBuilder builder = new StringBuilder();
//        try {
//            reader = new BufferedReader(new InputStreamReader(core.getContentResolver().openInputStream(uri)));
//
//            String line = "";
//
//            // reading line by line and adding a \n at the end
//            while ((line = reader.readLine()) != null) {
//                builder.append("\n" + line);
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (reader != null){
//                try {
//                    reader.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return builder.toString();
    }

    public static byte[] readTextFileBytes(Uri uri, MainActivity core){
        byte[] targetArray = new byte[0];
        try {

            InputStream inputStream = core.getContentResolver().openInputStream(uri);

            if ( inputStream != null ) {
//                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

//                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream();
                targetArray = new byte[inputStream.available()];

                inputStream.read(targetArray);
                //Stream<String> lines = bufferedReader.lines();

                //Log.e("ARRR", "lines: " + lines.count());

                //lines = bufferedReader.lines();


                //lines.collect( Collectors.joining( "\n" ) );
                inputStream.close();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        return targetArray;
    }
}