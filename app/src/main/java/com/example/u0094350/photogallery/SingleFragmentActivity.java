package com.example.u0094350.photogallery;


import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by u0094350 on 11/26/2016.
 */
public abstract class SingleFragmentActivity extends AppCompatActivity {
    private static final String TAG = "SingleFragmentActivity";

    protected abstract Fragment createFragment();



    @LayoutRes
    protected int getLayoutResId(){
        return R.layout.activity_fragment;
        //return R.layout.activity_twopane;
        //return R.layout.activity_masterdetail;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_fragment);
        setContentView(getLayoutResId());

        // checking how much memeory is available
        ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);

        int availMemInBytes = am.getMemoryClass() * 1024 * 1024;

        // LruCache bitmapCache = new LruCache<String, Bitmap> (vailMemInBytes / 8);
        Log.i(TAG, "onCreate: memory size =" + availMemInBytes + " bytes");
        Log.i(TAG, "onCreate: memory size =" + availMemInBytes / 1024 / 1024 + " Mb");



        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if(fragment == null){
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

}