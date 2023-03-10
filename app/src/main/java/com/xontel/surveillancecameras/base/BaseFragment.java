package com.xontel.surveillancecameras.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.xontel.surveillancecameras.di.component.ActivityComponent;
import com.xontel.surveillancecameras.di.component.DaggerFragmentComponent;
import com.xontel.surveillancecameras.di.component.FragmentComponent;
import com.xontel.surveillancecameras.di.module.FragmentModule;
import com.xontel.surveillancecameras.root.MyApp;
import com.xontel.surveillancecameras.utils.CommonUtils;


/**
 * Created on : Jan 19, 2019
 * Author     : AndroidWave
 * Email    : info@androidwave.com
 */
public abstract class BaseFragment extends Fragment implements MvpView {
    FragmentComponent fragmentComponent ;
    protected static ProgressDialog mProgressDialog;
    private BaseActivity mActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        fragmentComponent = DaggerFragmentComponent.builder()
                .fragmentModule(new FragmentModule(((AppCompatActivity)getActivity())))
                .applicationComponent(((MyApp) getBaseActivity().getApplication()).getComponent())
                .build();
    }

    public FragmentComponent getFragmentComponent() {
        return fragmentComponent;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUp(view);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            BaseActivity activity = (BaseActivity) context;
            this.mActivity = activity;
            activity.onFragmentAttached();
        }
    }

    @Override
    public void showLoading() {
        hideLoading();
        mProgressDialog = CommonUtils.showLoadingDialog(this.getContext());
    }

    @Override
    public void hideLoading() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }


    public void setCancelable() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.setCancelable(true);
            mProgressDialog.setCanceledOnTouchOutside(true);
        }
    }

    @Override
    public void onError(String message) {
        if (mActivity != null) {
            mActivity.onError(message);
        }
    }

    @Override
    public void onError(@StringRes int resId) {
        if (mActivity != null) {
            mActivity.onError(resId);
        }
    }

    @Override
    public void showMessage(String message) {
        if (mActivity != null) {
            mActivity.showMessage(message);
        }
    }

    @Override
    public void showMessage(@StringRes int resId) {
        if (mActivity != null) {
            mActivity.showMessage(resId);
        }
    }

    @Override
    public boolean isNetworkConnected() {
        if (mActivity != null) {
            return mActivity.isNetworkConnected();
        }
        return false;
    }

    @Override
    public void onDetach() {
        mActivity = null;
        super.onDetach();
    }

    @Override
    public void hideKeyboard() {
        if (mActivity != null) {
            mActivity.hideKeyboard();
        }
    }

    @Override
    public void openActivityOnTokenExpire() {
        if (mActivity != null) {
            mActivity.openActivityOnTokenExpire();
        }
    }

    @Override
    public void openLoginActivity() {
        if (mActivity != null) {
            mActivity.openLoginActivity();
        }
    }

    public ActivityComponent getActivityComponent() {
        if (mActivity != null) {
            return mActivity.getActivityComponent();
        }
        return null;
    }

    public BaseActivity getBaseActivity() {
        return mActivity;
    }


    protected abstract void setUp(View view);


    public interface Callback {

        void onFragmentAttached();

        void onFragmentDetached(String tag);
    }
}
