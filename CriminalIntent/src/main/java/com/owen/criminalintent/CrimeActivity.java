package com.owen.criminalintent;

import android.support.v4.app.Fragment;
import android.os.Bundle;

import com.owen.criminalintent.Utils.Constant;

import java.util.UUID;

public class CrimeActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        //return new CrimeFragment();

        UUID crimeId = (UUID) getIntent().getSerializableExtra(Constant.EXTRA_CRIME_ID);
        return CrimeFragment.newInstance(crimeId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
