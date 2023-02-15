package com.xontel.surveillancecameras.viewModels;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.xontel.surveillancecameras.base.BaseViewModel;
import com.xontel.surveillancecameras.data.DataManager;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.utils.MediaData;
import com.xontel.surveillancecameras.utils.StorageHelper;
import com.xontel.surveillancecameras.utils.rx.SchedulerProvider;

import org.videolan.libvlc.Media;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.SingleSource;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Function;

public class MediaViewModel extends BaseViewModel {
    public static final String TAG = MediaViewModel.class.getSimpleName();
    private Context context;
    public MutableLiveData<List<MediaData>> media = new MutableLiveData<>(new ArrayList<>());
    public MutableLiveData<List<MediaData>> videos = new MutableLiveData<>(new ArrayList<>());

    @Inject
    public MediaViewModel(Context context, SchedulerProvider mSchedulerProvider, CompositeDisposable mCompositeDisposable, DataManager manager) {
        super(mSchedulerProvider, mCompositeDisposable, manager);
        this.context = context ;
        getAllAppMedia(Environment.DIRECTORY_PICTURES);
        getAllAppMedia(Environment.DIRECTORY_MOVIES);
    }


    public void getAllAppMedia(String mediaType){
        getLoading().setValue(true);
        getCompositeDisposable().add(getDataManager()
                .getStoredMedia(context, mediaType)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(response -> {
                    getLoading().setValue(false);
                   List<MediaData> mediaData = media.getValue();
                   mediaData.addAll(response);
                   media.setValue(mediaData);

                }, error -> {
                    Log.e(TAG, error.getMessage() );
                    getLoading().setValue(false);
                    getError().setValue(true);
                    setErrorMessage(error.getMessage());
                }));
    }
}
