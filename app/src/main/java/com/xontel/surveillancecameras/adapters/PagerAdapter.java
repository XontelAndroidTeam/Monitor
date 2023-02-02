package com.xontel.surveillancecameras.adapters;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;


import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.fragments.PagerFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private int gridCount ;
    public List<Fragment> getFragmentList() {
        return mFragmentList;
    }

    public PagerAdapter(@NonNull FragmentManager fm, int gridCount) {
        super(fm);
        this.gridCount = gridCount ;
    }


    public void updateGridCount(int count){
        gridCount = count;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        for (Fragment fragment : mFragmentList){
            if ( ((Fragment)object).getId() == fragment.getId()){
                return POSITION_UNCHANGED;
            }
        }
        return  POSITION_NONE ;
    }

    public void getListOfData(List<IpCam> dbList){
        int size = 0 ;
        if (dbList.size() % gridCount != 0){ size = 1;}
        size = size + (dbList.size()/gridCount) ;
        if (!mFragmentList.isEmpty()){mFragmentList.clear();}
        for (int i = 0 ; i< size ; i++){
            mFragmentList.add(new PagerFragment());
        }
        notifyDataSetChanged();
    }

    public void addFragment() {
        mFragmentList.add(new PagerFragment());
        notifyDataSetChanged();
    }

    public void addFragments(int size){
        for (int i=0 ; i < size  ; i ++){
            mFragmentList.add(new PagerFragment());
        }
        notifyDataSetChanged();
    }

    public void removeFragment() {
        mFragmentList.remove(mFragmentList.size()-1);
        notifyDataSetChanged();
    }

    public void removeFragments(int size){
        for (int i=0 ; i < size  ; i ++){
            mFragmentList.remove(mFragmentList.size()-1);
        }
        notifyDataSetChanged();
    }

    public int getFragmentCount(){
        return mFragmentList.size();
    }


    public void removeAllFragment(){
        mFragmentList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        PagerFragment pagerFragment = (PagerFragment) mFragmentList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("IN",position);
        pagerFragment.setArguments(bundle);
        return pagerFragment;
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

}
