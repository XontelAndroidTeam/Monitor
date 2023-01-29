package com.xontel.surveillancecameras.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
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
import com.xontel.surveillancecameras.dahua.DahuaSinglePlayer;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.hikvision.HIKSinglePlayer;
import com.xontel.surveillancecameras.hikvision.HikUtil;
import com.xontel.surveillancecameras.utils.CamDeviceType;
import com.xontel.surveillancecameras.vlc.VlcSinglePlayer;

import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCVideoLayout;

import java.util.ArrayList;
import java.util.List;


public class CamsAdapter extends RecyclerView.Adapter<BaseViewHolder>  {
    public static final String KEY_CAMERAS = "cameras";
    private List<IpCam> ipCams;
    private Context context;
    private int gridCount;
    private static final int ITEM_CAM = 0;
    private static final int ITEM_ADD_CAM = 1;
    private static final int ITEM_VLC = 2;


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

            case ITEM_VLC:
                view = LayoutInflater.from(context).inflate(R.layout.item_vlc_cam, parent, false);
                return new CamsVlcViewHolder(view);

            case ITEM_ADD_CAM:
            default:
                view = LayoutInflater.from(context).inflate(R.layout.item_add_cam, parent, false);
                return new AddCamViewHolder(view);

        }

    }



    @Override
    public int getItemViewType(int position) {
        if (position <= ipCams.size() - 1){
            if (ipCams.get(position).getType() == CamDeviceType.OTHER.getValue()){
                return ITEM_VLC;
            }else{
                return ITEM_CAM;
            }
        }else{
            return ITEM_ADD_CAM;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return gridCount;
    }



    public class CamsViewHolder extends BaseViewHolder implements View.OnClickListener {
        private IpCam ipCam;
        private TextView camName;
        private SurfaceView surfaceView;
        private MediaPlayer mediaPlayer;
        private TextureView textureView;
        private boolean isBound = false;


        public CamsViewHolder(View itemView) {
            super(itemView);
            surfaceView = itemView.findViewById(R.id.player_view);
            camName = itemView.findViewById(R.id.tv_cam_name);
        }

        @Override
        public void onBind(int position) {
            super.onBind(position);
            ipCam = ipCams.get(position);
            if (ipCam.getType() == CamDeviceType.HIKVISION.getValue()){
                HIKSinglePlayer singlePlayer =  new HIKSinglePlayer(ipCam.getChannel(),ipCam.getLoginId(),ipCam.getType(),context);
                singlePlayer.initView(surfaceView);
            }else if(ipCam.getType() == CamDeviceType.DAHUA.getValue()){
                DahuaSinglePlayer singlePlayer =  new DahuaSinglePlayer(ipCam.getChannel(),ipCam.getLoginId(),ipCam.getType(),context);
                singlePlayer.initView(surfaceView);
            }
            itemView.setOnClickListener(this);
        }

        @Override
        protected void clear() {
          //  Log.i("TATZ", "Adapter_cleared: ");
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, CamerasActivity.class);
            intent.putExtra(KEY_CAMERAS, ipCams.get(getCurrentPosition()));
            context.startActivity(intent);
        }



    }



    public class CamsVlcViewHolder extends BaseViewHolder implements View.OnClickListener {
        private IpCam ipCam;
        private TextView camName;
        private VLCVideoLayout vlcVideoLayout;

        public CamsVlcViewHolder(View itemView) {
            super(itemView);
            vlcVideoLayout = itemView.findViewById(R.id.vlc_layout);
            camName = itemView.findViewById(R.id.tv_cam_name);
        }

        @Override
        public void onBind(int position) {
            super.onBind(position);
            ipCam = ipCams.get(position);
            if (ipCam.getType() == CamDeviceType.OTHER.getValue()){
                VlcSinglePlayer vlcSinglePlayer = new VlcSinglePlayer(context);
                vlcSinglePlayer.initVlcPlayer(ipCam.getUrlOrIpAddress(),vlcVideoLayout);
            }
            itemView.setOnClickListener(this);
        }

        @Override
        protected void clear() {
         //   Log.i("TATZ", "Adapter_cleared: ");
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, CamerasActivity.class);
            intent.putExtra(KEY_CAMERAS, ipCams.get(getCurrentPosition()));
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
