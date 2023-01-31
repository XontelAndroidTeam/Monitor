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
import androidx.databinding.Observable;
import androidx.lifecycle.ViewModelProvider;
import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.activities.AddNewDeviceActivity;
import com.xontel.surveillancecameras.adapters.PagerAdapter;
import com.xontel.surveillancecameras.base.BaseFragment;
import com.xontel.surveillancecameras.databinding.FragmentMonitorBinding;
import com.xontel.surveillancecameras.hikvision.CamDevice;
import com.xontel.surveillancecameras.viewModels.MainViewModel;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.viewModels.ViewModelProviderFactory;
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
    private List<IpCam> ipCams = new ArrayList<>();
    private List<CamDevice> camDevices = new ArrayList<>();
    private MainViewModel mainViewModel;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
    @Inject
    ViewModelProviderFactory providerFactory;


    public MonitorFragment() {}

    @Override
    public void onResume(){
        pagerAdapter.notifyDataSetChanged();
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
        requireActivity().setTitle(R.string.monitor);
        return binding.getRoot();
    }

    @Override
    protected void setUp(View view) {
        setupCamsPager();
        setupObservables();
    }

    private void setupCamsPager() {
        binding.camsPager.setEmptyView(binding.noCams.getRoot());
        if (pagerAdapter == null){
            pagerAdapter = new PagerAdapter(getChildFragmentManager(),mainViewModel.gridCount.getValue());
        }
        binding.camsPager.setAdapter(pagerAdapter);
        binding.dotsIndicator.setViewPager(binding.camsPager);
    }

    private void setupObservables() {
        mainViewModel.ipCams.observe(getViewLifecycleOwner(), allIpCams -> {
            if (allIpCams != null && !allIpCams.isEmpty()){
                if (ipCams.isEmpty()){
                    pagerAdapter.getListOfData(allIpCams);
                } else if (ipCams.size() > allIpCams.size()){
                //    handleDecreaseIpCam(allIpCams);
                }else if (ipCams.size() < allIpCams.size()){
                 //   handleIncreaseIpCam(allIpCams);
                }else{
                //    mainViewModel.refreshData.setValue(true);
                }
                handleCamsFromDb(allIpCams);
                mainViewModel.pagerCount.setValue(pagerAdapter.getFragmentCount());
            }else {
                if (!ipCams.isEmpty()){
                    pagerAdapter.removeAllFragment();
                    ipCams.clear();
                }
            }
        });

        mainViewModel.mGridObservable.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
            //    updateIpCams();
            }
        });

        mainViewModel.refreshGridCount.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean){
                handleChangeGrid();
                mainViewModel.refreshGridCount.setValue(false);
            }
        });

    }

    private void handleCamsFromDb(List<IpCam> ipCamsData){
        if (!ipCams.isEmpty()){ipCams.clear();}
        ipCams.addAll(ipCamsData);
    }

    private void handleChangeGrid(){
    //    int size =  getCalculatedCount(ipCams);
   //     if (size > pagerAdapter.getFragmentCount()){
     //       pagerAdapter.addFragments(size - pagerAdapter.getFragmentCount());
      //  }else if (size < pagerAdapter.getFragmentCount()){
    //        pagerAdapter.removeFragments(pagerAdapter.getFragmentCount() - size);
      //  }
      //  mainViewModel.pagerCount.setValue(pagerAdapter.getFragmentCount());
     //   pagerAdapter.updateGridCount(mainViewModel.gridCount.getValue());
        mainViewModel.refreshPagerGridCount.setValue(true);
    }

    private int getCalculatedCount(List<IpCam> allIpCams){
        int size = 0 ;
        if (allIpCams.size() % mainViewModel.gridCount.getValue() != 0){ size = 1;}
        size = size + (allIpCams.size()/mainViewModel.gridCount.getValue()) ;
        return size;
    }

    private void handleIncreaseIpCam(List<IpCam> allIpCams){
        int size =  getCalculatedCount(allIpCams);
        if (size > pagerAdapter.getFragmentCount()){
            pagerAdapter.addFragments(size - pagerAdapter.getFragmentCount());
        }
        mainViewModel.refreshData.setValue(true);
    }

    private void handleDecreaseIpCam(List<IpCam> allIpCams){
        int size =  getCalculatedCount(allIpCams);
        if (size < pagerAdapter.getFragmentCount()){
            pagerAdapter.removeFragments(pagerAdapter.getFragmentCount() - size);
        }
        mainViewModel.refreshData.setValue(true);
    }

}