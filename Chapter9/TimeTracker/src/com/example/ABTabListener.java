package com.example;

import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

public class ABTabListener implements android.app.ActionBar.TabListener {
    private ViewPager mViewPager;
    
    public ABTabListener(ViewPager viewPager) {
        mViewPager = viewPager;
    }
    
    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        int pos = tab.getPosition();
        mViewPager.setCurrentItem(pos);
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    }
}
