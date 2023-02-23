package com.xontel.surveillancecameras.viewModels;

import android.content.Context;
import android.util.Log;

import androidx.databinding.Observable;
import androidx.databinding.Observable.OnPropertyChangedCallback;
import androidx.lifecycle.MutableLiveData;

import com.xontel.surveillancecameras.base.BaseViewModel;
import com.xontel.surveillancecameras.data.DataManager;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.hikvision.CamDevice;
import com.xontel.surveillancecameras.hikvision.HIKPlayer;
import com.xontel.surveillancecameras.utils.rx.SchedulerProvider;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class GridViewModel extends BaseViewModel {
    public static final String TAG = MainViewModel.class.getSimpleName();
    private MainViewModel mMainViewModel;

    public MutableLiveData<Boolean> gridChanged = new MutableLiveData<>();

    private Context context;


    @Inject
    public GridViewModel(Context context, SchedulerProvider mSchedulerProvider, CompositeDisposable mCompositeDisposable, DataManager manager) {
        super(mSchedulerProvider, mCompositeDisposable, manager);
        this.context = context;

    }






    public void setMainViewModel(MainViewModel mainViewModel) {
        mMainViewModel = mainViewModel;
        mMainViewModel.mGridObservable.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                gridChanged.setValue(true);

            }
        });
    }


}
