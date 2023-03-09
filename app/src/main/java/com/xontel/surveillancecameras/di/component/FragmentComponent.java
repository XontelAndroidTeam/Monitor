package com.xontel.surveillancecameras.di.component;


import com.xontel.surveillancecameras.di.PerFragment;
import com.xontel.surveillancecameras.di.module.FragmentModule;
import com.xontel.surveillancecameras.di.module.ViewModelModule;
import com.xontel.surveillancecameras.fragments.CamPreviewFragment;
import com.xontel.surveillancecameras.fragments.DevicesFragment;
import com.xontel.surveillancecameras.fragments.MonitorFragment;
import com.xontel.surveillancecameras.fragments.SavedMediaFragment;
import com.xontel.surveillancecameras.fragments.SettingsFragment;

import dagger.Component;

@PerFragment
@Component(dependencies = ApplicationComponent.class, modules = {
        FragmentModule.class,
        ViewModelModule.class
})
public interface FragmentComponent {
    void inject(SettingsFragment settingsFragment);
    void inject(SavedMediaFragment savedMediaFragment);
    void inject(MonitorFragment gridFragment);
    void inject(DevicesFragment devicesFragment);
    void inject(CamPreviewFragment camPreviewFragment);
}