package com.xontel.surveillancecameras.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.storage.StorageVolume;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.utils.StorageBroadcastReceiver;
import com.xontel.surveillancecameras.utils.StorageHelper;
import com.xontel.surveillancecameras.viewModels.MainViewModel;
import com.xontel.surveillancecameras.viewModels.SettingViewModel;
import com.xontel.surveillancecameras.viewModels.ViewModelProviderFactory;
import com.xontel.surveillancecameras.base.BaseFragment;
import com.xontel.surveillancecameras.databinding.FragmentSettingsBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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
    private ArrayList<String> currentStorage = new ArrayList<>();

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
        getFragmentComponent().inject(this);
        getCurrentStorage();
        mSettingViewModel = new ViewModelProvider(this, mViewModelProviderFactory).get(SettingViewModel.class);
        mMainViewModel = new ViewModelProvider(requireActivity(), mViewModelProviderFactory).get(MainViewModel.class);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        requireActivity().setTitle(R.string.settings);
        binding = FragmentSettingsBinding.inflate(inflater);
        binding.setViewModel(mSettingViewModel);
        binding.setLifecycleOwner(this);
        StorageBroadcastReceiver.refreshRemovable.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean){
                    currentStorage.clear();
                    getCurrentStorage();
                    bindStorageAgain();
                StorageBroadcastReceiver.refreshRemovable.setValue(false);
            }
        });
        return binding.getRoot();
    }

    @Override
    protected void setUp(View view) {
        initUI();
    }

    private void initUI() {
        setupDropDowns();
    }

    private void getCurrentStorage(){
        for (StorageVolume s : StorageHelper.getActiveVolumes(requireContext())){
            currentStorage.add(StorageHelper.getLabelFromVolume(requireContext(),s));
        }
    }

    private void bindStorageAgain(){
        ArrayAdapter mediaDirsDropDownAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, currentStorage);
        String value = StorageHelper.getSaveStorageName(requireContext()) ;
        binding.mediaFilter.setText( currentStorage.contains(value) ? value : requireContext().getString(R.string.internal_storage)  );
        binding.mediaFilter.setAdapter(mediaDirsDropDownAdapter);
    }

    private void setupDropDowns() {
        ArrayAdapter mediaDirsDropDownAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, currentStorage);
        ArrayAdapter intervalsDirsDropDownAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.intervals));
        binding.mediaFilter.setText(StorageHelper.getSaveStorageName(requireContext()));
        binding.slideShowFilter.setText(StorageHelper.getSlideInterval(requireContext()) );
        binding.mediaFilter.setAdapter(mediaDirsDropDownAdapter);
        binding.slideShowFilter.setAdapter(intervalsDirsDropDownAdapter);

        binding.slideShowFilter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = binding.slideShowFilter.getText().toString();
                StorageHelper.saveSlideInterval(requireContext(),name);
                binding.slideShowFilter.setText(name);
                binding.slideShowFilter.setAdapter(intervalsDirsDropDownAdapter);
            }
        });

        binding.mediaFilter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = binding.mediaFilter.getText().toString();
                binding.mediaFilter.setText(name);
                binding.mediaFilter.setAdapter(mediaDirsDropDownAdapter);
                StorageHelper.saveStorageType(requireContext(),name);
            }
        });

    }


}