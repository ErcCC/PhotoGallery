package com.example.u0094350.photogallery;

import android.os.HandlerThread;
import android.util.Log;

/**
 * Created by u0094350 on 4/27/2017.
 */

public class ThumbnailDownloader<T> extends HandlerThread {

    private static final String TAG = "ThumbnailDownloader";
    private boolean mHasQuit = false;


    public ThumbnailDownloader() {
        super(TAG);
    }

    @Override
    public boolean quit() {
        mHasQuit = true;
        return super.quit();
    }

    public void queueThumbnail(T target, String url){
        Log.i(TAG, "Got a URL: " + url);
    }
}
