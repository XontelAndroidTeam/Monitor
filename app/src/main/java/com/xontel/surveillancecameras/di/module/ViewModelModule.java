package com.xontel.surveillancecameras.di.module;


import androidx.lifecycle.ViewModel;

import com.xontel.surveillancecameras.ViewModels.MainViewModel;
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
}
