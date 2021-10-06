package com.xontel.surveillancecameras.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
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

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.activities.AddCamActivity;
import com.xontel.surveillancecameras.activities.CamerasActivity;
import com.xontel.surveillancecameras.activities.MainActivity;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.utils.HikUtil;
import com.xontel.surveillancecameras.utils.VideoHelper;

//import org.videolan.libvlc.LibVLC;
//import org.videolan.libvlc.Media;
//import org.videolan.libvlc.MediaPlayer;
//import org.videolan.libvlc.util.VLCVideoLayout;

//import org.videolan.libvlc.Dialog;
//import org.videolan.libvlc.IVLCVout;
//import org.videolan.libvlc.LibVLC;
//import org.videolan.libvlc.Media;
//import org.videolan.libvlc.MediaPlayer;
//import org.videolan.libvlc.util.VLCVideoLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;


public class CamsAdapter extends RecyclerView.Adapter<CamsAdapter.CamsViewHolder> {
    private List<IpCam> cams;
    private Context context;
    private int gridCount;
//    private LibVLC libVLC ;
    private LifecycleObservable lifecycleObservable;
    private List<VideoHelper> videoHelpers ;


    public CamsAdapter(List<IpCam> cams, List<VideoHelper> videoHelpers, Context context, int gridCount) {
        this.cams = cams;
        this.context = context;
        this.gridCount = gridCount;
        lifecycleObservable = new LifecycleObservable();
        this.videoHelpers = videoHelpers;
        initLibvlc();
    }

    private void initLibvlc() {
        // libvlc initialization
        List<String> args = new ArrayList<String>();
        args.add("-vvv");
//        args.add("--vout=android-display");
//        args.add("--network-caching=33");
//        args.add("--file-caching=33");
//        args.add("--live-caching=33");
//        args.add("--clock-synchro=0");
//        args.add("--clock-jitter=0");
//        args.add("--h264-fps=60");
//        args.add("--avcodec-fast");
//        args.add("--avcodec-threads=1");
//        args.add("--no-audio");
//        libVLC = new LibVLC(context, (ArrayList<String>) args);
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
            holder.mSurfaceView.setVisibility(View.GONE);
            holder.placeholder.setVisibility(View.VISIBLE);
//            holder.progressDialog.setVisibility(View.INVISIBLE);
            holder.itemView.setOnClickListener(v -> {
                context.startActivity(new Intent(context, AddCamActivity.class));
            });

        } else {
            holder.mSurfaceView.setVisibility(View.VISIBLE);
            holder.placeholder.setVisibility(View.GONE);
            holder.camName.setText(ipCam.getName());
//            holder.setupVideoPlayer();
//            holder.initVlcPlayer();
            holder.initHikPlayer();
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
        private static final int PLAY_HIK_STREAM_CODE = 1001;
        private IpCam ipCam ;
        private TextView camName;
        private TextView textError;
        private ImageView placeholder;
        private VideoHelper videoHelper;
//        private ProgressBar progressDialog;
        private FrameLayout frameLayout;
//        private MediaPlayer mediaPlayer;
//        private LibVLC libVLC;
        private static final boolean USE_TEXTURE_VIEW = false;
        private static final boolean ENABLE_SUBTITLES = true;
//        private VLCVideoLayout vlcVideoLayout;
        private SurfaceView mSurfaceView;



        public CamsViewHolder(View itemView) {
            super(itemView);
            // observe oPause to pause all together
//            lifecycleObservable.addObserver((o, arg) -> {
//                int lifecycleStatus = ((LifecycleObservable) o).status;
//                if (lifecycleStatus == LifecycleObservable.ON_PAUSE) {
//                    if (videoHelper != null) {
//                        videoHelper.onStop();
//                        videoHelper.onDestroy();
//                    }
//
//
//                } else {
//                    if (cams.get(getAdapterPosition()).getUrl() != null)
//                        initVlcPlayer();
//                }
//
//            });
//            vlcVideoLayout = itemView.findViewById(R.id.video_layout);
//            progressDialog = itemView.findViewById(R.id.loading_dialog);
            mSurfaceView = itemView.findViewById(R.id.surface_view);
            frameLayout = itemView.findViewById(R.id.video_surface_frame);
            camName = itemView.findViewById(R.id.tv_cam_name);
            textError = itemView.findViewById(R.id.tv_error);
            placeholder = itemView.findViewById(R.id.iv_placeholder);
        }




//        private void initVlcPlayer() {
//            ipCam = cams.get(getAdapterPosition());
//            MediaPlayer mediaPlayer = new MediaPlayer(libVLC);
//
//
//             videoHelper = new VideoHelper(itemView, libVLC, mediaPlayer,
//                    R.id.video_surface_frame,
//                    R.id.surface_stub,
//                    R.id.subtitles_surface_stub,
//                    R.id.texture_stub);
//            videoHelper.setVIDEO_URL(ipCam.getUrl());
//            videoHelpers.add(videoHelper);
//
//            final ProgressBar loadingBar1 = itemView.findViewById(R.id.loading);
//
//
//            loadingBar1.setVisibility(View.VISIBLE);
//            mediaPlayer.setEventListener(new MediaPlayer.EventListener() {
//                float buffered = 0.0f;
//
//                @Override
//                public void onEvent(MediaPlayer.Event event) {
//                    if (event.type == MediaPlayer.Event.Buffering) {
//                        buffered = event.getBuffering();
//                    }
//                    if (buffered == 100.0) {
//                        loadingBar1.setVisibility(View.GONE);
//                        Log.d("EVENT", event.type + "");
//                    }
//
//                    if( event.type == MediaPlayer.Event.EncounteredError) {
//                        Log.d("EVENT", event.type + "");
//                        loadingBar1.setVisibility(View.GONE);
//                        textError.setVisibility(View.VISIBLE);
//                        textError.setText(R.string.error_occurred);
//                    }
//                }
//            });
//            videoHelper.onStart();
//
//        }
        private void initHikPlayer(){
                        ipCam = cams.get(getAdapterPosition());
            HikUtil hikUtil = new HikUtil();
             Handler mHandler = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    switch (msg.what) {
                        case PLAY_HIK_STREAM_CODE:
                            hikUtil.playOrStopStream();
                            break;
                        default:
                            break;
                    }
                    return false;
                }
            });
            HikUtil.initSDK();
            hikUtil.initView(mSurfaceView);
            Uri uri = Uri.parse(ipCam.getUrl());
            hikUtil.setDeviceData(uri.getHost(), uri.getPort() == -1 ? 8000 : uri.getPort() , uri.getUserInfo().split(":")[0], uri.getUserInfo().split(":")[1]);
            hikUtil.loginDevice(mHandler, PLAY_HIK_STREAM_CODE);
        }

        private void hideProgressDialog() {

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
