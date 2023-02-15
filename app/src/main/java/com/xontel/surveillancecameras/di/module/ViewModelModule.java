package com.xontel.surveillancecameras.di.module;


import androidx.lifecycle.ViewModel;

import com.xontel.surveillancecameras.viewModels.GridViewModel;
import com.xontel.surveillancecameras.viewModels.MainViewModel;
import com.xontel.surveillancecameras.viewModels.SettingViewModel;
import com.xontel.surveillancecameras.di.ViewModelKey;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel.class)
    public abstract ViewModel bindMainViewModel(MainViewModel mainViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(GridViewModel.class)
    public abstract ViewModel bindGridViewModel(GridViewModel gridViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SettingViewModel.class)
    public abstract ViewModel bindSettingViewModel(SettingViewModel settingViewModel);
}
