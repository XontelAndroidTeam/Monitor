package com.xontel.surveillancecameras.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.databinding.DialogDeleteProgressBinding;
import com.xontel.surveillancecameras.utils.CommonUtils;

import java.io.File;
import java.util.List;

public class DialogDeleteProgress extends Dialog {
    private Context context;
    private List<File> files;
    private DialogDeleteProgressBinding binding;
    private ClickAction clickAction;

    public DialogDeleteProgress(@NonNull Context context, List<File> files, ClickAction clickAction) {
        super(context);
        this.context = context;
        this.files = files;
        this.clickAction = clickAction;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_delete_progress, null, false);
        setContentView(binding.getRoot());
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        initUI();
    }

    private void initUI() {
        deleteFiles();

    }

    private void deleteFiles() {
        for (int i = 0; i < files.size(); i++) {
            if (!CommonUtils.deleteFile(files.get(i))) {
                Toast.makeText(getContext(), R.string.file_delete_error + files.get(i).getName(), Toast.LENGTH_LONG).show();
            }
            int progress = (((i + 1) * 100) / files.size());
            binding.tvProgress.setText(progress + " %");
            binding.pbProgressIndicator.setProgress(progress);
        }
        files.clear();
        dismiss();
        clickAction.onDeleteCompleted();

    }

    public interface ClickAction {
        void onDeleteCompleted();
    }
}

