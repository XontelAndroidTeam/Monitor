package com.xontel.surveillancecameras.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.github.niqdev.mjpeg.DisplayMode;
import com.github.niqdev.mjpeg.Mjpeg;
import com.github.niqdev.mjpeg.MjpegInputStream;
//import com.github.niqdev.mjpeg.MjpegSurfaceView;
import com.github.niqdev.mjpeg.MjpegView;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.activities.AddCamActivity;
import com.xontel.surveillancecameras.activities.CamerasActivity;
import com.xontel.surveillancecameras.data.db.model.IpCam;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCVideoLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class CamsAdapter extends RecyclerView.Adapter<CamsAdapter.CamsViewHolder> {
    private List<IpCam> cams;
    private Context context;
    private int gridCount;
    private List<Observable<MjpegInputStream>> observables ;

    public CamsAdapter(List<IpCam> cams, Context context, List<Observable<MjpegInputStream>> observable, int gridCount /*, Callback callback*/) {
        this.cams = cams;
        this.context = context;
        this.gridCount = gridCount;
        this.observables = observable;
    }

    public List<IpCam> getCams() {
        return cams;
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
    public CamsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cam, parent, false);
        GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        lp.height = parent.getMeasuredHeight() / (int) Math.sqrt(gridCount);
        view.setLayoutParams(lp);
        return new CamsViewHolder(view);
    }


    @Override
    public void onBindViewHolder(CamsViewHolder holder, int position) {
        IpCam ipCam = cams.get(position);
        if (ipCam.getUrl() == null) { // not set yet
            holder.camName.setText("");
            ((View) holder.vlcVideoLayout).setVisibility(View.GONE);
            holder.placeholder.setVisibility(View.VISIBLE);
            holder.itemView.setOnClickListener(v -> {
                context.startActivity(new Intent(context, AddCamActivity.class));
            });

        } else {
            ((View) holder.vlcVideoLayout).setVisibility(View.VISIBLE);
            holder.placeholder.setVisibility(View.GONE);
            holder.camName.setText(ipCam.getName());
//            holder.setupVideoPlayer();
            holder.initVlcPlayer();
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

    public void pauseAll() {

    }

    public void resumeAll() {

    }




    @Override
    public void onViewDetachedFromWindow(@NonNull CamsViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        Log.e("tag", "detached");
    }

    @Override
    public int getItemCount() {
        return cams.size();
    }

    public class CamsViewHolder extends RecyclerView.ViewHolder {
        private MediaPlayer mediaPlayer;
        private LibVLC libVLC;
        private static final boolean USE_TEXTURE_VIEW = false;
        private static final boolean ENABLE_SUBTITLES = true;
        private VLCVideoLayout vlcVideoLayout ;
        private TextView camName;
        private TextView textError;
        private ImageView placeholder;

        public CamsViewHolder(View itemView) {
            super(itemView);
            vlcVideoLayout = itemView.findViewById(R.id.video_layout);
//            mjpegView = itemView.findViewById(R.id.mjpeg_view);
            camName = itemView.findViewById(R.id.tv_cam_name);
            textError = itemView.findViewById(R.id.tv_error);
            placeholder = itemView.findViewById(R.id.iv_placeholder);
        }

        private void initVlcPlayer() {

            List<String> args = new ArrayList<String>();
            args.add("--vout=android-display");
            args.add("-vvv");
            libVLC = new LibVLC(context, args);
            mediaPlayer = new MediaPlayer(libVLC);
            mediaPlayer.attachViews(vlcVideoLayout, null, ENABLE_SUBTITLES, USE_TEXTURE_VIEW);

            final Media media = new Media(libVLC, Uri.parse(cams.get(getAdapterPosition()).getUrl()));
            mediaPlayer.setMedia(media);
            media.addOption(":fullscreen");
            media.release();

            mediaPlayer.play();


        }


        private void setupVideoPlayer() {
//        mjpegView.setAdjustHeight(true);
//        mjpegView.setAdjustWidth(true);
//        mjpegView.setMode(MjpegView.MODE_FIT_WIDTH);
//        mjpegView.setMsecWaitAfterReadImageError(1000);
//        mjpegView.setUrl(ipCam.getUrl());
//        mjpegView.setRecycleBitmap(true);
//        mjpegView.startStream();


//            int TIMEOUT = 5; //seconds
//            try {
//                Mjpeg.newInstance()
//                        .open(cams.get(getAdapterPosition()).getUrl(), TIMEOUT)
//                        .subscribe(inputStream -> {
//                            mjpegView.setSource(inputStream);
//                            mjpegView.setDisplayMode(DisplayMode.FULLSCREEN);
//                            mjpegView.showFps(true);
//                        }, throwable -> {
//                            textError.setVisibility(View.VISIBLE);
//                            Log.e(getClass().getSimpleName(), "mjpeg error", throwable);
////                    errorTextView.setText(throwable.getMessage());
////                    Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
//                        });
//            }catch (Exception e){
//                textError.setText(e.getMessage());
//            }

//            mjpegView.loadUrl(cams.get(getAdapterPosition()).getUrl());
        }


    }



    public interface Callback {
        void onCamClicked(int position);
    }
}
