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
    private List<MediaPlayer> mediaPlayers = new ArrayList<>();


    public GridFragment() {
        // Required empty public constructor
    }


    @Override
    public void onResume(){
        super.onResume();
        gridAdapter.notifyDataSetChanged();
    }
    @Override
    public void onPause() {
        super.onPause();
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
        Log.v("TAG_", "created "+hashCode());
        if (getArguments() != null) {
            fragmentOrder = getArguments().getInt(KEY_ORDER);
        }
        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        gridCount = mainViewModel.gridCount.getValue();
        updateIpCams();
    }



    public void updateIpCams(){
        int leapLastIndex = fragmentOrder * gridCount;
        Log.v("TAG_", "grid count : " + gridCount + " fragmentOrder : " + fragmentOrder + " hash : " + hashCode());
        ipCams.clear();
        ipCams.addAll(mainViewModel.ipCams.getValue().subList(leapLastIndex - gridCount, Math.min(leapLastIndex, mainViewModel.ipCams.getValue().size())));

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

//        Log.v("TAG_", "destroyed " + hashCode());
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
        mainViewModel.gridCount.observe(getViewLifecycleOwner(), newGridCount -> {
            gridCount = newGridCount;
            updateIpCams();
            setupCamGrid(mainViewModel.mediaPlayersLiveData.getValue());
        });
        mainViewModel.ipCams.observe(getViewLifecycleOwner(), allIpCams -> {
            updateIpCams();
            setupCamGrid(mainViewModel.mediaPlayersLiveData.getValue());
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
        Log.v("TAG_", mediaPlayers.size()+"");
        binding.rvGrid.setLayoutManager(new GridLayoutManager(getContext(), (int) Math.sqrt(gridCount)));
        gridAdapter = new CamsAdapter(ipCams, mediaPlayers, gridCount, getContext());
        binding.rvGrid.setAdapter(gridAdapter);
    }

}