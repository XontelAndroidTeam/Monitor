package com.xontel.surveillancecameras.di.component;


import com.xontel.surveillancecameras.di.module.ViewModelModule;
import com.xontel.surveillancecameras.fragments.GridFragment;

import dagger.Component;

@Component(modules = {
        ViewModelModule.class

})
public interface FragmentComponent {
    void inject(GridFragment gridFragment);
}
