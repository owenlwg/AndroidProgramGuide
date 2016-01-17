package com.owen.photogallery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.owen.photogallery.model.GalleryItem;
import com.owen.photogallery.utils.ImageDownloader;
import com.owen.photogallery.utils.ThumbnailDownloader;

import java.util.ArrayList;

/**
 * Created by Owen on 2016/1/11.
 */
public class PhotoGalleryFragment extends Fragment{

    private GridView mGridView;
    private ArrayList<GalleryItem> mGalleryItems;

    private ThumbnailDownloader mThumbnailDownloader;
    private ImageDownloader mImageDownloader;
    private Handler mHandler = new Handler();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        new FetchItemsTask().execute();
        mImageDownloader = new ImageDownloader(getActivity());
//        mThumbnailDownloader = new ThumbnailDownloader(getActivity(), mHandler);
//        mThumbnailDownloader.start();
//        mThumbnailDownloader.getLooper();
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
//            return new FlickrFetcher().fetchItems();
            return new FlickrFetcher().fetchItems(getActivity());
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
            Log.e("owen","mGalleryItems.size(): " + mGalleryItems.size());
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

        class ViewHolder{
            ImageView imageView;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        mThumbnailDownloader.quit();
    }
}
