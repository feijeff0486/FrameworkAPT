package com.jeff.framework.apt.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jeff.framework.mvp.BasePresenter;

/**
 * <p>
 *
 * @author Jeff
 * @date 2021/1/8
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public class MainPresenter extends BasePresenter<MainView> {
    private static final String TAG = "MainPresenter";

    public MainPresenter() {
        Log.d(TAG, "MainPresenter: ");
    }

    public void mainTest(){
        if (getView()==null)return;
        Log.d(TAG, "mainTest: ");
        getView().mainTest();
    }

    @Override
    public void onCreatePresenter(@Nullable Bundle savedState) {
        super.onCreatePresenter(savedState);
        Log.d(TAG, "onCreatePresenter: ");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: ");
    }

    @Override
    protected void onPresenterCleared() {
        super.onPresenterCleared();
        Log.d(TAG, "onPresenterCleared: ");
    }

    @Override
    public void detach() {
        super.detach();
        Log.d(TAG, "detach: ");
    }
}
