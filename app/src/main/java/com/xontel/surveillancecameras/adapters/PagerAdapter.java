package com.xontel.surveillancecameras.adapters;

import android.util.Log;
import android.view.ViewGroup;

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
    private int totalCams;

    private int pagesCount ;
    public PagerAdapter(@NonNull FragmentActivity activity, int pagesCount) {
        super(activity.getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.pagesCount = pagesCount;
    }

    public void setPagesCount(int pagesCount) {
        this.pagesCount = pagesCount;
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
       GridFragment gridFragment = (GridFragment)object;
        if(gridFragment.isResumed()){
            return gridFragment.calculateNewIndex();
        }
       return POSITION_NONE;
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public void startUpdate(@NonNull ViewGroup container) {
        super.startUpdate(container);
    }

    @Override
    public int getCount() {
        return pagesCount;
    }




    @NonNull
    @NotNull
    @Override
    public Fragment getItem(int position) {
        return GridFragment.newInstance(position);
    }
}
