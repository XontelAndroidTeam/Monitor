package com.xontel.surveillancecameras.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.ViewModels.MainViewModel;
import com.xontel.surveillancecameras.adapters.CamsAdapter;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.databinding.FragmentGridBinding;

import org.jetbrains.annotations.NotNull;
import org.videolan.libvlc.MediaPlayer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class GridFragment extends Fragment {

    public static final String KEY_ORDER = "order";
    private FragmentGridBinding binding;
    private int gridCount;
    private int fragmentOrder;
    private List<IpCam> ipCams = new ArrayList<>();
    private MainViewModel mainViewModel;
    private CamsAdapter gridAdapter;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
    private List<MediaPlayer> mediaPlayers = new ArrayList<>();


    public GridFragment() {
        // Required empty public constructor
    }





    @Override
    public void onResume(){
        Log.v("TAG_", "onResume" + " NUMBER : "+fragmentOrder + " Time : "+ simpleDateFormat.format(System.currentTimeMillis()));
        super.onResume();
        gridAdapter.setPlayers(mainViewModel.mediaPlayersLiveData.getValue());
        gridAdapter.notifyDataSetChanged();
    }
    @Override
    public void onPause() {
        Log.v("TAG_", "onPause" + " NUMBER : "+fragmentOrder + " Time : "+ simpleDateFormat.format(System.currentTimeMillis()));
        super.onPause();
        gridAdapter.setPlayers(new ArrayList<>());
        gridAdapter.notifyDataSetChanged();
        mainViewModel.resetPlayers();

    }


    public static GridFragment newInstance(int fragmentOrder) {
        GridFragment fragment = new GridFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_ORDER, fragmentOrder);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fragmentOrder = getArguments().getInt(KEY_ORDER);
        }
        Log.v("TAG_", "onCreate" + " NUMBER : "+fragmentOrder + " Time : "+ simpleDateFormat.format(System.currentTimeMillis()));
        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        gridCount = mainViewModel.gridCount.getValue();
        updateIpCams();
    }



    public void updateIpCams(){
        int leapLastIndex = fragmentOrder * gridCount;
        ipCams.clear();
        ipCams.addAll(mainViewModel.ipCams.getValue().subList(leapLastIndex - gridCount, Math.min(leapLastIndex, mainViewModel.ipCams.getValue().size())));

    }


    @Override
    public void onDestroy() {
        Log.v("TAG_", "onDestroy" + " NUMBER : "+fragmentOrder + " Time : "+ simpleDateFormat.format(System.currentTimeMillis()));
        super.onDestroy();
    }

    @Override
    public void onStart() {
//        Log.v("TAG_", "onStart" + " NUMBER : "+fragmentOrder);
        super.onStart();
    }

    @Override
    public void onStop() {
//        Log.v("TAG_", "onStop " + " NUMBER : "+fragmentOrder);
        super.onStop();
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
        setupCamGrid(new ArrayList<>());
        setupObservables();
    }

    private void setupObservables() {
        mainViewModel.ipCams.observe(getViewLifecycleOwner(), allIpCams -> {
            updateIpCams();
            gridAdapter.notifyDataSetChanged();
        });
    }

    private void updateGrid() {
        int oldGridCount = ((GridLayoutManager) binding.rvGrid.getLayoutManager()).getSpanCount();
        if (oldGridCount != gridCount) {
            ((GridLayoutManager) binding.rvGrid.getLayoutManager()).setSpanCount((int) Math.sqrt(gridCount));
            gridAdapter.notifyDataSetChanged();
        }

    }

    private void setupCamGrid(List<MediaPlayer> mediaPlayers) {
        binding.rvGrid.setLayoutManager(new GridLayoutManager(getContext(), (int) Math.sqrt(gridCount)));
        gridAdapter = new CamsAdapter(ipCams,new ArrayList<>(), gridCount, getContext());
        binding.rvGrid.setAdapter(gridAdapter);
    }

}