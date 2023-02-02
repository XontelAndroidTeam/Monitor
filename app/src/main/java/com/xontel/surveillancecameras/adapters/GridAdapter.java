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
import com.xontel.surveillancecameras.hikvision.HikCamView;
import com.xontel.surveillancecameras.utils.CamDeviceType;
import com.xontel.surveillancecameras.vlc.VlcCamView;

import org.videolan.libvlc.util.VLCVideoLayout;

import java.util.ArrayList;
import java.util.List;


public class GridAdapter extends BaseAdapter {
    List<View> camViews = new ArrayList<>();
    Context context;
    List<IpCam> cams = new ArrayList<>();
    private int gridCount;


    public GridAdapter(Context context, int gridCount, List<IpCam> cams) {
        this.context = context;
        this.cams.addAll(cams);
        this.gridCount = gridCount;
    }

    public void setGridCount(int gridCount) {
        this.gridCount = gridCount;
    }

    public void addItems(List<IpCam> cams) {
        this.cams.clear();
        this.cams.addAll(cams);
        notifyDataSetChanged();
    }

    public void addItem(IpCam cam) {
        cams.add(cam);
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return cams.get(position).getType();
    }

    @Override
    public Object getItem(int i) {
        return camViews.get(i);
    }

    @Override
    public long getItemId(int i) {
        return camViews.get(i).getId();
    }


    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        IpCam currentCam = cams.get(position);
        if (camViews.size() <= position)
            return createCamView(currentCam.getType(), position);
        return camViews.get(position);

    }

    private View createCamView(int type, int position) {
        if (CamDeviceType.HIKVISION.getValue() == type) {
          //  HikCamView hikCamView = new HikCamView(context, cams.get(position));
          //  camViews.add(hikCamView);
          //  return hikCamView;
        } else if (CamDeviceType.DAHUA.getValue() == type) {
//                    HikCamView hikCamView =new HikCamView(context, cams.get(i));
//                    camViews.add(hikCamView);
//                    return hikCamView;
        } else if (CamDeviceType.OTHER.getValue() == type) {
        //    VlcCamView vlcCamView = new VlcCamView(context, cams.get(position));
            //return vlcCamView;
        }

        return null;
    }


}
