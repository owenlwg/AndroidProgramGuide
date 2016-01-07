package com.owen.criminalintent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.view.WindowManager;

import com.owen.criminalintent.Model.Photo;
import com.owen.criminalintent.Utils.Constant;

import java.io.Serializable;
import java.util.UUID;

public class CrimeCameraActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        Photo photo = (Photo) getIntent().getSerializableExtra(Constant.EXTRA_PHOTO);
        return CrimeCameraFragment.newInstance(photo);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //隐藏title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐藏状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);

    }
}
