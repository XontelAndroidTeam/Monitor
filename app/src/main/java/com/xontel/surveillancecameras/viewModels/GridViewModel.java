package com.xontel.surveillancecameras.viewModels;

import android.content.Context;

import androidx.databinding.Observable;
import androidx.databinding.Observable.OnPropertyChangedCallback;
import androidx.lifecycle.MutableLiveData;

import com.xontel.surveillancecameras.base.BaseViewModel;
import com.xontel.surveillancecameras.data.DataManager;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.hikvision.CamDevice;
import com.xontel.surveillancecameras.utils.rx.SchedulerProvider;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class GridViewModel extends BaseViewModel {
    public static final String TAG = MainViewModel.class.getSimpleName();
    public final MutableLiveData<List<IpCam>> ipCams = new MutableLiveData<>(new ArrayList<>());
    private int currentGridCount ;
    private int index ;
    private MainViewModel mMainViewModel;
    private Context context;

    @Inject
    public GridViewModel(Context context, SchedulerProvider mSchedulerProvider, CompositeDisposable mCompositeDisposable, DataManager manager) {
        super(mSchedulerProvider, mCompositeDisposable, manager);
        this.context = context;

    }



    private void calculateNewIndex() {
        int newGridCount = mMainViewModel.mGridObservable.getValue();
        int oldGridCount = currentGridCount;
        if(newGridCount > oldGridCount){
            index = (int)Math.floor((index * 1.0 * oldGridCount) / newGridCount);
        }else{
            index = (int)Math.ceil((index * 1.0 * oldGridCount) / newGridCount);
        }
    }

    private void populateCamsList() {
        int newGridCount = mMainViewModel.mGridObservable.getValue();
        List<IpCam> newSubList = mMainViewModel.ipCams.getValue().subList(index * newGridCount, index* (newGridCount+1));
        ipCams.setValue(newSubList);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public MainViewModel getMainViewModel() {
        return mMainViewModel;
    }

    public void setMainViewModel(MainViewModel mainViewModel) {
        mMainViewModel = mainViewModel;
        currentGridCount = mMainViewModel.mGridObservable.getValue();
        mMainViewModel.mGridObservable.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                calculateNewIndex();
                populateCamsList();
            }
        });
    }



}
