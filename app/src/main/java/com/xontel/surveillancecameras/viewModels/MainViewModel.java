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
import com.xontel.surveillancecameras.hikvision.CamDevice;
import com.xontel.surveillancecameras.hikvision.HikUtil;
import com.xontel.surveillancecameras.root.AppConstant;
import com.xontel.surveillancecameras.utils.CamDeviceType;
import com.xontel.surveillancecameras.utils.StorageHelper;
import com.xontel.surveillancecameras.utils.rx.SchedulerProvider;

import org.videolan.libvlc.MediaPlayer;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainViewModel extends BaseViewModel {
    @Inject
    public GridObservable mGridObservable;
    public static final String TAG = MainViewModel.class.getSimpleName();
    public final MutableLiveData<List<IpCam>> ipCams = new MutableLiveData<>(new ArrayList<>());
    public final MutableLiveData<List<CamDevice>> camDevices = new MutableLiveData<>(new ArrayList<>());
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
                    extractDevices();
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
                            showToastMessage(R.string.device_deleted);
                        }, error -> {
                            Log.e(TAG, error.getMessage());
                            getLoading().setValue(false);
                            getError().setValue(true);
                            setErrorMessage(error.getMessage());
                        }));

    }

    private void showToastMessage(int device_deleted) {
        Toast.makeText(context, device_deleted, Toast.LENGTH_LONG).show();
    }


    private void extractDevices() {
        List<IpCam> tempIpCams = new ArrayList<>();
        if (camDevices.getValue() != null && !camDevices.getValue().isEmpty()) {
            for (CamDevice camDevice : camDevices.getValue()) {
                if (camDevice.deviceType == CamDeviceType.HIKVISION.getValue()) {
                    HikUtil.extractCamsFromDevice(camDevice);
                } else if (camDevice.deviceType == CamDeviceType.DAHUA.getValue()) {
                    DahuaUtil.extractCamsFromDevice(camDevice);
                } else {
                    camDevice.getCams().add(new IpCam(1, camDevice.getId(), camDevice.getDeviceType(), camDevice.getLogId(), camDevice.getName(), camDevice.getIpAddress().isEmpty() || camDevice.getIpAddress() == null ? camDevice.getUrl() : camDevice.getIpAddress()));

                }
                tempIpCams.addAll(camDevice.getCams());
            }
            ipCams.setValue(tempIpCams);
        }
    }

}
