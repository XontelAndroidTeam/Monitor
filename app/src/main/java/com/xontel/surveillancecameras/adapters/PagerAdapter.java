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
import java.util.Collections;
import java.util.List;

public class PagerAdapter extends XontelFragmentStatePagerAdapter {
    public static final String TAG = PagerAdapter.class.getSimpleName();
    private int totalCams;

    private int pagesCount;

    public PagerAdapter(@NonNull FragmentActivity activity, int pagesCount) {
        super(activity.getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.pagesCount = pagesCount;
    }

    public void setPagesCount(int pagesCount) {
        this.pagesCount = pagesCount;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Object o = super.instantiateItem(container, position);
        return o;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
//        Log.v(TAG, "getItemPosition "+((Fragment)object).hashCode());
        GridFragment gridFragment = (GridFragment) object;
        if (gridFragment.isResumed() && pagesCount > 0) {
            int newPosition = gridFragment.calculateNewIndex();
            int oldPosition = mFragments.indexOf(object);
            if(oldPosition < newPosition){
                while (mFragments.size() <= newPosition) {
                    mFragments.add(null);
                }
            }
            Collections.swap(mFragments, oldPosition, newPosition);
            return newPosition;
        }
        return POSITION_NONE;
    }


    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//        Log.v(TAG, "setPrimaryItem "+((Fragment)object).hashCode()+"");
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public int getCount() {
        return pagesCount;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        Log.v(TAG, "destroying " + object.hashCode() + " " + position);
        super.destroyItem(container, position, object);
    }

    @NonNull
    @NotNull
    @Override
    public Fragment getItem(int position) {
        GridFragment gridFragment = GridFragment.newInstance(position);
        Log.v(TAG, "creating " + (gridFragment.hashCode() + " " + position));
        return gridFragment;
    }
}
