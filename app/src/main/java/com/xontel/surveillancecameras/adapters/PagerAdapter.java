package com.xontel.surveillancecameras.adapters;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.xontel.surveillancecameras.fragments.GridFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PagerAdapter extends FragmentStatePagerAdapter {
    public static final String TAG = PagerAdapter.class.getSimpleName();
    private List<GridFragment> mGridFragments = new ArrayList<>();
    private int gridCount ;
    public PagerAdapter(@NonNull FragmentActivity activity, int gridCount) {
        super(activity.getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.gridCount = gridCount;
    }

    public void setGridCount(int pages) {
        this.gridCount = pages;
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
       return ((GridFragment)object).getPageIndex();
    }


    @Override
    public int getCount() {
        return gridCount;
    }




    @NonNull
    @NotNull
    @Override
    public Fragment getItem(int position) {
        return GridFragment.newInstance(position);
    }
}
