package com.xontel.surveillancecameras.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.niqdev.mjpeg.Mjpeg;
import com.github.niqdev.mjpeg.MjpegInputStream;
import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.activities.MainActivity;
import com.xontel.surveillancecameras.adapters.CamsAdapter;
import com.xontel.surveillancecameras.databinding.FragmentGridBinding;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.utils.CommonUtils;
import com.xontel.surveillancecameras.utils.RxBus;
import com.xontel.surveillancecameras.utils.VideoHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

public class GridFragment extends Fragment {

    public static final String KEY_CAMS = "cams";
    public static final int DEFAULT_GRID_COUNT = 4;
    private int gridCount;
    private ArrayList<IpCam> actualCams = new ArrayList<>();
    private ArrayList<IpCam> allCams = new ArrayList<>();
    private CamsAdapter gridAdapter;
    private FragmentGridBinding binding;
    private List<VideoHelper> videoHelpers = new ArrayList<>();


    public GridFragment() {
        // Required empty public constructor
    }


    @Override
    public void onResume() {
        Log.e("TAG", "onResume"+hashCode() );
        setupPlayers();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("TAG", "onPause"+hashCode() );
        gridAdapter.pauseAll();
//        actualCams.clear();
//        videoHelpers.clear();
//        setupCamGrid();
    }



    public static GridFragment newInstance(List<IpCam> cams) {
        GridFragment fragment = new GridFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(KEY_CAMS, new ArrayList<>(cams));
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e("TAG", "onCreate"+hashCode() );
        super.onCreate(savedInstanceState);
        gridCount = getContext().getSharedPreferences(CommonUtils.SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE).getInt(CommonUtils.KEY_GRID_COUNT, DEFAULT_GRID_COUNT);
    }



    @Override
    public void onDestroy() {
        Log.e("TAG", "onDestroy"+hashCode() );
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

        initUI();
    }


    private void initUI() {
        setupCamGrid();
    }


    private void setupCamGrid() {

        gridAdapter = new CamsAdapter(actualCams, videoHelpers, getContext(), gridCount);
        binding.rvGrid.setLayoutManager(new GridLayoutManager(getContext(), (int) Math.sqrt(gridCount)));
        binding.rvGrid.setAdapter(gridAdapter);
    }

    private void setupPlayers(){

        if (getArguments() != null) {
            gridAdapter.addItems(getArguments().getParcelableArrayList(KEY_CAMS));
        }
    }

}