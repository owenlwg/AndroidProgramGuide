package com.owen.criminalintent;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.owen.criminalintent.Utils.Constant;
import com.owen.criminalintent.Utils.PictureUtils;

/**
 * Created by Owen on 2015/12/29.
 */
public class PhotoDialogFragment extends DialogFragment{

    private ImageView mImageView;

    public PhotoDialogFragment() {

    }

    public static PhotoDialogFragment newInstance(String imagePath) {
        Bundle bundle = new Bundle();
//        bundle.putSerializable(Constant.EXTRA_IMAGE_PATH, imagePath);
        bundle.putString(Constant.EXTRA_IMAGE_PATH, imagePath);

        PhotoDialogFragment fragment = new PhotoDialogFragment();
        fragment.setArguments(bundle);
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mImageView = new ImageView(getActivity());
        String path = getArguments().getString(Constant.EXTRA_IMAGE_PATH);
        Drawable drawable = PictureUtils.getScaledDrawable(getActivity(), path);
        mImageView.setImageDrawable(drawable);
        return mImageView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PictureUtils.cleanImageView(mImageView);
    }
}
