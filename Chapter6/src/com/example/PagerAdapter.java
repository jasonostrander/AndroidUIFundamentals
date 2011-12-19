package com.example;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter {
    public static final int PAGE_COUNT = 2;
    TimerFragment mTimerFragment;
    TaskListFragment mTaskListFragment;
    
    public PagerAdapter(FragmentManager fm) {
        super(fm);
        mTimerFragment = new TimerFragment();
        mTaskListFragment = new TaskListFragment();
    }

    @Override
    public Fragment getItem(int arg0) {
        switch(arg0) {
        case 0:
            return mTimerFragment;
        case 1:
            return mTaskListFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

}
