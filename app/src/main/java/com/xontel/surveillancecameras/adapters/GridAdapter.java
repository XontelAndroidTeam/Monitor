package com.xontel.surveillancecameras.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.activities.CamerasActivity;
import com.xontel.surveillancecameras.data.db.model.IpCam;

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
        view = inflater.inflate(R.layout.item_cam, null); // inflate the layout
//        MjpegView mjpegView = view.findViewById(R.id.mjpeg_view);
        TextView camName = view.findViewById(R.id.tv_cam_name);
        TextView textError = view.findViewById(R.id.tv_error);
        ImageView placeholder = view.findViewById(R.id.player_surface_frame);

        IpCam ipCam = cams.get(i);
        if(ipCam.getUrl() == null){ // not set yet
            camName.setText("");
//            mjpegView.setVisibility(View.GONE);
            placeholder.setVisibility(View.VISIBLE);
            placeholder.setOnClickListener(v->{
                //TODO open add activity
            });

        }else {
//            mjpegView.setVisibility(View.VISIBLE);
            placeholder.setVisibility(View.GONE);
            camName.setText(ipCam.getName());
//            setupVideoPlayer(mjpegView, i);
            // TODO error text

            view.setOnClickListener(v -> {
                Log.e("adapter", "onBindViewHolder: ");
                Intent intent = new Intent(context, CamerasActivity.class);
                ArrayList<IpCam> cams = new ArrayList<>();
                cams.add(this.cams.get(i));
                intent.putParcelableArrayListExtra(CamerasActivity.KEY_CAMERAS, cams);
                context.startActivity(intent);
            });
        }
        return view;
    }


    private void setupVideoPlayer(/*MjpegView mjpegView* ,*/ int position){
//        mjpegView.setAdjustHeight(true);
//        mjpegView.setAdjustWidth(true);
//        mjpegView.setMode(MjpegView.MODE_FIT_WIDTH);
//        mjpegView.setMsecWaitAfterReadImageError(1000);
//        mjpegView.setUrl(cams.get(position).getUrl());
//        mjpegView.setRecycleBitmap(true);
//        mjpegView.startStream();


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

}
