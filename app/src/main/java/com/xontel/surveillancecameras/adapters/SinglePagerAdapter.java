package com.xontel.surveillancecameras.adapters;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.xontel.surveillancecameras.fragments.CamPreviewFragment;
import com.xontel.surveillancecameras.fragments.PagerFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SinglePagerAdapter extends FragmentStatePagerAdapter  {

    private final List<Fragment> mFragmentList = new ArrayList<>();

    public SinglePagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm,behavior);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }



    public void addFragment(CamPreviewFragment fragment) {
        mFragmentList.add(fragment);
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }


    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
