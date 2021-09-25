package com.xontel.surveillancecameras.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.activities.AddCamActivity;
import com.xontel.surveillancecameras.activities.CamerasActivity;
import com.xontel.surveillancecameras.data.db.model.IpCam;

//import org.videolan.libvlc.LibVLC;
//import org.videolan.libvlc.Media;
//import org.videolan.libvlc.MediaPlayer;
//import org.videolan.libvlc.util.VLCVideoLayout;

import org.videolan.libvlc.Dialog;
import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCVideoLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class CamsAdapter extends RecyclerView.Adapter<CamsAdapter.CamsViewHolder> {
    private List<IpCam> cams;
    private Context context;
    private int gridCount;
    private LifecycleObservable lifecycleObservable;


    public CamsAdapter(List<IpCam> cams, Context context, int gridCount) {
        this.cams = cams;
        this.context = context;
        this.gridCount = gridCount;
        lifecycleObservable = new LifecycleObservable();
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
        Log.e("taggo", lp.height+"");
        view.setLayoutParams(lp);
        Log.e("taggo", view.getWidth()+"");
        return new CamsViewHolder(view);
    }


    @Override
    public void onBindViewHolder(CamsViewHolder holder, int position) {
        IpCam ipCam = cams.get(position);
        if (ipCam.getUrl() == null) { // not set yet
            holder.camName.setText("");
            ((View) holder.mSurfaceView).setVisibility(View.GONE);
            holder.placeholder.setVisibility(View.VISIBLE);
            holder.progressDialog.setVisibility(View.INVISIBLE);
            holder.itemView.setOnClickListener(v -> {
                context.startActivity(new Intent(context, AddCamActivity.class));
            });

        } else {
            ((View) holder.mSurfaceView).setVisibility(View.VISIBLE);
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
        lifecycleObservable.lifecycleStatus(LifecycleObservable.ON_PAUSE);
    }

    public void resumeAll() {
        lifecycleObservable.lifecycleStatus(LifecycleObservable.ON_RESUME);
    }


    @Override
    public void onViewDetachedFromWindow(@NonNull CamsViewHolder holder) {
        super.onViewDetachedFromWindow(holder);



    }

    @Override
    public void onViewAttachedToWindow(@NonNull CamsViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public int getItemCount() {
        return cams.size();
    }

    public class CamsViewHolder extends RecyclerView.ViewHolder {
        private IpCam ipCam ;
        private TextView camName;
        private TextView textError;
        private ImageView placeholder;
        private ProgressBar progressDialog;
//        private MediaPlayer mediaPlayer;
//        private LibVLC libVLC;
        private static final boolean USE_TEXTURE_VIEW = false;
        private static final boolean ENABLE_SUBTITLES = true;
        private VLCVideoLayout vlcVideoLayout;
        private SurfaceView mSurfaceView;



        public CamsViewHolder(View itemView) {
            super(itemView);
            // observe oPause to pause all together
//            lifecycleObservable.addObserver((o, arg) -> {
//                int lifecycleStatus = ((LifecycleObservable) o).status;
//                if (lifecycleStatus == LifecycleObservable.ON_PAUSE) {
//                    if (ipCam.getMediaPlayer() != null) {
////                        pausePlayer();
//                        Log.e("taggo", "view detached" + getAdapterPosition());
////                        ipCam.getMediaPlayer().stop();
////                        ipCam.getMediaPlayer().detachViews();
//                    }
//
//
////                } else {
////                    if (cams.get(getAdapterPosition()).getUrl() != null)
////                        initVlcPlayer();
////                }
//                }
//            });
//            vlcVideoLayout = itemView.findViewById(R.id.video_layout);
            progressDialog = itemView.findViewById(R.id.loading_dialog);
            mSurfaceView = itemView.findViewById(R.id.surface_view);
            camName = itemView.findViewById(R.id.tv_cam_name);
            textError = itemView.findViewById(R.id.tv_error);
            placeholder = itemView.findViewById(R.id.iv_placeholder);
        }

        private void pausePlayer() {
//            progressDialog.setVisibility(View.VISIBLE);
//            mediaPlayer.stop();

        }
        void showProgressDialog(){
            progressDialog.setVisibility(View.VISIBLE);
        }
        void hideProgressDialog(){
            progressDialog.setVisibility(View.GONE);
        }

        private void initVlcPlayer() {
            ipCam = cams.get(getAdapterPosition());
            // libvlc initialization
//            List<String> args = new ArrayList<String>();
//            args.add("--vout=android-display");
//            args.add("-vvv");
//            libVLC = new LibVLC(context, args);
//
//             media player setup
//            mediaPlayer = new MediaPlayer(libVLC);
//            final Media media = new Media(ipCam.getMediaPlayer().getLibVLC(), Uri.parse(ipCam.getUrl()));
//            cams.get(getAdapterPosition()).getMediaPlayer().setMedia(media);

            ipCam.getMediaPlayer().setEventListener(new MediaPlayer.EventListener() {
                @Override
                public void onEvent(MediaPlayer.Event event) {
                    switch (event.type) {
                        case MediaPlayer.Event.EncounteredError:
                            hideProgressDialog();
                            textError.setVisibility(View.VISIBLE);
                            textError.setText(R.string.error_occurred);
                            break;
                        case MediaPlayer.Event.Playing:
                            hideProgressDialog();
                            break;
                        case MediaPlayer.Event.Buffering:
                            if(event.getBuffering()>= 100){
                                hideProgressDialog();
                            }
                            break;
                    }
                }
            });

//            media.addOption(":fullscreen");
//            media.release();
//            ViewGroup.LayoutParams videoParams = vlcVideoLayout.getLayoutParams();
//            ipCam.getMediaPlayer().getVLCVout().setWindowSize(videoParams.width, videoParams.height);
//            ipCam.getMediaPlayer().attachViews(vlcVideoLayout, null, false, false);
            ipCam.getMediaPlayer().getVLCVout().setVideoView(mSurfaceView);

            ipCam.getMediaPlayer().getVLCVout().attachViews();
            ipCam.getMediaPlayer().getVLCVout().addCallback(new IVLCVout.Callback() {
                @Override
                public void onSurfacesCreated(IVLCVout vlcVout) {
                    vlcVout.setWindowSize(mSurfaceView.getMeasuredWidth(), mSurfaceView.getMeasuredHeight());
                    ipCam.getMediaPlayer().setAspectRatio(mSurfaceView.getMeasuredWidth()+":"+mSurfaceView.getMeasuredHeight());
                }

                @Override
                public void onSurfacesDestroyed(IVLCVout vlcVout) {

                }
            });
            ipCam.getMediaPlayer().play();


        }


    }

    class LifecycleObservable extends java.util.Observable {
        public static final int ON_PAUSE = 1;
        public static final int ON_RESUME = 0;
        public int status = ON_RESUME;

        void lifecycleStatus(int status) {
            this.status = status;
            setChanged();
            notifyObservers();
        }
    }


}
