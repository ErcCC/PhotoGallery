package com.example.u0094350.photogallery;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.u0094350.photogallery.Util.NetworkUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by DWalker on 7/5/17.
 */

public class PollService extends IntentService{
    private static final String TAG = "PollService";

    // Set interval to 1 minute
    private static final long POLL_INTERVAL_MS = TimeUnit.MINUTES.toMillis(1);


    public static Intent newIntent(Context context){
        return new Intent(context, PollService.class);
    }

    public PollService() {
        super(TAG);
    }

    public static void setServiceAlarm(Context context, boolean isOn){
        Intent i = PollService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context,0, i, 0);

        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);

        if(isOn){
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(),
                    POLL_INTERVAL_MS,
                    pi);
        } else {
            alarmManager.cancel(pi);
            pi.cancel();
        }
    }

    public static boolean isServiceAlarmOn(Context contxt){
        Intent i = PollService.newIntent(contxt);

        PendingIntent pi = PendingIntent.getService(contxt, 0, i, PendingIntent.FLAG_NO_CREATE);

        return pi != null;

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(! (new NetworkUtil(getApplicationContext()).isNetworkAvailableAndConnected())){
            Log.i(TAG, "onHandleIntent: no network connection to download data");
            return;
        }

        
        //Log.i(TAG, "onHandleIntent: Received an intent: " + intent);
        String query = QueryPreferences.getStoredQuery(this);
        String lastResultId = QueryPreferences.getPrefLastResultId(this);
        List<GalleryItem> items;

        if(query == null){
            items = new FlickrFetchr().fetchRecentPhotos(0);
        } else {
            items = new FlickrFetchr().searchPhotos(query);
        }

        if(items.size() == 0){
            return;
        }

        String resultId = items.get(0).getId();
        if(resultId == items.get(0).getId()){
            Log.i(TAG, "Got an old result: " + resultId);
        } else {
            Log.i(TAG, "Got a new result: " + resultId);
        }

        QueryPreferences.setPrefLastResultId(this, resultId);


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
