package com.xontel.surveillancecameras.di.component;


import com.xontel.surveillancecameras.activities.AddCamActivity;
import com.xontel.surveillancecameras.activities.AddNewDeviceActivity;
import com.xontel.surveillancecameras.activities.CamerasActivity;
import com.xontel.surveillancecameras.activities.DevicesActivity;
import com.xontel.surveillancecameras.activities.HomeActivity;
import com.xontel.surveillancecameras.activities.SettingsActivity;
import com.xontel.surveillancecameras.di.PerActivity;
import com.xontel.surveillancecameras.di.module.ActivityModule;
import com.xontel.surveillancecameras.di.module.ViewModelModule;

import dagger.Component;

/**
 * Created on : Jan 19, 2019
 * Author     : AndroidWave
 * Email    : info@androidwave.com
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {
        ActivityModule.class,
        ViewModelModule.class

})
public interface ActivityComponent {
    void inject(HomeActivity homeActivity);

    void inject(AddCamActivity addCamActivity);

    void inject(AddNewDeviceActivity addNewDeviceActivity);

    void inject(DevicesActivity devicesActivity);

    void inject(CamerasActivity camerasActivity);

    void inject(SettingsActivity settingsActivity);


}