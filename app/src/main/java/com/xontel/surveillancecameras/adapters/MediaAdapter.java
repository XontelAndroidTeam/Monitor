package com.xontel.surveillancecameras.adapters;

import android.content.Context;
import android.opengl.Visibility;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.models.MediaItem;

import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaViewHolder> {
    private Context context ;
    private List<MediaItem> itemList ;
    private ClickActionListener clickAction ;

    public MediaAdapter(Context context, List<MediaItem> itemList, ClickActionListener clickAction) {
        this.context = context;
        this.itemList = itemList;
        this.clickAction = clickAction;
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MediaViewHolder(LayoutInflater.from(context).inflate(R.layout.item_media, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {

    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }


    class MediaViewHolder extends RecyclerView.ViewHolder{
        private ImageView image ;
        private ImageView checkBoxOverlay;
        private ImageView play ;
        private CheckBox checker;


        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);
              image = itemView.findViewById(R.id.iv_photo) ;
              checkBoxOverlay = itemView.findViewById(R.id.iv_overlay) ;
              play = itemView.findViewById(R.id.iv_Play) ;
              checker = itemView.findViewById(R.id.cb_checker) ;
        }


        public void showChecker(boolean show){
            int visibility = show ? View.VISIBLE : View.GONE ;
            checkBoxOverlay.setVisibility(visibility);
            checker.setVisibility(visibility);
        }

        public void showPlayVideo(boolean show){
            int visibility = show ? View.VISIBLE : View.GONE ;
            play.setVisibility(visibility);
        }
    }

    public interface ClickActionListener{

    }
}
