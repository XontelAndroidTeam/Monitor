package com.xontel.surveillancecameras.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.data.db.dao.DevicesDao;
import com.xontel.surveillancecameras.data.db.model.CamDevice;

import java.util.List;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.DeviceViewHolder> {
    private List<CamDevice> mDeviceList ;
    private Context mContext ;
    private ClickListener mClickListener;


    public DevicesAdapter(Context context, List<CamDevice> deviceList, ClickListener clickListener) {
        mContext = context;
        mDeviceList = deviceList;
        mClickListener = clickListener;
    }

    @NonNull
    @Override
    public DevicesAdapter.DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DeviceViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_device, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mDeviceList.size();
    }
    public class DeviceViewHolder extends RecyclerView.ViewHolder{

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public interface ClickListener{
        void onItemClicked(int position);
    }
}
