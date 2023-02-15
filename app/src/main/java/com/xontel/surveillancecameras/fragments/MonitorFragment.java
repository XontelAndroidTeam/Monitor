package com.xontel.surveillancecameras.fragments;

import android.content.Intent;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
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
import androidx.recyclerview.widget.RecyclerView;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.activities.AddNewDeviceActivity;
import com.xontel.surveillancecameras.adapters.PagerAdapter;
import com.xontel.surveillancecameras.base.BaseFragment;
import com.xontel.surveillancecameras.databinding.FragmentMonitorBinding;
import com.xontel.surveillancecameras.hikvision.CamDevice;
import com.xontel.surveillancecameras.utils.ViewPagerWithEmptyView;
import com.xontel.surveillancecameras.viewModels.MainViewModel;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.viewModels.ViewModelProviderFactory;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class MonitorFragment extends BaseFragment {
    public static final String TAG = MonitorFragment.class.getSimpleName();
    private FragmentMonitorBinding binding;
    private PagerAdapter pagerAdapter;
    private List<IpCam> ipCams = new ArrayList<>();
    private MainViewModel mainViewModel;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
    private DataSetObserver emptyObserver ;
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
        getFragmentComponent().inject(this);
        mainViewModel = new ViewModelProvider(requireActivity(), providerFactory).get(MainViewModel.class);
        setHasOptionsMenu(true);
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



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMonitorBinding.inflate(inflater);
        setupObservables();
        setupCamsPager();
        requireActivity().setTitle(R.string.monitor);
        return binding.getRoot();
    }

    @Override
    protected void setUp(View view) {
    }

    private void setupCamsPager() {
        emptyObserver = new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if(pagerAdapter != null && binding.noCams != null) {
                    boolean noCams = pagerAdapter.getCount() == 0 ;
                    binding.noCams.getRoot().setVisibility(noCams ?View.VISIBLE : View.GONE);
                    binding.camsPager.setVisibility(noCams ? View.GONE : View.VISIBLE);
                }
            }
        };
        pagerAdapter = new PagerAdapter(requireActivity(),0);
        pagerAdapter.registerDataSetObserver(emptyObserver);
        binding.camsPager.setAdapter(pagerAdapter);
        binding.camsPager.setOffscreenPageLimit(1);
        binding.dotsIndicator.setViewPager(binding.camsPager); //must be after adapter
    }

    private void setupObservables() {
        mainViewModel.ipCams.observe(getViewLifecycleOwner(), allIpCams -> {
           reload();
        });
        mainViewModel.mGridObservable.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                reload();
            }
        });
    }

    private void reload() {
            int pagesCount = (int)Math.ceil(mainViewModel.ipCams.getValue().size() * 1.0/ mainViewModel.mGridObservable.getValue());
            pagerAdapter.setGridCount(pagesCount);
    }


}