package com.xontel.surveillancecameras.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.storage.StorageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.activities.CamerasActivity;
import com.xontel.surveillancecameras.activities.MainActivity;
import com.xontel.surveillancecameras.base.BaseViewHolder;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.fragments.GridFragment;
import com.xontel.surveillancecameras.utils.StorageHelper;
import com.xontel.surveillancecameras.utils.VideoHelper;

import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.LoadingDots;
import org.videolan.libvlc.util.VLCVideoLayout;

import java.util.ArrayList;
import java.util.List;


public class CamsAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private List<IpCam> ipCams;
    private Context context;
    private List<MediaPlayer> mediaPlayers;
    private int gridCount ;
    private static final int ITEM_CAM = 0;
    private static final int ITEM_ADD_CAM = 1;

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);

    }

    @Override
    public void onViewAttachedToWindow(@NonNull BaseViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if(holder instanceof CamsViewHolder){
            ((CamsViewHolder)holder).initVlcPlayer();
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull BaseViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if(holder instanceof CamsViewHolder){
            ((CamsViewHolder)holder).mediaPlayer.stop();
            ((CamsViewHolder)holder).mediaPlayer.detachViews();
            ((CamsViewHolder)holder).mediaPlayer.release();
        }
    }


    public CamsAdapter(List<IpCam> ipCams, List<MediaPlayer> mediaPlayers, int gridCount , Context context) {
        this.ipCams = ipCams;
        this.context = context;
        this.gridCount = gridCount ;
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
                view = LayoutInflater.from(context).inflate(R.layout.item_cam, parent, false);
                GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
                lp.height = (parent.getMeasuredHeight() /  (int) Math.sqrt(gridCount)) - 10;
                view.setLayoutParams(lp);
                return new CamsViewHolder(view);

            case ITEM_ADD_CAM:
            default:
                view = LayoutInflater.from(context).inflate(R.layout.item_add_cam, parent, false);
                GridLayoutManager.LayoutParams lp2 = (GridLayoutManager.LayoutParams) view.getLayoutParams();
                lp2.height = (parent.getMeasuredHeight() / (int) Math.sqrt(gridCount)) - 10;
                view.setLayoutParams(lp2);
                return new AddCamViewHolder(view);

        }

    }

    @Override
    public int getItemViewType(int position) {
        return position < ipCams.size() ? ITEM_CAM : ITEM_ADD_CAM;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return gridCount;
    }

    public class CamsViewHolder extends BaseViewHolder{
        private IpCam ipCam;
        private TextView camName;
        private VLCVideoLayout vlcVideoLayout ;
        private MediaPlayer mediaPlayer ;


        public CamsViewHolder(View itemView) {
            super(itemView);
            vlcVideoLayout = itemView.findViewById(R.id.vlc_layout);
            camName = itemView.findViewById(R.id.tv_cam_name);
        }

        @Override
        public void onBind(int position) {
            super.onBind(position);
             ipCam = ipCams.get(position);
            camName.setText(ipCam.getName());
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, CamerasActivity.class);
                ArrayList<IpCam> ipCams = new ArrayList<>();
                ipCams.add(ipCams.get(getAdapterPosition()));
                intent.putParcelableArrayListExtra(CamerasActivity.KEY_CAMERAS, ipCams);
                context.startActivity(intent);
            });



        }

        @Override
        protected void clear() {

        }


        private void initVlcPlayer() {
            mediaPlayer = new MediaPlayer(itemView.getContext());
            mediaPlayer.attachViews(vlcVideoLayout);
            final Media media = new Media(mediaPlayer.getLibVLCInstance(), Uri.parse(ipCam.getUrl()));
            media.addCommonOptions();
            mediaPlayer.setMedia(media);
            media.release();
            mediaPlayer.play();
        }



    }

    public class AddCamViewHolder extends BaseViewHolder {
        public AddCamViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void onBind(int position) {
            super.onBind(position);
            itemView.setOnClickListener(v -> {
                ((MainActivity) context).addNewCam();
            });
        }

        @Override
        protected void clear() {
        }
    }




}
