package com.xontel.surveillancecameras.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.viewModels.MainViewModel;
import com.xontel.surveillancecameras.viewModels.SettingViewModel;
import com.xontel.surveillancecameras.viewModels.ViewModelProviderFactory;
import com.xontel.surveillancecameras.base.BaseFragment;
import com.xontel.surveillancecameras.databinding.FragmentSettingsBinding;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends BaseFragment {
    private FragmentSettingsBinding binding;
    private SettingViewModel mSettingViewModel ;
    private MainViewModel mMainViewModel;

    @Inject
    ViewModelProviderFactory mViewModelProviderFactory;


    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().setTitle(R.string.settings);
        getFragmentComponent().inject(this);
        mSettingViewModel = new ViewModelProvider(this, mViewModelProviderFactory).get(SettingViewModel.class);
        mMainViewModel = new ViewModelProvider(requireActivity(), mViewModelProviderFactory).get(MainViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    protected void setUp(View view) {
        initUI();
    }

    private void initUI() {

        setupDropDowns();
    }

    private void setupDropDowns() {
        ArrayAdapter mediaDirsDropDownAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.save_to));
        ArrayAdapter intervalsDirsDropDownAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.intervals));
        binding.mediaFilter.setAdapter(mediaDirsDropDownAdapter);
        binding.slideShowFilter.setAdapter(intervalsDirsDropDownAdapter);
        binding.setViewModel(mSettingViewModel);
        binding.setLifecycleOwner(this);
    }
}