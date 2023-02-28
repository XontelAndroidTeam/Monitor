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
import android.view.animation.Interpolator;

import androidx.annotation.NonNull;
import androidx.databinding.Observable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.activities.AddNewDeviceActivity;
import com.xontel.surveillancecameras.adapters.PagerAdapter;
import com.xontel.surveillancecameras.base.BaseFragment;
import com.xontel.surveillancecameras.databinding.FragmentMonitorBinding;
import com.xontel.surveillancecameras.hikvision.CamDevice;
import com.xontel.surveillancecameras.utils.FixedSpeedScroller;
import com.xontel.surveillancecameras.utils.ViewPagerWithEmptyView;
import com.xontel.surveillancecameras.viewModels.MainViewModel;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.viewModels.ViewModelProviderFactory;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class MonitorFragment extends BaseFragment {
    public static final String TAG = MonitorFragment.class.getSimpleName();
    private FragmentMonitorBinding binding;
    private PagerAdapter pagerAdapter;
    private MainViewModel mainViewModel;
    private DataSetObserver emptyObserver ;
    @Inject
    ViewModelProviderFactory providerFactory;


    public MonitorFragment() {}

    @Override
    public void onResume(){
        Log.v(TAG, "onResume");
        binding.noCams.btnAdd.setOnClickListener(view -> {
        navigateToDevices();
    });
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "onPause");
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
        Log.v(TAG, "onCreate");
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
                navigateToDevices();
                return true;
            }
            default: return super.onOptionsItemSelected(item);
        }
    }

    private void navigateToDevices() {
        NavHostFragment.findNavController(this).navigate(R.id.action_monitorFragment_to_deviceFragment);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView");
        binding = FragmentMonitorBinding.inflate(inflater);

        requireActivity().setTitle(R.string.monitor);
        return binding.getRoot();
    }

    @Override
    protected void setUp(View view) {
        setupObservables();
        setupCamsPager();
    }

    private void setupCamsPager() {
        try {
            Field mScroller;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            Field interpolator = ViewPager.class.getDeclaredField("sInterpolator");
            interpolator.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(getContext(),  (Interpolator) interpolator.get(null));
            // scroller.setFixedDuration(5000);
            mScroller.set(binding.camsPager, scroller);
        } catch (NoSuchFieldException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        }
        int pagesCount = (int)Math.ceil(mainViewModel.ipCams.getValue().size() * 1.0/ mainViewModel.mGridObservable.getValue());
        pagerAdapter = new PagerAdapter(requireActivity(), pagesCount);
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
//        pagerAdapter.registerDataSetObserver(emptyObserver);
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
        mainViewModel.getLoading().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean loading) {
                if(loading){showLoading();}else{hideLoading();}
            }
        });
    }

    private void reload() {
            int pagesCount = (int)Math.ceil(mainViewModel.ipCams.getValue().size() * 1.0/ mainViewModel.mGridObservable.getValue());
            Log.v("GridFragment", "rebindOuter");
            pagerAdapter.setPagesCount(pagesCount);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v(TAG, "onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
    }
}