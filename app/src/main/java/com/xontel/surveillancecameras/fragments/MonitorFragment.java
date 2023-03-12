package com.xontel.surveillancecameras.fragments;

import android.content.res.Configuration;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.databinding.Observable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager.widget.ViewPager;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.activities.HomeActivity;
import com.xontel.surveillancecameras.adapters.PagerAdapter;
import com.xontel.surveillancecameras.base.BaseFragment;
import com.xontel.surveillancecameras.databinding.FragmentMonitorBinding;
import com.xontel.surveillancecameras.utils.CustomSpinner;
import com.xontel.surveillancecameras.utils.FixedSpeedScroller;
import com.xontel.surveillancecameras.utils.StorageHelper;
import com.xontel.surveillancecameras.viewModels.MainViewModel;
import com.xontel.surveillancecameras.viewModels.ViewModelProviderFactory;

import org.videolan.libvlc.util.TimeCounter;

import java.lang.reflect.Field;
import java.util.Locale;

import javax.inject.Inject;

public class MonitorFragment extends BaseFragment implements AdapterView.OnItemSelectedListener {
    public static final String TAG = MonitorFragment.class.getSimpleName();
    private FragmentMonitorBinding binding;
    private PagerAdapter pagerAdapter;
    private MainViewModel mainViewModel;
    private DataSetObserver emptyObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            boolean noCams = pagerAdapter.getCount() == 0;
            binding.noCams.getRoot().setVisibility(noCams ? View.VISIBLE : View.GONE);
            binding.camsPager.setVisibility(noCams ? View.GONE : View.VISIBLE);
        }
    };

    private TimeCounter timer;

    private Spinner mSpinner;
    @Inject
    ViewModelProviderFactory providerFactory;

    private boolean isBtnsShown = true;

    private Handler btnsHandler = new Handler();

    private static final long TIME_TO_HIDE_BTNS = 5000;
    private static final long ANIMATION_DURATION = 300;
    private Runnable btnsRemovalRunnable = new Runnable() {
        @Override
        public void run() {
            hideButtons();
        }
    };

    private void hideButtons() {
        if (isBtnsShown) {
//            binding.dropDown.getSpinner().clos;
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
            binding.btnShowControllers.animate()
                    .alpha(1.0f)
                    .setDuration(ANIMATION_DURATION)
                    .setListener(null);
            isBtnsShown = false;
        }
    }

    private void showButtons() {
        if (!isBtnsShown) {
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
            binding.btnShowControllers.animate()
                    .alpha(0.0f)
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
        Log.v(TAG, "onResume " + hashCode());

    }

    @Override
    public void onPause() {
        super.onPause();
        pagerAdapter.setPagesCount(0);
        Log.v(TAG, "onPause " + hashCode());
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
        Log.v(TAG, "onCreate " + hashCode());
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView  " + hashCode());
        binding = FragmentMonitorBinding.inflate(inflater);
        binding.setViewModel(mainViewModel);
        requireActivity().setTitle(R.string.monitor);
        return binding.getRoot();
    }

    @Override
    protected void setUp(View view) {
        binding.btnAdd.setOnClickListener(view1 -> NavHostFragment.findNavController(MonitorFragment.this).navigate(R.id.action_monitorFragment_to_deviceFragment, new Bundle()));
        binding.noCams.btnAdd.setOnClickListener(view1 -> binding.btnAdd.performClick());
        binding.btnShowControllers.setOnClickListener((v) -> {
            showButtons();
            scheduleHidingBtns();
        });
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

        binding.btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainViewModel.recordVideo.setValue(!mainViewModel.recordVideo.getValue());
            }
        });
//        setupGridDropDown();
        setupObservables();
        setupCamsPager();
    }




    private void setupCamsPager() {
        int pagesCount = (int) Math.ceil(mainViewModel.ipCams.getValue().size() * 1.0 / mainViewModel.mGridObservable.getValue());
        pagerAdapter = new PagerAdapter(requireActivity(), pagesCount);
        pagerAdapter.registerDataSetObserver(emptyObserver);
        binding.camsPager.setAdapter(pagerAdapter);
        binding.camsPager.setOffscreenPageLimit(1);
        binding.dotsIndicator.attachTo(binding.camsPager); //must be after adapter
    }



    private void setupObservables() {
        mainViewModel.isRecording.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean record) {
                lockPager(record);
                if (timer != null) {
                    timer.stop();
                }
                if (record) {
                    timer = new TimeCounter(binding.recordPanel.timer);
                    timer.count();
                }

            }
        });
        mainViewModel.ipCams.observe(getViewLifecycleOwner(), allIpCams -> {
            if (!mainViewModel.getLoading().getValue()) {
                if(allIpCams.size() > 0) {
                    binding.llBtns.setVisibility(View.VISIBLE);
                    binding.btnShowControllers.setVisibility(View.VISIBLE);
                    ((HomeActivity) requireActivity()).getSupportActionBar().hide();
                    showButtons();
                    scheduleHidingBtns();
                }else {
                    binding.btnShowControllers.setVisibility(View.GONE);
                    binding.llBtns.setVisibility(View.GONE);
                    ((HomeActivity) requireActivity()).getSupportActionBar().show();
                    hideButtons();
                }
                reload();
            }
        });
        mainViewModel.mGridObservable.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                updateDropDownSelectionIfNotChanged();
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

    private void updateDropDownSelectionIfNotChanged() {
        int dropDownSelection = Integer.parseInt(getResources().getStringArray(R.array.grid_count)[mSpinner.getSelectedItemPosition()]);
        if (dropDownSelection != mainViewModel.mGridObservable.getValue()) {
            binding.dropDown.getSpinner().setSelection(0); // TODO fix it
        }
    }

    private void lockPager(Boolean record) {
        Log.v(TAG, "lock : " + record);
        binding.camsPager.disableScroll(record);
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
        Log.v(TAG, "onStart " + hashCode());
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(TAG, "onStop " + hashCode());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy " + hashCode());
    }



    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int gridCount = Integer.parseInt(getResources().getStringArray(R.array.grid_count)[i]);
                if (mainViewModel.getGridObservable().getValue() != gridCount) {
                    mainViewModel.mGridObservable.setGridCount(gridCount + "");
                }
            }
        }).start();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}