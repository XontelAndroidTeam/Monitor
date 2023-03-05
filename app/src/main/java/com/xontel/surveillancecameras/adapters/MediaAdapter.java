package com.xontel.surveillancecameras.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.activities.MediaViewerActivity;
import com.xontel.surveillancecameras.activities.SavedMediaActivity;
import com.xontel.surveillancecameras.utils.MediaData;

import java.io.File;
import java.io.IOException;
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
        itemList.clear();
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
            MediaData data = itemList.get(getAbsoluteAdapterPosition()) ;
            if(data.getMediaType().equals(Environment.DIRECTORY_PICTURES)) {
                Intent intent = new Intent(context, MediaViewerActivity.class);
                intent.putExtra(MediaViewerActivity.KEY_MEDIA_DATA, data);
                context.startActivity(intent);
            }else{
                Uri uri = data.getMediaUri();
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setDataAndType(uri, "video/mp4");
                context.startActivity(intent);
            }
        }



        public void onBind(int position) {
            showChecker(selectionModeEnabled);
            checker.setChecked(selectedItems.contains(itemList.get(getAbsoluteAdapterPosition())));
            MediaData data = itemList.get(position);
            play.setVisibility(data.getMediaType().equals(Environment.DIRECTORY_PICTURES) ? View.GONE : View.VISIBLE);
                try {
                    image.setImageBitmap(context.getContentResolver().loadThumbnail(
                            data.getMediaUri(),
                          new Size(500, 500), // TODO check for smarter alternatives
                            null
                    ));
                } catch (IOException e) {
                    e.printStackTrace();
                }

        }
    }

    public interface ClickActionListener{
        void onSelectionModeEnabled(boolean enabled);
        void notifySelectionMode();
    }
}
