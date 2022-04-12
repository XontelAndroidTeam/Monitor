package com.xontel.surveillancecameras.data.network;

import com.xontel.surveillancecameras.data.network.pojo.FeedItem;
import com.xontel.surveillancecameras.data.network.pojo.LoginRequest;
import com.xontel.surveillancecameras.data.network.pojo.TimeZoneResponse;
import com.xontel.surveillancecameras.data.network.pojo.UserProfile;
import com.xontel.surveillancecameras.data.network.pojo.WrapperResponse;

import java.util.List;

import io.reactivex.rxjava3.core.Single;


public interface RestApiHelper {

    Single<WrapperResponse<UserProfile>> doLoginApiCall(LoginRequest request);

    Single<WrapperResponse<List<FeedItem>>> getFeedList();
}
