package com.xontel.surveillancecameras.utils.rx;


import io.reactivex.rxjava3.core.Scheduler;

/**
 * Created by apple on 08/05/18.
 */


public interface SchedulerProvider {

    Scheduler ui();

    Scheduler computation();

    Scheduler io();

}
