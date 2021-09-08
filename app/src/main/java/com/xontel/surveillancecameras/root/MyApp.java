package com.xontel.surveillancecameras.root;

import android.app.Application;

import com.xontel.surveillancecameras.di.component.ApplicationComponent;
import com.xontel.surveillancecameras.di.component.DaggerApplicationComponent;
import com.xontel.surveillancecameras.di.module.ApplicationModule;

public class MyApp extends Application {
    private ApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this)).build();
        mApplicationComponent.inject(this);
    }

    public ApplicationComponent getComponent() {
        return mApplicationComponent;
    }


    // Needed to replace the component with a test specific one
    public void setComponent(ApplicationComponent applicationComponent) {
        mApplicationComponent = applicationComponent;
    }
}
