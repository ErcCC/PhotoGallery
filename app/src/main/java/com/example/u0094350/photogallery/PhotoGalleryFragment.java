package com.example.u0094350.photogallery;

import android.app.ActivityManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.example.u0094350.photogallery.Util.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by u0094350 on 3/15/2017.
 */

public class PhotoGalleryFragment extends Fragment{

    private static final String TAG = "PhotoGalleryFragment";

    private RecyclerView mPhotoRecyclerView;
    private List<GalleryItem> mItems = new ArrayList<>();
    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;

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
        setHasOptionsMenu(true);

        currentPage = 0;
        //new FetchItemsTask().execute();
        updateItems();

        Handler responseHandler = new Handler();

        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
        mThumbnailDownloader.setThumbnailDownloadListener(new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>() {
            @Override
            public void onThumbnailDownloaded(PhotoHolder photoHolder, Bitmap thumbnail) {
                Drawable drawable = new BitmapDrawable(getResources(), thumbnail);
                photoHolder.bindDrawable(drawable);
            }
        });

        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        Log.i(TAG, "Background thread started");


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_photo_gallery, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit: " + query);
                QueryPreferences.setStoredQuery(getActivity(), query);
                updateItems();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange: " + newText);
                return false;
            }
        });
        

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = QueryPreferences.getStoredQuery(getActivity());
                searchView.setQuery(query, false);
            }
        });
    }

    private void updateItems() {
        String query = QueryPreferences.getStoredQuery(getActivity());

        new FetchItemsTask(query).execute();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_clear:
                QueryPreferences.setStoredQuery(getActivity(), null);
                updateItems();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

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
                new FetchItemsTask(null).execute();
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

    @Override
    public void onDestroy() {
        Log.i(TAG, "Background thread destroyed before super");
        super.onDestroy();
        Log.i(TAG, "Background thread destroyed after super.onDestroy()");
        mThumbnailDownloader.quit();
        Log.i(TAG, "Background thread destroyed");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailDownloader.clearQueue();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: you are here , Background");
    }
    
    

    private void setupAdapter() {
        if(isAdded()){
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
        }
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>> {
        private String mQuery;

        public FetchItemsTask(String query){
            mQuery = query;
        }

        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
//            try{
//                String result = new FlickrFetchr()
//                        .getUrlString("http://tom661.freeshell.org");
//                Log.i(TAG, "doInBackground: Fetched contents of URL: " + result);
//            } catch (IOException ioe){
//                Log.e(TAG, "doInBackground: Failed to fetch URL: ", ioe);
//            }

            //return new FlickrFetchr().fetchItems(currentPage);
           // String query = "robot"; // Just for testing

            if(mQuery == null){
                return new FlickrFetchr().fetchRecentPhotos(currentPage);
            } else {
                return new FlickrFetchr().searchPhotos(mQuery);
            }

        }

        @Override
        protected void onPostExecute(List<GalleryItem> galleryItems) {
            mItems = galleryItems;
            setupAdapter();
        }
    }


    private class PhotoHolder extends RecyclerView.ViewHolder{
        private ImageView mItemImageView;

        public PhotoHolder(View itemView){
            super(itemView);

            mItemImageView = (ImageView) itemView.findViewById(R.id.item_image_view);
        }

//        public void bindGalleryItem(GalleryItem item){
//            mTitleTextView.setText(item.toString());
//        }
        public void bindDrawable(Drawable drawable){
            mItemImageView.setImageDrawable(drawable);
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder>{
        private List<GalleryItem> mGalleryItems;

        public PhotoAdapter(List<GalleryItem> galleryItems){
            mGalleryItems = galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            TextView textView = new TextView(getActivity());
//
//            return new PhotoHolder(textView);

            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_item_gallery, parent, false);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoHolder photoHolder, int position) {
            GalleryItem galleryItem = mGalleryItems.get(position);
//            photoHolder.bindGalleryItem(galleryItem);
            Drawable placeholder = getResources().getDrawable(R.drawable.koala_small);
            photoHolder.bindDrawable(placeholder);
            mThumbnailDownloader.queueThumbnail(photoHolder, galleryItem.getUrl());
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }
}
