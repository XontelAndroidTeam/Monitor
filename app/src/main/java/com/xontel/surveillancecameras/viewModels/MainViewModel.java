package com.xontel.surveillancecameras.viewModels;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.base.BaseViewModel;
import com.xontel.surveillancecameras.customObservers.GridObservable;
import com.xontel.surveillancecameras.dahua.DahuaUtil;
import com.xontel.surveillancecameras.data.DataManager;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.fragments.DevicesFragment;
import com.xontel.surveillancecameras.hikvision.CamDevice;
import com.xontel.surveillancecameras.hikvision.HikUtil;
import com.xontel.surveillancecameras.utils.CamDeviceType;
import com.xontel.surveillancecameras.utils.rx.SchedulerProvider;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.SingleSource;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainViewModel extends BaseViewModel {
    @Inject
    public GridObservable mGridObservable;
    public static final String TAG = MainViewModel.class.getSimpleName();
    public final MutableLiveData<List<IpCam>> ipCams = new MutableLiveData<>(new ArrayList<>());
    public final MutableLiveData<List<CamDevice>> camDevices = new MutableLiveData<>(new ArrayList<>());

    public final MutableLiveData<Boolean> reloader = new MutableLiveData<>(false);
    private Context context;


    @Inject
    public MainViewModel(Context context, SchedulerProvider mSchedulerProvider, CompositeDisposable mCompositeDisposable, DataManager manager) {
        super(mSchedulerProvider, mCompositeDisposable, manager);
        this.context = context;
    }

    public GridObservable getGridObservable() {
        return mGridObservable;
    }


    public void getAllDevices() {
        getLoading().setValue(true);
        getCompositeDisposable().add(getDataManager()
                .getDevicesAll()
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(response -> {
                    getLoading().setValue(false);
                    camDevices.setValue(response);
                    scanDevices();

                }, error -> {
                    Log.e(TAG, error.getMessage());
                    getLoading().setValue(false);
                    getError().setValue(true);
                    setErrorMessage(error.getMessage());
                }));
    }

    private void scanDevices() {
        List<CamDevice> devices = camDevices.getValue();
        for(CamDevice camDevice : devices){
            if(!camDevice.isScanned()){
                camDevice.login();
                camDevice.setScanned(true);
            }
        }
        camDevices.setValue(devices);
    }



    public void createDevice(CamDevice device) {
        getLoading().setValue(true);
        getCompositeDisposable().add(getDataManager()
                .insertCamDevice(device)
                .flatMap((Function<Long, SingleSource<Integer>>)
                        id -> getDataManager().loginHikDevice(device))
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(response -> {

                    getLoading().setValue(false);
                    showToastMessage(context, R.string.device_created);
                }, error -> {
                    Log.e(TAG, error.getMessage());
                    getLoading().setValue(false);
                    getError().setValue(true);
                    showToastMessage(context, R.string.Cant_LOGIN);
                }));

    }


    public void updateDevice(CamDevice device) {
        device.setScanned(false);
        getLoading().setValue(true);
        getCompositeDisposable().add(getDataManager()
                .updateCamDevice(device)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(response -> {
                    getLoading().setValue(false);
                    showToastMessage(context, R.string.device_updated);
                    reloader.setValue(true);
                }, error -> {
                    Log.e(TAG, error.getMessage());
                    getLoading().setValue(false);
                    getError().setValue(true);
                    setErrorMessage(error.getMessage());
                }));
    }

    public void deleteDevice(CamDevice device) {
        getLoading().setValue(true);
        getCompositeDisposable().add(
                getDataManager()
                        .deleteCamDevice(device)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(success -> {
                            getLoading().setValue(false);
                            showToastMessage(context, R.string.device_deleted);
                        }, error -> {
                            Log.e(TAG, error.getMessage());
                            getLoading().setValue(false);
                            getError().setValue(true);
                            setErrorMessage(error.getMessage());
                        }));

    }




//    private void extractDevices() {
//        List<IpCam> tempIpCams = new ArrayList<>();
//        if (camDevices.getValue() != null && !camDevices.getValue().isEmpty()) {
//            for (CamDevice camDevice : camDevices.getValue()) {
//                if (camDevice.deviceType == CamDeviceType.HIKVISION.getValue()) {
//                    HikUtil.extractCamsFromDevice(camDevice);
//                } else if (camDevice.deviceType == CamDeviceType.DAHUA.getValue()) {
//                    DahuaUtil.extractCamsFromDevice(camDevice);
//                } else {
//                    camDevice.getCams().add(new IpCam(1, camDevice.getId(), camDevice.getDeviceType(), camDevice.getLogId(), camDevice.getName()));
//
//                }
//                tempIpCams.addAll(camDevice.getCams());
//            }
//            ipCams.setValue(tempIpCams);
//        }
//    }




}
