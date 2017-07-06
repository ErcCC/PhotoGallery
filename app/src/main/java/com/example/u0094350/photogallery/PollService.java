package com.example.u0094350.photogallery;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.u0094350.photogallery.Util.NetworkUtil;

/**
 * Created by DWalker on 7/5/17.
 */

public class PollService extends IntentService{
    private static final String TAG = "PollService";


    public static Intent newIntent(Context context){
        return new Intent(context, PollService.class);
    }

    public PollService() {
        super(TAG);

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(! (new NetworkUtil(getApplicationContext()).isNetworkAvailableAndConnected())){
            Log.i(TAG, "onHandleIntent: no network connection to download data");
            return;
        }

        
        Log.i(TAG, "onHandleIntent: Received an intent: " + intent);

    }

//    private boolean isNetworkAvailableAndConnected(){
//        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
//
//        boolean isNetworkAvailabe = cm.getActiveNetworkInfo() != null;
//        boolean isNetworkConnected = isNetworkAvailabe &&
//                cm.getActiveNetworkInfo().isConnected();
//
//        return isNetworkConnected;
//    }


}
