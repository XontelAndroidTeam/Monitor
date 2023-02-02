package com.xontel.surveillancecameras.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.opengl.Visibility;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.activities.MediaViewerActivity;
import com.xontel.surveillancecameras.activities.SavedMediaActivity;
import com.xontel.surveillancecameras.utils.MediaData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaViewHolder> {
    private Context context ;
    private List<MediaData> itemList = new ArrayList<>();;
    private List<MediaData> selectedItems = new ArrayList<>();
    private ClickActionListener clickAction ;
    private boolean selectionModeEnabled = false ;

    public MediaAdapter(Context context, List<MediaData> itemList, ClickActionListener clickAction) {
        this.context = context;
        this.itemList = itemList;
        this.clickAction = clickAction;
    }

    public List<MediaData> getSelectedItems() {
        return selectedItems;
    }


    public void setAllData(List<MediaData> newItemList){
        if (!itemList.isEmpty()){itemList.clear();}
        itemList.addAll(newItemList);
        notifyDataSetChanged();
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



    public void enableSelectionMode(boolean enabled) {
        selectionModeEnabled = enabled;
        if(!enabled) selectedItems.clear();
        notifyDataSetChanged();
    }

    public void selectAll() {
        if(selectionModeEnabled){
            selectedItems.clear();
            selectedItems.addAll(itemList);
            clickAction.notifySelectionMode();
            notifyDataSetChanged();
        }
    }


    class MediaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView image ;
        private ImageView checkBoxOverlay;
        private ImageView play ;
        private CheckBox checker;


        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);
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
        @Override
        public void onClick(View v) {
            if(selectionModeEnabled){
                checker.toggle();
                if(checker.isChecked()){
                    selectedItems.add(itemList.get(getAdapterPosition()));
                }else{
                    selectedItems.remove(itemList.get(getAdapterPosition()));
                }
                clickAction.notifySelectionMode();
            }else{
                viewMedia();
            }
        }

        private void viewMedia() {
            MediaData file = itemList.get(getAdapterPosition()) ;
            Intent intent = new Intent(context, MediaViewerActivity.class);
            if(file.getName().toLowerCase().endsWith("jpg") || file.getName().toLowerCase().endsWith("jpeg") || file.getName().toLowerCase().endsWith("png")){
                intent.putExtra(MediaViewerActivity.KEY_MEDIA_TYPE, MediaViewerActivity.MEDIA_IMAGE);
            }else if(file.getName().toLowerCase().endsWith("mp4")){
                intent.putExtra(MediaViewerActivity.KEY_MEDIA_TYPE, MediaViewerActivity.MEDIA_VIDEO);
            }else{
                intent.putExtra(MediaViewerActivity.KEY_MEDIA_TYPE, MediaViewerActivity.MEDIA_VIDEO);
            }
            intent.putExtra(MediaViewerActivity.KEY_MEDIA_FILE_PATH, file);
            context.startActivity(intent);
        }


        public void onBind(int position) {
            showChecker(selectionModeEnabled);
            checker.setChecked(selectedItems.contains(itemList.get(getAdapterPosition())));
            MediaData data = itemList.get(position);
            String file = data.getImagePath() == null ? data.getVideoPath() : data.getImagePath();
            if(file.toLowerCase().endsWith("jpg") || file.toLowerCase().endsWith("jpeg") || file.toLowerCase().endsWith("png")){
                bindImage(file);
            }else if(file.toLowerCase().endsWith("mp4")){
                bindVideo(file);
            }else{
                bindVideo(file);
            }
        }

        private void bindVideo(String file) {
            play.setVisibility(View.VISIBLE);
//            Glide.with(context)
//                    .asBitmap()
//                    .load(file.getPath())
//                    .thumbnail(0.5f)// or URI/path// or URI/path
//                    .into(image);
            image.setImageBitmap(ThumbnailUtils.createVideoThumbnail(file,
                    MediaStore.Images.Thumbnails.MINI_KIND));
        }

        private void bindImage(String file) {
            play.setVisibility(View.GONE);
            Glide.with(context)
                    .load(file)
                    .into(image);
        }
    }

    public interface ClickActionListener{
        void onSelectionModeEnabled(boolean enabled);
        void notifySelectionMode();
    }
}
