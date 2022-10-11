package com.xontel.surveillancecameras.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xontel.surveillancecameras.R;

public class SideMenuAdapter extends BaseAdapter {
    private int[] labelsIds = {
            R.string.monitor,
            R.string.devices,
            R.string.saved_media,
            R.string.settings};

    private int[] iconsIds = {
            R.drawable.ic_baseline_video_cam_24,
            R.drawable.ic_baseline_devices_24,
            R.drawable.ic_saved_media,
            R.drawable.ic_baseline_settings_24};

    private Context mContext;
    private ClickCallback mClickCallback;
    private int selectedItemIndex = 0;

    public SideMenuAdapter(Context context, ClickCallback clickCallback) {
        mContext = context;
        this.mClickCallback = clickCallback;
    }


//    public void setSelectedItemIndex(int selectedItemIndex) {
//        this.selectedItemIndex = selectedItemIndex;
//        mClickCallback.onItemClicked(labelsIds[i]);
//    }


    @Override
    public int getCount() {
        return 4;
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
            view = LayoutInflater.from(mContext).inflate(R.layout.item_side_menu, viewGroup, false);
        }
        TextView label = view.findViewById(R.id .tv_label);
        ImageView icon = view.findViewById(R.id.iv_icon);
        label.setText(labelsIds[i]);
        icon.setImageDrawable(mContext.getResources().getDrawable(iconsIds[i]));
        if (i == selectedItemIndex) {
            view.setBackgroundColor(mContext.getResources().getColor(R.color.primary_color));
            icon.setImageTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.white)));
            label.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            view.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
            icon.setImageTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.primary_color)));
            label.setTextColor(mContext.getResources().getColor(R.color.black));
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickCallback.onItemClicked(labelsIds[i]);
                selectedItemIndex = i;
                notifyDataSetChanged();
            }
        });
        return view;
    }

    public interface ClickCallback {
        void onItemClicked(int labelsId);
    }
}
