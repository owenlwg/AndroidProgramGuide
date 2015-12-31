package com.owen.criminalintent;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.owen.criminalintent.Model.Crime;
import com.owen.criminalintent.Model.CrimeLab;
import com.owen.criminalintent.Utils.Constant;

import java.util.ArrayList;
import java.util.UUID;

public class CrimePagerActivity extends FragmentActivity {

    private ViewPager mViewPager;
    private ArrayList<Crime> mCrimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID crimeId = (UUID) getIntent().getSerializableExtra(Constant.EXTRA_CRIME_ID);

        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.viewPager);

        setContentView(mViewPager);

        mCrimes = CrimeLab.getCrimeLab(this).getCrimes();

        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);

                //创建Fragment并返回给托管Activity
                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });

        //设置初始分页显示项
        for (int i = 0; i < mCrimes.size(); i++) {
            if (crimeId.equals(mCrimes.get(i).getId())) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }

        //设置预加载相邻页面的数目
        mViewPager.setOffscreenPageLimit(3);

        //设置页面监听器
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Crime crime = mCrimes.get(position);
                if (crime.getTitle() != null) {
                    setTitle(crime.getTitle());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

}
