package com.example.letschat.Adaptor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class PagerAdaptor extends FragmentPagerAdapter {

    private ArrayList<Fragment> frag = new ArrayList<>();
    private ArrayList<String> title = new ArrayList<>();

    public PagerAdaptor(@NonNull FragmentManager fm) {
        super(fm);
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        return frag.get(position);
    }

    @Override
    public int getCount() {
        return title.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return title.get(position);
    }

    public void addFrag(Fragment f, String t) {
        frag.add(f);
        title.add(t);
    }
}
