package com.example.u0094350.photogallery.Util;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by u0094350 on 7/6/2017.
 */

public class NetworkUtil {
    private Context mContext;

    public NetworkUtil(Context context){
        mContext = context;
    }

    public boolean isNetworkAvailableAndConnected(){
        ConnectivityManager cm = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable &&
                cm.getActiveNetworkInfo().isConnected();

        return isNetworkConnected;
    }


}
