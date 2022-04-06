package com.xontel.surveillancecameras.ViewModels;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.xontel.surveillancecameras.activities.MainActivity;
import com.xontel.surveillancecameras.base.BaseViewModel;
import com.xontel.surveillancecameras.data.DataManager;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.utils.rx.SchedulerProvider;

import org.videolan.libvlc.MediaPlayer;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class MainViewModel extends BaseViewModel {
    public static final String TAG = MainViewModel.class.getSimpleName();
    public MutableLiveData<List<IpCam>> ipCams = new MutableLiveData<>(new ArrayList<>());
    public MutableLiveData<Integer> gridCount = new MutableLiveData<>(MainActivity.DEFAULT_GRID_COUNT);
    public MutableLiveData<List<MediaPlayer>> mediaPlayers = new MutableLiveData<>();

    @Inject
    public MainViewModel(SchedulerProvider mSchedulerProvider, CompositeDisposable mCompositeDisposable, DataManager manager) {
        super(mSchedulerProvider, mCompositeDisposable, manager);
    }


    public void getAllCameras(){
        getLoading().postValue(true);
        getCompositeDisposable().add(getDataManager()
                .getAll()
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(response -> {
                    getLoading().postValue(false);
                    ipCams.postValue(response);
                }, error -> {
                    Log.e("error", error.getMessage() );
                    getLoading().postValue(false);
                    getError().postValue(true);
                    setErrorMessage(error.getMessage());
                }));
    }
}
