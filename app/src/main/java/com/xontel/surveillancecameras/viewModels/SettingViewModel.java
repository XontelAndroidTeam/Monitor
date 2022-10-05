package com.xontel.surveillancecameras.viewModels;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.xontel.surveillancecameras.base.BaseViewModel;
import com.xontel.surveillancecameras.data.DataManager;
import com.xontel.surveillancecameras.utils.rx.SchedulerProvider;

import javax.inject.Inject;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class SettingViewModel extends BaseViewModel {


    private final MutableLiveData<Integer> slideShowInterval = new MutableLiveData<Integer>();
    private final ObservableBoolean autoSlideShow = new ObservableBoolean();


    public ObservableBoolean getAutoSlideShow() {
        return autoSlideShow;
    }

    public LiveData<Integer> getSlideShowInterval() {
        return slideShowInterval;
    }

    private final MutableLiveData<Integer> mediaStorage = new MutableLiveData<Integer>();

    public LiveData<Integer> getMediaStorage() {
        return mediaStorage;
    }


    private final MutableLiveData<Boolean> enableSlideShow = new MutableLiveData<Boolean>(false);

    public LiveData<Boolean> getEnableSlideShow() {
        return enableSlideShow;
    }


    @Inject
    public SettingViewModel(SchedulerProvider mSchedulerProvider, CompositeDisposable mCompositeDisposable, DataManager manager) {
        super(mSchedulerProvider, mCompositeDisposable, manager);

    }

    public void getAutoSlideShowValue() {
        enableSlideShow.setValue(getDataManager().getAutoSlideShow());
    }


}
