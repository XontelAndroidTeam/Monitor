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

import java.io.File;
import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaViewHolder> {
    private Context context ;
    private List<File> itemList ;
    private ClickActionListener clickAction ;
    private boolean selectionModeEnabled = false ;

    public MediaAdapter(Context context, List<File> itemList, ClickActionListener clickAction) {
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
       holder.onBind(position);
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }


    class MediaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private ImageView image ;
        private ImageView checkBoxOverlay;
        private ImageView play ;
        private CheckBox checker;


        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);
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

        @Override
        public void onClick(View v) {
            if(selectionModeEnabled){
                checker.toggle();
            }else{
                viewMedia();
            }
        }

        private void viewMedia() {
        }

        @Override
        public boolean onLongClick(View v) {
            if(!selectionModeEnabled){
                enableSelectionMode();
                itemView.performClick();
            }
            return true;
        }

        private void enableSelectionMode() {
            selectionModeEnabled = true;
            clickAction.onSelectionModeEnables(true);
            notifyDataSetChanged();
        }

        public void onBind(int position) {
            showChecker(selectionModeEnabled);
            File file = itemList.get(position);
            if(file.getName().endsWith(".jpg")){

            }else{

            }
            
        }
    }

    public interface ClickActionListener{

        void onSelectionModeEnables(boolean enabled);
    }
}
