package com.xontel.surveillancecameras.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.activities.AddCamActivity;
import com.xontel.surveillancecameras.base.BaseFragment;
import com.xontel.surveillancecameras.databinding.FragmentMonitorBinding;
import com.xontel.surveillancecameras.viewModels.MainViewModel;
import com.xontel.surveillancecameras.adapters.CamsAdapter;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.viewModels.ViewModelProviderFactory;

import org.videolan.libvlc.MediaPlayer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MonitorFragment extends BaseFragment {

    public static final String KEY_ORDER = "order";
    private FragmentMonitorBinding binding;
    private int gridCount;
    private int fragmentOrder;
    private List<IpCam> ipCams = new ArrayList<>();
    private MainViewModel mainViewModel;
    private CamsAdapter gridAdapter;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");

    @Inject
    ViewModelProviderFactory providerFactory;


    public MonitorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume(){
        Log.v("TAG_", "onResume" + " NUMBER : "+fragmentOrder + " Time : "+ simpleDateFormat.format(System.currentTimeMillis()));
        super.onResume();
        gridAdapter.setPlayers(mainViewModel.mediaPlayers);
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


    public static MonitorFragment newInstance(int fragmentOrder) {
        MonitorFragment fragment = new MonitorFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_ORDER, fragmentOrder);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().setTitle(R.string.monitor);
        getFragmentComponent().inject(this);
        mainViewModel = new ViewModelProvider(getActivity(), providerFactory).get(MainViewModel.class);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            fragmentOrder = getArguments().getInt(KEY_ORDER);
        }
        Log.v("TAG_", "onCreate" + " NUMBER : "+fragmentOrder + " Time : "+ simpleDateFormat.format(System.currentTimeMillis()));
        gridCount = mainViewModel.getGridObservable().getValue();
//        updateIpCams();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:{
                requireActivity().startActivity(new Intent(requireContext(), AddCamActivity.class));
                return true;}
            default: return super.onOptionsItemSelected(item);
        }

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
        Log.v("TAG_", "onStart" + " NUMBER : "+fragmentOrder);
        super.onStart();
    }

    @Override
    public void onStop() {
        Log.v("TAG_", "onStop " + " NUMBER : "+fragmentOrder);
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMonitorBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    protected void setUp(View view) {
        setupCamGrid(new ArrayList<>());
        setupObservables();
    }

    private void setupObservables() {
        mainViewModel.ipCams.observe(getViewLifecycleOwner(), allIpCams -> {
//            updateIpCams();
            gridAdapter.notifyDataSetChanged();
        });

//        mainViewModel.mGridObservable.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
//            @Override
//            public void onPropertyChanged(Observable sender, int propertyId) {
//                updateIpCams();
//            }
//        });


    }

    private void updateGrid() {
        int oldGridCount = ((GridLayoutManager) binding.rvGrid.getLayoutManager()).getSpanCount();
        if (oldGridCount != gridCount) {
            gridCount = mainViewModel.getGridObservable().getValue();
            ((GridLayoutManager) binding.rvGrid.getLayoutManager()).setSpanCount((int) Math.sqrt(gridCount));
            gridAdapter.setGridCount(gridCount);
            gridAdapter.notifyDataSetChanged();
        }

    }

    private void setupCamGrid(List<MediaPlayer> mediaPlayers) {
        binding.rvGrid.setLayoutManager(new GridLayoutManager(getContext(), (int) Math.sqrt(gridCount)));
        gridAdapter = new CamsAdapter(ipCams,new ArrayList<>(), gridCount, getContext());
        binding.rvGrid.setAdapter(gridAdapter);
    }


}