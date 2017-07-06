package com.example.u0094350.photogallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;


import java.io.IOException;

public class PhotoActivity extends AppCompatActivity {

    private static final String TAG = "PhotoActivity";
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        image = (ImageView)findViewById(R.id.photo_view);

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");

        Log.i(TAG, "onCreate: url = " + url);
        new LoadPhotoTask().execute(url);
    }


    private class LoadPhotoTask extends AsyncTask<String, Void, Bitmap> {


        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap mBitmap = null;


            try{
                String url = strings[0];
                Log.i(TAG, "doInBackground: url = " + url);

                byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(url);
                mBitmap = BitmapFactory
                        .decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
                Log.i(TAG, "doInBackground: Bitmap created");
            } catch (IOException ioe){
                Log.e(TAG, "doInBackground: failed to fetch URL: ", ioe);
            }

            return mBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            //super.onPostExecute(bitmap);
            if(bitmap == null){
                Log.i(TAG, "onPostExecute: bitmap = null");
            }

            image.setImageBitmap(bitmap);
        }
    }
}
