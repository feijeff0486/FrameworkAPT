package com.jeff.framework.mvp;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jeff
 * @describe
 * @date 2020/3/31.
 */
public class PresenterStore<V extends IBaseContract.IBaseView,P extends BasePresenter<V>> {
    private String TAG = "P-Store";
    private Map<String, P> mMap = new HashMap<>();

    public PresenterStore() {
    }

    public PresenterStore(String name) {
        this.TAG = name;
    }

    public final void put(String key, P presenter) {
        Log.d(TAG, "put: k:"+key+",p:"+presenter);
        P oldPresenter = mMap.put(key, presenter);
        if (oldPresenter != null) {
            oldPresenter.onPresenterCleared();
        }
    }

    public final P get(String key) {
        return mMap.get(key);
    }

    public final void clear() {
        Log.d(TAG, "clear: ");
        for (P presenter : mMap.values()) {
            presenter.onPresenterCleared();
        }
        mMap.clear();
    }

    public int getSize() {
        return mMap.size();
    }

    public Map<String, P> getMap() {
        return mMap;
    }
}