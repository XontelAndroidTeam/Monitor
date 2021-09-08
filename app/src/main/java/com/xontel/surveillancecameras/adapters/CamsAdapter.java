package com.xontel.surveillancecameras.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.github.niqdev.mjpeg.DisplayMode;
import com.github.niqdev.mjpeg.Mjpeg;
import com.github.niqdev.mjpeg.MjpegSurfaceView;
import com.longdo.mjpegviewer.MjpegView;
import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.activities.CamerasActivity;
import com.xontel.surveillancecameras.data.db.model.IpCam;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

public class CamsAdapter extends RecyclerView.Adapter<CamsAdapter.CamsViewHolder> {
    private List<IpCam> cams ;
    private Context context ;
    private Callback callback ;

    public CamsAdapter(List<IpCam> cams, Context context, Callback callback) {
        this.cams = cams;
        this.context = context;
        this.callback = callback;
    }

    public List<IpCam> getCams() {
        return cams;
    }

    public void addItems(List<IpCam> cams){
        this.cams.clear();
        this.cams.addAll(cams);
        notifyDataSetChanged();
    }

    public void addItem(IpCam cam){
        cams.add(cam);
        notifyDataSetChanged();
    }

    @Override
    public CamsViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        return new CamsViewHolder(LayoutInflater.from(context).inflate(R.layout.item_cam, parent, false));
    }

    @Override
    public void onBindViewHolder( CamsViewHolder holder, int position) {
        int TIMEOUT = 5; //seconds

//        Mjpeg.newInstance()
//                .open(cams.get(position).getUrl(), TIMEOUT)
//                .doOnError(new Action1<Throwable>() {
//                    @Override
//                    public void call(Throwable throwable) {
//                        holder.textError.setText(throwable.getMessage());
//                    }
//                })
//                .subscribe(inputStream -> {
//                    holder.mjpegView.setSource(inputStream);
//                    holder.mjpegView.setDisplayMode(DisplayMode.FULLSCREEN);
//                    holder.mjpegView.showFps(true);
//                });
        holder.camName.setText(cams.get(position).getName());
        holder.mjpegView.setAdjustHeight(true);
        holder.mjpegView.setAdjustWidth(true);
        holder.mjpegView.setMode(MjpegView.MODE_FIT_WIDTH);
        holder.mjpegView.setMsecWaitAfterReadImageError(1000);
        holder.mjpegView.setUrl(cams.get(position).getUrl());
        holder.mjpegView.setRecycleBitmap(true);
        holder.mjpegView.startStream();
        holder.itemView.setOnClickListener(v->{
            Log.e("adapter", "onBindViewHolder: ");
            Intent intent = new Intent(context, CamerasActivity.class);
            ArrayList<IpCam> cams = new ArrayList<>();
            cams.add(this.cams.get(position));
            intent.putParcelableArrayListExtra(CamerasActivity.KEY_CAMERAS, cams);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return cams.size();
    }

    public class CamsViewHolder extends RecyclerView.ViewHolder{
        private MjpegView mjpegView;
        private TextView camName ;
        private TextView textError ;

        public CamsViewHolder(View itemView) {
            super(itemView);
            mjpegView = itemView.findViewById(R.id.mjpeg_view);
            camName = itemView.findViewById(R.id.tv_cam_name);
            textError = itemView.findViewById(R.id.tv_error);
        }
    }

    public interface Callback{
        void onCamClicked(int position);
    }
}
