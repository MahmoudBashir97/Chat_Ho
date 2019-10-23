package com.example.chatho.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class Tabs_access_adpt extends FragmentPagerAdapter {

    private final List<Fragment> fragmentlist=new ArrayList<>();
    private final List<String> fragmentlisttitels=new ArrayList<>();

    public Tabs_access_adpt(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentlist.get(position);
    }

    @Override
    public int getCount() {
        return fragmentlisttitels.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentlisttitels.get(position);
    }
    public void addFragment (Fragment fragment , String Title){
        fragmentlist.add(fragment);
        fragmentlisttitels.add(Title);
    }
}
