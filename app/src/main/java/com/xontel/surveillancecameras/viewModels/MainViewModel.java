package com.xontel.surveillancecameras.viewModels;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.base.BaseViewModel;
import com.xontel.surveillancecameras.customObservers.GridObservable;
import com.xontel.surveillancecameras.dahua.DahuaPlayer;
import com.xontel.surveillancecameras.data.DataManager;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.hikvision.CamDevice;
import com.xontel.surveillancecameras.hikvision.HIKPlayer;
import com.xontel.surveillancecameras.hikvision.HikUtil;
import com.xontel.surveillancecameras.utils.CamPlayer;
import com.xontel.surveillancecameras.utils.rx.SchedulerProvider;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.ObservableSource;
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

    public final MutableLiveData<Boolean> oneCam = new MutableLiveData<>(false);

    public final MutableLiveData<Boolean> takeSnapShot = new MutableLiveData<>(false);

    public final MutableLiveData<Boolean> recordVideo = new MutableLiveData<>(false);


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
                        .toObservable()
                        .flatMapIterable(list -> list)
                        .flatMap(new Function<CamDevice, ObservableSource<CamDevice>>() {
                            @Override
                            public ObservableSource<CamDevice> apply(CamDevice camDevice) throws Throwable {
                                return getDataManager().loginDevice(camDevice).toObservable();
                            }
                        })
                        .flatMap(new Function<CamDevice, ObservableSource<CamDevice>>() {
                            @Override
                            public ObservableSource<CamDevice> apply(CamDevice camDevice) throws Throwable {
                                return getDataManager().getChannelsInfo(camDevice).toObservable();
                            }
                        })
                        .toList()
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(response -> {
                    getLoading().setValue(false);
                   camDevices.setValue(response);
                    populateIpCams();

                }, error -> {
                    Log.e(TAG, error.getMessage());
                    getLoading().setValue(false);
                    getError().setValue(true);
                    setErrorMessage(error.getMessage());
                }));
    }

    private void populateIpCams() {
        List<CamDevice> devices = camDevices.getValue();
        List<IpCam> cams = new ArrayList<>();

        for(CamDevice camDevice: devices){
            cams.addAll(camDevice.getCams());
        }
        ipCams.setValue(cams);
    }


    public void createDevice(CamDevice device) {
        getLoading().setValue(true);
        getCompositeDisposable().add(getDataManager()
                .insertCamDevice(device)
                        .flatMap(new Function<Long, SingleSource<CamDevice>>() {
                            @Override
                            public SingleSource<CamDevice> apply(Long deviceId) throws Throwable {
                                device.setId(deviceId);
                                return getDataManager().loginDevice(device);
                            }
                        })
                        .flatMap(new Function<CamDevice, SingleSource<CamDevice>>() {
                            @Override
                            public SingleSource<CamDevice> apply(CamDevice camDevice) throws Throwable {
                                return getDataManager().getChannelsInfo(device);
                            }
                        })
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(response -> {
                    getLoading().setValue(false);
                    addNewDevice(response);
                    showToastMessage(context, R.string.device_created);
                     reloader.setValue(true);
                }, error -> {
                    Log.e(TAG, error.getMessage());
                    getLoading().setValue(false);
                    getError().setValue(true);
                    showToastMessage(context,error.getMessage());
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
                    updateDeviceInList(device);
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
                            deleteDeviceById(device);
                            deleteIpCamsWithID(device);
                            getLoading().setValue(false);
                            showToastMessage(context, R.string.device_deleted);
                        }, error -> {
                            Log.e(TAG, error.getMessage());
                            getLoading().setValue(false);
                            getError().setValue(true);
                            setErrorMessage(error.getMessage());
                        }));

    }

    private void deleteIpCamsWithID(CamDevice device) {
        List<IpCam> cams = ipCams.getValue();
        for(int i = cams.size() - 1 ; i >=0 ; i--){
            if(cams.get(i).getDeviceId() == device.getId()){
                cams.remove(i);
            }
        }
        ipCams.setValue(cams);
    }

    private void deleteDeviceById(CamDevice device) {
        List<CamDevice> devices = camDevices.getValue();
        devices.remove(device);
        camDevices.setValue(devices);
    }

    private void addNewDevice(CamDevice camDevice) {
        List<CamDevice> devices = camDevices.getValue();
        devices.add(camDevice);
        camDevices.setValue(devices);
        populateIpCams();
    }

    private void updateDeviceInList(CamDevice device) {
        List<CamDevice> devices = camDevices.getValue();
        int deviceIndex = devices.indexOf(device);
        devices.remove(deviceIndex);
        devices.add(deviceIndex, device);
        camDevices.setValue(devices);
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
