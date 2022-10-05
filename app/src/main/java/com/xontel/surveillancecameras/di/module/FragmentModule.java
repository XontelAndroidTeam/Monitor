package com.xontel.surveillancecameras.di.module;


import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.xontel.surveillancecameras.utils.rx.AppSchedulerProvider;
import com.xontel.surveillancecameras.utils.rx.SchedulerProvider;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class FragmentModule {

    private AppCompatActivity mActivity;

    public FragmentModule(AppCompatActivity activity) {
        this.mActivity = activity;
    }

    @Provides
    Context provideContext() {
        return mActivity;
    }

    @Provides
    AppCompatActivity provideActivity() {
        return mActivity;
    }


    @Provides
    CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }


    @Provides
    SchedulerProvider provideSchedulerProvider() {
        return new AppSchedulerProvider();
    }
}
