package com.playroomtv.exoplayerdemo.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> screens = new ArrayList<>();

    public void addScreen (Fragment fragment)
    {
        this.screens.add(fragment);
    }

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @Override
    public Fragment getItem(int position) {
        return screens.get(position);
    }

    @Override
    public int getCount() {
        return screens.size();
    }
}
