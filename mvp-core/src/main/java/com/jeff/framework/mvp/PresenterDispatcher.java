package com.jeff.framework.mvp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Iterator;
import java.util.Map;

/**
 * @author Jeff
 * @describe
 * @date 2020/3/31.
 */
public class PresenterDispatcher {
    private String TAG = "P-Dispatcher";
    private PresenterStore mPresenterStore;

    public PresenterDispatcher(PresenterStore store) {
        this("P-Dispatcher", store);
    }

    public PresenterDispatcher(String name, PresenterStore store) {
        TAG = name;
        this.mPresenterStore = store;
    }

    public <V extends IBaseContract.IBaseView, P extends BasePresenter<V>> void attach(V view) {
        Iterator<Map.Entry<String, P>> it = mPresenterStore.getMap().entrySet().iterator();
        Log.d(TAG, "attach: " + mPresenterStore.getSize());
        while (it.hasNext()) {
            Map.Entry<String, P> entry = it.next();
            Log.d(TAG, "attach: key= " + entry.getKey() + ", value= " + entry.getValue());
            P presenter = entry.getValue();
            if (presenter != null) {
                presenter.attach(view);
            } else {
                Log.e(TAG, "attach: null value >>> " + entry.getKey());
            }
        }
    }

    public <V extends IBaseContract.IBaseView, P extends BasePresenter<V>> void detach() {
        Iterator<Map.Entry<String, P>> it = mPresenterStore.getMap().entrySet().iterator();
        Log.d(TAG, "detach: " + mPresenterStore.getSize());
        while (it.hasNext()) {
            Map.Entry<String, P> entry = it.next();
            Log.d(TAG, "detach: key= " + entry.getKey() + ", value= " + entry.getValue());
            P presenter = entry.getValue();
            if (presenter != null) {
                presenter.detach();
            } else {
                Log.e(TAG, "detach: null value >>> " + entry.getKey());
            }
        }
    }

    public <V extends IBaseContract.IBaseView, P extends BasePresenter<V>> void onCreatePresenter(@Nullable Bundle savedState) {
        Iterator<Map.Entry<String, P>> it = mPresenterStore.getMap().entrySet().iterator();
        Log.d(TAG, "onCreatePresenter: " + mPresenterStore.getSize());
        while (it.hasNext()) {
            Map.Entry<String, P> entry = it.next();
            Log.d(TAG, "onCreatePresenter: key= " + entry.getKey() + ", value= " + entry.getValue());
            P presenter = entry.getValue();
            if (presenter != null) {
                presenter.onCreatePresenter(savedState);
            } else {
                Log.e(TAG, "onCreatePresenter: null value >>> " + entry.getKey());
            }
        }
    }

    public <V extends IBaseContract.IBaseView, P extends BasePresenter<V>> void onSaveInstanceState(Bundle outState) {
        Iterator<Map.Entry<String, P>> it = mPresenterStore.getMap().entrySet().iterator();
        Log.d(TAG, "onSaveInstanceState: " + mPresenterStore.getSize());
        while (it.hasNext()) {
            Map.Entry<String, P> entry = it.next();
            Log.d(TAG, "onSaveInstanceState: key= " + entry.getKey() + ", value= " + entry.getValue());
            P presenter = entry.getValue();
            if (presenter != null) {
                presenter.onSaveInstanceState(outState);
            } else {
                Log.e(TAG, "onSaveInstanceState: null value >>> " + entry.getKey());
            }
        }
    }

}
