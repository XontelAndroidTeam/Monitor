package com.xontel.surveillancecameras.viewModels;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.xontel.surveillancecameras.base.BaseViewModel;
import com.xontel.surveillancecameras.customObservers.GridObservable;
import com.xontel.surveillancecameras.dahua.DahuaUtil;
import com.xontel.surveillancecameras.data.DataManager;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.hikvision.CamDevice;
import com.xontel.surveillancecameras.hikvision.HikUtil;
import com.xontel.surveillancecameras.root.AppConstant;
import com.xontel.surveillancecameras.utils.CamDeviceType;
import com.xontel.surveillancecameras.utils.rx.SchedulerProvider;

import org.videolan.libvlc.MediaPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class MainViewModel extends BaseViewModel {

    @Inject
    public GridObservable mGridObservable ;
    public static final String TAG = MainViewModel.class.getSimpleName();
    public MutableLiveData<List<IpCam>> ipCams = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<CamDevice>> camDevices = new MutableLiveData<>(new ArrayList<>());
    public MutableLiveData<Integer> gridCount = new MutableLiveData<>(4);
    public MutableLiveData<Integer> lifeCycleObservable = new MutableLiveData<>(0);
    private Context context ;


    @Inject
    public MainViewModel(Context context, SchedulerProvider mSchedulerProvider, CompositeDisposable mCompositeDisposable, DataManager manager) {
        super(mSchedulerProvider, mCompositeDisposable, manager);
        this.context = context ;
        getAllCameras();
    }

    public GridObservable getGridObservable() {
        return mGridObservable;
    }


    public void setIpCams(List<IpCam> ipCamsData){
        Objects.requireNonNull(ipCams.getValue()).addAll(ipCamsData);
    }

    public void getAllCameras(){
        getLoading().setValue(true);
        getCompositeDisposable().add(getDataManager()
                .getDevicesAll()
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(response -> {
                    getLoading().setValue(false);
                    camDevices.setValue(response);
                    extractDevices();
                }, error -> {
                    Log.e(TAG, error.getMessage() );
                    getLoading().setValue(false);
                    getError().setValue(true);
                    setErrorMessage(error.getMessage());
                }));
    }


    private void extractDevices() {
        if (camDevices.getValue() != null && !camDevices.getValue().isEmpty()){
            for (CamDevice camDevice : camDevices.getValue()){
                if(camDevice.deviceType == CamDeviceType.HIKVISION.getValue()){
                    HikUtil.extractCamsFromDevice(camDevice);
                }else if (camDevice.deviceType == CamDeviceType.DAHUA.getValue()){
                    DahuaUtil.extractCamsFromDevice(camDevice);
                }else{
                    //Todo MediaPlayer
                }
                ipCams.setValue(camDevice.getCams());
            }
        }
    }
}
