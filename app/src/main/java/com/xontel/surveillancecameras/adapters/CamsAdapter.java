package com.xontel.surveillancecameras.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.activities.CamerasActivity;
import com.xontel.surveillancecameras.activities.HomeActivity;
import com.xontel.surveillancecameras.base.BaseViewHolder;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.hikvision.HikUtil;

import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCVideoLayout;

import java.util.ArrayList;
import java.util.List;


public class CamsAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private List<IpCam> ipCams;
    private Context context;
    private int gridCount;
    private static final int ITEM_CAM = 0;
    private static final int ITEM_ADD_CAM = 1;


    public CamsAdapter(List<IpCam> ipCams, int gridCount, Context context) {
        this.ipCams = ipCams;
        this.context = context;
        this.gridCount = gridCount;
    }


    public void setGridCount(int gridCount) {
        this.gridCount = gridCount;
    }

    public void addItems(List<IpCam> cams) {
        ipCams.clear();
        ipCams.addAll(cams);
        notifyDataSetChanged();
    }


    public void addItem(IpCam cam) {
        ipCams.add(cam);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case ITEM_CAM:
                view = LayoutInflater.from(context).inflate(R.layout.item_live_media, parent, false);
                return new CamsViewHolder(view);

            case ITEM_ADD_CAM:
            default:
                view = LayoutInflater.from(context).inflate(R.layout.item_add_cam, parent, false);
                return new AddCamViewHolder(view);

        }

    }



    @Override
    public int getItemViewType(int position) {
        return position < ipCams.size() ? ITEM_CAM : ITEM_ADD_CAM;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        Log.e("TAGGG", "position : " + position
                + " code : " + holder.hashCode() + (position == 15 ? "\n ============================ \n" : ""));
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return gridCount;
    }


    public class CamsViewHolder extends BaseViewHolder implements View.OnClickListener {
        private IpCam ipCam;
        private TextView camName;
        private VLCVideoLayout vlcVideoLayout;
        private MediaPlayer mediaPlayer;
        private TextureView textureView;
        private boolean isBound = false;


        public CamsViewHolder(View itemView) {
            super(itemView);
            vlcVideoLayout = itemView.findViewById(R.id.vlc_layout);
            camName = itemView.findViewById(R.id.tv_cam_name);
        }

        @Override
        public void onBind(int position) {
            super.onBind(position);
            ipCam = ipCams.get(position);
            // camName.setText(ipCam.getName());
//            initVlcPlayer();
            itemView.setOnClickListener(this);
            isBound = true;
        }

        @Override
        protected void clear() {

        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, CamerasActivity.class);
            ArrayList<IpCam> cams = new ArrayList<>();
            cams.add(ipCams.get(getAdapterPosition()));
            context.startActivity(intent);
        }
    }

    public class AddCamViewHolder extends BaseViewHolder implements View.OnClickListener {
        public AddCamViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onBind(int position) {
            super.onBind(position);
        }

        @Override
        protected void clear() {
        }

        @Override
        public void onClick(View view) {
            ((HomeActivity) context).addNewCam();
        }
    }



}
