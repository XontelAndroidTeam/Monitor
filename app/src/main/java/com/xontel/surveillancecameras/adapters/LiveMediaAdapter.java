package com.xontel.surveillancecameras.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.xontel.surveillancecameras.R;

import java.util.List;

public class LiveMediaAdapter<IpCam> extends BaseAdapter {
    private List<IpCam> mItemsList ;
    private Context mContext ;
    private ClickCallback mClickCallback;

    public LiveMediaAdapter(List<IpCam> dataModels, Context context, ClickCallback clickCallback) {
        mItemsList = dataModels;
        mContext = context;
        this.mClickCallback = clickCallback ;
    }


    public void addItems(List<IpCam> list){
        mItemsList.clear();
        mItemsList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mItemsList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_live_media, viewGroup, false);
        }

        return view;
    }


    public interface ClickCallback{
//        void onItemClicked(IpCam item);
    }
}
