package com.owen.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.owen.criminalintent.Model.Photo;
import com.owen.criminalintent.Utils.Constant;
import com.owen.criminalintent.Utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;


/**
 * Created by Owen on 2015/12/28.
 */
public class CrimeCameraFragment extends Fragment {
    private static final String TAG = "CrimeCameraFragment";

    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private View mProgressContainer;
    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            mProgressContainer.setVisibility(View.VISIBLE);
        }
    };
    private Camera.PictureCallback mJepgCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            if (FileUtils.getFileUtils(getActivity()).isExternalSpaceWritable()) {
                String picName = getPicName();
                File file = FileUtils.getFileUtils(getActivity()).getExternalPictrue(picName);
                FileOutputStream fos = null;
                boolean success = true;
//                if (!file.exists()) {
                    try {
                        fos = new FileOutputStream(file);
                        fos.write(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                        success = false;
                    }  finally {
                        if (fos != null) {
                            try {
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                                success = false;
                            }
                        }
                    }
//                }

                if (success) {
                    Log.d(TAG, "JPEG saved at " + getPicName());
                    Intent intent = new Intent();
                    intent.putExtra(Constant.EXTRA_PHOTO_NAME, picName);
                    getActivity().setResult(Activity.RESULT_OK, intent);
                } else {
                    getActivity().setResult(Activity.RESULT_CANCELED);
                }

                getActivity().finish();
            }
        }
    };


    private String getPicName() {
        if (mPhoto == null || TextUtils.isEmpty(mPhoto.getPicName())) {
            CharSequence time = DateFormat.format(Constant.PIC_NAME_FORMAT, new Date());
            return UUID.randomUUID().toString() + "_" + time + ".jpg";
        } else {
            return mPhoto.getPicName();
        }
    }

    private Photo mPhoto;

    public CrimeCameraFragment() {
    }

    public static CrimeCameraFragment newInstance(Photo photo){
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.EXTRA_PHOTO, photo);

        CrimeCameraFragment fragment = new CrimeCameraFragment();
        fragment.setArguments(bundle);

        return fragment;
    }
    
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mPhoto = (Photo) getArguments().getSerializable(Constant.EXTRA_PHOTO);

        View view = inflater.inflate(R.layout.fragment_crime_camera, container, false);

        mProgressContainer = view.findViewById(R.id.crime_camera_progressContainer);
        mProgressContainer.setVisibility(View.INVISIBLE);

        Button takeButton = (Button) view.findViewById(R.id.crime_camera_takeButton);
        takeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCamera != null) {
                    mCamera.takePicture(mShutterCallback, null, mJepgCallback);
                }
            }
        });

        mSurfaceView = (SurfaceView) view.findViewById(R.id.crime_camera_surfaceView);
        SurfaceHolder holder = mSurfaceView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (mCamera != null) {
                        mCamera.setPreviewDisplay(holder);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if (mCamera == null) {
                    return;
                }

                Camera.Parameters parameters = mCamera.getParameters();
                Camera.Size size = getBestSupportedSizes(parameters.getSupportedPreviewSizes(), width, height);
                parameters.setPreviewSize(size.width, size.height);
//                size = getBestSupportedSizes(parameters.getSupportedPictureSizes(), width, height);
//                parameters.setPictureSize(size.width, size.height);
                parameters.setPictureSize(size.width, size.height);
                mCamera.setParameters(parameters);
                try {
                    mCamera.startPreview();
                } catch (Exception e) {
                    e.printStackTrace();
                    mCamera.release();
                    mCamera = null;
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (mCamera != null) {
                    mCamera.stopPreview();
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            mCamera = Camera.open(0);
        } else {
            mCamera = Camera.open();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }


    /**
     * 找出设备支持的最佳尺寸
     */
    private Camera.Size getBestSupportedSizes(List<Camera.Size> sizes, int width, int height) {
        Camera.Size bestSize = sizes.get(0);
        int largestArea = bestSize.width * bestSize.height;
        for (Camera.Size s:sizes) {
            int area = s.width * s.height;
            if (area > largestArea) {
                bestSize = s;
                largestArea = area;
            }
        }
        return bestSize;
    }
}
