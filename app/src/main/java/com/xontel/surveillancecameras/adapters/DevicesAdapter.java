package com.xontel.surveillancecameras.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.data.db.model.CamDevice;

import java.util.ArrayList;
import java.util.List;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.DeviceViewHolder> {
    public static final int NO_SELECTION = -1;
    private List<CamDevice> mDeviceList = new ArrayList<>();
    private Context mContext;
    private ClickListener mClickListener;
    private int selectedItemPosition = 0;


    public DevicesAdapter(Context context, List<CamDevice> deviceList, ClickListener clickListener) {
        mContext = context;
        mClickListener = clickListener;
        addItems(deviceList);
    }

    public int getSelectedItemPosition() {
        return selectedItemPosition;
    }

    public CamDevice getSelectedDevice() {
        try {
            return mDeviceList.get(selectedItemPosition);
        } catch (Exception e) {
            return null;
        }
    }

    public List<CamDevice> getDeviceList() {
        return mDeviceList;
    }

    @NonNull
    @Override
    public DevicesAdapter.DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DeviceViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_device, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        CamDevice data = mDeviceList.get(position);
        int textColorRes = position == selectedItemPosition ? R.color.white_color : R.color.black_color;
        int iconColorRes = position == selectedItemPosition ? R.color.white_color : R.color.accent_color;
        int bgColor = position == selectedItemPosition ? R.color.accent_color : R.color.white_color;
        holder.status.setColorFilter(ContextCompat.getColor(mContext, data.isLoggedIn() ? R.color.green_color : R.color.red_color));
        holder.channels.setText(data.getChannels()+"");
        holder.title.setText(data.getName());
        holder.ipOrUrl.setText(data.getDomain());
        holder.title.setTextColor(ContextCompat.getColor(mContext, textColorRes));
        holder.ipOrUrl.setTextColor(ContextCompat.getColor(mContext, textColorRes));
        holder.channels.setTextColor(ContextCompat.getColor(mContext, textColorRes));
        holder.separator.setBackgroundColor(ContextCompat.getColor(mContext, textColorRes));
        ((CardView)holder.itemView).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, bgColor)));
        holder.imageView.setColorFilter(ContextCompat.getColor(mContext, iconColorRes));

    }


    public void setCurrentSelectedItem(int position) {
        int oldSelectedItem = selectedItemPosition;
        selectedItemPosition = mDeviceList.isEmpty() ? NO_SELECTION : mDeviceList.size() <= position ? 0 : position;
        mClickListener.onItemClicked(getSelectedDevice());
//        if(oldSelectedItem != selectedItemPosition) {
//            notifyItemChanged(oldSelectedItem);
//            notifyItemChanged(selectedItemPosition);
//        }
        notifyDataSetChanged();
    }

    public void addItems(List<CamDevice> data) {
        mDeviceList.clear();
        mDeviceList.addAll(data);
        setCurrentSelectedItem(selectedItemPosition);

    }

    @Override
    public int getItemCount() {
        return mDeviceList.size();
    }

    public class DeviceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        TextView ipOrUrl;
        ImageView imageView;

        ImageView status ;
        TextView channels;
        View separator ;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            title = itemView.findViewById(R.id.tv_title);
            ipOrUrl = itemView.findViewById(R.id.tv_desc);
            imageView = itemView.findViewById(R.id.iv_cam);
             status = itemView.findViewById(R.id.status);
             channels = itemView.findViewById(R.id.channels);
             separator =itemView.findViewById(R.id.separator);;
        }

        @Override
        public void onClick(View view) {
            int clickPosition = getAbsoluteAdapterPosition();
            if (clickPosition != selectedItemPosition)
                setCurrentSelectedItem(clickPosition);
        }
    }

    public interface ClickListener {
        void onItemClicked(CamDevice camDevice);
    }
}
