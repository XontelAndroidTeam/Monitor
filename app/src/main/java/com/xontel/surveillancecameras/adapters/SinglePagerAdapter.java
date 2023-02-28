package com.xontel.surveillancecameras.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.xontel.surveillancecameras.fragments.CamPreviewFragment;
import com.xontel.surveillancecameras.fragments.GridFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SinglePagerAdapter extends FragmentStateAdapter {
    private int pages;


    public SinglePagerAdapter(@NonNull FragmentActivity fragmentActivity, int pages) {
        super(fragmentActivity);
        this.pages = pages;
    }



    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return GridFragment.newInstance(position);
    }

    @Override
    public int getItemCount() {
        return pages;
    }
}
