package com.xontel.surveillancecameras.di.module;

import android.app.Application;
import android.content.Context;

import com.xontel.surveillancecameras.BuildConfig;
import com.xontel.surveillancecameras.data.BaseDataManager;
import com.xontel.surveillancecameras.data.DataManager;
import com.xontel.surveillancecameras.data.db.AppDatabase;
import com.xontel.surveillancecameras.data.network.NetworkService;
import com.xontel.surveillancecameras.data.network.RestApiHelper;
import com.xontel.surveillancecameras.data.network.RestApiManager;
import com.xontel.surveillancecameras.data.prefs.PreferencesHelper;
import com.xontel.surveillancecameras.data.prefs.PreferencesManager;
import com.xontel.surveillancecameras.di.ApiInfo;
import com.xontel.surveillancecameras.di.ApplicationContext;
import com.xontel.surveillancecameras.di.DatabaseInfo;
import com.xontel.surveillancecameras.di.PreferenceInfo;
import com.xontel.surveillancecameras.root.AppConstant;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created on : Jan 19, 2019
 * Author     : AndroidWave
 * Email    : info@androidwave.com
 */
@Module
public class ApplicationModule {

    private Application mApplication;

    public ApplicationModule(Application application) {
        this.mApplication = application;
    }


    @Provides
    @ApplicationContext
    Context provideContext() {
        return mApplication;
    }

    @Provides
    Application provideApplication() {
        return mApplication;
    }

    @Provides
    @DatabaseInfo
    String provideDatabaseName() {
        return AppConstant.DB_NAME;
    }

    @Provides
    @ApiInfo
    String provideApiKey() {
        return "43534k5345ljj534j534j545kl34l4kl";
    }

    @Provides
    @PreferenceInfo
    String providePreferenceName() {
        return AppConstant.PREF_NAME;
    }

    @Provides
    @Singleton
    DataManager provideDataManager(BaseDataManager mDataManager) {
        return mDataManager;
    }


    @Provides
    @Singleton
    AppDatabase provideAppDatabase() {
        return AppDatabase.getDatabaseInstance(mApplication);
    }

    @Provides
    @Singleton
    PreferencesHelper providePreferencesHelper(PreferencesManager preferencesManager) {
        return preferencesManager;
    }

    @Provides
    @Singleton
    RestApiHelper provideRestApiHelper(RestApiManager restApiManager) {
        return restApiManager;
    }


    /**
     * @return HTTTP Client
     */
    @Provides
    public OkHttpClient provideClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

        return new OkHttpClient.Builder().addInterceptor(interceptor).addInterceptor(chain -> {
            Request request = chain.request();
            return chain.proceed(request);
        }).build();
    }

    /**
     * provide Retrofit instances
     *
     * @param baseURL base url for api calling
     * @param client  OkHttp client
     * @return Retrofit instances
     */

    @Provides
    public Retrofit provideRetrofit(String baseURL, OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(baseURL)
                .client(client)
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * Provide Api service
     *
     * @return ApiService instances
     */
    @Provides
    public NetworkService provideApiService() {
        return provideRetrofit("http://192.168.1.152", provideClient()).create(NetworkService.class);
    }
}
