package com.xontel.surveillancecameras.viewModels;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.xontel.surveillancecameras.base.BaseViewModel;
import com.xontel.surveillancecameras.customObservers.GridObservable;
import com.xontel.surveillancecameras.data.DataManager;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.root.AppConstant;
import com.xontel.surveillancecameras.utils.rx.SchedulerProvider;

import org.videolan.libvlc.MediaPlayer;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class MainViewModel extends BaseViewModel {
//    public static final int ON_CREATE = 1 ;
//    public static final int ON_RESUME = 2 ;
//    public static final int ON_PAUSE = 3 ;
//    public static final int ON_DESTROY = 4 ;


    @Inject
    public GridObservable mGridObservable ;
    public static final String TAG = MainViewModel.class.getSimpleName();
    public MutableLiveData<List<IpCam>> ipCams = new MutableLiveData<>(new ArrayList<>());
//    public MutableLiveData<Integer> gridCount = new MutableLiveData<>(getDataManager().getGridCount());
    public List<MediaPlayer> mediaPlayers ;
//    public MutableLiveData<Integer> lifeCycleObservable = new MutableLiveData<>(0);
    private Context context ;


    @Inject
    public MainViewModel(Context context, SchedulerProvider mSchedulerProvider, CompositeDisposable mCompositeDisposable, DataManager manager) {
        super(mSchedulerProvider, mCompositeDisposable, manager);
        this.context = context ;
        mediaPlayers = createMediaPlayers();
        getAllCameras();
    }

    public GridObservable getGridObservable() {
        return mGridObservable;
    }

    private List<MediaPlayer> createMediaPlayers() {
        List<MediaPlayer> mediaPlayers = new ArrayList<>();
        for(int i = 0; i < AppConstant.MAX_CAMS_IN_WINDOW; i ++){
            mediaPlayers.add(new MediaPlayer(context));
        }
        return mediaPlayers;
    }

    public void resetPlayers(){
        for(int i = 0 ; i < mediaPlayers.size() ; i++){
            mediaPlayers.get(i).stop();
            mediaPlayers.get(i).detachViews();
        }
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
                    Log.e(TAG, error.getMessage() );
                    getLoading().postValue(false);
                    getError().postValue(true);
                    setErrorMessage(error.getMessage());
                }));
    }
}
