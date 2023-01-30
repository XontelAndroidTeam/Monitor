package com.xontel.surveillancecameras.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.activities.CamerasActivity;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.utils.CamDeviceType;

import org.videolan.libvlc.util.VLCVideoLayout;

import java.util.ArrayList;
import java.util.List;


public class GridAdapter extends BaseAdapter {
    Context context;
    List<IpCam> cams;
    LayoutInflater inflater;

    public GridAdapter(Context context, List<IpCam> cams) {
        this.context = context;
        this.cams = cams;
        inflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return cams.size();
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
        IpCam ipCam = cams.get(i);
        if (ipCam.getType() == CamDeviceType.OTHER.getValue()){
            view = inflater.inflate(R.layout.item_live_media, null);
            SurfaceView surfaceView = view.findViewById(R.id.player_view);
        }else{
            view = inflater.inflate(R.layout.item_vlc_cam, null);
            VLCVideoLayout vlcVideoLayout = view.findViewById(R.id.vlc_layout);
        }
        TextView camName = view.findViewById(R.id.tv_cam_name);

        return view;
    }



}
