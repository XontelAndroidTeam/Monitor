package com.xontel.surveillancecameras.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xontel.surveillancecameras.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaViewHolder> {
    private Context context;
    private List<File> itemList;
    private List<File> selectedFiles = new ArrayList<>();
    private ClickActionListener clickAction;
    private boolean selectionModeEnabled = false;

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

    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public List<File> getSelectedFiles() {
        return selectedFiles;
    }

    public void unSelectAll() {
        selectionModeEnabled = false;
        selectedFiles.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemsCount() {
        return selectedFiles.size();
    }


    class MediaViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private ImageView checkBoxOverlay;
        private ImageView play;
        private CheckBox checker;


        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnLongClickListener(v -> {
                if (!selectionModeEnabled) {
                    selectionModeEnabled = true;
                    itemView.performClick();
                }
                return true;
            });

            itemView.setOnClickListener(v -> {
                if (selectionModeEnabled) {
                    toggleChecker();
                    if (checker.isChecked())
                        selectedFiles.add(itemList.get(getAdapterPosition()));
                    else{
                        selectedFiles.remove(itemList.get(getAdapterPosition()));
                    }
                }
            });
            image = itemView.findViewById(R.id.iv_photo);
            checkBoxOverlay = itemView.findViewById(R.id.iv_overlay);
            play = itemView.findViewById(R.id.iv_Play);
            checker = itemView.findViewById(R.id.cb_checker);
        }


        public void toggleChecker() {
            checker.toggle();
            checkBoxOverlay.setVisibility(checker.isChecked() ? View.VISIBLE : View.GONE);
        }

        public void showPlayVideo(boolean show) {
            int visibility = show ? View.VISIBLE : View.GONE;
            play.setVisibility(visibility);
        }

    }

    public interface ClickActionListener {
        void onItemLongClicked(int position);

        void onItemClicked(int position);
    }
}
