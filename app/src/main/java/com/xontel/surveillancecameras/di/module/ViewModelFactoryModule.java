package com.xontel.surveillancecameras.di.module;


import androidx.lifecycle.ViewModelProvider;

import com.xontel.surveillancecameras.ViewModels.ViewModelProviderFactory;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class ViewModelFactoryModule {
    @Binds
    public abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelProviderFactory modelProviderFactory);
}
