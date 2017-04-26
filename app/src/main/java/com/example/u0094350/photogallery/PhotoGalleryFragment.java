package com.example.u0094350.photogallery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import com.example.u0094350.photogallery.Util.EndlessRecyclerViewScrollListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by u0094350 on 3/15/2017.
 */

public class PhotoGalleryFragment extends Fragment{

    private static final String TAG = "PhotoGalleryFragment";

    private RecyclerView mPhotoRecyclerView;
    private List<GalleryItem> mItems = new ArrayList<>();

    private EndlessRecyclerViewScrollListener scrollListener;

    private int mGridColumns;
    private GridLayoutManager mGridlayoutManager;
    private int COLUMN_SIZE = 300;

    private int pageCounts;


    //The minimum number of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 5;
    // The current offset index of data you have loaded
    private int currentPage = 0;
    // The total nnumber of items in the dataset after the last load
    private int previousTotalItemCount = 0;
    // True if we are still waiting for the last set of data to load
    private boolean loading = true;
    // Sets the starting page index
    private int startingPageIndex = 0;

    //"photos":{"page":4,"pages":10,"perpage":100,"total":1000,"photo":
    private int totalItemCount = 1000;


    public static PhotoGalleryFragment newInstance(){
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        currentPage = 0;
        new FetchItemsTask().execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        mPhotoRecyclerView = (RecyclerView)v.findViewById(R.id.photo_recycler_view);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        setupAdapter();

        scrollListener = new EndlessRecyclerViewScrollListener((GridLayoutManager)mPhotoRecyclerView.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                currentPage = page;
                new FetchItemsTask().execute();
                Log.d(TAG, "onLoadMore: currentPage = " + currentPage);
            }
        };

        // Adds the scroll listener to RecycleView
        mPhotoRecyclerView.addOnScrollListener(scrollListener);

        mPhotoRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mPhotoRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                // Adjust the columns to fit based on width of RecyclerView
                int width = mPhotoRecyclerView.getWidth();
                mGridColumns = width / COLUMN_SIZE;
                mGridlayoutManager = new GridLayoutManager(getActivity(), mGridColumns);
                mPhotoRecyclerView.setLayoutManager(mGridlayoutManager);
                setupAdapter();
            }
        });


/*
        mPhotoRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            GridLayoutManager mGridLayoutManager = (GridLayoutManager)mPhotoRecyclerView.getLayoutManager();

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int lastPosition = mGridLayoutManager.findLastVisibleItemPosition();
                if(lastPosition == mItems.size() - 3){
                    Toast.makeText(getActivity(), "Bottom", Toast.LENGTH_SHORT).show();
                    ++currentPage;
                   new FetchItemsTask().execute();
                  //  Log.d(TAG, "onScrolled: current page = " + currentPage + " dy = " + dy);
                }
                Log.d(TAG, "onScrolled: current page = " + currentPage + " dy = " + dy);

            }
        });
*/
        return v;
    }

    private void setupAdapter() {
        if(isAdded()){
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
        }
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>> {
        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
//            try{
//                String result = new FlickrFetchr()
//                        .getUrlString("http://tom661.freeshell.org");
//                Log.i(TAG, "doInBackground: Fetched contents of URL: " + result);
//            } catch (IOException ioe){
//                Log.e(TAG, "doInBackground: Failed to fetch URL: ", ioe);
//            }

            return new FlickrFetchr().fetchItems(currentPage);

        }

        @Override
        protected void onPostExecute(List<GalleryItem> galleryItems) {
            mItems = galleryItems;
            setupAdapter();
        }
    }


    private class PhotoHolder extends RecyclerView.ViewHolder{
        private TextView mTitleTextView;

        public PhotoHolder(View itemView){
            super(itemView);

            mTitleTextView = (TextView) itemView;
        }

        public void bindGalleryItem(GalleryItem item){
            mTitleTextView.setText(item.toString());
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder>{
        private List<GalleryItem> mGalleryItems;

        public PhotoAdapter(List<GalleryItem> galleryItems){
            mGalleryItems = galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView textView = new TextView(getActivity());

            return new PhotoHolder(textView);
        }

        @Override
        public void onBindViewHolder(PhotoHolder photoHolder, int position) {
            GalleryItem galleryItem = mGalleryItems.get(position);
            photoHolder.bindGalleryItem(galleryItem);
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }
}
