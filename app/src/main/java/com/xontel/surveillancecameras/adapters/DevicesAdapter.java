package com.xontel.surveillancecameras.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.data.db.dao.DevicesDao;
import com.xontel.surveillancecameras.data.db.model.CamDevice;
import com.xontel.surveillancecameras.hikvision.HIKDevice;

import java.util.List;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.DeviceViewHolder> {
    private List<HIKDevice> mDeviceList ;
    private Context mContext ;
    private ClickListener mClickListener;
    private int selectedItemPosition = 0;


    public DevicesAdapter(Context context, List<HIKDevice> deviceList, ClickListener clickListener) {
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
       HIKDevice data = mDeviceList.get(position);
        RelativeLayout rowData = holder.itemView.findViewById(R.id.rowData);
        TextView title = holder.itemView.findViewById(R.id.tv_title);
        TextView ipOrUrl = holder.itemView.findViewById(R.id.tv_desc);
        ImageView imageView = holder.itemView.findViewById(R.id.iv_cam);
       if (position == selectedItemPosition){
           title.setTextColor(ContextCompat.getColor(mContext,R.color.white_color));
           ipOrUrl.setTextColor(ContextCompat.getColor(mContext,R.color.white_color));
           rowData.setBackgroundColor(ContextCompat.getColor(mContext,R.color.accent_color));
           imageView.setColorFilter(ContextCompat.getColor(mContext,R.color.white_color));
       }else{
           rowData.setBackgroundColor(ContextCompat.getColor(mContext,R.color.white_color));
           imageView.setColorFilter(ContextCompat.getColor(mContext,R.color.accent_color));
           title.setTextColor(ContextCompat.getColor(mContext,R.color.black_color));
           ipOrUrl.setTextColor(ContextCompat.getColor(mContext,R.color.black_color));
       }
       title.setText(data.getName());
       ipOrUrl.setText( data.getIpAddress().isEmpty() ? data.getUrl() : data.getIpAddress()  );

       rowData.setOnClickListener(view -> {
           selectedItemPosition = position ;
           mClickListener.onItemClicked(data,position);
           notifyDataSetChanged();
       });
    }

    public void setList(List<HIKDevice> data){
        mDeviceList = data;
        notifyDataSetChanged();
    }

    public void setCurrentSelectedItem(int position){
        selectedItemPosition = position;
        notifyDataSetChanged();
    }

    public void clearList(){
        mDeviceList.clear();
        selectedItemPosition = 0;
        notifyDataSetChanged();
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
        void onItemClicked(HIKDevice data,int position );
    }
}
