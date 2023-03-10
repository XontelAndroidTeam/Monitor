package com.xontel.surveillancecameras.data.network;

import com.xontel.surveillancecameras.data.network.pojo.FeedItem;
import com.xontel.surveillancecameras.data.network.pojo.LoginRequest;
import com.xontel.surveillancecameras.data.network.pojo.TimeZoneResponse;
import com.xontel.surveillancecameras.data.network.pojo.UserProfile;
import com.xontel.surveillancecameras.data.network.pojo.WrapperResponse;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Single;


@Singleton
public class RestApiManager implements RestApiHelper {

    NetworkService mService;

    @Inject
    public RestApiManager(NetworkService apiService) {
        mService = apiService;
    }

    @Override
    public Single<WrapperResponse<UserProfile>> doLoginApiCall(LoginRequest request) {
        return mService.doLoginApiCall(request);
    }

    @Override
    public Single<WrapperResponse<List<FeedItem>>> getFeedList() {
        return mService.getFeedList();
    }

}
