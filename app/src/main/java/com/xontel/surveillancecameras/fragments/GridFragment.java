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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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
    private Mjpeg mjpeg ;
    private List<MjpegInputStream> streams = new ArrayList<>() ;


    public GridFragment() {
        // Required empty public constructor
    }


    @Override
    public void onResume() {
//        initializeObservable();
//        gridAdapter.resumeAll();
//        gridAdapter.notifyDataSetChanged();
        initUI();
        super.onResume();
    }

    @Override
    public void onPause() {
        try {
            pauseAllPlayers();
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onPause();
//        stopObservables();
//        gridAdapter.pauseAll();
//        gridAdapter.notifyDataSetChanged();
    }

    private void stopObservables() {
//        for(int i =0 ; i<observables.size() ; i++){
//            observables.get(i).unsubscribeOn(Schedulers.io());
//        }
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
        super.onCreate(savedInstanceState);
//        mjpeg = Mjpeg.newInstance();
        gridCount = getContext().getSharedPreferences(CommonUtils.SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE).getInt(CommonUtils.KEY_GRID_COUNT, DEFAULT_GRID_COUNT);
        if (getArguments() != null) {
            actualCams = getArguments().getParcelableArrayList(KEY_CAMS);
        }
    }

    private void initializeObservable() {
        for(int i =0 ; i<actualCams.size();i++){
//            observables.add(mjpeg.open(actualCams.get(i).getUrl()));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private void pauseAllPlayers() {
        for(int i = 0 ; i< actualCams.size() ; i++){
            actualCams.get(i).getMediaPlayer().stop();
//            actualCams.get(i).getMediaPlayer().detachViews();
        }
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


    }


    private void initUI() {
        setupCamGrid();
    }


    private void setupCamGrid() {
        int allCamsSize = ((MainActivity)getContext()).getCams().size();
        allCams.clear();
        allCams.addAll(actualCams);
        if (actualCams.size() < gridCount && allCamsSize < 16) {
            for (int i = actualCams.size(); i < gridCount; i++) { // completing the grid with empty items till filling them
                allCams.add(new IpCam());
            }
        }
        gridAdapter = new CamsAdapter(allCams, getContext(),  gridCount);
        binding.rvGrid.setLayoutManager(new GridLayoutManager(getContext(), (int) Math.sqrt(gridCount)));
        binding.rvGrid.setAdapter(gridAdapter);



    }

}