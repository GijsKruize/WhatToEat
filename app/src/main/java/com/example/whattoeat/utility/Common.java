package com.example.whattoeat.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Common {

    public static boolean isConnectedToInternet(Context context) {
        // get system service
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(context.CONNECTIVITY_SERVICE);

        // Check if there is a connection
        if (connectivityManager != null) {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null) {
                if (info.isConnected() || info.isAvailable()) {
                    return true;
                }
            }
        }
        return false;
    }
}
