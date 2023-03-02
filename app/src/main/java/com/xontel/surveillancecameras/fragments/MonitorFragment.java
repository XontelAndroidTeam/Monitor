package com.xontel.surveillancecameras.fragments;

import static android.view.FrameMetrics.ANIMATION_DURATION;

import android.content.Intent;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.databinding.Observable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.textfield.TextInputLayout;
import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.activities.AddNewDeviceActivity;
import com.xontel.surveillancecameras.activities.HomeActivity;
import com.xontel.surveillancecameras.adapters.PagerAdapter;
import com.xontel.surveillancecameras.base.BaseFragment;
import com.xontel.surveillancecameras.databinding.FragmentMonitorBinding;
import com.xontel.surveillancecameras.hikvision.CamDevice;
import com.xontel.surveillancecameras.utils.FixedSpeedScroller;
import com.xontel.surveillancecameras.utils.StorageHelper;
import com.xontel.surveillancecameras.utils.ViewPagerWithEmptyView;
import com.xontel.surveillancecameras.viewModels.MainViewModel;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.viewModels.ViewModelProviderFactory;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MonitorFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    public static final String TAG = MonitorFragment.class.getSimpleName();
    private FragmentMonitorBinding binding;
    private PagerAdapter pagerAdapter;
    private MainViewModel mainViewModel;
    private DataSetObserver emptyObserver;
    @Inject
    ViewModelProviderFactory providerFactory;

    private boolean isBtnsShown = true;

    private Handler btnsHandler = new Handler();

    private static final long TIME_TO_HIDE_BTNS = 5000;
    private static final long ANIMATION_DURATION = 300;
    private  Runnable btnsRemovalRunnable = new Runnable() {
        @Override
        public void run() {
            hideButtons();
        }
    };

    private void hideButtons() {
        if(isBtnsShown) {
            binding.llBtns.animate()
                    .alpha(0.0f)
                    .translationY(-100)
                    .setDuration(ANIMATION_DURATION)
                    .setListener(null);
            binding.dotsIndicator.animate()
                    .alpha(0.0f)
                    .translationY(100)
                    .setDuration(ANIMATION_DURATION)
                    .setListener(null);
            isBtnsShown = false;
        }
    }

    private void showButtons() {
        if(!isBtnsShown) {
            binding.llBtns.animate()
                    .alpha(1.0f)
                    .translationY(0)
                    .setDuration(ANIMATION_DURATION)
                    .setListener(null);
            binding.dotsIndicator.animate()
                    .alpha(1.0f)
                    .translationY(0)
                    .setDuration(ANIMATION_DURATION)
                    .setListener(null);
            isBtnsShown = true;
        }
    }

    private void scheduleHidingBtns() {
        btnsHandler.removeCallbacks(btnsRemovalRunnable);//add this
        btnsHandler.postDelayed(btnsRemovalRunnable, TIME_TO_HIDE_BTNS);
    }


    public MonitorFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity) getActivity()).getSupportActionBar().hide();
//        reload();
        Log.v(TAG, "onResume");

    }

    @Override
    public void onPause() {
        super.onPause();
        ((HomeActivity) getActivity()).getSupportActionBar().show();
        pagerAdapter.setPagesCount(0);
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
        Log.v(TAG, "onCreate");
    }


    private void navigateToDevices() {
        NavHostFragment.findNavController(this).navigate(R.id.action_monitorFragment_to_deviceFragment);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView");
        binding = FragmentMonitorBinding.inflate(inflater);
        binding.setViewModel(mainViewModel);
        requireActivity().setTitle(R.string.monitor);
        return binding.getRoot();
    }

    @Override
    protected void setUp(View view) {
        mainViewModel.oneCam.setValue(mainViewModel.mGridObservable.getValue() == 1);
        binding.btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((HomeActivity) requireActivity()).toggleSideMenu();
            }
        });
        binding.btnSnapshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainViewModel.takeSnapShot.setValue(true);
            }
        });
        setupGridDropDown();
        setupObservables();
        setupCamsPager();
    }


        private void setupGridDropDown() {
        ArrayAdapter gridDropDownAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.grid_count));
        binding.slideShowFilter.setText(String.valueOf(mainViewModel.mGridObservable.getValue()));
        binding.slideShowFilter.setAdapter(gridDropDownAdapter);
        binding.slideShowFilter.setOnItemClickListener(this);

        binding.setLifecycleOwner(this);
    }


    private void setupCamsPager() {
        try {
            Field mScroller;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            Field interpolator = ViewPager.class.getDeclaredField("sInterpolator");
            interpolator.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(getContext(), (Interpolator) interpolator.get(null));
            // scroller.setFixedDuration(5000);
            mScroller.set(binding.camsPager, scroller);
        } catch (NoSuchFieldException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        }
        int pagesCount = (int) Math.ceil(mainViewModel.ipCams.getValue().size() * 1.0 / mainViewModel.mGridObservable.getValue());
        pagerAdapter = new PagerAdapter(requireActivity(), pagesCount);
        emptyObserver = new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (pagerAdapter != null && binding.noCams != null) {
                    boolean noCams = pagerAdapter.getCount() == 0;
                    binding.noCams.getRoot().setVisibility(noCams ? View.VISIBLE : View.GONE);
                    binding.camsPager.setVisibility(noCams ? View.GONE : View.VISIBLE);
                }
            }
        };
        pagerAdapter.registerDataSetObserver(emptyObserver);
        binding.camsPager.setAdapter(pagerAdapter);
        binding.camsPager.setOffscreenPageLimit(1);
        binding.dotsIndicator.setViewPager(binding.camsPager); //must be after adapter
    }

    private void setupObservables() {
        mainViewModel.recordVideo.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean record) {
                if (mainViewModel.mGridObservable.getValue() == 1) {
                    lockPager(record);
                }
            }
        });
        mainViewModel.ipCams.observe(getViewLifecycleOwner(), allIpCams -> {
            reload();
        });
        mainViewModel.mGridObservable.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                mainViewModel.oneCam.setValue(mainViewModel.mGridObservable.getValue() == 1);
                reload();
            }
        });
        mainViewModel.getLoading().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean loading) {
                if (loading) {
                    showLoading();
                } else {
                    hideLoading();
                }
            }
        });
    }

    private void lockPager(Boolean record) {
        binding.camsPager.setEnabled(!record);
    }

    private void reload() {
        int newGrid = mainViewModel.mGridObservable.getValue();
        int camsCount = mainViewModel.ipCams.getValue().size();
        int pagesCount = (int) Math.ceil(camsCount * 1.0 / newGrid);
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int gridCount = Integer.parseInt(binding.slideShowFilter.getText().toString());
                if (mainViewModel.getGridObservable().getValue() != gridCount) {
                    StorageHelper.saveGridCount(getContext(), gridCount);
                    mainViewModel.mGridObservable.setGridCount(gridCount + "");
                }
            }
        }).start();

    }
}