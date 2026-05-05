package com.example.homely.utils;

import android.content.*;
import android.net.*;

/**
 * kiểm tra kết nối mạng.
 */
public class NetworkUtils {
    /**
     * Kiểm tra xem thiết bị có đang kết nối Internet (Wi-Fi hoặc di động) hay không.
     * @param context Context ứng dụng (Activity hoặc Application)
     * @return true nếu có kết nối, false nếu không có hoặc không xác định
     */
    public static boolean isNetworkAvailable(Context context){

        if(context == null) return false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(cm == null) return  false;

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
