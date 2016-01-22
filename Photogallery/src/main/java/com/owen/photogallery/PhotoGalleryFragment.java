package com.owen.photogallery;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SearchView;

import com.owen.photogallery.model.GalleryItem;
import com.owen.photogallery.utils.ImageDownloader;
import com.owen.photogallery.utils.ThumbnailDownloader;

import java.util.ArrayList;

/**
 * Created by Owen on 2016/1/11.
 */
public class PhotoGalleryFragment extends Fragment {

    private GridView mGridView;
    private ArrayList<GalleryItem> mGalleryItems;

    private ThumbnailDownloader mThumbnailDownloader;
    private ImageDownloader mImageDownloader;
    private Handler mHandler = new Handler();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        setHasOptionsMenu(true);

        updateItems();
        mImageDownloader = new ImageDownloader(getActivity());
//        mThumbnailDownloader = new ThumbnailDownloader(getActivity(), mHandler);
//        mThumbnailDownloader.start();
//        mThumbnailDownloader.getLooper();
    }

    public void updateItems() {
        new FetchItemsTask().execute();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        mGridView = (GridView) view.findViewById(R.id.gridview);

        setupAdapter();

        return view;
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, ArrayList<GalleryItem>> {

        @Override
        protected ArrayList<GalleryItem> doInBackground(Void... params) {
//            String query = "android";  //just for test
            String query = PreferenceManager.getDefaultSharedPreferences(getActivity())
                                    .getString(FlickrFetcher.PREF_SEARCH_QUERY, null);
            if (!TextUtils.isEmpty(query)) {
                return new FlickrFetcher().search(getActivity(), query);
            } else {
//            return new FlickrFetcher().fetchItems();
                return new FlickrFetcher().fetchItems(getActivity());
            }
        }

        @Override
        protected void onPostExecute(ArrayList<GalleryItem> items) {
            mGalleryItems = items;
            setupAdapter();
        }
    }

    private void setupAdapter() {
        if (getActivity() == null || mGridView == null) {
            return;
        }
        if (mGalleryItems != null) {
/*            mGridView.setAdapter(new ArrayAdapter<GalleryItem>(getActivity(),
                                  android.R.layout.simple_gallery_item, mGalleryItems));*/
            Log.e("owen", "mGalleryItems.size(): " + mGalleryItems.size());
            mGridView.setAdapter(new GalleryItemAdapter(mGalleryItems));
        } else {
            mGridView.setAdapter(null);
        }
    }

    private class GalleryItemAdapter extends ArrayAdapter<GalleryItem> {

        public GalleryItemAdapter(ArrayList<GalleryItem> items) {
            super(getActivity(), 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                                      .inflate(R.layout.gallery_item, parent, false);
                holder = new ViewHolder();
                holder.imageView = (ImageView) convertView.findViewById(R.id.gallery_item_imageview);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            Log.e("owen", "position:" + position);
            GalleryItem item = mGalleryItems.get(position);

            holder.imageView.setImageResource(R.drawable.default_image);
//            mThumbnailDownloader.queueThumbnail(holder.imageView, item.getUrl());
            mImageDownloader.loadImage(holder.imageView, item.getUrl());


            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_photo_gallery, menu);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            MenuItem item = menu.findItem(R.id.menu_item_search);
            SearchView searchView = (SearchView) item.getActionView();

            SearchManager searchManager = (SearchManager)getActivity()
                                               .getSystemService(Context.SEARCH_SERVICE);
            ComponentName name = getActivity().getComponentName();
            SearchableInfo searchableInfo = searchManager.getSearchableInfo(name);

            searchView.setSearchableInfo(searchableInfo);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_search:
                getActivity().onSearchRequested();
                return true;
            case R.id.menu_item_clear:
                clearPerference();
                updateItems();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clearPerference();
        mImageDownloader.cancelAllTasks();
//        mThumbnailDownloader.quit();
    }

    private void clearPerference() {
        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .edit()
                .putString(FlickrFetcher.PREF_SEARCH_QUERY, null)
                .commit();
    }
}
