package com.xontel.surveillancecameras.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.service.controls.DeviceTypes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.Observable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.activities.AddCamActivity;
import com.xontel.surveillancecameras.activities.AddNewDeviceActivity;
import com.xontel.surveillancecameras.adapters.PagerAdapter;
import com.xontel.surveillancecameras.base.BaseFragment;
import com.xontel.surveillancecameras.dahua.DahuaUtil;
import com.xontel.surveillancecameras.databinding.FragmentMonitorBinding;
import com.xontel.surveillancecameras.hikvision.CamDevice;
import com.xontel.surveillancecameras.hikvision.HikUtil;
import com.xontel.surveillancecameras.presenters.MainDeviceMvpPresenter;
import com.xontel.surveillancecameras.presenters.MainDeviceMvpView;
import com.xontel.surveillancecameras.utils.CamDeviceType;
import com.xontel.surveillancecameras.viewModels.MainViewModel;
import com.xontel.surveillancecameras.adapters.CamsAdapter;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.viewModels.ViewModelProviderFactory;

import org.videolan.libvlc.MediaPlayer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MonitorFragment extends BaseFragment  {
    public static final String TAG = MonitorFragment.class.getSimpleName();
    public static final String KEY_ORDER = "order";
    private FragmentMonitorBinding binding;
    private int gridCount;
    private int fragmentOrder;
    private PagerAdapter pagerAdapter;
    private boolean isInitialized = false;
    private List<IpCam> ipCams = new ArrayList<>();
    private List<CamDevice> camDevices = new ArrayList<>();
    private MainViewModel mainViewModel;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
    @Inject
    ViewModelProviderFactory providerFactory;


    public MonitorFragment() {}

    @Override
    public void onResume(){
        binding.noCams.btnAdd.setOnClickListener(view -> {
        requireActivity().startActivity(new Intent(requireContext(), AddNewDeviceActivity.class));
    });
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    public static MonitorFragment newInstance() {
        MonitorFragment fragment = new MonitorFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().setTitle(R.string.monitor);
        getFragmentComponent().inject(this);
        mainViewModel = new ViewModelProvider(requireActivity(), providerFactory).get(MainViewModel.class);
        setHasOptionsMenu(true);
        //gridCount = mainViewModel.getGridObservable().getValue();
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
                requireActivity().startActivity(new Intent(requireContext(), AddNewDeviceActivity.class));
                return true;
            }
            default: return super.onOptionsItemSelected(item);
        }
    }

   // public void updateIpCams(){
   //     int leapLastIndex = fragmentOrder * gridCount;
   //     ipCams.clear();
   //     ipCams.addAll(mainViewModel.ipCams.getValue().subList(leapLastIndex - gridCount, Math.min(leapLastIndex, mainViewModel.ipCams.getValue().size())));
  //  }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMonitorBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    protected void setUp(View view) {
        setupCamsPager();
        setupObservables();
        isInitialized = true;
    }

    private void setupCamsPager() {
        Log.i("TATZ", "setupCamsPagerInstance: "+pagerAdapter);
        binding.camsPager.setEmptyView(binding.noCams.getRoot());
        if (!isInitialized){
            pagerAdapter = new PagerAdapter(getChildFragmentManager(),1);
        }
        binding.camsPager.setAdapter(pagerAdapter);
        binding.dotsIndicator.setViewPager(binding.camsPager);
    }

    private void setupObservables() {
        mainViewModel.ipCams.observe(getViewLifecycleOwner(), allIpCams -> {
            if (allIpCams != null && !allIpCams.isEmpty()){
                if (ipCams.isEmpty()){
                    Log.i("TATZ", "DataFirstInitialized: ");
                    pagerAdapter.getListOfData(allIpCams);
                } else if (ipCams.size() > allIpCams.size()){
                    Log.i("TATZ", "DataRemoved: ");
                }else if (ipCams.size() < allIpCams.size()){
                    Log.i("TATZ", "NewData: ");
                }
                handleCamsFromDb(allIpCams);
            }else {
                if (!ipCams.isEmpty()){
                    //TODO all cams deleted
                }
            }
        });

        mainViewModel.mGridObservable.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
            //    updateIpCams();
            }
        });


    }

    private void handleCamsFromDb(List<IpCam> ipCamsData){
        if (!ipCams.isEmpty()){ipCams.clear();}
        ipCams.addAll(ipCamsData);
    }


}