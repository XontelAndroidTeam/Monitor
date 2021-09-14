package com.xontel.surveillancecameras.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.github.niqdev.mjpeg.DisplayMode;
import com.github.niqdev.mjpeg.Mjpeg;
import com.github.niqdev.mjpeg.MjpegSurfaceView;
import com.longdo.mjpegviewer.MjpegView;
import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.activities.AddCamActivity;
import com.xontel.surveillancecameras.activities.CamerasActivity;
import com.xontel.surveillancecameras.data.db.model.IpCam;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

public class CamsAdapter extends RecyclerView.Adapter<CamsAdapter.CamsViewHolder> {
    private List<IpCam> cams ;
    private Context context ;
    private int gridCount ;

    public CamsAdapter(List<IpCam> cams, Context context, int gridCount /*, Callback callback*/) {
        this.cams = cams;
        this.context = context;
        this.gridCount = gridCount;
//        this.callback = callback;
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
    }

    @Override
    public CamsViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(context).inflate(R.layout.item_cam, parent, false);
        GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        lp.height = parent.getMeasuredHeight() / (int)Math.sqrt(gridCount);
        view.setLayoutParams(lp);
        return new CamsViewHolder(view);
    }

    @Override
    public void onBindViewHolder( CamsViewHolder holder, int position) {
        IpCam ipCam = cams.get(position);
        if(ipCam.getUrl() == null){ // not set yet
            holder.camName.setText("");
            holder.mjpegView.setVisibility(View.GONE);
            holder.placeholder.setVisibility(View.VISIBLE);
            holder.itemView.setOnClickListener(v->{
                context.startActivity(new Intent(context, AddCamActivity.class));
            });

        }else {
            holder.mjpegView.setVisibility(View.VISIBLE);
            holder.placeholder.setVisibility(View.GONE);
            holder.camName.setText(ipCam.getName());
            setupVideoPlayer( holder.mjpegView, ipCam);
            // TODO error text

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, CamerasActivity.class);
                ArrayList<IpCam> cams = new ArrayList<>();
                cams.add(ipCam);
                intent.putParcelableArrayListExtra(CamerasActivity.KEY_CAMERAS, cams);
                context.startActivity(intent);
            });
        }


    }
    private void setupVideoPlayer(MjpegView mjpegView , IpCam ipCam){
        mjpegView.setAdjustHeight(true);
        mjpegView.setAdjustWidth(true);
        mjpegView.setMode(MjpegView.MODE_FIT_WIDTH);
        mjpegView.setMsecWaitAfterReadImageError(1000);
        mjpegView.setUrl(ipCam.getUrl());
        mjpegView.setRecycleBitmap(true);
        mjpegView.startStream();


        //        int TIMEOUT = 5; //seconds
//
//        Mjpeg.newInstance()
//                .open(cams.get(i).getUrl(), TIMEOUT)
//                .doOnError(new Action1<Throwable>() {
//                    @Override
//                    public void call(Throwable throwable) {
//                        textError.setText(throwable.getMessage());
//                    }
//                })
//                .subscribe(inputStream -> {
//                    mjpegView.setSource(inputStream);
//                    mjpegView.setDisplayMode(DisplayMode.FULLSCREEN);
//                    mjpegView.showFps(true);
//                });
    }


    @Override
    public int getItemCount() {
        return cams.size();
    }

    public class CamsViewHolder extends RecyclerView.ViewHolder{
        private MjpegView mjpegView;
        private TextView camName ;
        private TextView textError ;
        private ImageView placeholder;

        public CamsViewHolder(View itemView) {
            super(itemView);
            mjpegView = itemView.findViewById(R.id.mjpeg_view);
            camName = itemView.findViewById(R.id.tv_cam_name);
            textError = itemView.findViewById(R.id.tv_error);
            placeholder = itemView.findViewById(R.id.iv_placeholder);
        }
    }

    public interface Callback{
        void onCamClicked(int position);
    }
}
