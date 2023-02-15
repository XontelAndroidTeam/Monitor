package com.xontel.surveillancecameras.hikvision;

import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;
import com.hikvision.netsdk.RealPlayCallBack;
import com.xontel.surveillancecameras.data.db.model.IpCam;

import org.reactivestreams.Subscriber;

import io.reactivex.Flowable;

public class HikStreamObservable extends Flowable<StreamData> implements RealPlayCallBack {
    private IpCam mIpCam ;
    private Subscriber<? super StreamData> mSubscriber ;
    private StreamData mStreamData = new StreamData();


    public HikStreamObservable(IpCam ipCam) {
        mIpCam = ipCam;
    }

    public void openStream(Subscriber<? super StreamData> s) {
            NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
            previewInfo.lChannel = mIpCam.getChannel();
            previewInfo.dwStreamType = 1; // mainstream
            previewInfo.bBlocked = 1;

            mIpCam.setRealPlayId(HCNetSDK.getInstance().NET_DVR_RealPlay_V40((int)mIpCam.getLoginId(), previewInfo, this));

            if (mIpCam.getRealPlayId() < 0L) {
                s.onError(new Throwable( "NET_DVR_RealPlay is failed!Err: " + HCNetSDK.getInstance().NET_DVR_GetLastError()));
                return;
            }
    }

    @Override
    protected void subscribeActual(Subscriber<? super StreamData> s) {
        this.mSubscriber = s;
        openStream(s);
    }

    @Override
    public void fRealDataCallBack(int iRealHandle, int iDataType, byte[] pDataBuffer, int iDataSize) {
        mStreamData.setDataSize(iDataSize);
        mStreamData.setDataBuffer(pDataBuffer);
        mStreamData.setDataType(iDataType);
        mStreamData.setRealHandle(iRealHandle);
        mSubscriber.onNext(mStreamData);
    }
}
