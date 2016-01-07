package com.owen.criminalintent;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.owen.criminalintent.Model.Crime;
import com.owen.criminalintent.Utils.Constant;

public class CrimeListActivity extends SingleFragmentActivity
        implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.crimes_title);
    }


    @Override
    public void onCrimeSelected(Crime crime) {
        //如果是手机，返回的View为null
        if (findViewById(R.id.detailFragmentContainer) == null) {
            Intent intent = new Intent(this, CrimePagerActivity.class);
            intent.putExtra(Constant.EXTRA_CRIME_ID, crime.getId());
            startActivity(intent);
        } else {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();

            Fragment oldFragment = fm.findFragmentById(R.id.detailFragmentContainer);
            Fragment newFragment = CrimeFragment.newInstance(crime.getId());

            if (oldFragment != null) {
                ft.remove(oldFragment);
            }

            ft.add(R.id.detailFragmentContainer, newFragment);
            ft.commit();
        }
    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        FragmentManager fm = getSupportFragmentManager();
        CrimeListFragment fragment = (CrimeListFragment) fm.findFragmentById(R.id.fragmentContainer);
        fragment.updateUI();
    }
}
