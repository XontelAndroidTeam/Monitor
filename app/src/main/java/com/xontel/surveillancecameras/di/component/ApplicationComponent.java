package com.xontel.surveillancecameras.di.component;

import android.app.Application;
import android.content.Context;

import com.xontel.surveillancecameras.data.DataManager;
import com.xontel.surveillancecameras.di.ApplicationContext;
import com.xontel.surveillancecameras.di.module.ApplicationModule;
import com.xontel.surveillancecameras.di.module.ViewModelModule;
import com.xontel.surveillancecameras.root.MyApp;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;

/**
 * Created on : Jan 19, 2019
 * Author     : AndroidWave
 * Email    : info@androidwave.com
 */
@Singleton
@Component(modules = {
        ApplicationModule.class,
        ViewModelModule.class})
public interface ApplicationComponent {

    void inject(MyApp app);

    @ApplicationContext
    Context context();
    Application application();
    DataManager getDataManager();
//    @Component.Builder
//    interface Builder {
//        ApplicationComponent build();
//
//        Builder applicationModule(ApplicationModule applicationModule);
//
//        DataManager getDataManager();
//    }


}
