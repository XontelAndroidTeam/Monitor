package com.xontel.surveillancecameras.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.adapters.CamsAdapter;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.databinding.FragmentGridBinding;
import com.xontel.surveillancecameras.utils.CommonUtils;
import com.xontel.surveillancecameras.utils.VideoHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GridFragment extends Fragment {

    public static final String KEY_CAMS = "cams";
    public static final String KEY_CAMS_COUNT = "cams_count";
    public static final int DEFAULT_GRID_COUNT = 4;
    private int gridCount;
    private LifecycleCallbacks lifecycleCallbacks;
    private ArrayList<IpCam> actualCams = new ArrayList<>();
    private ArrayList<IpCam> allCams = new ArrayList<>();
    private CamsAdapter gridAdapter;
    private int camsCount ;
    private FragmentGridBinding binding;
    private List<VideoHelper> videoHelpers = new ArrayList<>();

    public void setLifecycleCallbacks(LifecycleCallbacks lifecycleCallbacks) {
        this.lifecycleCallbacks = lifecycleCallbacks;
    }

    public GridFragment() {
        // Required empty public constructor
    }


    @Override
    public void onResume() {
        Log.e("TAG", "onResume" + hashCode());
        gridAdapter.notifyDataSetChanged();
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.e("TAG", "onPause" + hashCode());
//        gridAdapter.pauseAll();
        stopPlayers();
        setupCamGrid();
//        actualCams.clear();
//        videoHelpers.clear();
//        setupCamGrid();
    }


    public static GridFragment newInstance(List<IpCam> cams, int camsCount) {
        GridFragment fragment = new GridFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(KEY_CAMS, new ArrayList<>(cams));
        args.putInt(KEY_CAMS_COUNT, camsCount);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e("TAG", "onCreate" + hashCode());
        super.onCreate(savedInstanceState);
        gridCount = getContext().getSharedPreferences(CommonUtils.SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE).getInt(CommonUtils.KEY_GRID_COUNT, DEFAULT_GRID_COUNT);
    }


    @Override
    public void onDestroy() {
        Log.e("TAG", "onDestroy" + hashCode());
        super.onDestroy();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_grid, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e("TAG", "onViewCreated" + hashCode());
        if (getArguments() != null) {
            actualCams.clear();
            actualCams.addAll(getArguments().getParcelableArrayList(KEY_CAMS));
            camsCount = getArguments().getInt(KEY_CAMS_COUNT);
        }
        initUI();
    }


    private void initUI() {
        setupCamGrid();
    }


    private void setupCamGrid() {
        binding.rvGrid.setLayoutManager(new GridLayoutManager(getContext(), (int) Math.sqrt(gridCount)));
        gridAdapter = new CamsAdapter(this, actualCams, videoHelpers, getContext(), camsCount, gridCount);
        binding.rvGrid.setAdapter(gridAdapter);
        Log.e("TAG__", "setupCamGrid: "+ binding.rvGrid.getMeasuredHeight() );
    }


    private void startPlayers() {
        for (VideoHelper videoHelper : videoHelpers) {
            if (videoHelper != null) {
                videoHelper.onStart();

            }
        }
    }


    private void stopPlayers() {
        for (int i = 0 ; i< videoHelpers.size() ; i++) {
            VideoHelper videoHelper = videoHelpers.get(i);
            if (videoHelper!= null) {
                videoHelper.onStop();
                videoHelper.onDestroy();

            }
            videoHelper = null ;
        }

        videoHelpers.clear();
    }

    public interface LifecycleCallbacks {
        void onResumed();
    }

}