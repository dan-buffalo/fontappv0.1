package com.davide.fontappbeta;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class SectionStatePagerAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentListName = new ArrayList<>();

    private void addFragment(Fragment fragment, String title){
        mFragmentList.add(fragment);
        mFragmentListName.add(title);

    }

    public SectionStatePagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
